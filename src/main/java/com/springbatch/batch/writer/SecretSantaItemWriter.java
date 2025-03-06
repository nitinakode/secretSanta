package com.springbatch.batch.writer;

import com.springbatch.batch.dto.SecretSanta;
import com.springbatch.batch.service.RedisService;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@JobScope
@Setter
public class SecretSantaItemWriter implements ItemWriter<SecretSanta> {

    private static final Logger logger = LoggerFactory.getLogger(SecretSantaItemWriter.class);

    private String fileName;
    private String year;

    @Autowired
    private RedisService redisService;


    public SecretSantaItemWriter(String fileName, String year) {


        this.fileName = fileName;
        this.year = year;
    }

    @Override
    public void write(Chunk<? extends SecretSanta> secretSantas) throws Exception {
        logger.info("Writing {} secret santa assignments for year {}", secretSantas.getItems().size(), year);

        List<SecretSanta> secretSantasList = new ArrayList<>(secretSantas.getItems());
        saveToRedis(secretSantasList, year);
    }

    private void saveToRedis(List<SecretSanta> secretSantaEmployeeList, String year) {
        Map<String, String> employeeAndChild = new TreeMap<>();

        if (secretSantaEmployeeList != null && !secretSantaEmployeeList.isEmpty()) {
            logger.info("Saving secret santa data for year {} to Redis", year);

            secretSantaEmployeeList.forEach(employee -> {
                employeeAndChild.put(employee.getEmployeeEmailID(), employee.getSecretChildEmailID());
            });

            redisService.putValue("secret-santa", year, employeeAndChild);
        } else {
            logger.warn("No secret santa assignments to save for year {}", year);
        }
    }
}
