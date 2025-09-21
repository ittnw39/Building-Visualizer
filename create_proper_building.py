import pandas as pd
import numpy as np

# 정확한 건축물 데이터 생성
data = []

# 건물 기본 정보
building_width = 100  # 건물 폭 (m)
building_length = 80  # 건물 길이 (m)
floor_height = 3.5    # 층고 (m)
num_floors = 5        # 총 5층 (지하1층 + 지상4층)

# 층별 Z 좌표 정의 (정확한 값)
floor_z_coords = {
    0: 0.0,      # 지하1층
    1: 3.5,      # 1층
    2: 7.0,      # 2층
    3: 10.5,     # 3층
    4: 14.0,     # 4층
    5: 17.5      # 지붕
}

print("건물 구조 생성 중...")
print(f"건물 크기: {building_width}m × {building_length}m")
print(f"층수: {num_floors}층 (지하1층 + 지상4층)")
print(f"층고: {floor_height}m")

# 각 층별로 구조 생성
for floor_num in range(num_floors + 1):  # 0~5층 (지하1층~지붕)
    z_coord = floor_z_coords[floor_num]
    floor_name = f"{floor_num}층" if floor_num > 0 else "지하1층" if floor_num == 0 else "지붕"
    
    print(f"  {floor_name} (Z={z_coord}m) 생성 중...")
    
    # 외곽 벽 (각 층마다)
    # 전면/후면 벽
    for x in range(0, building_width + 1, 2):
        data.append({'x': x, 'y': 0, 'z': z_coord, 'type': '외곽'})
        data.append({'x': x, 'y': building_length, 'z': z_coord, 'type': '외곽'})
    
    # 좌측/우측 벽
    for y in range(0, building_length + 1, 2):
        data.append({'x': 0, 'y': y, 'z': z_coord, 'type': '외곽'})
        data.append({'x': building_width, 'y': y, 'z': z_coord, 'type': '외곽'})
    
    # 기둥 (격자형 배치 - 10m 간격)
    for x in range(10, building_width, 10):
        for y in range(10, building_length, 10):
            data.append({'x': x, 'y': y, 'z': z_coord, 'type': '기둥'})
    
    # 홀 (중앙 공간 - 3m 간격으로 촘촘하게)
    for x in range(20, building_width - 20, 3):
        for y in range(20, building_length - 20, 3):
            data.append({'x': x, 'y': y, 'z': z_coord, 'type': '홀'})
    
    # 계단 (2개 위치 - 각 층마다)
    # 계단 1 (우측 상단)
    for x in range(85, 95, 2):
        for y in range(10, 20, 2):
            data.append({'x': x, 'y': y, 'z': z_coord, 'type': '계단'})
    
    # 계단 2 (좌측 하단)
    for x in range(5, 15, 2):
        for y in range(60, 70, 2):
            data.append({'x': x, 'y': y, 'z': z_coord, 'type': '계단'})
    
    # 출입문 (각 층마다 4개 방향)
    data.append({'x': 50, 'y': 0, 'z': z_coord, 'type': '출입문'})      # 정면
    data.append({'x': 50, 'y': building_length, 'z': z_coord, 'type': '출입문'})  # 후면
    data.append({'x': 0, 'y': 40, 'z': z_coord, 'type': '출입문'})      # 좌측
    data.append({'x': building_width, 'y': 40, 'z': z_coord, 'type': '출입문'})   # 우측

# DataFrame 생성
df = pd.DataFrame(data)

# 중복 제거
df = df.drop_duplicates()

print(f"\n건물 데이터 생성 완료!")
print(f"총 데이터 개수: {len(df)}")
print(f"컬럼: {df.columns.tolist()}")
print(f"X 범위: {df['x'].min()} ~ {df['x'].max()}")
print(f"Y 범위: {df['y'].min()} ~ {df['y'].max()}")
print(f"Z 범위: {df['z'].min()} ~ {df['z'].max()}")

print(f"\n층별 데이터 개수:")
for floor_num in range(num_floors + 1):
    z_coord = floor_z_coords[floor_num]
    floor_data = df[df['z'] == z_coord]
    floor_name = f"{floor_num}층" if floor_num > 0 else "지하1층" if floor_num == 0 else "지붕"
    print(f"  {floor_name} (Z={z_coord}m): {len(floor_data)}개")

print(f"\n타입별 개수:")
print(df['type'].value_counts())

print(f"\n샘플 데이터:")
print(df.head(10))

# 엑셀 파일로 저장
output_filename = 'proper_building.xlsx'
df.to_excel(output_filename, index=False)
print(f"\n{output_filename} 파일이 생성되었습니다!")

# Z 좌표별 정확한 분포 확인
print(f"\nZ 좌표별 정확한 분포:")
z_counts = df['z'].value_counts().sort_index()
for z_val, count in z_counts.items():
    print(f"  Z={z_val}m: {count}개 데이터")
