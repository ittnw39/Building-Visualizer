package com.example.visualizer;

import javax.swing.SwingUtilities;

import com.example.visualizer.controller.MainController;

/**
 * Building Visualizer 메인 애플리케이션 클래스
 * MVC 패턴을 사용하여 구조화된 애플리케이션
 */
public class BuildingVisualizer {
    private MainController mainController;
    
    public BuildingVisualizer() {
        this.mainController = new MainController();
    }
    
    /**
     * 애플리케이션 시작
     */
    public void start() {
        mainController.start();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BuildingVisualizer().start();
        });
    }
}
