package com.allocation.models;

public class Student {
    private String registerNumber;
    private String name;
    private String email;
    private String subjectCode;

    public Student() {
    }

    public Student(String registerNumber, String name, String email) {
        this.registerNumber = registerNumber;
        this.name = name;
        this.email = email;
    }

    public Student(String registerNumber, String name, String email, String subjectCode) {
        this.registerNumber = registerNumber;
        this.name = name;
        this.email = email;
        this.subjectCode = subjectCode;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    @Override
    public String toString() {
        return "Student{" +
                "registerNumber='" + registerNumber + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                '}';
    }
}
