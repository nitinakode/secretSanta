package com.springbatch.batch.processor;

import com.springbatch.batch.dto.Employee;
import org.springframework.batch.item.ItemProcessor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeItemProcessor.class);

    @Override
    public Employee process(Employee employee) throws Exception {

        logger.info("Processing employeeEmailId: {}", employee.getEmployeeEmailID());

        return employee;
    }
}

