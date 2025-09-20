import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.font_manager as fm
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

def visualize_excel(file_path, unit_scale=1.0, output_dir=None):
    df = pd.read_excel(file_path)

    if not {'x','y'}.issubset(df.columns):
        raise ValueError("Excel must contain 'x' and 'y' columns")

    df['x'] = df['x'] * unit_scale
    df['y'] = df['y'] * unit_scale

    groups = df['type'].unique() if 'type' in df.columns else ['좌표']
    cmap = plt.get_cmap("tab10")

    plt.figure(figsize=(8,6))
    if 'type' in df.columns:
        for i, (t, sub) in enumerate(df.groupby('type')):
            plt.scatter(sub['x'], sub['y'], label=t, s=80, color=cmap(i % 10))
    else:
        plt.scatter(df['x'], df['y'], label="좌표", s=80)

    plt.xlabel("X (scaled)")
    plt.ylabel("Y (scaled)")
    plt.title("건물 평면 좌표 시각화")
    plt.legend()
    plt.axis("equal")
    plt.grid(True)

    if not output_dir:
        output_dir = os.path.dirname(file_path)
    os.makedirs(output_dir, exist_ok=True)
    img_path = os.path.join(output_dir, "coords_plot.png")
    plt.savefig(img_path, dpi=150)
    plt.close()

    out_excel = os.path.join(output_dir, "coords_with_plot.xlsx")
    with pd.ExcelWriter(out_excel, engine="openpyxl") as writer:
        df.to_excel(writer, index=False, sheet_name="coords")

    return img_path, out_excel

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
    img, out = visualize_excel(file_path, unit_scale=unit_scale)
    print("Saved image:", img)
    print("Saved excel:", out)
