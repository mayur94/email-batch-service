package com.email.batch.service.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.email.request.handling.model.EmailRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableBatchProcessing
public class EmailRequestItemProcesser implements ItemProcessor<EmailRequest, EmailRequest> {
	
	@Autowired
	public RabbitTemplate template;
	
	EmailRequest emailIdPublishToQueue= new EmailRequest();
	
	@Override
	public EmailRequest process(EmailRequest emailRequestBulkFromDB) throws Exception {
		emailIdPublishToQueue.setEmail(emailRequestBulkFromDB.getEmail());
		return emailRequestBulkFromDB;
	}

	@Scheduled(fixedRate = 10000)
	public void queuepublisher() {
		if (!StringUtils.isEmpty(emailIdPublishToQueue.getEmail())) {
			log.info("Email Id published to queue" + emailIdPublishToQueue.getEmail());
			template.convertAndSend("", MessagingConfig.INPUT_QUEUE, emailIdPublishToQueue);
		}
		
	}
}
