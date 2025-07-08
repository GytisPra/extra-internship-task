import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

df = pd.read_json('output/top15stations.json').convert_dtypes()
df.sort_values('name', inplace=True, key=lambda col: col.str.lower())

print(f'sorted=\n{df["name"]}')

plt.hist(df["passengers"], bins=10)
plt.show()