package com.springbatch.batch.dto;

import java.io.Serializable;

public class SecretSanta {

    private String employeeEmailID;
    private String employeeName;
    private String secretChildName;
    private String secretChildEmailID;


    public String getEmployeeEmailID() {
        return employeeEmailID;
    }

    public void setEmployeeEmailID(String employeeEmailID) {
        this.employeeEmailID = employeeEmailID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getSecretChildEmailID() {
        return secretChildEmailID;
    }

    public void setSecretChildEmailID(String secretChildEmailID) {
        this.secretChildEmailID = secretChildEmailID;
    }

    public String getSecretChildName() {
        return secretChildName;
    }

    public void setSecretChildName(String secretChildName) {
        this.secretChildName = secretChildName;
    }
}
