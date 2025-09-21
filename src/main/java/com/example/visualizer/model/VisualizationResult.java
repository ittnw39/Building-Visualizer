package com.example.visualizer.model;

import java.io.File;

/**
 * 시각화 결과를 나타내는 모델 클래스
 */
public class VisualizationResult {
    private File imageFile;
    private File excelFile;
    private String imagePath;
    private String excelPath;
    private boolean success;
    private String errorMessage;
    private double unitScale;
    
    public VisualizationResult() {
        this.success = false;
    }
    
    public VisualizationResult(File imageFile, File excelFile, double unitScale) {
        this.imageFile = imageFile;
        this.excelFile = excelFile;
        this.imagePath = imageFile != null ? imageFile.getAbsolutePath() : null;
        this.excelPath = excelFile != null ? excelFile.getAbsolutePath() : null;
        this.unitScale = unitScale;
        this.success = imageFile != null && excelFile != null;
    }
    
    // Getters and Setters
    public File getImageFile() {
        return imageFile;
    }
    
    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
        this.imagePath = imageFile != null ? imageFile.getAbsolutePath() : null;
    }
    
    public File getExcelFile() {
        return excelFile;
    }
    
    public void setExcelFile(File excelFile) {
        this.excelFile = excelFile;
        this.excelPath = excelFile != null ? excelFile.getAbsolutePath() : null;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public String getExcelPath() {
        return excelPath;
    }
    
    public void setExcelPath(String excelPath) {
        this.excelPath = excelPath;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }
    
    public double getUnitScale() {
        return unitScale;
    }
    
    public void setUnitScale(double unitScale) {
        this.unitScale = unitScale;
    }
    
    public String getImageFileName() {
        return imageFile != null ? imageFile.getName() : null;
    }
    
    public String getExcelFileName() {
        return excelFile != null ? excelFile.getName() : null;
    }
}
