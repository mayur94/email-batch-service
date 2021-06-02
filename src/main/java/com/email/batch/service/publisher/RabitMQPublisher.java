package com.email.batch.service.publisher;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.email.request.handling.model.EmailRequest;

public class RabitMQPublisher implements ItemWriter<EmailRequest> {
	
	
	@Autowired
	public RabbitTemplate template;
	
	

	@Override
	public void write(List<? extends EmailRequest> items) throws Exception {
		
		
	}

}
