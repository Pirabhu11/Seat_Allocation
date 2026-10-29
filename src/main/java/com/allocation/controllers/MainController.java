package com.allocation.controllers;

import com.allocation.dao.AllocationDAO;
import com.allocation.dao.StudentDAO;
import com.allocation.models.Allocation;
import com.allocation.models.Student;
import com.allocation.services.AllocationService;
import com.allocation.services.AttendanceSheetService;
import com.allocation.services.EmailService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    // Layout Control
    @FXML private TabPane mainTabPane;

    // Page 1 Controls
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private ComboBox<String> amPmComboBox;
    
    @FXML private TextField setupRoomField;
    @FXML private ListView<String> roomListView;
    @FXML private TextField setupSubjectField;
    @FXML private ListView<String> subjectListView;

    // Page 3 Controls
    @FXML private ComboBox<String> classroomComboBox;
    @FXML private TextField regNumField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> subjectComboBox;
    @FXML private TableView<Student> studentsTable;

    private ObservableList<String> roomsList = FXCollections.observableArrayList();
    private ObservableList<String> subjectsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup Date & Time Controls
        SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 9);
        hourSpinner.setValueFactory(hourFactory);
        
        SpinnerValueFactory<Integer> minFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        minuteSpinner.setValueFactory(minFactory);
        
        amPmComboBox.setItems(FXCollections.observableArrayList("AM", "PM"));
        amPmComboBox.setValue("AM");

        // Fetch persisted Classrooms and Subjects from the database!
        com.allocation.dao.SettingsDAO settingsDAO = new com.allocation.dao.SettingsDAO();
        roomsList.addAll(settingsDAO.getRooms());
        subjectsList.addAll(settingsDAO.getSubjects());

        roomListView.setItems(roomsList);
        subjectListView.setItems(subjectsList);
        classroomComboBox.setItems(roomsList);
        subjectComboBox.setItems(subjectsList);

        // Setup table columns with SimpleStringProperty to completely bypass Java 17 reflection issues
        TableColumn<Student, String> regCol = new TableColumn<>("Register No.");
        regCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRegisterNumber()));
        regCol.setPrefWidth(120);

        TableColumn<Student, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(180);

        TableColumn<Student, String> subCol = new TableColumn<>("Subject");
        subCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSubjectCode() != null ? cellData.getValue().getSubjectCode() : ""));
        subCol.setPrefWidth(100);

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        emailCol.setPrefWidth(180);

        TableColumn<Student, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(100);
        Callback<TableColumn<Student, Void>, TableCell<Student, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Student, Void> call(final TableColumn<Student, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Delete");
                    {
                        btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                        btn.setOnAction((ActionEvent event) -> {
                            Student data = getTableView().getItems().get(getIndex());
                            handleDeleteStudent(data);
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        actionCol.setCellFactory(cellFactory);

        studentsTable.getColumns().addAll(regCol, nameCol, subCol, emailCol, actionCol);
    }

    private void handleDeleteStudent(Student student) {
        try {
            StudentDAO dao = new StudentDAO();
            dao.deleteStudent(student.getRegisterNumber());
            handleRoomSelectionChanged(null); // Refresh table
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Student " + student.getName() + " removed successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete student: " + e.getMessage());
        }
    }

    // --- PAGE 1: SETUP ---

    @FXML
    void handleProceedToPage2(ActionEvent event) {
        if (roomsList.isEmpty() || subjectsList.isEmpty() || datePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Incomplete", "Please set Date/Time, and add at least one Room and Subject.");
            return;
        }
        mainTabPane.getSelectionModel().select(1); // Go to Page 2
    }

    @FXML
    void handleProceedToPage3(ActionEvent event) {
        mainTabPane.getSelectionModel().select(2); // Go to Page 3
    }

    @FXML
    void handleAddRoom(ActionEvent event) {
        String room = setupRoomField.getText().trim();
        if (!room.isEmpty() && !roomsList.contains(room)) {
            roomsList.add(room);
            new com.allocation.dao.SettingsDAO().addRoom(room);
            setupRoomField.clear();
        }
    }

    @FXML
    void handleAddSubject(ActionEvent event) {
        String subject = setupSubjectField.getText().trim();
        if (!subject.isEmpty() && !subjectsList.contains(subject)) {
            subjectsList.add(subject);
            new com.allocation.dao.SettingsDAO().addSubject(subject);
            setupSubjectField.clear();
        }
    }

    // --- PAGE 2: IMPORT ---

    @FXML
    void handleDownloadTemplate(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV Template");
        fileChooser.setInitialFileName("Student_Import_Template.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());
        
        if (file != null) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                writer.writeNext(new String[]{"RegisterNumber", "Name", "Email", "SubjectCode", "RoomNumber"});
                writer.writeNext(new String[]{"1001", "John Doe", "john@example.com", "CS101", "101"});
                showAlert(Alert.AlertType.INFORMATION, "Success", "Template saved to " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create template: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleImportCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Student CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(new Stage());
        
        if (file != null) {
            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                List<String[]> lines = reader.readAll();
                StudentDAO dao = new StudentDAO();
                int count = 0;
                int duplicates = 0;
                for (int i = 1; i < lines.size(); i++) { // skip header
                    String[] row = lines.get(i);
                    if (row.length >= 5) {
                        String reg = row[0].trim();
                        if (dao.studentExists(reg)) {
                            duplicates++;
                            continue;
                        }
                        dao.addStudent(new Student(reg, row[1].trim(), row[2].trim()), row[4].trim());
                        dao.addStudentExam(reg, row[3].trim());
                        count++;
                    }
                }
                String msg = "Successfully imported " + count + " students into the database!";
                if (duplicates > 0) {
                    msg += "\n(" + duplicates + " existing students were kept unchanged)";
                }
                showAlert(Alert.AlertType.INFORMATION, "Import Complete", msg);
                
                // Navigate directly to Page 3
                mainTabPane.getSelectionModel().select(2);
                
                // If a room is currently selected in page 3, refresh it
                if (classroomComboBox.getValue() != null) {
                    handleRoomSelectionChanged(null);
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Import Error", "Failed to read CSV: " + e.getMessage());
            }
        }
    }

    // --- PAGE 3: ALLOCATE & EXPORT ---

    @FXML
    void handleRoomSelectionChanged(ActionEvent event) {
        String selectedRoom = classroomComboBox.getValue();
        if (selectedRoom != null) {
            try {
                StudentDAO dao = new StudentDAO();
                // Fetch ONLY students assigned to this room
                List<AllocationService.StudentExamInfo> studentInfos = dao.getStudentsForRoom(selectedRoom);
                
                // Convert to Student models for the table display with subject info
                List<Student> students = new ArrayList<>();
                for (AllocationService.StudentExamInfo info : studentInfos) {
                    Student s = info.student;
                    s.setSubjectCode(info.subjectCode);
                    students.add(s);
                }
                studentsTable.setItems(FXCollections.observableArrayList(students));
                
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Fetch Error", "Could not load students: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleAddStudent(ActionEvent event) {
        String room = classroomComboBox.getValue();
        if (room == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a Classroom from the top dropdown first!");
            return;
        }
        
        String regNum = regNumField.getText() != null ? regNumField.getText().trim() : "";
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String subject = subjectComboBox.getValue();

        if (regNum.isEmpty() || name.isEmpty() || email.isEmpty() || subject == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "All manual entry fields and subject must be filled!");
            return;
        }

        try {
            StudentDAO dao = new StudentDAO();
            
            // Register Number is Primary Key: prevent duplicates from overwriting existing students
            Student existingStudent = dao.getStudentByRegisterNumber(regNum);
            if (existingStudent != null) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Register Number",
                    "A student with Register Number '" + regNum + "' already exists!\n\n"
                    + "Name: " + existingStudent.getName() + "\n"
                    + "Email: " + existingStudent.getEmail() + "\n\n"
                    + "Duplicate register numbers cannot be added. Please enter a unique register number.");
                return;
            }

            // Save student and link exam
            dao.addStudent(new Student(regNum, name, email), room);
            dao.addStudentExam(regNum, subject);
            
            regNumField.clear(); nameField.clear(); emailField.clear();
            handleRoomSelectionChanged(null); // refresh table immediately
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Student manually added to " + room + "!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    void handleRunAllocation(ActionEvent event) {
        String room = classroomComboBox.getValue();
        if (room == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a Classroom to allocate!");
            return;
        }

        try {
            StudentDAO studentDAO = new StudentDAO();
            // Fetch students. To make it dynamic per room, we fetch all and take the first 45. 
            // A more complex system would map students to rooms, but for now we allocate available students to this room.
            List<AllocationService.StudentExamInfo> list = studentDAO.getStudentsForRoom(room);
            
            if (list.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "No students found to allocate!");
                return;
            }

            AllocationService allocationService = new AllocationService();
            List<Allocation> allocations = allocationService.allocateSeats(room, list);
            
            AllocationDAO allocDAO = new AllocationDAO();
            allocDAO.clearAllocationsForRoom(room);
            allocDAO.saveAllocations(allocations);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Generated seats for " + allocations.size() + " students in " + room);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Allocation Failed", e.getMessage());
        }
    }

    private String getFormattedDateTime() {
        if (datePicker.getValue() == null) return "";
        String date = datePicker.getValue().toString();
        String hour = String.format("%02d", hourSpinner.getValue());
        String min = String.format("%02d", minuteSpinner.getValue());
        return date + " " + hour + ":" + min + " " + amPmComboBox.getValue();
    }

    @FXML
    void handleExportAttendance(ActionEvent event) {
        String room = classroomComboBox.getValue();
        String dateTime = getFormattedDateTime();
        if (room == null || dateTime.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Ensure you have set the Date/Time in Page 1, and selected a Room in Page 3.");
            return;
        }
        
        try {
            AllocationDAO allocDAO = new AllocationDAO();
            List<AllocationDAO.AllocationDetail> details = allocDAO.getAllocationsWithDetails(room);
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF");
            fileChooser.setInitialFileName("Attendance_" + room + ".pdf");
            File file = fileChooser.showSaveDialog(new Stage());
            
            if (file != null) {
                AttendanceSheetService pdfService = new AttendanceSheetService();
                pdfService.generateAttendanceSheet(room, details, file.getAbsolutePath(), dateTime);
                showAlert(Alert.AlertType.INFORMATION, "Success", "PDF Saved!");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    void handleExportCSV(ActionEvent event) {
        String room = classroomComboBox.getValue();
        String dateTime = getFormattedDateTime();
        if (room == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a Classroom.");
            return;
        }
        
        try {
            AllocationDAO allocDAO = new AllocationDAO();
            List<AllocationDAO.AllocationDetail> details = allocDAO.getAllocationsWithDetails(room);
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save CSV");
            fileChooser.setInitialFileName("Data_" + room + ".csv");
            File file = fileChooser.showSaveDialog(new Stage());
            
            if (file != null) {
                try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                    writer.writeNext(new String[]{"Exam Date", "Room", "Row", "Column", "Seat Pos", "Register Number", "Student Name", "Email"});
                    for (AllocationDAO.AllocationDetail d : details) {
                        writer.writeNext(new String[]{dateTime, d.roomNumber, String.valueOf(d.row), String.valueOf(d.col), String.valueOf(d.seatPos), d.registerNumber, d.studentName, d.email});
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", "CSV Saved!");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    void handleSendEmails(ActionEvent event) {
        String room = classroomComboBox.getValue();
        String dateTime = getFormattedDateTime();
        if (room == null || dateTime.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Ensure you have set the Date/Time in Page 1, and selected a Room in Page 3.");
            return;
        }

        try {
            AllocationDAO allocDAO = new AllocationDAO();
            List<AllocationDAO.AllocationDetail> details = allocDAO.getAllocationsWithDetails(room);
            EmailService emailService = new EmailService();
            
            int count = 0;
            for (AllocationDAO.AllocationDetail d : details) {
                emailService.sendAllocationEmail(d.email, d.studentName, d.subjectCode, d.roomNumber, d.row, d.col, d.seatPos, dateTime);
                count++;
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Sent " + count + " emails!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
