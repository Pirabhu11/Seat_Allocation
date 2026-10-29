# Classroom Seat Allocation System

An automated, intelligent exam seating arrangement desktop application built with **JavaFX** and embedded **SQLite**. Designed for educational institutions to streamline exam hall seating, prevent malpractice, generate attendance sheets, and notify students via email.

---

## 🌟 Key Features

- **Intuitive Multi-step Wizard**:
  - **Step 1: Exam Setup**: Configure classroom numbers, subject codes, exam date, and time.
  - **Step 2: Student Enrollment**: Import students in bulk using CSV files or enter individual students manually.
  - **Step 3: Seating Allocation & Export**: View enrolled students by classroom, execute intelligent auto-allocation, export attendance sheets, and dispatch email notices.
- **Anti-Malpractice Algorithm**: Automatically distributes students across 3-seater desks ensuring no adjacent students write the same exam subject.
- **Embedded SQLite Local Database**: Zero-configuration, lightning-fast database running locally with automatic schema setup.
- **Instant Email Notifications**: Sends customized seat allotment order emails (Subject, Date, Time, Room, Desk Row, Column, Seat Position) directly to students using JavaMail.
- **Attendance Sheet Export**: Generates printable PDF attendance sheets using iText and CSV exports for institution records.

---

## 🛠️ Tech Stack & Dependencies

- **Java**: JDK 17+
- **GUI Framework**: JavaFX 17 (Controls & FXML)
- **Database**: SQLite (via `org.xerial:sqlite-jdbc`)
- **CSV Processing**: OpenCSV
- **PDF Generation**: iText PDF
- **Email Dispatcher**: JavaMail (`javax.mail`)
- **Build Tool**: Apache Maven

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/Praniel3/Seat_Allocation.git
cd Seat_Allocation
```

### 2. Run via IntelliJ IDEA
1. Open the project folder in IntelliJ IDEA.
2. Allow Maven to download project dependencies (`pom.xml` -> Maven Reload).
3. Navigate to `src/main/java/com/allocation/Launcher.java`.
4. Right-click `Launcher.java` and select **Run 'Launcher.main()'**.

### 3. Run Standalone (Windows)
Double-click `Launch_Seat_Allocation.bat` in the project root directory.

---

## 🔐 Default Credentials
- **Username**: `admin`
- **Password**: `admin`
