package com.email.batch.service.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.email.batch.service.publisher.RabitMQPublisher;
import com.email.request.handling.model.EmailRequest;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
@EnableAsync
public class EmailBatchConfiguration {
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	
	  @Autowired 
	  public DataSource dataSource;
	

	@Bean
	public JdbcCursorItemReader<EmailRequest> reader(){
		
		JdbcCursorItemReader<EmailRequest> reader= new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql("SELECT email FROM emailaddressbatch.user");
		reader.setRowMapper(new UserRowMapper());
		return reader;
	}

	
	public class UserRowMapper implements RowMapper<EmailRequest> {

		@Override
		public EmailRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
			EmailRequest user = new EmailRequest();
			user.setEmail(rs.getString("email"));
			return user;
		}

	}
	
	@Bean
	public EmailRequestItemProcesser processer() {
		return new EmailRequestItemProcesser();
	}
	
	@Bean
	public RabitMQPublisher publishToQueue() {
		return new RabitMQPublisher();
	}

	@Bean
	public Step step1() {
		
		return stepBuilderFactory.get("step1").<EmailRequest,EmailRequest> chunk(1)
				.reader(reader())
				.processor(processer())
				.writer(publishToQueue())
				.build();
	}

	@Bean
	public Job exportUserJob() {
		return jobBuilderFactory.get("exportUserJob")
				.incrementer(new RunIdIncrementer())
				.flow(step1())
				.end()
				.build();
		
		
	}
	
}
