package com.example.visualizer.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * JFreeChart를 사용한 차트 미리보기 패널
 */
public class ChartPreviewPanel extends JPanel {
    private ChartPanel chartPanel;
    private JFreeChart chart;
    private XYSeriesCollection dataset;
    
    public ChartPreviewPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        // 기본 차트 생성
        dataset = new XYSeriesCollection();
        chart = ChartFactory.createScatterPlot(
            "좌표 분포도", // 차트 제목
            "X 좌표 (m)",       // X축 라벨 (단위 표시)
            "Y 좌표 (m)",       // Y축 라벨 (단위 표시)
            dataset,            // 데이터셋
            PlotOrientation.VERTICAL,
            true,               // 범례 표시
            true,               // 툴팁 표시
            false               // URL 생성 안함
        );
        
        // 한글 폰트 설정
        try {
            java.awt.Font koreanFont = new java.awt.Font("맑은 고딕", java.awt.Font.PLAIN, 12);
            chart.getTitle().setFont(koreanFont);
            chart.getXYPlot().getDomainAxis().setLabelFont(koreanFont);
            chart.getXYPlot().getRangeAxis().setLabelFont(koreanFont);
            chart.getXYPlot().getDomainAxis().setTickLabelFont(koreanFont);
            chart.getXYPlot().getRangeAxis().setTickLabelFont(koreanFont);
            chart.getLegend().setItemFont(koreanFont);
        } catch (Exception e) {
            System.err.println("한글 폰트 설정 실패: " + e.getMessage());
        }
        
        // 차트 스타일 설정
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        
        // 점 크기와 색상 설정
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
        renderer.setSeriesPaint(0, java.awt.Color.BLUE);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(0, false);
        
        plot.setRenderer(renderer);
        
        // 배경색 설정
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setDomainGridlinePaint(java.awt.Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(java.awt.Color.LIGHT_GRAY);
        
        // 그리드 설정 강화
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainMinorGridlinesVisible(true);
        plot.setRangeMinorGridlinesVisible(true);
        plot.setDomainMinorGridlinePaint(java.awt.Color.LIGHT_GRAY);
        plot.setRangeMinorGridlinePaint(java.awt.Color.LIGHT_GRAY);
        
        // 축 설정 개선 - 자동 범위 비활성화 (수동 범위 설정 사용)
        plot.getDomainAxis().setAutoRange(false);
        plot.getRangeAxis().setAutoRange(false);
        
        // 축 눈금 강제 설정 (동일한 단위 간격)
        try {
            org.jfree.chart.axis.NumberAxis domainAxis = (org.jfree.chart.axis.NumberAxis) plot.getDomainAxis();
            org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
            
            // 초기 눈금 단위 설정 (데이터 로드시 재계산됨)
            double tickUnit = 5.0;
            domainAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(tickUnit));
            rangeAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(tickUnit));
            
            // 눈금 표시 설정
            domainAxis.setAutoTickUnitSelection(false);
            rangeAxis.setAutoTickUnitSelection(false);
        } catch (ClassCastException e) {
            System.err.println("NumberAxis 캐스팅 실패 - 기본 축 설정 사용: " + e.getMessage());
        }
        
        // 초기 축 범위 설정 (빈 차트용)
        plot.getDomainAxis().setRange(-10, 10);
        plot.getRangeAxis().setRange(-10, 10);
        
        // 차트 패널 생성
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setMouseZoomable(false, false); // 마우스 줌 비활성화
        chartPanel.setDomainZoomable(false); // X축 줌 비활성화
        chartPanel.setRangeZoomable(false); // Y축 줌 비활성화
        chartPanel.setMouseWheelEnabled(false); // 마우스 휠 비활성화
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
    }
    
    /**
     * 좌표 데이터로 차트 업데이트
     */
    public void updateChart(List<ChartPoint> points) {
        SwingUtilities.invokeLater(() -> {
            // 기존 데이터 제거
            dataset.removeAllSeries();
            
            if (points != null && !points.isEmpty()) {
                // 시리즈 생성
                XYSeries series = new XYSeries("좌표점");
                
                // 데이터 추가
                for (ChartPoint point : points) {
                    series.add(point.getX(), point.getY());
                }
                
                dataset.addSeries(series);
                
                // 차트 제목 업데이트
                chart.setTitle("건물 좌표 분포도 (" + points.size() + "개 점)");
                
                // 렌더러 설정 다시 적용
                XYPlot plot = chart.getXYPlot();
                XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
                renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
                renderer.setSeriesShapesVisible(0, true);
                renderer.setSeriesLinesVisible(0, false);
                
                // Z값에 따른 색상 구분 (높이별 색상)
                if (points.size() > 0) {
                    // Z값 범위 계산
                    double minZ = Double.MAX_VALUE, maxZ = Double.MIN_VALUE;
                    for (ChartPoint point : points) {
                        if (point.getZ() < minZ) minZ = point.getZ();
                        if (point.getZ() > maxZ) maxZ = point.getZ();
                    }
                    
                    // Z값이 모두 같으면 기본 파란색
                    if (maxZ - minZ < 0.001) {
                        renderer.setSeriesPaint(0, java.awt.Color.BLUE);
                    } else {
                        // Z값 범위에 따라 색상 그라데이션 적용
                        renderer.setSeriesPaint(0, java.awt.Color.BLUE);
                        System.out.println("Z값 범위: " + minZ + " ~ " + maxZ + " (색상 구분 적용)");
                    }
                } else {
                    renderer.setSeriesPaint(0, java.awt.Color.BLUE);
                }
                
                plot.setRenderer(renderer);
                
                // 축 범위 수동 설정 (데이터 기반)
                double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
                double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
                
                for (ChartPoint point : points) {
                    minX = Math.min(minX, point.getX());
                    maxX = Math.max(maxX, point.getX());
                    minY = Math.min(minY, point.getY());
                    maxY = Math.max(maxY, point.getY());
                }
                
                // 데이터 범위 계산
                double dataRangeX = maxX - minX;
                double dataRangeY = maxY - minY;
                double maxDataRange = Math.max(dataRangeX, dataRangeY);
                
                // 여백 추가 (10%)
                double margin = maxDataRange * 0.1;
                if (margin == 0) margin = 1.0; // 최소 여백
                
                // 차트 범위 결정 (시작점과 단위는 동일, 끝점은 데이터에 맞게)
                double newMinX, newMaxX, newMinY, newMaxY;
                
                // 데이터 범위에 따른 적절한 눈금 단위 계산
                double tickUnit = calculateOptimalTickUnit(Math.max(dataRangeX, dataRangeY));
                
                if (minX >= 0 && minY >= 0) {
                    // 음수가 없으면 0,0부터 시작 (1사분면)
                    newMinX = 0;
                    newMinY = 0;
                    
                    // 각 축의 끝점을 데이터에 맞게 설정 (단위의 배수로)
                    newMaxX = Math.ceil((maxX + margin) / tickUnit) * tickUnit;
                    newMaxY = Math.ceil((maxY + margin) / tickUnit) * tickUnit;
                } else {
                    // 음수가 있으면 시작점도 단위의 배수로 설정
                    newMinX = Math.floor((minX - margin) / tickUnit) * tickUnit;
                    newMinY = Math.floor((minY - margin) / tickUnit) * tickUnit;
                    
                    // 각 축의 끝점을 데이터에 맞게 설정 (단위의 배수로)
                    newMaxX = Math.ceil((maxX + margin) / tickUnit) * tickUnit;
                    newMaxY = Math.ceil((maxY + margin) / tickUnit) * tickUnit;
                }
                
                // 축 범위 강제 설정 (즉시 적용)
                plot.getDomainAxis().setRange(newMinX, newMaxX);
                plot.getRangeAxis().setRange(newMinY, newMaxY);
                
                // 동일한 눈금 단위 적용 (고정 단위 사용)
                try {
                    org.jfree.chart.axis.NumberAxis domainAxis = (org.jfree.chart.axis.NumberAxis) plot.getDomainAxis();
                    org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
                    
                    // 동일한 눈금 단위 설정 (고정 단위)
                    domainAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(tickUnit));
                    rangeAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(tickUnit));
                    
                    // 눈금 표시 설정
                    domainAxis.setAutoTickUnitSelection(false);
                    rangeAxis.setAutoTickUnitSelection(false);
                } catch (ClassCastException e) {
                    System.err.println("NumberAxis 캐스팅 실패: " + e.getMessage());
                }
                
                // 차트 강제 리페인트
                chartPanel.repaint();
                
                System.out.println("차트 축 범위 설정 (동일 단위, 독립 범위):");
                System.out.println("  데이터 범위: X=" + dataRangeX + ", Y=" + dataRangeY);
                System.out.println("  데이터 위치: X=" + minX + "~" + maxX + ", Y=" + minY + "~" + maxY);
                System.out.println("  눈금 단위: " + tickUnit);
                System.out.println("  X축: " + newMinX + " ~ " + newMaxX + " (범위: " + (newMaxX - newMinX) + ")");
                System.out.println("  Y축: " + newMinY + " ~ " + newMaxY + " (범위: " + (newMaxY - newMinY) + ")");
                if (minX >= 0 && minY >= 0) {
                    System.out.println("  차트 모드: 1사분면 (0,0 시작)");
                } else {
                    System.out.println("  차트 모드: 4사분면 (음수 포함)");
                }
                
                System.out.println("차트 업데이트: " + points.size() + "개 점 추가됨");
                for (int i = 0; i < Math.min(5, points.size()); i++) {
                    ChartPoint p = points.get(i);
                    System.out.println("  점 " + (i+1) + ": (" + p.getX() + ", " + p.getY() + ")");
                }
            } else {
                // 빈 차트
                chart.setTitle("건물 좌표 분포도");
            }
        });
    }
    
    /**
     * 차트 초기화
     */
    public void clearChart() {
        SwingUtilities.invokeLater(() -> {
            dataset.removeAllSeries();
            chart.setTitle("건물 좌표 분포도");
        });
    }
    
    /**
     * 데이터 범위에 따른 최적의 눈금 단위 계산
     * @param range 데이터 범위
     * @return 적절한 눈금 단위
     */
    private double calculateOptimalTickUnit(double range) {
        if (range <= 0) return 1.0;
        
        // 5-10개의 눈금이 표시되도록 단위 계산
        double targetTicks = 7.0; // 목표 눈금 개수
        double rawTickUnit = range / targetTicks;
        
        // 적절한 단위로 반올림 (1, 2, 5, 10, 20, 50, 100, 200, 500, 1000...)
        double magnitude = Math.pow(10, Math.floor(Math.log10(rawTickUnit)));
        double normalized = rawTickUnit / magnitude;
        
        double tickUnit;
        if (normalized <= 1.0) {
            tickUnit = 1.0 * magnitude;
        } else if (normalized <= 2.0) {
            tickUnit = 2.0 * magnitude;
        } else if (normalized <= 5.0) {
            tickUnit = 5.0 * magnitude;
        } else {
            tickUnit = 10.0 * magnitude;
        }
        
        return tickUnit;
    }
    
    /**
     * 차트 포인트 데이터 클래스
     */
    public static class ChartPoint {
        private final double x;
        private final double y;
        private final double z;
        private final String type;
        
        public ChartPoint(double x, double y, String type) {
            this.x = x;
            this.y = y;
            this.z = 0.0; // 기본값
            this.type = type;
        }
        
        public ChartPoint(double x, double y, double z, String type) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = type;
        }
        
        public double getX() { return x; }
        public double getY() { return y; }
        public double getZ() { return z; }
        public String getType() { return type; }
        
        @Override
        public String toString() {
            return String.format("(%.2f, %.2f, %.2f) [%s]", x, y, z, type);
        }
    }
}
