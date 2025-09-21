package com.example.visualizer.controller;

import com.example.visualizer.model.ExcelData;
import com.example.visualizer.model.ProgressInfo;
import com.example.visualizer.service.ExcelService;
import com.example.visualizer.service.FileService;
import com.example.visualizer.view.MainFrame;

import javax.swing.*;

import java.io.File;
import java.io.IOException;

/**
 * 엑셀 파일 처리를 담당하는 컨트롤러
 */
public class ExcelController {
    private final MainFrame view;
    private final ExcelService excelService;
    private final FileService fileService;
    private ExcelData currentExcelData;
    
    public ExcelController(MainFrame view) {
        this.view = view;
        this.excelService = new ExcelService();
        this.fileService = new FileService();
        this.currentExcelData = null;
    }
    
    /**
     * 엑셀 파일 열기 처리
     */
    public void openExcelFile() {
        try {
            // 파일 선택
            File selectedFile = fileService.selectExcelFile(view);
            if (selectedFile == null) {
                return;
            }
            
            // 파일 유효성 검사
            if (!excelService.isValidExcelFile(selectedFile)) {
                view.showError("유효하지 않은 엑셀 파일입니다.");
                return;
            }
            
            // 진행 상태 업데이트
            view.getProgressPanel().updateProgress(
                new ProgressInfo("파일 로딩 중...", "엑셀 파일을 로딩 중...", true)
            );
            
            // 엑셀 파일 읽기
            currentExcelData = excelService.readExcelFile(selectedFile);
            
            // 데이터 유효성 검사
            if (!excelService.validateExcelData(currentExcelData)) {
                view.showError("엑셀 파일에 'x'와 'y' 컬럼이 필요합니다.");
                currentExcelData = null;
                return;
            }
            
            // UI 업데이트
            updateUI();
            
            // 완료 상태
            view.getProgressPanel().updateProgress(
                new ProgressInfo(0, "준비 완료", "엑셀 파일 로딩 완료. 시각화를 실행하세요.")
            );
            
        } catch (IOException e) {
            view.showError("엑셀 파일 읽기 오류: " + e.getMessage());
            currentExcelData = null;
        } catch (Exception e) {
            view.showError("예상치 못한 오류: " + e.getMessage());
            currentExcelData = null;
        }
    }
    
    /**
     * UI 업데이트
     */
    private void updateUI() {
        if (currentExcelData != null) {
            // 파일 정보 표시
            view.getFileInfoPanel().setSelectedFile(currentExcelData.getFileName());
            
            // 테이블 모델 설정
            var tableModel = excelService.createTableModel(currentExcelData);
            view.getDataTable().setModel(tableModel);
            
            // 시각화 버튼 활성화
            view.getControlPanel().setRunButtonEnabled(true);
        } else {
            // 초기화
            view.getFileInfoPanel().setSelectedFile(null);
            view.getDataTable().setModel(new javax.swing.table.DefaultTableModel());
            view.getControlPanel().setRunButtonEnabled(false);
        }
    }
    
    /**
     * 현재 엑셀 데이터 반환
     */
    public ExcelData getCurrentExcelData() {
        return currentExcelData;
    }
    
    /**
     * 엑셀 데이터 초기화
     */
    public void clearExcelData() {
        currentExcelData = null;
        updateUI();
    }
}
