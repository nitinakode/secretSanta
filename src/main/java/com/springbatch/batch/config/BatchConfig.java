package com.springbatch.batch.config;

import com.springbatch.batch.dto.Employee;
import com.springbatch.batch.dto.SecretSanta;
import com.springbatch.batch.processor.EmployeeItemProcessor;
import com.springbatch.batch.processor.SecretSantaItemProcessor;
import com.springbatch.batch.writer.EmployeeItemWriter;
import com.springbatch.batch.writer.SecretSantaItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private static final Logger logger = LoggerFactory.getLogger(BatchConfig.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job employeeJob(JobRepository jobRepository) {
        logger.info("Creating employee job");
        return new JobBuilder("employeeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(employeeStep())
                .build();
    }

    @Bean
    public Step employeeStep() {
        logger.info("Creating employeeStep for employee job");
        return new StepBuilder("step1", jobRepository)
                .allowStartIfComplete(true)
                .<Employee, Employee>chunk(10, platformTransactionManager)
                .reader(employeeItemReader(null))
                .processor(employeeItemProcessor())
                .writer(employeeItemWriter(null, null))
                .build();
    }

    @Bean
    public Job secretSantaJob(JobRepository jobRepository) {
        logger.info("Creating secretSanta job");
        return new JobBuilder("secretSantaJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(secretSantaStep())
                .build();
    }

    @Bean
    public Step secretSantaStep() {
        logger.info("Creating secretSantaStep for secretSanta job");
        return new StepBuilder("step2", jobRepository)
                .allowStartIfComplete(true)
                .<SecretSanta, SecretSanta>chunk(10, platformTransactionManager)
                .reader(secretSantaItemReader(null))
                .processor(secretSantaItemProcessor())
                .writer(secretSantaItemWriter(null,null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<SecretSanta> secretSantaItemReader(@Value("#{jobParameters['filePath']}") String filePath) throws UnexpectedInputException, ParseException {
        logger.info("Creating SecretSanta ItemReader with filePath: {}", filePath);
        FlatFileItemReader<SecretSanta> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(filePath));
        reader.setLinesToSkip(1);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("employeeName", "employeeEmailID", "secretChildName", "secretChildEmailID");
        tokenizer.setDelimiter(",");
        tokenizer.setQuoteCharacter('"');

        DefaultLineMapper<SecretSanta> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);

        BeanWrapperFieldSetMapper<SecretSanta> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(SecretSanta.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public ItemProcessor<SecretSanta, SecretSanta> secretSantaItemProcessor() {
        logger.info("Creating SecretSanta ItemProcessor");
        return new SecretSantaItemProcessor();
    }

    @Bean
    @StepScope
    public ItemWriter<SecretSanta> secretSantaItemWriter(@Value("#{jobParameters['fileName']}") String fileName,@Value("#{jobParameters['year']}") String year) {
        logger.info("Creating SecretSanta ItemWriter with fileName: {}", fileName);
        return new SecretSantaItemWriter(fileName,year);
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Employee> employeeItemReader(@Value("#{jobParameters['filePath']}") String filePath) throws UnexpectedInputException, ParseException {
        logger.info("Creating Employee ItemReader with filePath: {}", filePath);
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(filePath));
        reader.setLinesToSkip(1);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("employeeName", "employeeEmailID");
        tokenizer.setDelimiter(",");
        tokenizer.setQuoteCharacter('"');

        DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);

        BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Employee.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public ItemProcessor<Employee, Employee> employeeItemProcessor() {
        logger.info("Creating Employee ItemProcessor");
        return new EmployeeItemProcessor();
    }

    @Bean
    @StepScope
    public ItemWriter<Employee> employeeItemWriter(@Value("#{jobParameters['fileName']}") String fileName, @Value("#{jobParameters['year']}") String year) {
        logger.info("Creating Employee ItemWriter with fileName: {} and year: {}", fileName, year);
        return new EmployeeItemWriter(fileName, year);
    }
}
