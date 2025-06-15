import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

df = pd.read_csv("data/winequality-synthetic.csv")

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

plt.figure(figsize=(8, 5))
ax = sns.countplot(x='type', hue='quality_range', data=df_bins)
plt.title("Quality distribution")
plt.xlabel("Wine type")
plt.ylabel("Quantity")
plt.legend(title="Quality", labels=["Bad", "Good", "Excellent"])


for container in ax.containers:
    for bar in container:
        height = bar.get_height()
        if height > 0:
            ax.text(
                bar.get_x() + bar.get_width() / 2,
                height,
                str(height),
                ha='center',
                va='bottom',
                fontsize=9
            )

plt.tight_layout()
plt.show()