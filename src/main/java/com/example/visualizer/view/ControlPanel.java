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
    private JTextField scaleField;
    
    // 축 단위 설정 필드들
    private JTextField txtXUnit, txtYUnit, txtZUnit;
    private JButton btnApplyUnits;
    
    public ControlPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        openExcelButton = new JButton("엑셀 열기");
        runVisualizationButton = new JButton("시각화 실행");
        openResultButton = new JButton("결과 엑셀 열기");
        scaleField = new JTextField("1.0", 5);
        
        // 축 단위 설정 필드들
        txtXUnit = new JTextField("1.0", 4);
        txtYUnit = new JTextField("1.0", 4);
        txtZUnit = new JTextField("1.0", 4);
        btnApplyUnits = new JButton("축 단위 적용");
        
        // 초기 상태 설정
        openResultButton.setEnabled(false);
        
        // 스타일 설정
        openExcelButton.setPreferredSize(new Dimension(100, 30));
        runVisualizationButton.setPreferredSize(new Dimension(100, 30));
        openResultButton.setPreferredSize(new Dimension(120, 30));
        scaleField.setPreferredSize(new Dimension(60, 25));
        
        // 축 단위 필드 스타일
        txtXUnit.setPreferredSize(new Dimension(40, 25));
        txtYUnit.setPreferredSize(new Dimension(40, 25));
        txtZUnit.setPreferredSize(new Dimension(40, 25));
        btnApplyUnits.setPreferredSize(new Dimension(100, 25));
    }
    
    private void setupLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBorder(BorderFactory.createTitledBorder("컨트롤"));
        
        add(openExcelButton);
        add(new JLabel("단위 변환 배율:"));
        add(scaleField);
        add(runVisualizationButton);
        add(openResultButton);
        
        // 축 단위 설정 추가 (3D 뷰어용)
        add(new JSeparator(SwingConstants.VERTICAL));
        add(new JLabel("축 단위 (3D 뷰어용):"));
        add(new JLabel("X:"));
        add(txtXUnit);
        add(new JLabel("Y:"));
        add(txtYUnit);
        add(new JLabel("Z:"));
        add(txtZUnit);
        add(btnApplyUnits);
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
    
    public void setAllButtonsEnabled(boolean enabled) {
        openExcelButton.setEnabled(enabled);
        runVisualizationButton.setEnabled(enabled);
        openResultButton.setEnabled(enabled);
    }
    
    public String getScaleValue() {
        return scaleField.getText().trim();
    }
    
    public void setScaleValue(String value) {
        scaleField.setText(value);
    }
    
    // 축 단위 설정 관련 메서드
    public JButton getApplyUnitsButton() {
        return btnApplyUnits;
    }
    
    public String getXUnitValue() {
        return txtXUnit.getText().trim();
    }
    
    public String getYUnitValue() {
        return txtYUnit.getText().trim();
    }
    
    public String getZUnitValue() {
        return txtZUnit.getText().trim();
    }
}
