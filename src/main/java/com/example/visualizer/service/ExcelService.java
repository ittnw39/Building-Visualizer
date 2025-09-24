package com.example.visualizer.service;

import com.example.visualizer.model.ExcelData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 엑셀 파일 처리를 담당하는 서비스 클래스
 */
public class ExcelService {
    
    /**
     * 엑셀 파일을 읽어서 ExcelData 객체로 변환
     */
    public ExcelData readExcelFile(File file) throws IOException {
        ExcelData excelData = new ExcelData(file);
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 헤더 읽기
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            if (headerRow != null) {
                for (Cell cell : headerRow) {
                    headers.add(cell.toString());
                }
            }
            excelData.setHeaders(headers);
            
            // type 컬럼 존재 여부 확인
            excelData.setHasTypeColumn(headers.contains("type"));
            
            // 데이터 읽기
            List<List<Object>> data = new ArrayList<>();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                
                List<Object> rowData = new ArrayList<>();
                for (int c = 0; c < headers.size(); c++) {
                    Cell cell = row.getCell(c);
                    rowData.add(cell == null ? "" : cell.toString());
                }
                data.add(rowData);
            }
            excelData.setData(data);
        }
        
        return excelData;
    }
    
    /**
     * ExcelData를 JTable 모델로 변환
     */
    public DefaultTableModel createTableModel(ExcelData excelData) {
        if (excelData == null || !excelData.isValid()) {
            return new DefaultTableModel();
        }
        
        String[] headers = excelData.getHeaders().toArray(new String[0]);
        DefaultTableModel model = new DefaultTableModel(headers, 0);
        
        for (List<Object> rowData : excelData.getData()) {
            model.addRow(rowData.toArray());
        }
        
        return model;
    }
    
    /**
     * 엑셀 데이터 유효성 검사
     */
    public boolean validateExcelData(ExcelData excelData) {
        if (excelData == null) {
            return false;
        }
        
        if (!excelData.hasRequiredColumns()) {
            return false;
        }
        
        if (excelData.getData() == null || excelData.getData().isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 엑셀 파일 확장자 검사
     */
    public boolean isValidExcelFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
    }
    
    /**
     * 엑셀 파일에 x,y 좌표 차트를 생성하고 저장
     * @param excelData 좌표 데이터
     * @param outputFile 출력할 엑셀 파일
     * @throws IOException 파일 처리 오류
     */
    public void createExcelChart(ExcelData excelData, File outputFile) throws IOException {
        if (excelData == null || !excelData.hasRequiredColumns() || excelData.getData() == null || excelData.getData().isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 엑셀 데이터입니다.");
        }
        
        // 새 워크북 생성
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("좌표 데이터");
            
            // 헤더 작성
            Row headerRow = sheet.createRow(0);
            List<String> headers = excelData.getHeaders();
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }
            
            // 데이터 작성
            List<List<Object>> data = excelData.getData();
            for (int r = 0; r < data.size(); r++) {
                Row row = sheet.createRow(r + 1);
                List<Object> rowData = data.get(r);
                for (int c = 0; c < rowData.size(); c++) {
                    Cell cell = row.createCell(c);
                    Object value = rowData.get(c);
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }
            
            // 차트 생성
            createScatterChart(sheet, excelData);
            
            // 파일 저장
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
            }
        }
    }
    
    /**
     * 산점도 차트 생성
     */
    private void createScatterChart(XSSFSheet sheet, ExcelData excelData) {
        // 차트 그리기 영역 생성
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        
        // 차트 위치 설정 (데이터 아래쪽에 배치)
        int dataRows = excelData.getRowCount() + 1; // 헤더 포함
        int chartStartRow = dataRows + 2;
        int chartEndRow = chartStartRow + 15;
        int chartStartCol = 0;
        int chartEndCol = 6;
        
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 
            chartStartCol, chartStartRow, chartEndCol, chartEndRow);
        
        // 산점도 차트 생성
        XDDFChart chart = drawing.createChart(anchor);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);
        
        // X축 설정
        XDDFValueAxis bottomAxis = chart.createValueAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("X 좌표");
        bottomAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        
        // Y축 설정
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Y 좌표");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        
        // 데이터 소스 생성
        int xColIndex = excelData.getHeaders().indexOf("x");
        int yColIndex = excelData.getHeaders().indexOf("y");
        int dataRowsCount = excelData.getRowCount();
        
        // X축 데이터 (x 컬럼)
        XDDFDataSource<Double> xData = XDDFDataSourcesFactory.fromNumericCellRange(
            sheet, new CellRangeAddress(1, dataRowsCount, xColIndex, xColIndex));
        
        // Y축 데이터 (y 컬럼)
        XDDFNumericalDataSource<Double> yData = XDDFDataSourcesFactory.fromNumericCellRange(
            sheet, new CellRangeAddress(1, dataRowsCount, yColIndex, yColIndex));
        
        // 차트 데이터 생성
        XDDFChartData data = chart.createData(ChartTypes.SCATTER, bottomAxis, leftAxis);
        
        // 시리즈 추가
        XDDFScatterChartData.Series series = (XDDFScatterChartData.Series) data.addSeries(xData, yData);
        series.setTitle("좌표점", null);
        
        // 차트 스타일 설정
        series.setSmooth(false);
        series.setMarkerStyle(MarkerStyle.CIRCLE);
        // series.setMarkerSize((byte) 5); // XML-BEANS 스키마 문제로 주석 처리
        
        // 차트 플롯
        chart.plot(data);
        
        // 차트 제목 설정 (제목은 시리즈 이름으로 대체)
        // XDDFChart의 제목 설정은 복잡하므로 시리즈 제목으로 대체
    }
    
    /**
     * 기존 엑셀 파일에 차트를 추가하여 새 파일로 저장
     * @param inputFile 원본 엑셀 파일
     * @param outputFile 차트가 추가된 출력 파일
     * @throws IOException 파일 처리 오류
     */
    public void addChartToExistingFile(File inputFile, File outputFile) throws IOException {
        // 원본 파일 읽기
        ExcelData excelData = readExcelFile(inputFile);
        
        // 차트가 포함된 새 파일 생성
        createExcelChart(excelData, outputFile);
    }
}
