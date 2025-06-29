import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

data = {
    'sepal_length': [51, 73, 51, 56, 51],
    'petal_length': [30, 63, 30, 45, 30],
    'species': ['Iris-versicolor', 'Iris-virginica', 'Iris-versicolor', 'Iris-versicolor', 'Iris-versicolor']
}
df = pd.DataFrame(data)

colors = {'Iris-versicolor': 'blue', 'Iris-virginica': 'red'}
df['color'] = df['species'].map(colors)

def entropy(split):
    left = df[df['sepal_length'] <= split]
    right = df[df['sepal_length'] > split]
    
    def ent(subset):
        counts = subset['species'].value_counts(normalize=True)
        return -np.sum(counts * np.log2(counts + 1e-9))
    
    left_weight = len(left) / len(df)
    right_weight = len(right) / len(df)
    
    return left_weight * ent(left) + right_weight * ent(right)

splits = np.linspace(df['sepal_length'].min(), df['sepal_length'].max(), 100)
entropies = [entropy(s) for s in splits]
best_split = splits[np.argmin(entropies)]

plt.figure(figsize=(8,6))
for species in df['species'].unique():
    subset = df[df['species'] == species]
    plt.scatter(subset['sepal_length'], subset['petal_length'], label=species, color=colors[species], s=100)

plt.axvline(best_split, color='black', linestyle='--', label=f'Mejor split: {best_split:.2f}')
plt.xlabel('Sepal Length')
plt.ylabel('Petal Length')
plt.title('Separación óptima por entropía')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.show()
