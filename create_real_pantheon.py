import pandas as pd
import numpy as np
import math

def create_real_pantheon():
    """실제 로마 판테온을 참고한 정확한 구조"""
    
    # 판테온 실제 치수 (X,Y와 비슷한 스케일로 조정)
    radius = 21.5  # 판테온 내부 반지름
    height = 12    # 돔 높이 (구형) - X,Y 범위에 맞춤
    portico_width = 15  # 포르티코 폭
    portico_depth = 8   # 포르티코 깊이
    
    data = []
    
    # 1. 원형 외벽 (두꺼운 벽체)
    print("원형 외벽 생성 중...")
    wall_thickness = 2  # 벽 두께
    
    for angle in np.linspace(0, 2*math.pi, 128):
        # 외벽
        x_outer = (radius + wall_thickness) * math.cos(angle)
        y_outer = (radius + wall_thickness) * math.sin(angle)
        # 내벽
        x_inner = radius * math.cos(angle)
        y_inner = radius * math.sin(angle)
        
        # 벽체 (높이 8m)
        for z in np.linspace(0, 8, 9):
            data.append({
                'x': x_outer,
                'y': y_outer,
                'z': z,
                'type': '외벽'
            })
            data.append({
                'x': x_inner,
                'y': y_inner,
                'z': z,
                'type': '내벽'
            })
    
    # 2. 거대한 돔 (완만한 곡선)
    print("거대한 돔 생성 중...")
    for angle_xy in np.linspace(0, 2*math.pi, 64):
        for angle_z in np.linspace(0, math.pi/2, 32):  # 반구
            # 더 완만한 곡선을 위해 sin^2 사용
            curve_factor = math.sin(angle_z) ** 0.7  # 0.7 지수로 더 완만하게
            x = radius * math.cos(angle_xy) * curve_factor
            y = radius * math.sin(angle_xy) * curve_factor
            z = 8 + height * math.cos(angle_z)  # 벽 위에서 시작
            
            data.append({
                'x': x,
                'y': y,
                'z': z,
                'type': '돔'
            })
    
    # 3. 오큘러스 (천장 구멍) - 돔 중앙에 구멍
    print("오큘러스 생성 중...")
    oculus_radius = 4.5  # 오큘러스 반지름
    
    # 오큘러스 가장자리 (구멍의 경계)
    for angle in np.linspace(0, 2*math.pi, 32):
        x = oculus_radius * math.cos(angle)
        y = oculus_radius * math.sin(angle)
        z = 8 + height  # 돔 꼭대기
        data.append({
            'x': x,
            'y': y,
            'z': z,
            'type': '오큘러스'
        })
    
    # 4. 포르티코 (기둥 현관) - 판테온의 특징적인 입구
    print("포르티코 생성 중...")
    num_columns = 16  # 기둥 개수
    column_height = 6   # 기둥 높이
    
    # 기둥들 (입구 앞)
    for i in range(num_columns):
        x = -portico_width/2 + (i * portico_width / (num_columns-1))
        y = radius + wall_thickness + 2  # 외벽 앞
        
        for z in np.linspace(0, column_height, 7):
            data.append({
                'x': x,
                'y': y,
                'z': z,
                'type': '기둥'
            })
    
    # 포르티코 지붕
    for x in np.linspace(-portico_width/2, portico_width/2, 20):
        for y in np.linspace(radius + wall_thickness, radius + wall_thickness + portico_depth, 10):
            z = column_height
            data.append({
                'x': x,
                'y': y,
                'z': z,
                'type': '포르티코지붕'
            })
    
    # 5. 입구 (포르티코에서 내부로)
    print("입구 생성 중...")
    entrance_width = 6
    entrance_height = 6
    
    for x in np.linspace(-entrance_width/2, entrance_width/2, 8):
        for z in np.linspace(0, entrance_height, 7):
            data.append({
                'x': x,
                'y': radius,  # 내벽 위치
                'z': z,
                'type': '입구'
            })
    
    # 6. 내부 바닥 (원형)
    print("내부 바닥 생성 중...")
    for r in np.linspace(0, radius-1, 20):  # 벽에서 1m 안쪽
        for angle in np.linspace(0, 2*math.pi, 64):
            x = r * math.cos(angle)
            y = r * math.sin(angle)
            data.append({
                'x': x,
                'y': y,
                'z': 0,
                'type': '바닥'
            })
    
    # 7. 내부 장식 요소들 (간단하게)
    print("내부 장식 생성 중...")
    
    # 벽감 (벽에 있는 작은 공간들)
    for i in range(8):  # 8개의 벽감
        angle = (2 * math.pi * i) / 8
        x = (radius - 1) * math.cos(angle)
        y = (radius - 1) * math.sin(angle)
        
        for z in np.linspace(1, 4, 4):
            data.append({
                'x': x,
                'y': y,
                'z': z,
                'type': '벽감'
            })
    
    # DataFrame 생성
    df = pd.DataFrame(data)
    
    # 데이터 정보 출력
    print(f"\n=== 실제 판테온 스타일 데이터 생성 완료 ===")
    print(f"총 데이터 포인트: {len(df)}개")
    print(f"타입별 분포:")
    print(df['type'].value_counts())
    print(f"\n좌표 범위:")
    print(f"X: {df['x'].min():.1f} ~ {df['x'].max():.1f}")
    print(f"Y: {df['y'].min():.1f} ~ {df['y'].max():.1f}")
    print(f"Z: {df['z'].min():.1f} ~ {df['z'].max():.1f}")
    
    return df

if __name__ == "__main__":
    # 실제 판테온 스타일 데이터 생성
    pantheon_df = create_real_pantheon()
    
    # Excel 파일로 저장
    output_file = "real_pantheon_style.xlsx"
    pantheon_df.to_excel(output_file, index=False)
    print(f"\n파일 저장 완료: {output_file}")
    
    # 샘플 데이터 미리보기
    print(f"\n=== 샘플 데이터 (처음 10개) ===")
    print(pantheon_df.head(10))
