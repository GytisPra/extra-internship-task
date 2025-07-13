import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

df = pd.read_json('output/top15stations.json').convert_dtypes()
df.sort_values('name', inplace=True, key=lambda col: col.str.lower())

R = df["passengers"].max() - df["passengers"].min() # Range
n = len(df)                                         # Number of rows
std = df["passengers"].std()                        # Standard deviation

optimal_bin_num = np.ceil(R * ((n**(1/3)) / (3.49*std))).astype(int)

plt.hist(df["passengers"], bins=optimal_bin_num, edgecolor='black')
plt.xlabel('Passengers count')
plt.title('Top 15 stations distribution graph (sorted by name)')
plt.show()