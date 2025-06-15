import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd

df = pd.read_csv("data/winequality-combined.csv")

"""
    If quality >= 0 and < 5 --> Bad
    If quality >= 0 and < 8 --> Good
    If quality >= 8 Excellent
"""
bins = [0, 5, 8, 10] 
labels = [0, 1, 2]    # 0: Bad, 1: Good, 2: Excellent

df['quality_range'] = pd.cut(
    x=df['quality'],
    bins=bins,
    labels=labels,
    right=False,           
    include_lowest=True    # Includes 0
)

df_bins = df.drop('quality', axis=1)

fig, axes = plt.subplots(4, 2, figsize=(16, 20))
fig.suptitle('Wine Characteristics by Quality & Type', fontsize=16)

sns.violinplot(x='quality_range', y='density', hue='type', data=df_bins, split=True, inner='quart', linewidth=1.3,
               palette={'red': 'red', 'white': 'white'}, ax=axes[0,0])
axes[0,0].set_xlabel("Quality Class", size=13, alpha=0.8)
axes[0,0].set_ylabel("Density", size=13, alpha=0.8)

sns.violinplot(x='quality_range', y='fixed acidity', hue='type', data=df_bins, split=True, inner='quart', linewidth=1.3,
               palette={'red': 'darkred', 'white': 'white'}, ax=axes[0,1])
axes[0,1].set_xlabel("Quality Class", size=13, alpha=0.8)
axes[0,1].set_ylabel("Fixed Acidity", size=13, alpha=0.8)

sns.violinplot(x='quality_range', y='total sulfur dioxide', hue='type', data=df_bins, split=True, inner='quart', linewidth=1.3,
               palette={'red': 'red', 'white': 'white'}, ax=axes[1,0])
axes[1,0].set_xlabel("Quality Class", size=13, alpha=0.8)
axes[1,0].set_ylabel("Total Sulfur Dioxide", size=13, alpha=0.8)

sns.violinplot(x='quality_range', y='residual sugar', hue='type', data=df_bins, split=True, inner='quart', linewidth=1.3,
               palette={'red': 'darkred', 'white': 'white'}, ax=axes[1,1])
axes[1,1].set_xlabel("Quality Class", size=13, alpha=0.8)
axes[1,1].set_ylabel("Residual Sugar", size=13, alpha=0.8)

sns.violinplot(x='quality_range', y='free sulfur dioxide', hue='type', data=df_bins, split=True, inner='quart', linewidth=1.3,
               palette={'red': 'red', 'white': 'white'}, ax=axes[2,0])
axes[2,0].set_xlabel("Quality Class", size=13, alpha=0.8)
axes[2,0].set_ylabel("Free Sulfur Dioxide", size=13, alpha=0.8)

sns.violinplot(x='quality_range', y='sulphates', hue='type', data=df_bins, split=True, inner='quart', linewidth=1.3,
               palette={'red': 'darkred', 'white': 'white'}, ax=axes[2,1])
axes[2,1].set_xlabel("Quality Class", size=13, alpha=0.8)
axes[2,1].set_ylabel("Sulphates", size=13, alpha=0.8)

sns.violinplot(x='quality_range', y='chlorides', hue='type', data=df_bins, split=True, inner='quart', linewidth=1.3,
               palette={'red': 'red', 'white': 'white'}, ax=axes[3,0])
axes[3,0].set_xlabel("Quality Class", size=13, alpha=0.8)
axes[3,0].set_ylabel("Chlorides", size=13, alpha=0.8)

sns.violinplot(x='quality_range', y='alcohol', hue='type', data=df_bins, split=True, inner='quart', linewidth=1.3,
               palette={'red': 'darkred', 'white': 'white'}, ax=axes[3,1])
axes[3,1].set_xlabel("Quality Class", size=13, alpha=0.8)
axes[3,1].set_ylabel("Alcohol", size=13, alpha=0.8)

plt.tight_layout(rect=[0, 0, 1, 0.96])
plt.show()

