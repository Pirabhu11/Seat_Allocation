package com.allocation.models;

import java.time.LocalDate;

public class Exam {
    private int id;
    private String subjectCode;
    private LocalDate examDate;

    public Exam() {
    }

    public Exam(int id, String subjectCode, LocalDate examDate) {
        this.id = id;
        this.subjectCode = subjectCode;
        this.examDate = examDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", subjectCode='" + subjectCode + '\'' +
                ", examDate=" + examDate +
                '}';
    }
}
