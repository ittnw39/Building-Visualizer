package com.example.visualizer.view;

import javax.swing.*;
import java.awt.*;

/**
 * 메인 프레임 뷰 클래스
 */
public class MainFrame extends JFrame {
    private ControlPanel controlPanel;
    private FileInfoPanel fileInfoPanel;
    private ProgressPanel progressPanel;
    private VisualizationPanel visualizationPanel;
    private JTable dataTable;
    private JSplitPane mainSplitPane;
    
    public MainFrame() {
        initializeComponents();
        setupLayout();
        setupFrame();
    }
    
    private void initializeComponents() {
        controlPanel = new ControlPanel();
        fileInfoPanel = new FileInfoPanel();
        progressPanel = new ProgressPanel();
        visualizationPanel = new VisualizationPanel();
        dataTable = new JTable();
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 상단 패널 (컨트롤 + 파일 정보)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(fileInfoPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        
        // 메인 콘텐츠 영역 (원본 → 화살표 → 결과)
        JPanel mainContentPanel = createMainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);
        
        // 하단 진행 상태 패널
        add(progressPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 원본 데이터 패널
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("원본 데이터"));
        leftPanel.setPreferredSize(new Dimension(400, 0));
        leftPanel.add(new JScrollPane(dataTable), BorderLayout.CENTER);
        
        // 화살표 패널
        JPanel arrowPanel = createArrowPanel();
        arrowPanel.setPreferredSize(new Dimension(30, 0));
        arrowPanel.setMaximumSize(new Dimension(30, Integer.MAX_VALUE));
        arrowPanel.setMinimumSize(new Dimension(30, 0));
        
        // 시각화 결과 패널
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("시각화 결과"));
        rightPanel.setPreferredSize(new Dimension(500, 0));
        rightPanel.add(visualizationPanel, BorderLayout.CENTER);
        
        // 가로로 나란히 배치 (BoxLayout 사용)
        JPanel horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        horizontalPanel.add(leftPanel);
        horizontalPanel.add(Box.createHorizontalStrut(5)); // 작은 간격
        horizontalPanel.add(arrowPanel);
        horizontalPanel.add(Box.createHorizontalStrut(5)); // 작은 간격
        horizontalPanel.add(rightPanel);
        
        mainPanel.add(horizontalPanel, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private JPanel createArrowPanel() {
        JPanel arrowPanel = new JPanel(new BorderLayout());
        arrowPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // 화살표 라벨 (중앙 정렬)
        JLabel arrowLabel = new JLabel("→", JLabel.CENTER);
        arrowLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        arrowLabel.setForeground(Color.BLUE);
        
        arrowPanel.add(arrowLabel, BorderLayout.CENTER);
        return arrowPanel;
    }
    
    private void setupFrame() {
        setTitle("Building Visualizer");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    // Getters for components
    public ControlPanel getControlPanel() {
        return controlPanel;
    }
    
    public FileInfoPanel getFileInfoPanel() {
        return fileInfoPanel;
    }
    
    public ProgressPanel getProgressPanel() {
        return progressPanel;
    }
    
    public VisualizationPanel getVisualizationPanel() {
        return visualizationPanel;
    }
    
    public JTable getDataTable() {
        return dataTable;
    }
    
    // Convenience methods
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }
}
