package com.example.visualizer.view;

import javax.swing.*;
import java.awt.*;

/**
 * 컨트롤 버튼들을 포함하는 뷰 컴포넌트
 */
public class ControlPanel extends JPanel {
    private JButton openExcelButton;
    private JButton runVisualizationButton;
    private JButton openResultButton;
    private JButton createChartButton;
    private JButton addChartButton;
    private JTextField scaleField;
    
    
    public ControlPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        openExcelButton = new JButton("엑셀 열기");
        runVisualizationButton = new JButton("시각화 실행");
        openResultButton = new JButton("결과 엑셀 열기");
        createChartButton = new JButton("엑셀 차트 생성");
        addChartButton = new JButton("기존 파일에 차트 추가");
        scaleField = new JTextField("1.0", 5);
        
        
        // 초기 상태 설정
        openResultButton.setEnabled(false);
        createChartButton.setEnabled(false);
        addChartButton.setEnabled(true); // 항상 활성화 (파일 선택 방식)
        
        // 스타일 설정
        openExcelButton.setPreferredSize(new Dimension(100, 30));
        runVisualizationButton.setPreferredSize(new Dimension(100, 30));
        openResultButton.setPreferredSize(new Dimension(120, 30));
        createChartButton.setPreferredSize(new Dimension(120, 30));
        addChartButton.setPreferredSize(new Dimension(150, 30));
        scaleField.setPreferredSize(new Dimension(60, 25));
        
    }
    
    private void setupLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBorder(BorderFactory.createTitledBorder("컨트롤"));
        
        add(openExcelButton);
        add(new JLabel("단위 변환 배율:"));
        add(scaleField);
        add(runVisualizationButton);
        add(openResultButton);
        
        // 차트 생성 버튼들 추가
        add(new JSeparator(SwingConstants.VERTICAL));
        add(createChartButton);
        add(addChartButton);
        
    }
    
    // Getters
    public JButton getOpenExcelButton() {
        return openExcelButton;
    }
    
    public JButton getRunVisualizationButton() {
        return runVisualizationButton;
    }
    
    public JButton getOpenResultButton() {
        return openResultButton;
    }
    
    public JButton getCreateChartButton() {
        return createChartButton;
    }
    
    public JButton getAddChartButton() {
        return addChartButton;
    }
    
    public JTextField getScaleField() {
        return scaleField;
    }
    
    // 상태 관리 메서드
    public void setRunButtonEnabled(boolean enabled) {
        runVisualizationButton.setEnabled(enabled);
    }
    
    public void setOpenResultButtonEnabled(boolean enabled) {
        openResultButton.setEnabled(enabled);
    }
    
    public void setCreateChartButtonEnabled(boolean enabled) {
        createChartButton.setEnabled(enabled);
    }
    
    public void setAllButtonsEnabled(boolean enabled) {
        openExcelButton.setEnabled(enabled);
        runVisualizationButton.setEnabled(enabled);
        openResultButton.setEnabled(enabled);
        createChartButton.setEnabled(enabled);
        // addChartButton은 항상 활성화
    }
    
    public String getScaleValue() {
        return scaleField.getText().trim();
    }
    
    public void setScaleValue(String value) {
        scaleField.setText(value);
    }
    
}
