# Building Visualizer (Maven Version)

## 프로젝트 개요
건물 평면 좌표 데이터를 시각화하는 Java Swing 애플리케이션입니다. Excel 파일에서 좌표 데이터를 읽어와 Python을 통해 그래프를 생성하고 결과를 GUI에 표시합니다.

## 프로젝트 구성
- `visualize.py`: Python 시각화 스크립트 (Excel → 그래프)
- `src/main/java/com/example/visualizer/BuildingVisualizer.java`: Java Swing UI
- `pom.xml`: Maven 프로젝트 설정
- `requirements.txt`: Python 의존성 패키지 목록
- `coords.xlsx`: 샘플 좌표 데이터

## 시스템 요구사항
- **Java**: JDK 17 이상
- **Python**: Python 3.7 이상
- **Maven**: 3.6 이상
- **운영체제**: Windows 10/11

## 상세 설치 및 실행 방법

### 1. Python 환경 준비
```bash
# Python 패키지 설치
pip install -r requirements.txt
```

설치되는 패키지:
- `pandas>=1.5.0`: 데이터 처리
- `matplotlib>=3.5.0`: 그래프 생성
- `openpyxl>=3.0.0`: Excel 파일 읽기/쓰기

### 2. Maven 빌드
```bash
# 프로젝트 빌드
mvn clean package
```

### 3. 애플리케이션 실행
```bash
# Maven exec 플러그인을 통한 실행 (권장)
mvn exec:java

# 또는 직접 Java 실행
java -cp target/building-visualizer-1.0-SNAPSHOT.jar com.example.visualizer.BuildingVisualizer
```

## 사용 방법

### GUI 사용법
1. **애플리케이션 실행**: `mvn exec:java` 명령어로 실행
2. **Excel 파일 선택**: "엑셀 열기" 버튼 클릭하여 좌표 데이터가 포함된 Excel 파일 선택
3. **데이터 확인**: 선택한 파일의 데이터가 테이블에 표시됨
4. **단위 변환 설정**: "단위 변환 배율" 필드에 변환 비율 입력
   - 예: `0.001` (mm → m), `1000` (m → mm)
5. **시각화 실행**: "시각화 실행" 버튼 클릭
6. **결과 확인**: 하단에 생성된 그래프 이미지가 표시됨

### Excel 파일 형식
Excel 파일은 다음 컬럼을 포함해야 합니다:
- `x`: X 좌표값
- `y`: Y 좌표값
- `type`: (선택사항) 그룹 분류용 컬럼

### 출력 파일
실행 후 다음 파일들이 생성됩니다:
- `coords_plot.png`: 시각화된 그래프 이미지
- `coords_with_plot.xlsx`: 처리된 좌표 데이터가 포함된 Excel 파일

## 주요 기능
- **Excel 데이터 로딩**: Apache POI를 사용한 Excel 파일 읽기
- **데이터 테이블 표시**: JTable을 통한 데이터 미리보기
- **단위 변환**: 사용자 정의 배율로 좌표 단위 변환
- **Python 시각화**: matplotlib을 사용한 고품질 그래프 생성
- **그룹별 색상**: type 컬럼이 있는 경우 자동으로 그룹별 색상 지정
- **실시간 결과 표시**: GUI 내에서 생성된 이미지 즉시 확인

## 문제 해결

### 한글 폰트 경고
Python에서 한글 폰트 관련 경고가 나타날 수 있습니다. 이는 그래프 생성에는 영향을 주지 않습니다.

### Maven 실행 오류
PowerShell에서 Maven exec 명령어 실행 시 문제가 있다면:
```bash
# cmd에서 실행하거나
cmd /c "mvn exec:java"

# 또는 직접 Java 실행
java -cp target/building-visualizer-1.0-SNAPSHOT.jar com.example.visualizer.BuildingVisualizer
```

### Python 스크립트 경로 오류
- **문제**: `python: can't open file 'visualize.py': [Errno 2] No such file or directory`
- **해결**: 
  - EXE 생성 시 `--app-content visualize.py` 옵션 필수 포함
  - Java 코드에서 자동으로 Python 스크립트 경로 감지
  - EXE 디렉토리와 Python 스크립트가 함께 배포됨

### Log4j 경고
- **문제**: "Log4j2 could not find a logging implementation" 경고
- **해결**: 이는 정상적인 경고이며 애플리케이션 실행에는 영향 없음

## EXE 파일 생성 방법

### jpackage를 사용한 EXE 생성
Java 14+에서 제공하는 jpackage 도구를 사용하여 독립 실행 가능한 exe 파일을 생성할 수 있습니다.

#### 1. Fat JAR 생성
```bash
# Maven shade 플러그인으로 모든 의존성이 포함된 JAR 생성
mvn clean package
```

#### 2. EXE 파일 생성
```bash
# jpackage로 독립 실행 가능한 애플리케이션 생성 (Python 스크립트 포함)
"C:\Program Files\Java\jdk-21\bin\jpackage.exe" --type app-image --name "BuildingVisualizer" --input target --main-jar building-visualizer-fat.jar --main-class com.example.visualizer.BuildingVisualizer --dest dist --win-console --app-content visualize.py
```

**중요**: `--app-content visualize.py` 옵션을 반드시 포함해야 Python 시각화 기능이 정상 작동합니다.

#### 3. 실행
생성된 exe 파일은 `dist\BuildingVisualizer\BuildingVisualizer.exe`에서 실행할 수 있습니다.

### WiX를 사용한 MSI 설치 파일 생성 (선택사항)
더 완전한 설치 프로그램을 원한다면 WiX 도구를 설치하여 MSI 파일을 생성할 수 있습니다:

1. [WiX Toolset](https://wixtoolset.org/) 다운로드 및 설치
2. WiX를 PATH에 추가
3. 다음 명령어로 MSI 파일 생성:
```bash
"C:\Program Files\Java\jdk-21\bin\jpackage.exe" --type msi --name "BuildingVisualizer" --input target --main-jar building-visualizer-fat.jar --main-class com.example.visualizer.BuildingVisualizer --dest dist --win-console --win-shortcut --win-menu --app-content visualize.py
```

### EXE 파일의 장점
- **독립 실행**: Java 런타임이 포함되어 별도 Java 설치 불필요
- **간편한 배포**: 단일 폴더로 모든 파일 포함
- **사용자 친화적**: 일반 Windows 애플리케이션처럼 실행

## 개발 환경
- **Java**: OpenJDK 21
- **Maven**: 3.9.11
- **Python**: 3.13
- **IDE**: IntelliJ IDEA / Eclipse 권장
