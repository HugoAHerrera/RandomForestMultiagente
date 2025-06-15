import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

df = pd.read_csv("data/winequality-combined.csv")

plt.figure(figsize=(15,18))

for i in range(1, 13):
    plt.subplot(4, 3, i)
    data = df[df.columns[i]].dropna()
    
    plt.hist(data, bins=30, density=True, alpha=0.6, color='g')
    
    mean_value  = np.mean(data)
    standar_deriv = np.std(data)
    
    xmin, xmax = plt.xlim()
    x = np.linspace(xmin, xmax, 100)
    
    gaussian_curve = (1/(standar_deriv * np.sqrt(2 * np.pi))) * np.exp(-0.5 * ((x - mean_value )/standar_deriv)**2)
    plt.plot(x, gaussian_curve, 'r', linewidth=2)
    
    plt.title(df.columns[i])

plt.tight_layout()
plt.show()

