import pandas as pd
import numpy as np

# 건축 기본 구조 데이터 생성
data = []

# 1층 (z=0)
# 외곽 벽
for x in [0, 50]:
    for y in range(0, 31, 5):
        data.append({'x': x, 'y': y, 'z': 0, 'type': '외곽'})

for y in [0, 30]:
    for x in range(0, 51, 5):
        data.append({'x': x, 'y': y, 'z': 0, 'type': '외곽'})

# 1층 내부 구조
# 기둥
for x in [10, 20, 30, 40]:
    for y in [10, 20]:
        data.append({'x': x, 'y': y, 'z': 0, 'type': '기둥'})

# 홀 (중앙 공간)
for x in range(15, 36, 5):
    for y in range(15, 26, 5):
        data.append({'x': x, 'y': y, 'z': 0, 'type': '홀'})

# 계단
for x in [45, 46, 47]:
    for y in [5, 6, 7]:
        data.append({'x': x, 'y': y, 'z': 0, 'type': '계단'})

# 출입문
data.append({'x': 25, 'y': 0, 'z': 0, 'type': '출입문'})
data.append({'x': 25, 'y': 30, 'z': 0, 'type': '출입문'})

# 2층 (z=3) - 1층과 동일한 구조
# 외곽 벽
for x in [0, 50]:
    for y in range(0, 31, 5):
        data.append({'x': x, 'y': y, 'z': 3, 'type': '외곽'})

for y in [0, 30]:
    for x in range(0, 51, 5):
        data.append({'x': x, 'y': y, 'z': 3, 'type': '외곽'})

# 2층 기둥
for x in [10, 20, 30, 40]:
    for y in [10, 20]:
        data.append({'x': x, 'y': y, 'z': 3, 'type': '기둥'})

# 2층 홀
for x in range(15, 36, 5):
    for y in range(15, 26, 5):
        data.append({'x': x, 'y': y, 'z': 3, 'type': '홀'})

# 2층 계단
for x in [45, 46, 47]:
    for y in [5, 6, 7]:
        data.append({'x': x, 'y': y, 'z': 3, 'type': '계단'})

# 2층 출입문
data.append({'x': 25, 'y': 0, 'z': 3, 'type': '출입문'})
data.append({'x': 25, 'y': 30, 'z': 3, 'type': '출입문'})

# 3층 (z=6) - 지붕 구조
# 지붕 외곽
for x in [0, 50]:
    for y in range(0, 31, 10):
        data.append({'x': x, 'y': y, 'z': 6, 'type': '외곽'})

for y in [0, 30]:
    for x in range(0, 51, 10):
        data.append({'x': x, 'y': y, 'z': 6, 'type': '외곽'})

# 지붕 기둥
for x in [10, 20, 30, 40]:
    for y in [10, 20]:
        data.append({'x': x, 'y': y, 'z': 6, 'type': '기둥'})

# 지붕 중앙
for x in range(20, 31, 5):
    for y in range(15, 26, 5):
        data.append({'x': x, 'y': y, 'z': 6, 'type': '홀'})

# DataFrame 생성
df = pd.DataFrame(data)

print('건축 기본 구조 데이터:')
print(f'총 데이터 개수: {len(df)}')
print(f'컬럼: {df.columns.tolist()}')
print(f'Z 값 범위: {df["z"].min()} ~ {df["z"].max()}')
print(f'타입별 개수:')
print(df['type'].value_counts())
print('\n샘플 데이터:')
print(df.head(10))

# 엑셀 파일로 저장
df.to_excel('building_structure.xlsx', index=False)
print('\nbuilding_structure.xlsx 파일이 생성되었습니다!')
