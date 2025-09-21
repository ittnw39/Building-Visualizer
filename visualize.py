import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.font_manager as fm
from mpl_toolkits.mplot3d import Axes3D
import sys
import os

# 한글 폰트 설정
def setup_korean_font():
    try:
        # Windows에서 사용 가능한 한글 폰트 찾기
        font_list = ['Malgun Gothic', 'Microsoft YaHei', 'SimHei', 'DejaVu Sans']
        for font_name in font_list:
            try:
                plt.rcParams['font.family'] = font_name
                plt.rcParams['axes.unicode_minus'] = False
                break
            except:
                continue
    except:
        # 폰트 설정 실패 시 기본 설정 유지
        pass

# 한글 폰트 설정 실행
setup_korean_font()

def generate_colors_for_types(unique_types):
    """타입별로 동적으로 색상 생성"""
    import matplotlib.colors as mcolors
    import numpy as np
    
    # 기본 색상 팔레트 (CAD 스타일에 적합한 색상들)
    base_colors = [
        'darkblue', 'darkred', 'darkgreen', 'darkorange', 'darkviolet',
        'darkcyan', 'darkmagenta', 'darkgoldenrod', 'darkolivegreen', 'darkslategray',
        'navy', 'maroon', 'forestgreen', 'chocolate', 'indigo'
    ]
    
    type_colors = {}
    for i, type_name in enumerate(unique_types):
        if i < len(base_colors):
            type_colors[type_name] = base_colors[i]
        else:
            # 더 많은 타입이 있으면 HSV 색상 공간에서 동적 생성
            hue = (i * 137.5) % 360  # 황금각을 이용한 균등 분포
            type_colors[type_name] = mcolors.hsv_to_rgb([hue/360, 0.7, 0.8])
    
    return type_colors

def visualize_excel(file_path, unit_scale=1.0, output_dir=None):
    df = pd.read_excel(file_path)

    if not {'x','y'}.issubset(df.columns):
        raise ValueError("Excel must contain 'x' and 'y' columns")

    # 단위변환배율은 전체적인 스케일 조정용 (기존 방식 유지)
    df['x'] = df['x'] * unit_scale
    df['y'] = df['y'] * unit_scale
    
    if 'z' in df.columns:
        df['z'] = df['z'] * unit_scale
        print(f"Z 컬럼 발견: {len(df)}개 데이터 포인트")
    else:
        print("Z 컬럼 없음: 2D 시각화")

    # Z 컬럼이 있으면 3D 시각화, 없으면 2D 시각화
    if 'z' in df.columns:
        # 3D 시각화
        fig = plt.figure(figsize=(14, 10))
        ax = fig.add_subplot(111, projection='3d')
        
        # 타입별로 색상 구분
        if 'type' in df.columns:
            # 동적 색상 생성 (하드코딩 제거)
            unique_types = df['type'].unique()
            type_colors = generate_colors_for_types(unique_types)
            
            for type_name, group in df.groupby('type'):
                color = type_colors.get(type_name, 'gray')
                ax.scatter(group['x'], group['y'], group['z'], 
                          label=type_name, s=20, color=color, alpha=0.8)  # CAD 스타일로 작은 점
                
                # 일부 점에만 좌표 정보 표시 (너무 많으면 복잡해짐)
                for idx, row in group.iterrows():
                    if idx % 5 == 0:  # 5개마다 하나씩만 표시
                        coord_text = f'({row["x"]:.0f},{row["y"]:.0f},{row["z"]:.0f})'
                        ax.text(row['x'], row['y'], row['z'], coord_text, 
                               fontsize=6, alpha=0.8)
        else:
            # type 컬럼이 없는 경우
            ax.scatter(df['x'], df['y'], df['z'], s=20, color='blue', alpha=0.8)  # CAD 스타일로 작은 점
            
            # 일부 점에만 좌표 정보 표시
            for idx, row in df.iterrows():
                if idx % 5 == 0:
                    coord_text = f'({row["x"]:.0f},{row["y"]:.0f},{row["z"]:.0f})'
                    ax.text(row['x'], row['y'], row['z'], coord_text, 
                           fontsize=6, alpha=0.8)
        
        ax.set_xlabel("X 좌표")
        ax.set_ylabel("Y 좌표")
        ax.set_zlabel("Z 좌표 (층)")
        ax.set_title("건물 3D 좌표 시각화")
        
        # 축 범위를 명시적으로 설정 (X, Y는 0부터 시작)
        ax.set_xlim(0, df['x'].max())
        ax.set_ylim(0, df['y'].max())
        ax.set_zlim(0, df['z'].max())
        
        # X, Y축 비율을 동일하게 유지하고 Z축만 적절히 확대
        x_range = df['x'].max()
        y_range = df['y'].max()
        z_range = df['z'].max()
        
        # X, Y축은 동일한 비율로 유지 (1:1)
        # Z축만 건축 도면처럼 적절히 확대
        z_expansion_factor = max(3.0, min(8.0, z_range / (max(x_range, y_range) * 0.1)))
        aspect_ratio = [1, 1, (z_range/max(x_range, y_range)) * z_expansion_factor]
        ax.set_box_aspect(aspect_ratio)
        
        # 모든 축의 눈금을 데이터 크기에 따라 동적으로 설정
        x_max = df['x'].max()
        y_max = df['y'].max()
        z_max = df['z'].max()
        
        # 눈금 간격을 데이터 크기에 따라 조정 (최대 20개 눈금)
        x_step = max(1, int(x_max / 20))
        y_step = max(1, int(y_max / 20))
        z_step = max(1, int(z_max / 20))
        
        # X축 눈금
        x_ticks = list(range(0, int(x_max) + x_step, x_step))
        ax.set_xticks(x_ticks)
        
        # Y축 눈금
        y_ticks = list(range(0, int(y_max) + y_step, y_step))
        ax.set_yticks(y_ticks)
        
        # Z축 눈금
        z_ticks = list(range(0, int(z_max) + z_step, z_step))
        ax.set_zticks(z_ticks)
        
        # 격자 표시로 3D 구조 더 명확하게
        ax.grid(True, alpha=0.3)
        
        if 'type' in df.columns:
            ax.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
        
        # 3D 뷰 각도 설정 (더 좋은 시각화를 위해)
        ax.view_init(elev=25, azim=45)
        
    else:
        # 2D 시각화 (기존 코드)
        plt.figure(figsize=(12, 10))
        
        # 타입별로 색상 구분 (간단한 색상 사용)
        if 'type' in df.columns:
            # 동적 색상 생성 (하드코딩 제거)
            unique_types = df['type'].unique()
            type_colors = generate_colors_for_types(unique_types)
            
            for type_name, group in df.groupby('type'):
                color = type_colors.get(type_name, 'gray')  # 기본값은 회색
                plt.scatter(group['x'], group['y'], label=type_name, s=20, color=color, alpha=0.8)  # CAD 스타일로 작은 점
                
                # 각 점에 좌표 정보 표시
                for idx, row in group.iterrows():
                    coord_text = f'({row["x"]:.0f},{row["y"]:.0f})'
                    plt.annotate(coord_text, 
                               (row['x'], row['y']), 
                               xytext=(5, 5), textcoords='offset points',
                               fontsize=8, alpha=0.8)
        else:
            # type 컬럼이 없는 경우
            plt.scatter(df['x'], df['y'], s=20, color='blue', alpha=0.8)  # CAD 스타일로 작은 점
            
            # 각 점에 좌표 정보 표시
            for idx, row in df.iterrows():
                coord_text = f'({row["x"]:.0f},{row["y"]:.0f})'
                plt.annotate(coord_text, 
                           (row['x'], row['y']), 
                           xytext=(5, 5), textcoords='offset points',
                           fontsize=8, alpha=0.8)

        plt.xlabel("X 좌표")
        plt.ylabel("Y 좌표")
        plt.title("건물 평면 좌표 시각화")
        
        # 2D 시각화에서도 X, Y축 눈금을 데이터 크기에 따라 동적으로 설정
        x_max = df['x'].max()
        y_max = df['y'].max()
        
        # 눈금 간격을 데이터 크기에 따라 조정 (최대 20개 눈금)
        x_step = max(1, int(x_max / 20))
        y_step = max(1, int(y_max / 20))
        
        x_ticks = list(range(0, int(x_max) + x_step, x_step))
        y_ticks = list(range(0, int(y_max) + y_step, y_step))
        plt.xticks(x_ticks)
        plt.yticks(y_ticks)
        
        if 'type' in df.columns:
            plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
        plt.axis("equal")
        plt.grid(True, alpha=0.3)
    
    # 여백 조정으로 이미지가 잘리지 않도록 함
    plt.tight_layout()

    # 타임스탬프를 포함한 고유한 파일명 생성
    from datetime import datetime
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    
    if not output_dir:
        # 사용자 Downloads 폴더에 실행별 폴더 생성
        # EXE 실행 여부를 더 확실하게 체크
        is_exe = (getattr(sys, 'frozen', False) or 
                 hasattr(sys, '_MEIPASS') or 
                 os.path.basename(sys.executable).endswith('.exe'))
        
        if is_exe:
            # EXE로 실행된 경우 - Downloads 폴더에 실행별 폴더 생성
            downloads_path = os.path.join(os.path.expanduser("~"), "Downloads")
            output_dir = os.path.join(downloads_path, f"BuildingVisualizer_{timestamp}")
            print(f"EXE 실행 감지: Downloads 폴더에 저장 - {output_dir}")
        else:
            # 개발 환경에서 실행하는 경우
            output_dir = os.path.dirname(file_path) or '.'
            print(f"개발 환경 실행: 현재 폴더에 저장 - {output_dir}")
    
    os.makedirs(output_dir, exist_ok=True)
    img_path = os.path.join(output_dir, "coords_plot.png")
    plt.savefig(img_path, dpi=150, bbox_inches='tight')
    plt.close()

    # 엑셀 파일에 이미지 삽입
    out_excel = os.path.join(output_dir, "coords_with_plot.xlsx")
    
    # 원본 데이터를 엑셀에 저장
    with pd.ExcelWriter(out_excel, engine="openpyxl") as writer:
        df.to_excel(writer, index=False, sheet_name="coords")
        
        # 워크북과 워크시트 가져오기
        workbook = writer.book
        worksheet = writer.sheets["coords"]
        
        # 이미지를 엑셀에 삽입
        from openpyxl.drawing.image import Image
        img = Image(img_path)
        
        # 이미지 크기 조정 (엑셀 셀에 맞게)
        img.width = 400
        img.height = 300
        
        # 이미지를 데이터 아래쪽에 삽입 (H열부터 시작)
        worksheet.add_image(img, 'H2')
        
        # 이미지 설명 추가
        worksheet['H1'] = "시각화 결과"

        return img_path, out_excel

def send_3d_data_to_java(df, unit_scale=1.0, output_dir=None):
    """Java 3D 뷰어에 데이터 전송"""
    try:
        import socket
        import json
        
        # 3D 데이터 준비 (이미 unit_scale이 적용된 데이터를 그대로 사용)
        points = []
        for idx, row in df.iterrows():
            point = {
                'x': float(row['x']),
                'y': float(row['y']),
                'z': float(row['z']) if 'z' in row else 0.0,
                'type': str(row['type']) if 'type' in row else 'default'
            }
            points.append(point)
        
        # Java 애플리케이션에 데이터 전송 (간단한 HTTP 요청)
        import urllib.request
        import urllib.parse
        
        data = {
            'points': points,
            'total_count': len(points)
        }
        
        json_data = json.dumps(data).encode('utf-8')
        
        try:
            # Java 애플리케이션의 3D 뷰어에 데이터 전송
            req = urllib.request.Request(
                'http://localhost:8080/3d-data',
                data=json_data,
                headers={'Content-Type': 'application/json'}
            )
            urllib.request.urlopen(req, timeout=1)
            print(f"3D 데이터 전송 완료: {len(points)}개 포인트")
        except:
            # HTTP 전송 실패 시 파일로 저장
            if output_dir:
                json_path = os.path.join(output_dir, "3d_data.json")
                with open(json_path, 'w', encoding='utf-8') as f:
                    json.dump(data, f, ensure_ascii=False, indent=2)
                print(f"3D 데이터 파일 저장: {json_path} ({len(points)}개 포인트)")
            else:
                print("3D 데이터 전송 실패: output_dir이 설정되지 않음")
            
    except Exception as e:
        print(f"3D 데이터 전송 실패: {e}")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python visualize.py <excel_file> [unit_scale]")
        sys.exit(1)
    file_path = sys.argv[1]
    
    # 단위 변환 배율 처리 개선
    if len(sys.argv) > 2 and sys.argv[2].strip():
        try:
            unit_scale = float(sys.argv[2].strip())
        except ValueError:
            print("Warning: Invalid unit scale, using default value 1.0")
            unit_scale = 1.0
    else:
        unit_scale = 1.0
    
    # 데이터 로드
    df = pd.read_excel(file_path)
    
    # 이미지 생성
    img, out = visualize_excel(file_path, unit_scale=unit_scale)
    print("Saved image:", img)
    print("Saved excel:", out)
    
    # 3D 데이터 전송
    send_3d_data_to_java(df, unit_scale, os.path.dirname(img))
