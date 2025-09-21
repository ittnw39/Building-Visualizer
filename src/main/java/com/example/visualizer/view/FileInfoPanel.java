package com.example.visualizer.view;

import javax.swing.*;
import java.awt.*;

/**
 * 파일 정보를 표시하는 뷰 컴포넌트
 */
public class FileInfoPanel extends JPanel {
    private JLabel selectedFileLabel;
    private JLabel resultFileLabel;
    
    public FileInfoPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        selectedFileLabel = new JLabel("선택된 파일: 없음");
        resultFileLabel = new JLabel("결과 파일: 없음");
        
        // 스타일 설정
        selectedFileLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        resultFileLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        
        selectedFileLabel.setForeground(Color.BLUE);
        resultFileLabel.setForeground(Color.GREEN);
    }
    
    private void setupLayout() {
        setLayout(new GridLayout(2, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder("파일 정보"));
        add(selectedFileLabel);
        add(resultFileLabel);
    }
    
    public void setSelectedFile(String fileName) {
        selectedFileLabel.setText("선택된 파일: " + (fileName != null ? fileName : "없음"));
    }
    
    public void setResultFile(String fileName) {
        resultFileLabel.setText("결과 파일: " + (fileName != null ? fileName : "없음"));
    }
    
    public void clearResultFile() {
        resultFileLabel.setText("결과 파일: 없음");
    }
    
    public String getResultFileName() {
        String text = resultFileLabel.getText();
        return text.replace("결과 파일: ", "");
    }
}
