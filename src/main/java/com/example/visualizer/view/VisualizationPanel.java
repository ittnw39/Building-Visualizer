package com.example.visualizer.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 시각화 결과를 표시하는 뷰 컴포넌트
 */
public class VisualizationPanel extends JPanel {
    private JLabel imageLabel;
    private ChartPreviewPanel chartPreviewPanel;
    private Interactive3DViewer interactive3DViewer;
    private JTabbedPane tabbedPane;
    
    public VisualizationPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        // 이미지 라벨 (기존 호환성을 위해 유지)
        imageLabel = new JLabel("결과 이미지가 여기에 표시됩니다", SwingConstants.CENTER);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // 차트 미리보기 패널
        chartPreviewPanel = new ChartPreviewPanel();
        
        // 3D 뷰어
        interactive3DViewer = new Interactive3DViewer();
        
        // 탭 패널
        tabbedPane = new JTabbedPane();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 탭 추가
        tabbedPane.addTab("2D 차트", chartPreviewPanel);
        tabbedPane.addTab("3D 인터랙티브", interactive3DViewer);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    public void displayImage(ImageIcon icon) {
        SwingUtilities.invokeLater(() -> {
            if (icon != null) {
                // 원본 이미지 크기와 비율 계산
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();
                
                // 패널 크기 (여백 고려)
                int panelWidth = getWidth() > 0 ? getWidth() - 20 : 480;
                int panelHeight = getHeight() > 0 ? getHeight() - 40 : 360;
                
                // 원본 비율 유지하면서 패널에 맞게 스케일링
                double widthRatio = (double) panelWidth / originalWidth;
                double heightRatio = (double) panelHeight / originalHeight;
                double scaleRatio = Math.min(widthRatio, heightRatio); // 더 작은 비율 사용
                
                int scaledWidth = (int) (originalWidth * scaleRatio);
                int scaledHeight = (int) (originalHeight * scaleRatio);
                
                Image scaledImage = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
                imageLabel.setText("");
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("이미지 생성 실패");
            }
        });
    }
    
    public void clearImage() {
        SwingUtilities.invokeLater(() -> {
            imageLabel.setIcon(null);
            imageLabel.setText("결과 이미지가 여기에 표시됩니다");
        });
    }
    
    public void setErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            imageLabel.setIcon(null);
            imageLabel.setText(message);
        });
    }
    
    /**
     * 3D 데이터를 인터랙티브 뷰어에 표시
     */
    public void display3DData(List<Interactive3DViewer.BuildingPoint> points) {
        SwingUtilities.invokeLater(() -> {
            if (points != null && !points.isEmpty()) {
                interactive3DViewer.visualizeData(points);
                // 2D 탭을 먼저 보여줌
                tabbedPane.setSelectedIndex(0);
            }
        });
    }
    
    /**
     * 3D 뷰 초기화
     */
    public void reset3DView() {
        SwingUtilities.invokeLater(() -> {
            interactive3DViewer.resetView();
        });
    }
    
    /**
     * Interactive3DViewer 인스턴스 반환
     */
    public Interactive3DViewer getInteractive3DViewer() {
        return interactive3DViewer;
    }
    
    /**
     * 2D 차트에 좌표 데이터 표시
     */
    public void display2DChart(List<ChartPreviewPanel.ChartPoint> points) {
        SwingUtilities.invokeLater(() -> {
            if (points != null && !points.isEmpty()) {
                chartPreviewPanel.updateChart(points);
                // 2D 차트 탭을 선택
                tabbedPane.setSelectedIndex(0);
            }
        });
    }
    
    /**
     * 2D 차트 초기화
     */
    public void clear2DChart() {
        SwingUtilities.invokeLater(() -> {
            chartPreviewPanel.clearChart();
        });
    }
    
}
