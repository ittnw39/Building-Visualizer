package com.example.visualizer.model;

/**
 * 진행 상태 정보를 나타내는 모델 클래스
 */
public class ProgressInfo {
    private int progress;
    private String message;
    private String status;
    private boolean indeterminate;
    
    public ProgressInfo() {
        this.progress = 0;
        this.message = "대기 중...";
        this.status = "준비";
        this.indeterminate = false;
    }
    
    public ProgressInfo(int progress, String message, String status) {
        this.progress = progress;
        this.message = message;
        this.status = status;
        this.indeterminate = false;
    }
    
    public ProgressInfo(String message, String status, boolean indeterminate) {
        this.progress = 0;
        this.message = message;
        this.status = status;
        this.indeterminate = indeterminate;
    }
    
    // Getters and Setters
    public int getProgress() {
        return progress;
    }
    
    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isIndeterminate() {
        return indeterminate;
    }
    
    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }
    
    public void reset() {
        this.progress = 0;
        this.message = "대기 중...";
        this.status = "준비";
        this.indeterminate = false;
    }
}
