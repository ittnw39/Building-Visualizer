package com.example.visualizer.model;

import java.io.File;
import java.util.List;

/**
 * 엑셀 데이터를 나타내는 모델 클래스
 */
public class ExcelData {
    private File file;
    private String fileName;
    private List<String> headers;
    private List<List<Object>> data;
    private boolean hasTypeColumn;
    
    public ExcelData(File file) {
        this.file = file;
        this.fileName = file.getName();
    }
    
    // Getters and Setters
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
        this.fileName = file.getName();
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public List<String> getHeaders() {
        return headers;
    }
    
    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
    
    public List<List<Object>> getData() {
        return data;
    }
    
    public void setData(List<List<Object>> data) {
        this.data = data;
    }
    
    public boolean hasTypeColumn() {
        return hasTypeColumn;
    }
    
    public void setHasTypeColumn(boolean hasTypeColumn) {
        this.hasTypeColumn = hasTypeColumn;
    }
    
    public boolean isValid() {
        return file != null && file.exists() && headers != null && data != null;
    }
    
    public boolean hasRequiredColumns() {
        return headers != null && headers.contains("x") && headers.contains("y");
    }
    
    /**
     * Z 컬럼이 있는지 확인
     */
    public boolean hasZColumn() {
        return headers != null && headers.contains("z");
    }
    
    /**
     * 데이터 행 개수 반환
     */
    public int getRowCount() {
        return data != null ? data.size() : 0;
    }
    
    /**
     * 특정 행과 컬럼의 double 값 반환
     */
    public double getDoubleValue(int row, String columnName) {
        if (data == null || headers == null || row >= data.size()) {
            return 0.0;
        }
        
        int columnIndex = headers.indexOf(columnName);
        if (columnIndex == -1 || columnIndex >= data.get(row).size()) {
            return 0.0;
        }
        
        Object value = data.get(row).get(columnIndex);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
    
    /**
     * 특정 행과 컬럼의 String 값 반환
     */
    public String getStringValue(int row, String columnName) {
        if (data == null || headers == null || row >= data.size()) {
            return "";
        }
        
        int columnIndex = headers.indexOf(columnName);
        if (columnIndex == -1 || columnIndex >= data.get(row).size()) {
            return "";
        }
        
        Object value = data.get(row).get(columnIndex);
        return value != null ? value.toString() : "";
    }
}
