package com.example.visualizer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BuildingVisualizer extends JFrame {
    private JTable table;
    private JLabel imageLabel;
    private JTextField scaleField;
    private File selectedFile;

    public BuildingVisualizer() {
        setTitle("Building Visualizer");
        setSize(900,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton openBtn = new JButton("엑셀 열기");
        JButton runBtn = new JButton("시각화 실행");
        scaleField = new JTextField("1.0",5);
        topPanel.add(openBtn);
        topPanel.add(new JLabel("단위 변환 배율:"));
        topPanel.add(scaleField);
        topPanel.add(runBtn);
        add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        imageLabel = new JLabel("결과 이미지가 여기에 표시됩니다", SwingConstants.CENTER);
        add(imageLabel, BorderLayout.SOUTH);

        openBtn.addActionListener(e -> openExcel());
        runBtn.addActionListener(e -> runPython());
    }

    private void openExcel() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            loadExcelToTable(selectedFile);
        }
    }

    private void loadExcelToTable(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            DefaultTableModel model = new DefaultTableModel();
            Row header = sheet.getRow(0);
            for (Cell cell : header) {
                model.addColumn(cell.toString());
            }
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Object[] rowData = new Object[row.getLastCellNum()];
                for (int c = 0; c < row.getLastCellNum(); c++) {
                    Cell cell = row.getCell(c);
                    rowData[c] = (cell == null) ? "" : cell.toString();
                }
                model.addRow(rowData);
            }
            table.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "엑셀 불러오기 오류: " + ex.getMessage());
        }
    }

    private void runPython() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "먼저 엑셀 파일을 선택하세요.");
            return;
        }
        try {
            String scale = scaleField.getText().trim();
            if (scale.isEmpty()) {
                scale = "1.0";
            }
            // 숫자 유효성 검사
            try {
                Double.parseDouble(scale);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "단위 변환 배율은 숫자여야 합니다.");
                return;
            }
            // Python 스크립트 경로 찾기
            String pythonScript = findPythonScript();
            ProcessBuilder pb = new ProcessBuilder("python", pythonScript,
                                                   selectedFile.getAbsolutePath(), scale);
            pb.directory(new File("."));
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line; String imgPath = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Saved image:")) {
                    imgPath = line.replace("Saved image:","").trim();
                }
                System.out.println(line);
            }
            p.waitFor();
            if (imgPath != null) {
                ImageIcon icon = new ImageIcon(imgPath);
                imageLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(400,300,Image.SCALE_SMOOTH)));
                imageLabel.setText("");
            } else {
                imageLabel.setText("이미지 생성 실패");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Python 실행 오류: " + ex.getMessage());
        }
    }

    private String findPythonScript() {
        // 현재 실행 중인 JAR 파일의 위치 찾기
        String jarPath = BuildingVisualizer.class.getProtectionDomain()
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BuildingVisualizer().setVisible(true);
        });
    }
}
