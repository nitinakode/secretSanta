package com.springbatch.batch.processor;

import com.springbatch.batch.dto.SecretSanta;
import org.springframework.batch.item.ItemProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecretSantaItemProcessor implements ItemProcessor<SecretSanta, SecretSanta> {

    private static final Logger logger = LoggerFactory.getLogger(SecretSantaItemProcessor.class);

    public SecretSanta process(SecretSanta item) throws Exception {
        logger.info("Processing employeeEmailId: {}", item.getEmployeeEmailID());
        return item;
    }
}
