package com.example.visualizer.view;

import com.example.visualizer.model.ProgressInfo;
import javax.swing.*;
import java.awt.*;

/**
 * 진행 상태를 표시하는 뷰 컴포넌트
 */
public class ProgressPanel extends JPanel {
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    public ProgressPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("대기 중...");
        
        statusLabel = new JLabel("엑셀 파일을 선택하세요");
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("진행 상태"));
        add(statusLabel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);
    }
    
    public void updateProgress(ProgressInfo progressInfo) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(progressInfo.getProgress());
            progressBar.setString(progressInfo.getMessage());
            progressBar.setIndeterminate(progressInfo.isIndeterminate());
            statusLabel.setText(progressInfo.getStatus());
        });
    }
    
    public void reset() {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(0);
            progressBar.setString("대기 중...");
            progressBar.setIndeterminate(false);
            statusLabel.setText("엑셀 파일을 선택하세요");
        });
    }
    
    public void setIndeterminate(boolean indeterminate) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(indeterminate);
        });
    }
}
