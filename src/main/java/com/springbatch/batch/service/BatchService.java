package com.springbatch.batch.service;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BatchService {

    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private RedisService redisService;

    private String fileName;
    private String year;
    private String filePath;

    @Autowired
    private Job employeeJob;

    @Autowired
    private Job secretSantaJob;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void runBatchJob(boolean isSecretSanta) throws Exception {
        logger.info("Running batch job with file path: {}", filePath);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", filePath)
                .addString("year", year)
                .addString("fileName", this.fileName)
                .toJobParameters();

        Job jobToRun = isSecretSanta ? secretSantaJob : employeeJob;
        JobExecution jobExecution = jobLauncher.run(jobToRun, jobParameters);

        if (jobExecution.getStatus().isUnsuccessful()) {
            logger.error("Job failed with status: {}", jobExecution.getStatus());
            throw new Exception("Job failed with status: " + jobExecution.getStatus());
        } else {
            logger.info("Job completed successfully with status: {}", jobExecution.getStatus());
        }
    }
}
