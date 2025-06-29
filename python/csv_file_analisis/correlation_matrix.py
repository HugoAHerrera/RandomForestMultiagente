import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import numpy as np
import gc

file = "data/winequality-synthetic-big.csv"
chunksize = 80_000_000
correlations = []

for chunk in pd.read_csv(file, chunksize=chunksize):
    chunk['wine type'] = chunk['type'].map({'red': 0, 'white': 1})
    chunk.drop(columns=['type'], inplace=True)
    corr = chunk.corr(numeric_only=True)
    correlations.append(corr)
    del chunk, corr
    gc.collect()

mean_corr = sum(correlations) / len(correlations)

plt.figure(figsize=(12, 10))
sns.heatmap(mean_corr, annot=True, fmt=".2f", cmap="coolwarm", square=True)
plt.title("Mean correlation matrix (by chunks)")
plt.show()