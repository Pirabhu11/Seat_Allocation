package com.allocation.dao;

import com.allocation.models.Student;
import com.allocation.services.AllocationService;
import com.allocation.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void addStudent(Student student, String roomNumber) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO students (register_number, name, email, room_number) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, student.getRegisterNumber());
                pstmt.setString(2, student.getName());
                pstmt.setString(3, student.getEmail());
                pstmt.setString(4, roomNumber);
                pstmt.executeUpdate();
            }
        }
    }

    public boolean studentExists(String registerNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE register_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, registerNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public Student getStudentByRegisterNumber(String registerNumber) throws SQLException {
        String sql = "SELECT register_number, name, email FROM students WHERE register_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, registerNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getString("register_number"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    public void addStudentExam(String registerNumber, String subjectCode) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Safely check if exam subject exists before inserting
            int examId = -1;
            String checkSql = "SELECT id FROM exams WHERE subject_code = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setString(1, subjectCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        examId = rs.getInt("id");
                    }
                }
            }
            
            if (examId == -1) {
                String examSql = "INSERT INTO exams (subject_code, exam_date) VALUES (?, CURRENT_DATE)";
                try (PreparedStatement pstmt = conn.prepareStatement(examSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, subjectCode);
                    pstmt.executeUpdate();
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            examId = rs.getInt(1);
                        }
                    }
                }
            }

            // Link student to exam (prevent duplicate linking)
            String linkSql = "INSERT OR IGNORE INTO student_exams (register_number, exam_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(linkSql)) {
                pstmt.setString(1, registerNumber);
                pstmt.setInt(2, examId);
                pstmt.executeUpdate();
            }
        }
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                students.add(new Student(
                    rs.getString("register_number"),
                    rs.getString("name"),
                    rs.getString("email")
                ));
            }
        }
        return students;
    }

    public List<AllocationService.StudentExamInfo> getStudentsForRoom(String roomNumber) throws SQLException {
        List<AllocationService.StudentExamInfo> list = new ArrayList<>();
        String sql = "SELECT s.register_number, s.name, s.email, e.subject_code " +
                     "FROM students s " +
                     "JOIN student_exams se ON s.register_number = se.register_number " +
                     "JOIN exams e ON se.exam_id = e.id " +
                     "WHERE s.room_number = ?";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student(
                        rs.getString("register_number"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                    AllocationService.StudentExamInfo info = new AllocationService.StudentExamInfo(
                        student,
                        rs.getString("subject_code")
                    );
                    list.add(info);
                }
            }
        }
        return list;
    }

    public void deleteStudent(String registerNumber) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete from allocations first (foreign key)
                try (PreparedStatement p1 = conn.prepareStatement("DELETE FROM allocations WHERE register_number = ?")) {
                    p1.setString(1, registerNumber);
                    p1.executeUpdate();
                }
                // Delete from student_exams (foreign key)
                try (PreparedStatement p2 = conn.prepareStatement("DELETE FROM student_exams WHERE register_number = ?")) {
                    p2.setString(1, registerNumber);
                    p2.executeUpdate();
                }
                // Delete from students
                try (PreparedStatement p3 = conn.prepareStatement("DELETE FROM students WHERE register_number = ?")) {
                    p3.setString(1, registerNumber);
                    p3.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
