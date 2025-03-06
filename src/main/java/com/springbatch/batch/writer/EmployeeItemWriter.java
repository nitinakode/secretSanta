package com.springbatch.batch.writer;

import com.opencsv.CSVWriter;
import com.springbatch.batch.service.RedisService;
import com.springbatch.batch.dto.SecretSanta;
import com.springbatch.batch.dto.Employee;
import lombok.Setter;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@JobScope
@Setter
public class EmployeeItemWriter implements ItemWriter<Employee> {

    @Autowired
    private RedisService redisService;

    private String year;

    private String fileName;

    public EmployeeItemWriter(String fileName, String year) {
        this.fileName = fileName;
        this.year = year;
    }

    @Override
    public void write(Chunk<? extends Employee> items) throws Exception {
        String previousYear = String.valueOf(Integer.parseInt(year) - 1);
        Map<String, String> previousEmployeeAndChild = getPreviousEmployeeData(previousYear);
        Map<String, String> employeeNameAndEmail = new HashMap<>();
        items.getItems().forEach(item -> employeeNameAndEmail.put(item.getEmployeeEmailID(), item.getEmployeeName()));
        List<String> newEmployeeEmails = new ArrayList<>(items.getItems().stream().map(Employee::getEmployeeEmailID).toList());
        Collections.sort(newEmployeeEmails);
        List<String> secretChildEmailAlreadyAdded = new ArrayList<>();
        List<SecretSanta> secretSantaEmployeeList = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < newEmployeeEmails.size(); i++) {
            String currentEmployeeEmail = newEmployeeEmails.get(i);
            SecretSanta secretSanta = new SecretSanta();
            secretSanta.setEmployeeEmailID(currentEmployeeEmail);
            if (employeeNameAndEmail.containsKey(currentEmployeeEmail))
                secretSanta.setEmployeeName(employeeNameAndEmail.get(currentEmployeeEmail));
            String secretChildEmail = newEmployeeEmails.get(random.nextInt(newEmployeeEmails.size()));
            while (secretChildEmail.equals(currentEmployeeEmail) || secretChildEmailAlreadyAdded.contains(secretChildEmail)) {
                secretChildEmail = newEmployeeEmails.get(random.nextInt(newEmployeeEmails.size()));
            }
            if (previousEmployeeAndChild != null && !previousEmployeeAndChild.isEmpty()) {
                if (previousEmployeeAndChild.containsKey(currentEmployeeEmail) &&
                        previousEmployeeAndChild.get(currentEmployeeEmail).equalsIgnoreCase(secretChildEmail)) {
                    secretChildEmail = newEmployeeEmails.get(random.nextInt(newEmployeeEmails.size()));
                    while (secretChildEmail.equals(currentEmployeeEmail) || secretChildEmailAlreadyAdded.contains(secretChildEmail)) {
                        secretChildEmail = newEmployeeEmails.get(random.nextInt(newEmployeeEmails.size()));
                    }
                }
            }

            secretSanta.setSecretChildEmailID(secretChildEmail);
            if (employeeNameAndEmail.containsKey(secretChildEmail)) secretSanta.setSecretChildName(employeeNameAndEmail.get(secretChildEmail));
            secretChildEmailAlreadyAdded.add(secretChildEmail);
            secretSantaEmployeeList.add(secretSanta);
        }

        writeToCsv(secretSantaEmployeeList, fileName);
    }

    private void writeToCsv(List<SecretSanta> assignments, String fileName) {
        // Get the path to the resources folder in the project
        String resourceFolderPath = System.getProperty("user.dir") + "/src/main/resources/";

        // Ensure the file name includes the path in the resources folder
        String filePath = resourceFolderPath + fileName;

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{"Employee_Name", "Employee_EmailID", "Secret_Child_Name", "Secret_Child_EmailID"});
            for (SecretSanta data : assignments) {
                writer.writeNext(new String[]{
                        data.getEmployeeName(),
                        data.getEmployeeEmailID(),
                        data.getSecretChildName(),
                        data.getSecretChildEmailID()
                });
            }
            System.out.println("Assignments saved to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Map<String, String> getPreviousEmployeeData(String year) {
        return (Map<String, String>) redisService.getValue("secret-santa", year);
    }
}
