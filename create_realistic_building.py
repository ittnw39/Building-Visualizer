import pandas as pd
import numpy as np

# 실제 건축물 데이터 생성 (더 많은 좌표)
data = []

# 건물 기본 정보
building_width = 100  # 건물 폭
building_length = 80  # 건물 길이
floor_height = 3.5    # 층고
num_floors = 5        # 총 5층

# 1층 (z=0) - 기초 및 1층
# 외곽 벽 (더 촘촘하게)
for x in range(0, building_width + 1, 2):
    for y in [0, building_length]:
        data.append({'x': x, 'y': y, 'z': 0, 'type': '외곽'})

for y in range(0, building_length + 1, 2):
    for x in [0, building_width]:
        data.append({'x': x, 'y': y, 'z': 0, 'type': '외곽'})

# 1층 내부 구조
# 기둥 (격자형 배치)
for x in range(10, building_width, 10):
    for y in range(10, building_length, 10):
        data.append({'x': x, 'y': y, 'z': 0, 'type': '기둥'})

# 홀 (중앙 공간 - 더 넓게)
for x in range(20, building_width - 20, 3):
    for y in range(20, building_length - 20, 3):
        data.append({'x': x, 'y': y, 'z': 0, 'type': '홀'})

# 계단 (2개 위치)
# 계단 1
for x in range(85, 95):
    for y in range(10, 20):
        data.append({'x': x, 'y': y, 'z': 0, 'type': '계단'})

# 계단 2
for x in range(5, 15):
    for y in range(60, 70):
        data.append({'x': x, 'y': y, 'z': 0, 'type': '계단'})

# 출입문 (4개 방향)
data.append({'x': 50, 'y': 0, 'z': 0, 'type': '출입문'})      # 정면
data.append({'x': 50, 'y': building_length, 'z': 0, 'type': '출입문'})  # 후면
data.append({'x': 0, 'y': 40, 'z': 0, 'type': '출입문'})      # 좌측
data.append({'x': building_width, 'y': 40, 'z': 0, 'type': '출입문'})   # 우측

# 2-5층 (z=3.5, 7, 10.5, 14)
for floor in range(1, num_floors):
    z_coord = floor * floor_height
    
    # 외곽 벽
    for x in range(0, building_width + 1, 2):
        for y in [0, building_length]:
            data.append({'x': x, 'y': y, 'z': z_coord, 'type': '외곽'})

    for y in range(0, building_length + 1, 2):
        for x in [0, building_width]:
            data.append({'x': x, 'y': y, 'z': z_coord, 'type': '외곽'})

    # 기둥
    for x in range(10, building_width, 10):
        for y in range(10, building_length, 10):
            data.append({'x': x, 'y': y, 'z': z_coord, 'type': '기둥'})

    # 홀 (중앙 공간)
    for x in range(20, building_width - 20, 3):
        for y in range(20, building_length - 20, 3):
            data.append({'x': x, 'y': y, 'z': z_coord, 'type': '홀'})

    # 계단
    for x in range(85, 95):
        for y in range(10, 20):
            data.append({'x': x, 'y': y, 'z': z_coord, 'type': '계단'})

    for x in range(5, 15):
        for y in range(60, 70):
            data.append({'x': x, 'y': y, 'z': z_coord, 'type': '계단'})

    # 출입문 (각 층마다)
    data.append({'x': 50, 'y': 0, 'z': z_coord, 'type': '출입문'})
    data.append({'x': 50, 'y': building_length, 'z': z_coord, 'type': '출입문'})
    data.append({'x': 0, 'y': 40, 'z': z_coord, 'type': '출입문'})
    data.append({'x': building_width, 'y': 40, 'z': z_coord, 'type': '출입문'})

# 지붕 (z=17.5) - 최상층
# 지붕 외곽
for x in range(0, building_width + 1, 5):
    for y in [0, building_length]:
        data.append({'x': x, 'y': y, 'z': 17.5, 'type': '외곽'})

for y in range(0, building_length + 1, 5):
    for x in [0, building_width]:
        data.append({'x': x, 'y': y, 'z': 17.5, 'type': '외곽'})

# 지붕 기둥
for x in range(10, building_width, 10):
    for y in range(10, building_length, 10):
        data.append({'x': x, 'y': y, 'z': 17.5, 'type': '기둥'})

# 지붕 중앙
for x in range(30, building_width - 30, 5):
    for y in range(30, building_length - 30, 5):
        data.append({'x': x, 'y': y, 'z': 17.5, 'type': '홀'})

# DataFrame 생성
df = pd.DataFrame(data)

print('실제 건축물 데이터:')
print(f'총 데이터 개수: {len(df)}')
print(f'컬럼: {df.columns.tolist()}')
print(f'X 범위: {df["x"].min()} ~ {df["x"].max()}')
print(f'Y 범위: {df["y"].min()} ~ {df["y"].max()}')
print(f'Z 범위: {df["z"].min()} ~ {df["z"].max()}')
print(f'타입별 개수:')
print(df['type'].value_counts())
print('\n샘플 데이터:')
print(df.head(10))

# 엑셀 파일로 저장
df.to_excel('realistic_building.xlsx', index=False)
print('\nrealistic_building.xlsx 파일이 생성되었습니다!')
