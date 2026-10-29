package com.allocation.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // SQLite local database file (stored directly in the project root folder)
    private static final String URL = "jdbc:sqlite:seat_allocation.db";

    private static boolean initialized = false;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(URL);
            
            // Enable Foreign Key support in SQLite
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            if (!initialized) {
                initializeSchema(conn);
                initialized = true;
            }
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found. Please reload Maven in your IDE.", e);
        }
    }

    private static synchronized void initializeSchema(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // classrooms
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS classrooms (" +
                    "room_number TEXT PRIMARY KEY)");

            // subjects
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS subjects (" +
                    "subject_code TEXT PRIMARY KEY)");

            // students
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS students (" +
                    "register_number TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "email TEXT, " +
                    "room_number TEXT)");

            // exams
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS exams (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "subject_code TEXT, " +
                    "exam_date TEXT)");

            // student_exams
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS student_exams (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "register_number TEXT, " +
                    "exam_id INTEGER)");

            // allocations
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS allocations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "room_number TEXT, " +
                    "register_number TEXT, " +
                    "subject_code TEXT, " +
                    "desk_row INTEGER, " +
                    "desk_col INTEGER, " +
                    "seat_pos INTEGER)");
        } catch (SQLException e) {
            System.err.println("Notice while initializing database schema: " + e.getMessage());
        }
    }
}
