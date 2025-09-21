package com.example.visualizer.view;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 인터랙티브 3D 뷰어 컴포넌트
 * JavaFX 3D를 사용하여 마우스로 회전/확대/축소 가능한 3D 시각화
 */
public class Interactive3DViewer extends JPanel {
    private JFXPanel jfxPanel;
    private Group root;
    private PerspectiveCamera camera;
    private double mousePosX, mousePosY;
    private double mouseOldX, mouseOldY;
    private double mouseDeltaX, mouseDeltaY;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    private final Translate translate = new Translate(0, 0, 0); // 초기값, 데이터 로드 시 동적 조정됨
    private final javafx.scene.transform.Scale scale = new javafx.scene.transform.Scale(1.0, 1.0, 1.0);
    
    // 키보드 조작 설명 패널
    private JPanel instructionPanel;
    
    // 축 단위 설정 (외부에서 설정됨)
    private double xUnitScale = 1.0, yUnitScale = 1.0, zUnitScale = 1.0;
    
    // 색상 매핑
    private final Map<String, Color> typeColors = new HashMap<>();
    
    
    // CAD 스타일에 적합한 기본 색상 팔레트
    private static final Color[] BASE_COLORS = {
        Color.DARKBLUE, Color.DARKRED, Color.DARKGREEN, Color.DARKORANGE, Color.DARKVIOLET,
        Color.DARKCYAN, Color.DARKMAGENTA, Color.DARKGOLDENROD, Color.DARKOLIVEGREEN, Color.DARKSLATEGRAY,
        Color.NAVY, Color.MAROON, Color.FORESTGREEN, Color.CHOCOLATE, Color.INDIGO
    };
    
    public Interactive3DViewer() {
        initializeColors();
        setupJFXPanel();
    }
    
    private void initializeColors() {
        // 동적 색상 생성 (하드코딩 제거)
        // 색상은 visualizeData 메서드에서 필요에 따라 동적으로 생성됨
    }
    
    /**
     * 타입별로 동적으로 색상 생성
     */
    private Color getColorForType(String type) {
        if (typeColors.containsKey(type)) {
            return typeColors.get(type);
        }
        
        // 새로운 타입에 대해 색상 할당
        int colorIndex = typeColors.size() % BASE_COLORS.length;
        Color color = BASE_COLORS[colorIndex];
        typeColors.put(type, color);
        
        return color;
    }
    
    private void setupJFXPanel() {
        setLayout(new BorderLayout());
        
        // 3D 뷰어 패널
        jfxPanel = new JFXPanel();
        
        // 키보드 조작 설명 패널 생성
        instructionPanel = createInstructionPanel();
        
        // 레이아웃 설정
        add(jfxPanel, BorderLayout.CENTER);
        add(instructionPanel, BorderLayout.SOUTH);
        
        Platform.runLater(() -> {
            createScene();
        });
    }
    
    /**
     * 키보드 조작 설명 패널 생성
     */
    private JPanel createInstructionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("3D 뷰 조작법"));
        panel.setPreferredSize(new Dimension(0, 60));
        
        // 키보드 조작 설명
        JLabel instructionLabel = new JLabel("<html>" +
            "<b>키보드 조작:</b> " +
            "<b>W/S</b> - X축 회전 | " +
            "<b>A/D</b> - Y축 회전 | " +
            "<b>Q/E</b> - Z축 회전 | " +
            "<b>R</b> - 뷰 리셋 | " +
            "<b>+/-</b> - 전체 확대/축소 | " +
            "<b>마우스 드래그</b> - 회전 | " +
            "<b>마우스 휠</b> - 전체 확대/축소" +
            "</html>");
        
        instructionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
        panel.add(instructionLabel);
        
        return panel;
    }
    
    
    /**
     * 축 단위 설정 (외부에서 호출)
     */
    public void setUnitScales(double xScale, double yScale, double zScale) {
        this.xUnitScale = xScale;
        this.yUnitScale = yScale;
        this.zUnitScale = zScale;
        System.out.println("축 단위 설정: X=" + xUnitScale + ", Y=" + yUnitScale + ", Z=" + zUnitScale);
    }
    
    private void createScene() {
        // 3D 씬 생성
        root = new Group();
        
        // 카메라 설정
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0); // 더 먼 거리까지 볼 수 있도록
        camera.setTranslateZ(0); // 초기값, 데이터 로드 시 동적 조정됨
        
        // 3D 씬 생성
        Scene scene = new Scene(root, 800, 600, true);
        scene.setCamera(camera);
        scene.setFill(Color.LIGHTGRAY);
        
        // 키보드 이벤트를 받기 위해 씬에 포커스 설정
        root.setFocusTraversable(true);
        root.requestFocus();
        
        // 마우스 이벤트 핸들러
        setupMouseHandlers(scene);
        
        // 조명 설정
        setupLighting();
        
        // 축 표시
        createAxes();
        
        jfxPanel.setScene(scene);
    }
    
    private void setupMouseHandlers(Scene scene) {
        // CAD 스타일 마우스 컨트롤
        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
            
            // 마우스 클릭 시 포커스 요청 (키보드 이벤트 활성화)
            root.requestFocus();
        });
        
        scene.setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);
            
            // 마우스 드래그 방향과 일치하는 회전
            if (me.isShiftDown()) {
                // Shift + 드래그: Pan (이동)
                translate.setX(translate.getX() + mouseDeltaX * 0.5);
                translate.setY(translate.getY() + mouseDeltaY * 0.5);
            } else {
                // 일반 드래그: 마우스 방향과 정확히 일치하는 회전 (감도 조정)
                // 마우스를 오른쪽으로 드래그하면 오른쪽으로 회전
                // 마우스를 아래로 드래그하면 아래로 회전
                rotateY.setAngle(rotateY.getAngle() - mouseDeltaX * 0.1); // 감도 낮춤
                rotateX.setAngle(rotateX.getAngle() + mouseDeltaY * 0.1); // 감도 낮춤
            }
        });
        
        // 마우스 휠로 확대/축소 (전체적인 확대/축소)
        scene.setOnScroll((ScrollEvent event) -> {
            double delta = event.getDeltaY();
            double scaleFactor = 1.05; // 확대/축소 비율
            
            if (delta < 0) {
                scaleFactor = 1.0 / scaleFactor; // 축소
            }
            
            // 전체적인 확대/축소 (Scale 변환 사용)
            double currentScale = scale.getX();
            double newScale = currentScale * scaleFactor;
            newScale = Math.max(0.1, Math.min(10.0, newScale)); // 최소 0.1배, 최대 10배로 제한
            scale.setX(newScale);
            scale.setY(newScale);
            scale.setZ(newScale);
        });
        
        // 우클릭 메뉴 추가 (뷰 리셋 등)
        scene.setOnContextMenuRequested(event -> {
            showContextMenu(event.getScreenX(), event.getScreenY());
        });
        
        // 키보드 단축키 추가
        scene.setOnKeyPressed(this::handleKeyPress);
    }
    
    /**
     * 우클릭 컨텍스트 메뉴 표시
     */
    private void showContextMenu(double screenX, double screenY) {
        ContextMenu contextMenu = new ContextMenu();
        
        // 뷰 리셋
        MenuItem resetView = new MenuItem("뷰 리셋");
        resetView.setOnAction(e -> resetView());
        
        // 간단한 메뉴 (키보드 조작 안내)
        MenuItem helpMenu = new MenuItem("키보드 조작법 보기");
        helpMenu.setOnAction(e -> showKeyboardHelp());
        
        contextMenu.getItems().addAll(resetView, helpMenu);
        contextMenu.show(jfxPanel.getScene().getWindow(), screenX, screenY);
    }
    
    /**
     * 키보드 조작법 도움말 표시
     */
    private void showKeyboardHelp() {
        String helpText = "3D 뷰 키보드 조작법\n\n" +
            "• W/S - X축 회전 (위/아래)\n" +
            "• A/D - Y축 회전 (좌/우)\n" +
            "• Q/E - Z축 회전 (반시계/시계)\n" +
            "• R - 뷰 리셋\n" +
            "• +/- - 전체 확대/축소 (0.1배~10배)\n" +
            "• 마우스 드래그 - 회전\n" +
            "• 마우스 휠 - 전체 확대/축소\n" +
            "• Shift + 드래그 - 이동";
        
        JOptionPane.showMessageDialog(this, helpText, "3D 뷰 조작법", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    /**
     * X축 회전 (위/아래)
     */
    private void rotateX(double angle) {
        Platform.runLater(() -> {
            rotateX.setAngle(rotateX.getAngle() + angle);
        });
    }
    
    /**
     * Y축 회전 (좌/우)
     */
    private void rotateY(double angle) {
        Platform.runLater(() -> {
            rotateY.setAngle(rotateY.getAngle() + angle);
        });
    }
    
    /**
     * Z축 회전 (시계방향/반시계방향) - 높이 기준 회전
     */
    private void rotateZ(double angle) {
        Platform.runLater(() -> {
            rotateZ.setAngle(rotateZ.getAngle() + angle);
        });
    }
    
    
    /**
     * 키보드 단축키 처리 - 축별 회전
     */
    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case R:
                // R키: 뷰 리셋
                resetView();
                break;
            case W:
                // W키: X축 위로 회전 (마우스 위로 드래그와 동일)
                rotateX(2);
                break;
            case S:
                // S키: X축 아래로 회전 (마우스 아래로 드래그와 동일)
                rotateX(-2);
                break;
            case A:
                // A키: Y축 왼쪽으로 회전 (마우스 왼쪽으로 드래그와 동일)
                rotateY(2);
                break;
            case D:
                // D키: Y축 오른쪽으로 회전 (마우스 오른쪽으로 드래그와 동일)
                rotateY(-2);
                break;
            case Q:
                // Q키: Z축 반시계방향 회전 (높이 기준 회전)
                rotateZ(-2);
                break;
            case E:
                // E키: Z축 시계방향 회전 (높이 기준 회전)
                rotateZ(2);
                break;
            case EQUALS:
            case ADD:
                // +키: 전체 확대
                double currentScale = scale.getX();
                double newScale = currentScale * 1.1;
                newScale = Math.max(0.1, Math.min(10.0, newScale)); // 최소 0.1배, 최대 10배로 제한
                scale.setX(newScale);
                scale.setY(newScale);
                scale.setZ(newScale);
                break;
            case MINUS:
            case SUBTRACT:
                // -키: 전체 축소
                currentScale = scale.getX();
                newScale = currentScale * 0.9;
                newScale = Math.max(0.1, Math.min(10.0, newScale)); // 최소 0.1배, 최대 10배로 제한
                scale.setX(newScale);
                scale.setY(newScale);
                scale.setZ(newScale);
                break;
            default:
                // 다른 키는 무시
                break;
        }
    }
    
    private void setupLighting() {
        // 환경광
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().add(root);
        
        // 방향광
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(100);
        pointLight.setTranslateY(-100);
        pointLight.setTranslateZ(-100);
        root.getChildren().add(pointLight);
    }
    
    private void createAxes() {
        // 기본 축 크기 (데이터가 로드되면 동적으로 조정됨)
        double defaultSize = 50;
        
        // X축 (빨간색)
        createAxisLine(0, 0, 0, defaultSize, 0, 0, Color.RED, "X");
        // Y축 (초록색)
        createAxisLine(0, 0, 0, 0, defaultSize, 0, Color.GREEN, "Y");
        // Z축 (파란색)
        createAxisLine(0, 0, 0, 0, 0, defaultSize, Color.BLUE, "Z");
        
        // Z 레이어는 제거됨 - 데이터 포인트만 표시
    }
    
    
    private void createAxisLine(double x1, double y1, double z1, double x2, double y2, double z2, Color color, String label) {
        // 축선은 간단한 구로 표현
        Sphere startSphere = new Sphere(1);
        Sphere endSphere = new Sphere(1);
        
        PhongMaterial material = new PhongMaterial(color);
        startSphere.setMaterial(material);
        endSphere.setMaterial(material);
        
        startSphere.setTranslateX(x1);
        startSphere.setTranslateY(y1);
        startSphere.setTranslateZ(z1);
        
        endSphere.setTranslateX(x2);
        endSphere.setTranslateY(y2);
        endSphere.setTranslateZ(z2);
        
        root.getChildren().addAll(startSphere, endSphere);
    }
    
    /**
     * 3D 데이터를 시각화
     */
    public void visualizeData(List<BuildingPoint> points) {
        Platform.runLater(() -> {
            // 기존 데이터 제거 (축 제외)
            root.getChildren().clear();
            setupLighting();
            createAxes();
            
            if (points == null || points.isEmpty()) {
                return;
            }
            
            // 데이터 정규화를 위한 범위 계산
            double minX = points.stream().mapToDouble(p -> p.x).min().orElse(0);
            double maxX = points.stream().mapToDouble(p -> p.x).max().orElse(100);
            double minY = points.stream().mapToDouble(p -> p.y).min().orElse(0);
            double maxY = points.stream().mapToDouble(p -> p.y).max().orElse(100);
            double minZ = points.stream().mapToDouble(p -> p.z).min().orElse(0);
            double maxZ = points.stream().mapToDouble(p -> p.z).max().orElse(100);
            
            
            // 사용자 설정 단위 적용
            double xRange = (maxX - minX) * xUnitScale;
            double yRange = (maxY - minY) * yUnitScale;
            double maxXYRange = Math.max(xRange, yRange);
            
            // X, Y축은 동일한 스케일로 정규화 (1:1 비율 유지)
            double xyScale = maxXYRange / 80.0;
            // Z축은 사용자 설정에 따라 조정
            double zScale = xyScale / 6.0; // 기본 6배 확대
            
            double offsetX = -(maxX + minX) / 2.0;
            double offsetY = -(maxY + minY) / 2.0;
            double offsetZ = -(maxZ + minZ) / 2.0;
            
            System.out.println("3D 데이터 정규화: " + points.size() + "개 포인트");
            System.out.println("X: " + minX + "~" + maxX + ", Y: " + minY + "~" + maxY + ", Z: " + minZ + "~" + maxZ);
            System.out.println("XY스케일: " + xyScale + ", Z스케일: " + zScale + ", 오프셋: (" + offsetX + ", " + offsetY + ", " + offsetZ + ")");
            
                // 각 데이터 포인트를 3D 구로 표시 (CAD 스타일)
                for (BuildingPoint point : points) {
                    Sphere sphere = new Sphere(0.3); // CAD처럼 작은 구로 표시
                
                // 동적 색상 설정
                Color color = getColorForType(point.type);
                PhongMaterial material = new PhongMaterial(color);
                sphere.setMaterial(material);
                
                // 위치 설정 (정규화 적용) - 사용자 설정 단위 적용
                double normalizedX = ((point.x * xUnitScale) + offsetX) / xyScale;
                double normalizedY = ((point.y * yUnitScale) + offsetY) / xyScale;
                double normalizedZ = ((point.z * zUnitScale) + offsetZ) / zScale; // 사용자 설정 단위 적용
                
                sphere.setTranslateX(normalizedX);
                sphere.setTranslateY(normalizedY);
                sphere.setTranslateZ(normalizedZ);
                
                root.getChildren().add(sphere);
            }
            
            // 카메라 자동 조정 - 모든 좌표가 보이도록
            adjustCameraToFitData(points, xyScale, zScale, offsetX, offsetY, offsetZ);
            
            // 초기 스케일을 1.0으로 설정 (R키 리셋과 동일하게)
            scale.setX(1.0);
            scale.setY(1.0);
            scale.setZ(1.0);
            
            // 초기 회전도 0도로 설정 (R키 리셋과 동일하게)
            rotateX.setAngle(0);
            rotateY.setAngle(0);
            rotateZ.setAngle(0);
            
            // 초기 translate도 R키 리셋과 동일하게 설정
            translate.setX(0);
            translate.setY(0);
            translate.setZ(0); // R키 리셋과 동일하게 0으로 설정
            
            // 초기 로드 시 모든 값 콘솔 출력
            System.out.println("=== 초기 로드 시 모든 값 ===");
            System.out.println("회전 각도:");
            System.out.println("  rotateX: " + rotateX.getAngle() + "도");
            System.out.println("  rotateY: " + rotateY.getAngle() + "도");
            System.out.println("  rotateZ: " + rotateZ.getAngle() + "도");
            System.out.println("이동 위치:");
            System.out.println("  translateX: " + translate.getX());
            System.out.println("  translateY: " + translate.getY());
            System.out.println("  translateZ: " + translate.getZ());
            System.out.println("스케일:");
            System.out.println("  scaleX: " + scale.getX());
            System.out.println("  scaleY: " + scale.getY());
            System.out.println("  scaleZ: " + scale.getZ());
            System.out.println("카메라 위치:");
            System.out.println("  cameraZ: " + camera.getTranslateZ());
            System.out.println("========================");
            
            // 변환 적용 (모든 회전, 이동, 스케일 포함)
            root.getTransforms().clear();
            root.getTransforms().addAll(rotateX, rotateY, rotateZ, translate, scale);
        });
    }
    
    /**
     * 카메라를 데이터에 맞게 자동 조정
     */
    private void adjustCameraToFitData(List<BuildingPoint> points, double xyScale, double zScale, 
                                     double offsetX, double offsetY, double offsetZ) {
        if (points == null || points.isEmpty()) {
            return;
        }
        
        // 정규화된 좌표의 최대 범위 계산
        double maxDistance = 0;
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = Double.MIN_VALUE;
        
        for (BuildingPoint point : points) {
            double normalizedX = ((point.x * xUnitScale) + offsetX) / xyScale;
            double normalizedY = ((point.y * yUnitScale) + offsetY) / xyScale;
            double normalizedZ = ((point.z * zUnitScale) + offsetZ) / zScale;
            
            // 원점에서의 거리 계산
            double distance = Math.sqrt(normalizedX * normalizedX + normalizedY * normalizedY + normalizedZ * normalizedZ);
            maxDistance = Math.max(maxDistance, distance);
            
            // 각 축별 최대/최소값 계산
            minX = Math.min(minX, normalizedX);
            maxX = Math.max(maxX, normalizedX);
            minY = Math.min(minY, normalizedY);
            maxY = Math.max(maxY, normalizedY);
            minZ = Math.min(minZ, normalizedZ);
            maxZ = Math.max(maxZ, normalizedZ);
        }
        
        // 데이터의 실제 크기 계산
        double dataWidth = maxX - minX;
        double dataHeight = maxY - minY;
        double dataDepth = maxZ - minZ;
        double maxDimension = Math.max(Math.max(dataWidth, dataHeight), dataDepth);
        
        // 카메라 거리 계산 (데이터 크기와 시야각을 고려)
        // 45도 시야각에서 데이터가 화면에 잘 보이도록 계산
        double fov = 45.0; // 시야각 (도)
        double fovRadians = Math.toRadians(fov);
        double cameraDistance = (maxDimension / 2.0) / Math.tan(fovRadians / 2.0);
        
        // 여유분 추가 (3배 - 더 멀리서 보기)
        cameraDistance *= 3.0;
        
        // 최소/최대 거리 제한 (데이터 크기에 따라 동적 조정)
        double minDistance = Math.max(200, maxDimension * 1.0); // 최소 거리는 데이터 크기의 1배
        double maxDistanceLimit = Math.max(1500, maxDimension * 8.0); // 최대 거리는 데이터 크기의 8배
        cameraDistance = Math.max(cameraDistance, minDistance);
        cameraDistance = Math.min(cameraDistance, maxDistanceLimit);
        
        // 카메라 위치 설정 (R키 리셋과 동일한 값으로)
        camera.setTranslateZ(-cameraDistance);
        // translate는 visualizeData에서 별도로 설정
        
        System.out.println("카메라 자동 조정 (데이터 기반):");
        System.out.println("  데이터 포인트 수: " + points.size() + "개");
        System.out.println("  데이터 크기: " + String.format("%.1f x %.1f x %.1f", dataWidth, dataHeight, dataDepth));
        System.out.println("  최대 차원: " + String.format("%.1f", maxDimension));
        System.out.println("  계산된 카메라 거리: " + String.format("%.1f", cameraDistance));
        System.out.println("  실제 설정된 카메라 Z: " + String.format("%.1f", -cameraDistance));
        System.out.println("  거리 범위: " + String.format("%.1f ~ %.1f", minDistance, maxDistanceLimit));
    }
    
    /**
     * 3D 뷰 초기화
     */
    public void resetView() {
        Platform.runLater(() -> {
            // 모든 회전을 0으로 리셋
            rotateX.setAngle(0);
            rotateY.setAngle(0);
            rotateZ.setAngle(0);
            translate.setX(0);
            translate.setY(0);
            translate.setZ(0); // 기본 거리로 리셋 (데이터 로드 시 동적 조정됨)
            scale.setX(1.0);
            scale.setY(1.0);
            scale.setZ(1.0); // 스케일도 1.0으로 리셋
            
            // R키 리셋 시 모든 값 콘솔 출력
            System.out.println("=== R키 리셋 시 모든 값 ===");
            System.out.println("회전 각도:");
            System.out.println("  rotateX: " + rotateX.getAngle() + "도");
            System.out.println("  rotateY: " + rotateY.getAngle() + "도");
            System.out.println("  rotateZ: " + rotateZ.getAngle() + "도");
            System.out.println("이동 위치:");
            System.out.println("  translateX: " + translate.getX());
            System.out.println("  translateY: " + translate.getY());
            System.out.println("  translateZ: " + translate.getZ());
            System.out.println("스케일:");
            System.out.println("  scaleX: " + scale.getX());
            System.out.println("  scaleY: " + scale.getY());
            System.out.println("  scaleZ: " + scale.getZ());
            System.out.println("카메라 위치:");
            System.out.println("  cameraZ: " + camera.getTranslateZ());
            System.out.println("========================");
            
            // 모든 변환 적용
            root.getTransforms().clear();
            root.getTransforms().addAll(rotateX, rotateY, rotateZ, translate, scale);
        });
    }
    
    /**
     * 건물 포인트 데이터 클래스
     */
    public static class BuildingPoint {
        public final double x, y, z;
        public final String type;
        
        public BuildingPoint(double x, double y, double z, String type) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = type;
        }
    }
}
