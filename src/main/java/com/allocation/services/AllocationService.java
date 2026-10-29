package com.allocation.services;

import com.allocation.models.Allocation;
import com.allocation.models.Student;

import java.util.*;

public class AllocationService {

    public static class StudentExamInfo {
        public Student student;
        public String subjectCode;

        public StudentExamInfo(Student student, String subjectCode) {
            this.student = student;
            this.subjectCode = subjectCode;
        }
    }

    /**
     * Allocates students to desks in a classroom ensuring 3 different subjects per desk.
     * @param roomNumber The classroom identifier.
     * @param studentExams List of students and their exams to be allocated to this room (max 45).
     * @return List of generated Allocations.
     * @throws Exception if constraints cannot be met.
     */
    public List<Allocation> allocateSeats(String roomNumber, List<StudentExamInfo> studentExams) throws Exception {
        if (studentExams.size() > 45) {
            throw new IllegalArgumentException("Cannot allocate more than 45 students in a classroom.");
        }

        // Group students by subject
        Map<String, List<Student>> studentsBySubject = new HashMap<>();
        for (StudentExamInfo se : studentExams) {
            studentsBySubject.computeIfAbsent(se.subjectCode, k -> new ArrayList<>()).add(se.student);
        }

        List<Allocation> allocations = new ArrayList<>();
        int desksTotal = 15; // 5 rows * 3 columns
        int totalAllocated = 0;
        int totalToAllocate = studentExams.size();

        // Iterate through all 15 desks (Rows 1 to 5, Cols 1 to 3)
        for (int row = 1; row <= 5; row++) {
            for (int col = 1; col <= 3; col++) {
                
                if (totalAllocated >= totalToAllocate) {
                    break; // All students allocated
                }

                // Sort subjects by number of unallocated students (Descending)
                // This ensures we always try to disperse the largest groups first
                List<Map.Entry<String, List<Student>>> sortedSubjects = new ArrayList<>(studentsBySubject.entrySet());
                sortedSubjects.sort((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()));

                // Remove empty subject lists
                sortedSubjects.removeIf(e -> e.getValue().isEmpty());

                int seatsToFillOnThisDesk = Math.min(3, totalToAllocate - totalAllocated);

                // If we don't have enough distinct subjects to fill 3 seats on this desk,
                // we just fill as many as we have distinct subjects for, and leave the rest of the desk empty.
                // This spreads students with the same subject across multiple desks!
                int seatsWeCanFill = Math.min(seatsToFillOnThisDesk, sortedSubjects.size());

                if (seatsWeCanFill == 0) {
                    break; // No more subjects to allocate
                }

                // Pick one student from the top N distinct subjects for this desk
                for (int seatPos = 1; seatPos <= seatsWeCanFill; seatPos++) {
                    Map.Entry<String, List<Student>> entry = sortedSubjects.get(seatPos - 1);
                    String subjectCode = entry.getKey();
                    Student student = entry.getValue().remove(0); // take first student from this subject group

                    Allocation allocation = new Allocation(roomNumber, student.getRegisterNumber(), subjectCode, row, col, seatPos);
                    allocations.add(allocation);
                    totalAllocated++;
                }
            }
        }

        if (totalAllocated < totalToAllocate) {
            throw new Exception("Algorithm failure: Cannot satisfy constraint! There are too many students with the same subject, and not enough desks to keep them separated. (" + totalAllocated + "/" + totalToAllocate + " allocated)");
        }

        return allocations;
    }
}
