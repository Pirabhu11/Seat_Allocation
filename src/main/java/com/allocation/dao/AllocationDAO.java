package com.allocation.dao;

import com.allocation.models.Allocation;
import com.allocation.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AllocationDAO {

    public void saveAllocations(List<Allocation> allocations) throws SQLException {
        String sql = "INSERT INTO allocations (room_number, register_number, subject_code, desk_row, desk_col, seat_pos) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // We use batch processing to insert all allocations at once efficiently
            for (Allocation alloc : allocations) {
                pstmt.setString(1, alloc.getRoomNumber());
                pstmt.setString(2, alloc.getRegisterNumber());
                pstmt.setString(3, alloc.getSubjectCode());
                pstmt.setInt(4, alloc.getDeskRow());
                pstmt.setInt(5, alloc.getDeskCol());
                pstmt.setInt(6, alloc.getSeatPos());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    public void clearAllocationsForRoom(String roomNumber) throws SQLException {
        String sql = "DELETE FROM allocations WHERE room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNumber);
            pstmt.executeUpdate();
        }
    }

    public static class AllocationDetail {
        public String studentName;
        public String email;
        public String registerNumber;
        public String roomNumber;
        public String subjectCode;
        public int row;
        public int col;
        public int seatPos;
    }

    public List<AllocationDetail> getAllocationsWithDetails(String roomNumber) throws SQLException {
        List<AllocationDetail> details = new java.util.ArrayList<>();
        String sql = "SELECT s.name, s.email, a.register_number, a.room_number, a.subject_code, a.desk_row, a.desk_col, a.seat_pos " +
                     "FROM allocations a JOIN students s ON a.register_number = s.register_number " +
                     "WHERE a.room_number = ?";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, roomNumber);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AllocationDetail d = new AllocationDetail();
                    d.studentName = rs.getString("name");
                    d.email = rs.getString("email");
                    d.registerNumber = rs.getString("register_number");
                    d.roomNumber = rs.getString("room_number");
                    d.subjectCode = rs.getString("subject_code");
                    d.row = rs.getInt("desk_row");
                    d.col = rs.getInt("desk_col");
                    d.seatPos = rs.getInt("seat_pos");
                    details.add(d);
                }
            }
        }
        return details;
    }
}
