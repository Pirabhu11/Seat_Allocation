package com.allocation.dao;

import com.allocation.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SettingsDAO {

    public SettingsDAO() {
        // Automatically create the tables if they don't exist yet
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS classrooms (room_number VARCHAR(50) PRIMARY KEY)");
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS subjects (subject_code VARCHAR(50) PRIMARY KEY)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addRoom(String room) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT OR IGNORE INTO classrooms (room_number) VALUES (?)")) {
            pstmt.setString(1, room);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getRooms() {
        List<String> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT room_number FROM classrooms ORDER BY room_number")) {
            while (rs.next()) {
                list.add(rs.getString("room_number"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addSubject(String subject) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT OR IGNORE INTO subjects (subject_code) VALUES (?)")) {
            pstmt.setString(1, subject);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getSubjects() {
        List<String> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT subject_code FROM subjects ORDER BY subject_code")) {
            while (rs.next()) {
                list.add(rs.getString("subject_code"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
