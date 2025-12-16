package com.bookmyturf.service.implementation;

import com.bookmyturf.models.AdminBookingDTO;
import com.bookmyturf.models.DashboardResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

@Service
public class CsvService {

    public void generateDashboardExcel(DashboardResponse dashboard, String locationName, OutputStream os) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Dashboard Stats");

        // ================= STYLES =================
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // ================= ADD TITLE ROW =================
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BOOK MY TURF");
        titleCell.setCellStyle(titleStyle);

        // Merge first row across two columns
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));

        // ================= HEADER ROW =================
        Row headerRow = sheet.createRow(1);
        headerRow.createCell(0).setCellValue("Metric");
        headerRow.createCell(1).setCellValue("Value");

        for (Cell cell : headerRow) {
            cell.setCellStyle(headerStyle);
        }

        // ================= DASHBOARD DATA =================
        String[][] data = {
                {"Total Locations", String.valueOf(dashboard.getTotalLocations())},
                {"Total Sports", String.valueOf(dashboard.getTotalSports())},
                {"Total Categories", String.valueOf(dashboard.getTotalCategories())},
                {"Total Sales", "₹" + dashboard.getTotalSales()},
                {"Current Month Sales", "₹" + dashboard.getCurrentMonthSales()},
                {"Yearly Sales", "₹" + dashboard.getYearlySales()},
                {"Current Month Slot Count", String.valueOf(dashboard.getCurrentMonthSlotCount())},
                {"Yearly Slot Count", String.valueOf(dashboard.getYearlySlotCount())},
                {"BookMyTurf Charge Current Month", "₹" + dashboard.getBookMyTurfChargeCurrentMonth()},
                {"BookMyTurf Charge Yearly", "₹" + dashboard.getBookMyTurfChargeYearly()},
                {"Admin Earning Current Month", "₹" + dashboard.getAdminEarningCurrentMonth()},
                {"Admin Earning Yearly", "₹" + dashboard.getAdminEarningYearly()}
        };

        int rowIdx = 2; // start after title and header
        for (String[] rowData : data) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowData[0]);
            row.createCell(1).setCellValue(rowData[1]);
        }

        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        // Write to output stream
        workbook.write(os);
        workbook.close();
    }

    public void generateBookingExcel(List<AdminBookingDTO> bookings, String locationName, OutputStream os) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Confirmed Bookings");

        // ================= STYLES =================
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // ================= ADD TITLE ROW =================
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BOOK MY TURF");
        titleCell.setCellStyle(titleStyle);

        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

        // ================= HEADER ROW =================
        Row headerRow = sheet.createRow(1);
        String[] headers = {"Booking ID", "User ID", "User Name", "Sport Name", "Location Name", "Total Amount", "Booking Time"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ================= DATA ROWS =================
        int rowIdx = 2;
        for (AdminBookingDTO booking : bookings) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(booking.getBookingId());
            row.createCell(1).setCellValue(booking.getUserId());
            row.createCell(2).setCellValue(booking.getUserName());
            row.createCell(3).setCellValue(booking.getSportName());
            row.createCell(4).setCellValue(booking.getLocationName());
            row.createCell(5).setCellValue(booking.getTotalAmount());
            row.createCell(6).setCellValue(booking.getBookingTime().toString());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to output
        workbook.write(os);
        workbook.close();
    }
}



