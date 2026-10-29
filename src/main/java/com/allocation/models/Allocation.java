package com.allocation.models;

public class Allocation {
    private int id;
    private String roomNumber;
    private String registerNumber;
    private String subjectCode;
    private int deskRow; // 1 to 5
    private int deskCol; // 1 to 3
    private int seatPos; // 1 to 3 (Left=1, Middle=2, Right=3)

    public Allocation() {
    }

    public Allocation(String roomNumber, String registerNumber, String subjectCode, int deskRow, int deskCol, int seatPos) {
        this.roomNumber = roomNumber;
        this.registerNumber = registerNumber;
        this.subjectCode = subjectCode;
        this.deskRow = deskRow;
        this.deskCol = deskCol;
        this.seatPos = seatPos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public int getDeskRow() {
        return deskRow;
    }

    public void setDeskRow(int deskRow) {
        this.deskRow = deskRow;
    }

    public int getDeskCol() {
        return deskCol;
    }

    public void setDeskCol(int deskCol) {
        this.deskCol = deskCol;
    }

    public int getSeatPos() {
        return seatPos;
    }

    public void setSeatPos(int seatPos) {
        this.seatPos = seatPos;
    }

    @Override
    public String toString() {
        return "Allocation{" +
                "roomNumber='" + roomNumber + '\'' +
                ", registerNumber='" + registerNumber + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                ", deskRow=" + deskRow +
                ", deskCol=" + deskCol +
                ", seatPos=" + seatPos +
                '}';
    }
}
