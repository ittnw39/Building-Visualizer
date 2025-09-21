package com.example.visualizer.service;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 파일 관련 작업을 담당하는 서비스 클래스
 */
public class FileService {
    
    /**
     * 엑셀 파일 선택 다이얼로그
     */
    public File selectExcelFile(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || 
                       f.getName().toLowerCase().endsWith(".xlsx") || 
                       f.getName().toLowerCase().endsWith(".xls");
            }
            
            @Override
            public String getDescription() {
                return "Excel 파일 (*.xlsx, *.xls)";
            }
        });
        
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        
        return null;
    }
    
    /**
     * 기본 프로그램으로 파일 열기
     */
    public boolean openFileWithDefaultProgram(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        
        try {
            Desktop.getDesktop().open(file);
            return true;
        } catch (IOException e) {
            System.err.println("파일 열기 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 파일 존재 여부 확인
     */
    public boolean fileExists(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }
    
    /**
     * 파일명에서 확장자 제거
     */
    public String getFileNameWithoutExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        
        return fileName;
    }
    
    /**
     * 파일 확장자 가져오기
     */
    public String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        return "";
    }
}
