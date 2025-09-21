package com.example.visualizer.service;

import com.example.visualizer.model.ExcelData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 엑셀 파일 처리를 담당하는 서비스 클래스
 */
public class ExcelService {
    
    /**
     * 엑셀 파일을 읽어서 ExcelData 객체로 변환
     */
    public ExcelData readExcelFile(File file) throws IOException {
        ExcelData excelData = new ExcelData(file);
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 헤더 읽기
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            if (headerRow != null) {
                for (Cell cell : headerRow) {
                    headers.add(cell.toString());
                }
            }
            excelData.setHeaders(headers);
            
            // type 컬럼 존재 여부 확인
            excelData.setHasTypeColumn(headers.contains("type"));
            
            // 데이터 읽기
            List<List<Object>> data = new ArrayList<>();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                
                List<Object> rowData = new ArrayList<>();
                for (int c = 0; c < headers.size(); c++) {
                    Cell cell = row.getCell(c);
                    rowData.add(cell == null ? "" : cell.toString());
                }
                data.add(rowData);
            }
            excelData.setData(data);
        }
        
        return excelData;
    }
    
    /**
     * ExcelData를 JTable 모델로 변환
     */
    public DefaultTableModel createTableModel(ExcelData excelData) {
        if (excelData == null || !excelData.isValid()) {
            return new DefaultTableModel();
        }
        
        String[] headers = excelData.getHeaders().toArray(new String[0]);
        DefaultTableModel model = new DefaultTableModel(headers, 0);
        
        for (List<Object> rowData : excelData.getData()) {
            model.addRow(rowData.toArray());
        }
        
        return model;
    }
    
    /**
     * 엑셀 데이터 유효성 검사
     */
    public boolean validateExcelData(ExcelData excelData) {
        if (excelData == null) {
            return false;
        }
        
        if (!excelData.hasRequiredColumns()) {
            return false;
        }
        
        if (excelData.getData() == null || excelData.getData().isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 엑셀 파일 확장자 검사
     */
    public boolean isValidExcelFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
    }
}
