package com.example.visualizer.controller;

import com.example.visualizer.model.ExcelData;
import com.example.visualizer.model.ProgressInfo;
import com.example.visualizer.model.VisualizationResult;
import com.example.visualizer.service.PythonService;
import com.example.visualizer.service.FileService;
import com.example.visualizer.view.Interactive3DViewer;
import com.example.visualizer.view.MainFrame;
import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * 시각화 처리를 담당하는 컨트롤러
 */
public class VisualizationController {
    private final MainFrame view;
    private final PythonService pythonService;
    private final FileService fileService;
    private VisualizationResult lastResult;
    private ExcelData currentExcelData;
    
    public VisualizationController(MainFrame view) {
        this.view = view;
        this.pythonService = new PythonService();
        this.fileService = new FileService();
        this.lastResult = null;
    }
    
    /**
     * 시각화 실행
     */
    public void runVisualization(ExcelData excelData) {
        if (excelData == null) {
            view.showError("먼저 엑셀 파일을 선택하세요.");
            return;
        }
        
        // 현재 Excel 데이터 저장 (3D 뷰어용)
        this.currentExcelData = excelData;
        
        // 단위 변환 배율 검증 (축단위 설정 우선 적용)
        String xUnitText = view.getControlPanel().getXUnitValue();
        String yUnitText = view.getControlPanel().getYUnitValue();
        String zUnitText = view.getControlPanel().getZUnitValue();
        
        // 단위변환배율 사용 (축단위는 3D 뷰어에서만 개별 적용)
        final double unitScale;
        try {
            String scaleText = view.getControlPanel().getScaleValue();
            if (!pythonService.isValidScale(scaleText)) {
                view.showError("단위 변환 배율은 숫자여야 합니다.");
                return;
            }
            unitScale = pythonService.parseScale(scaleText);
            System.out.println("Python 시각화 단위변환배율: " + unitScale);
        } catch (NumberFormatException e) {
            view.showError("단위변환배율에 올바른 숫자를 입력하세요.");
            return;
        }
        
        // UI 상태 업데이트
        view.getControlPanel().setAllButtonsEnabled(false);
        view.getProgressPanel().updateProgress(
            new ProgressInfo(0, "시각화 시작...", "Python 시각화를 시작합니다...")
        );
        
        // 백그라운드에서 시각화 실행
        SwingUtilities.invokeLater(() -> {
            try {
                // Python 시각화 실행
                pythonService.runVisualization(excelData, unitScale, this::updateProgress);
                
                // 결과 파일 경로 찾기 - Downloads 폴더의 실행별 폴더에서 찾기
                String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";
                File downloadsDir = new File(downloadsPath);
                File[] buildingDirs = downloadsDir.listFiles((dir, name) -> name.startsWith("BuildingVisualizer_"));
                
                File resultFile = null;
                File imageFile = null;
                
                if (buildingDirs != null && buildingDirs.length > 0) {
                    // 가장 최근 폴더 선택
                    File latestDir = buildingDirs[0];
                    for (File dir : buildingDirs) {
                        if (dir.lastModified() > latestDir.lastModified()) {
                            latestDir = dir;
                        }
                    }
                    
                    resultFile = new File(latestDir, "coords_with_plot.xlsx");
                    imageFile = new File(latestDir, "coords_plot.png");
                } else {
                    // 기존 방식 (현재 디렉토리에서 찾기)
                    resultFile = new File("coords_with_plot.xlsx");
                    imageFile = new File("coords_plot.png");
                }
                
                if (resultFile.exists() && imageFile.exists()) {
                    lastResult = new VisualizationResult(imageFile, resultFile, unitScale);
                    updateUIWithResult(lastResult);
                } else {
                    throw new RuntimeException("결과 파일을 찾을 수 없습니다.");
                }
                
            } catch (Exception e) {
                handleVisualizationError(e);
            } finally {
                // UI 상태 복원
                view.getControlPanel().setAllButtonsEnabled(true);
            }
        });
    }
    
    /**
     * 진행 상태 업데이트 콜백
     */
    private void updateProgress(ProgressInfo progressInfo) {
        SwingUtilities.invokeLater(() -> {
            view.getProgressPanel().updateProgress(progressInfo);
        });
    }
    
    /**
     * 시각화 결과로 UI 업데이트
     */
    private void updateUIWithResult(VisualizationResult result) {
        if (result != null && result.isSuccess()) {
            // 파일 정보 업데이트
            view.getFileInfoPanel().setResultFile(result.getExcelFileName());
            
            // 이미지 표시
            if (result.getImageFile() != null && result.getImageFile().exists()) {
                ImageIcon icon = new ImageIcon(result.getImageFile().getAbsolutePath());
                view.getVisualizationPanel().displayImage(icon);
            }
            
            // 3D 데이터 표시 (ExcelData에서 추출)
            display3DDataFromExcel();
            
            // 결과 엑셀 열기 버튼 활성화
            view.getControlPanel().setOpenResultButtonEnabled(true);
            
            view.showMessage("시각화가 완료되었습니다!");
        }
    }
    
    /**
     * Excel 데이터에서 3D 포인트 추출하여 표시
     */
    private void display3DDataFromExcel() {
        // Python에서 생성된 3D 데이터 JSON 파일 읽기
        // Downloads 폴더에서 BuildingVisualizer_* 폴더들 찾기
        String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";
        File downloadsDir = new File(downloadsPath);
        File[] buildingDirs = downloadsDir.listFiles((dir, name) -> name.startsWith("BuildingVisualizer_"));
        
        if (buildingDirs != null && buildingDirs.length > 0) {
            // 가장 최근 폴더 선택 (폴더명의 타임스탬프 기준)
            File latestDir = buildingDirs[0];
            for (File dir : buildingDirs) {
                if (dir.lastModified() > latestDir.lastModified()) {
                    latestDir = dir;
                }
            }
            
            // 해당 폴더에서 3d_data.json 파일 찾기
            File jsonFile = new File(latestDir, "3d_data.json");
            if (jsonFile.exists()) {
                try {
                    load3DDataFromJson(jsonFile);
                    return;
                } catch (Exception e) {
                    System.err.println("JSON 파일 읽기 실패: " + e.getMessage());
                }
            }
        }
        
        // JSON 파일이 없으면 Excel 데이터에서 추출
        ExcelData currentData = getCurrentExcelData();
        if (currentData != null && currentData.hasZColumn()) {
            List<Interactive3DViewer.BuildingPoint> points = new ArrayList<>();
            
            // Excel 데이터를 3D 포인트로 변환
            for (int i = 0; i < currentData.getRowCount(); i++) {
                double x = currentData.getDoubleValue(i, "x");
                double y = currentData.getDoubleValue(i, "y");
                double z = currentData.getDoubleValue(i, "z");
                String type = currentData.getStringValue(i, "type");
                
                points.add(new Interactive3DViewer.BuildingPoint(x, y, z, type));
            }
            
            // 3D 뷰어에 표시 (카메라 자동 조정 포함)
            view.getVisualizationPanel().display3DData(points);
        }
    }
    
    /**
     * JSON 파일에서 3D 데이터 로드
     */
    private void load3DDataFromJson(File jsonFile) {
        try {
            // JSON 파일 읽기
            String jsonContent = new String(java.nio.file.Files.readAllBytes(jsonFile.toPath()));
            
            List<Interactive3DViewer.BuildingPoint> points = new ArrayList<>();
            
            // 더 안정적인 JSON 파싱
            String[] lines = jsonContent.split("\n");
            boolean inPointsArray = false;
            boolean inPointObject = false;
            double x = 0, y = 0, z = 0;
            String type = "default";
            
            for (String line : lines) {
                line = line.trim();
                
                if (line.contains("\"points\": [")) {
                    inPointsArray = true;
                    continue;
                }
                
                if (inPointsArray && line.equals("]")) {
                    break;
                }
                
                if (inPointsArray && line.equals("{")) {
                    inPointObject = true;
                    x = 0; y = 0; z = 0; type = "default";
                    continue;
                }
                
                if (inPointObject && line.equals("},")) {
                    points.add(new Interactive3DViewer.BuildingPoint(x, y, z, type));
                    inPointObject = false;
                    continue;
                }
                
                if (inPointObject && line.equals("}")) {
                    points.add(new Interactive3DViewer.BuildingPoint(x, y, z, type));
                    inPointObject = false;
                    continue;
                }
                
                if (inPointObject && line.contains("\"x\":")) {
                    x = Double.parseDouble(line.split(":")[1].replace(",", "").trim());
                } else if (inPointObject && line.contains("\"y\":")) {
                    y = Double.parseDouble(line.split(":")[1].replace(",", "").trim());
                } else if (inPointObject && line.contains("\"z\":")) {
                    z = Double.parseDouble(line.split(":")[1].replace(",", "").trim());
                } else if (inPointObject && line.contains("\"type\":")) {
                    type = line.split(":")[1].replace(",", "").replace("\"", "").trim();
                }
            }
            
            System.out.println("JSON에서 3D 데이터 로드: " + points.size() + "개 포인트");
            
            // 3D 뷰어에 표시 (카메라 자동 조정 포함)
            view.getVisualizationPanel().display3DData(points);
            
        } catch (Exception e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 현재 Excel 데이터 가져오기
     */
    private ExcelData getCurrentExcelData() {
        return currentExcelData;
    }
    
    /**
     * 시각화 오류 처리
     */
    private void handleVisualizationError(Exception e) {
        view.getProgressPanel().updateProgress(
            new ProgressInfo(0, "오류 발생", "오류: " + e.getMessage())
        );
        view.getVisualizationPanel().setErrorMessage("시각화 실패: " + e.getMessage());
        view.showError("시각화 실행 오류: " + e.getMessage());
    }
    
    /**
     * 결과 엑셀 파일 열기
     */
    public void openResultExcel() {
        if (lastResult != null && lastResult.getExcelFile() != null) {
            boolean success = fileService.openFileWithDefaultProgram(lastResult.getExcelFile());
            if (success) {
                view.getProgressPanel().updateProgress(
                    new ProgressInfo(0, "파일 열기 완료", "결과 엑셀 파일을 열었습니다: " + lastResult.getExcelFileName())
                );
            } else {
                view.showError("엑셀 파일을 열 수 없습니다.");
            }
        } else {
            // 파일명으로 찾기 시도
            String resultFileName = view.getFileInfoPanel().getResultFileName();
            if (!resultFileName.equals("없음")) {
                File resultFile = new File(resultFileName);
                if (resultFile.exists()) {
                    boolean success = fileService.openFileWithDefaultProgram(resultFile);
                    if (!success) {
                        view.showError("엑셀 파일을 열 수 없습니다.");
                    }
                } else {
                    view.showError("결과 파일을 찾을 수 없습니다: " + resultFileName);
                }
            } else {
                view.showError("결과 파일이 없습니다.");
            }
        }
    }
    
    /**
     * 마지막 결과 반환
     */
    public VisualizationResult getLastResult() {
        return lastResult;
    }
    
    /**
     * 결과 초기화
     */
    public void clearResult() {
        lastResult = null;
        view.getFileInfoPanel().clearResultFile();
        view.getVisualizationPanel().clearImage();
        view.getControlPanel().setOpenResultButtonEnabled(false);
    }
}
