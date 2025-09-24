package com.example.visualizer.controller;

import com.example.visualizer.model.ExcelData;
import com.example.visualizer.view.MainFrame;

import javax.swing.*;

/**
 * 메인 컨트롤러 - 전체 애플리케이션의 흐름을 관리
 */
public class MainController {
    private final MainFrame view;
    private final ExcelController excelController;
    private final VisualizationController visualizationController;
    
    public MainController() {
        // 뷰 생성
        this.view = new MainFrame();
        
        // 컨트롤러들 생성
        this.excelController = new ExcelController(view);
        this.visualizationController = new VisualizationController(view);
        
        // 이벤트 리스너 설정
        setupEventListeners();
    }
    
    /**
     * 이벤트 리스너 설정
     */
    private void setupEventListeners() {
        // 엑셀 열기 버튼
        view.getControlPanel().getOpenExcelButton().addActionListener(e -> {
            excelController.openExcelFile();
        });
        
        // 시각화 실행 버튼
        view.getControlPanel().getRunVisualizationButton().addActionListener(e -> {
            ExcelData excelData = excelController.getCurrentExcelData();
            visualizationController.runVisualization(excelData);
        });
        
        // 결과 엑셀 열기 버튼
        view.getControlPanel().getOpenResultButton().addActionListener(e -> {
            visualizationController.openResultExcel();
        });
        
        
        // 엑셀 차트 생성 버튼
        view.getControlPanel().getCreateChartButton().addActionListener(e -> {
            excelController.createExcelChart();
        });
        
        // 기존 파일에 차트 추가 버튼
        view.getControlPanel().getAddChartButton().addActionListener(e -> {
            excelController.addChartToExistingFile();
        });
    }
    
    
    /**
     * 애플리케이션 시작
     */
    public void start() {
        SwingUtilities.invokeLater(() -> {
            view.setVisible(true);
        });
    }
    
    /**
     * 애플리케이션 종료
     */
    public void shutdown() {
        // 필요한 정리 작업 수행
        System.exit(0);
    }
}
