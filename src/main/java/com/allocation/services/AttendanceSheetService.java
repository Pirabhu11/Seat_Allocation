package com.allocation.services;

import com.allocation.dao.AllocationDAO.AllocationDetail;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.List;

public class AttendanceSheetService {

    public void generateAttendanceSheet(String roomNumber, List<AllocationDetail> details, String destinationPath, String dateTime) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(destinationPath));
        
        document.open();
        
        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Examination Attendance Sheet - " + roomNumber, titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        
        // Date & Time Subtitle
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC);
        Paragraph subtitle = new Paragraph("Date & Time: " + dateTime, subtitleFont);
        subtitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        
        document.add(title);
        document.add(subtitle);
        
        // Table setup (4 columns)
        PdfPTable table = new PdfPTable(4); 
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 2f, 2.5f, 2.5f});
        
        // Headers
        String[] headers = {"Student Name", "Register Number", "Booklet Number", "Student Signature"};
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setPadding(10);
            cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
        
        // Data Rows
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        for (AllocationDetail d : details) {
            // Name
            PdfPCell nameCell = new PdfPCell(new Phrase(d.studentName, dataFont));
            nameCell.setPadding(10);
            nameCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
            table.addCell(nameCell);
            
            // Register Number
            PdfPCell regCell = new PdfPCell(new Phrase(d.registerNumber, dataFont));
            regCell.setPadding(10);
            regCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            regCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
            table.addCell(regCell);
            
            // Blank cell for Booklet Number
            PdfPCell blankCell1 = new PdfPCell(new Phrase(""));
            table.addCell(blankCell1);
            
            // Blank cell for Signature
            PdfPCell blankCell2 = new PdfPCell(new Phrase(""));
            table.addCell(blankCell2);
        }
        
        document.add(table);
        document.close();
    }
}
