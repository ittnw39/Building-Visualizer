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
        
        // 축 단위 적용 버튼
        view.getControlPanel().getApplyUnitsButton().addActionListener(e -> {
            applyUnitSettings();
        });
    }
    
    /**
     * 축 단위 설정 적용
     */
    private void applyUnitSettings() {
        try {
            double xScale = Double.parseDouble(view.getControlPanel().getXUnitValue());
            double yScale = Double.parseDouble(view.getControlPanel().getYUnitValue());
            double zScale = Double.parseDouble(view.getControlPanel().getZUnitValue());
            
            // 유효성 검사
            if (xScale <= 0 || yScale <= 0 || zScale <= 0) {
                JOptionPane.showMessageDialog(view, "축 단위는 0보다 큰 값이어야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
                // 3D 뷰어에 축 단위 설정 적용
                view.getVisualizationPanel().getInteractive3DViewer().setUnitScales(xScale, yScale, zScale);
                
                JOptionPane.showMessageDialog(view, 
                    "축 단위가 적용되었습니다.\n\n" +
                    "• 3D 뷰어에만 적용됩니다\n" +
                    "• Python 시각화는 '단위 변환 배율'을 사용합니다\n\n" +
                    "X축: " + xScale + "\nY축: " + yScale + "\nZ축: " + zScale, 
                    "축 단위 설정 완료", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "올바른 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
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
