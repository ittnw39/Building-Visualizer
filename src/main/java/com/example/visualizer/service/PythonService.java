package com.example.visualizer.service;

import com.example.visualizer.model.ExcelData;
import com.example.visualizer.model.VisualizationResult;
import com.example.visualizer.model.ProgressInfo;

import java.io.*;
import java.util.function.Consumer;

/**
 * Python 시각화 처리를 담당하는 서비스 클래스
 */
public class PythonService {
    
    /**
     * Python 시각화 실행
     */
    public void runVisualization(ExcelData excelData, double unitScale, 
                                Consumer<ProgressInfo> progressCallback) throws Exception {
        
        if (excelData == null || !excelData.isValid()) {
            throw new IllegalArgumentException("유효하지 않은 엑셀 데이터입니다.");
        }
        
        // Python 스크립트 경로 찾기
        String pythonScript = findPythonScript();
        
        // 진행 상태 업데이트
        progressCallback.accept(new ProgressInfo(20, "Python 스크립트 실행 중...", "Python 시각화 스크립트를 실행합니다..."));
        
        // Python 프로세스 실행
        ProcessBuilder pb = new ProcessBuilder("python", pythonScript,
                excelData.getFile().getAbsolutePath(), String.valueOf(unitScale));
        pb.directory(new File("."));
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        
        // 진행 상태 업데이트
        progressCallback.accept(new ProgressInfo(40, "데이터 처리 중...", "엑셀 데이터를 처리하고 그래프를 생성합니다..."));
        
        // 출력 읽기
        String imagePath = null;
        String excelPath = null;
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Saved image:")) {
                    imagePath = line.replace("Saved image:", "").trim();
                    progressCallback.accept(new ProgressInfo(70, "이미지 생성 완료...", "그래프 이미지를 생성했습니다."));
                }
                if (line.startsWith("Saved excel:")) {
                    excelPath = line.replace("Saved excel:", "").trim();
                    progressCallback.accept(new ProgressInfo(90, "엑셀 파일 생성 완료...", "결과 엑셀 파일을 생성했습니다."));
                }
                System.out.println(line);
            }
        }
        
        // 프로세스 완료 대기
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            throw new RuntimeException("Python 스크립트 실행 실패 (종료 코드: " + exitCode + ")");
        }
        
        // 최종 진행 상태
        progressCallback.accept(new ProgressInfo(100, "완료!", "시각화가 완료되었습니다!"));
        
        // 결과 생성
        if (imagePath != null && excelPath != null) {
            File imageFile = new File(imagePath);
            File excelFile = new File(excelPath);
            VisualizationResult result = new VisualizationResult(imageFile, excelFile, unitScale);
            // 결과는 별도로 반환하지 않고 콜백으로 처리
        } else {
            throw new RuntimeException("시각화 결과 파일을 생성할 수 없습니다.");
        }
    }
    
    /**
     * Python 스크립트 경로 찾기
     */
    private String findPythonScript() {
        // 현재 실행 중인 JAR 파일의 위치 찾기
        String jarPath = PythonService.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath();
        
        // JAR 파일이 실행 중인 경우
        if (jarPath.endsWith(".jar")) {
            File jarFile = new File(jarPath);
            File jarDir = jarFile.getParentFile();
            
            // JAR 파일과 같은 디렉토리에서 visualize.py 찾기
            File pythonScript = new File(jarDir, "visualize.py");
            if (pythonScript.exists()) {
                return pythonScript.getAbsolutePath();
            }
        }
        
        // 개발 환경에서 실행 중인 경우
        File currentDir = new File(".");
        File pythonScript = new File(currentDir, "visualize.py");
        if (pythonScript.exists()) {
            return pythonScript.getAbsolutePath();
        }
        
        // 기본값으로 현재 디렉토리의 visualize.py
        return "visualize.py";
    }
    
    /**
     * 단위 변환 배율 유효성 검사
     */
    public boolean isValidScale(String scaleText) {
        if (scaleText == null || scaleText.trim().isEmpty()) {
            return true; // 빈 값은 기본값 1.0으로 처리
        }
        
        try {
            double scale = Double.parseDouble(scaleText.trim());
            return scale > 0; // 양수만 허용
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 단위 변환 배율 파싱
     */
    public double parseScale(String scaleText) {
        if (scaleText == null || scaleText.trim().isEmpty()) {
            return 1.0;
        }
        
        try {
            return Double.parseDouble(scaleText.trim());
        } catch (NumberFormatException e) {
            return 1.0;
        }
    }
}
