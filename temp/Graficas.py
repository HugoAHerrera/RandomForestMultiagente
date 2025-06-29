import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Leer CSV
df = pd.read_csv('src_antiguo/data/dataset.csv')

# Función para calcular MSE
def calcular_mse(valores):
    if len(valores) == 0:
        return 0
    media = np.mean(valores)
    return np.mean((valores - media) ** 2)

# Definir split óptimo
split = 0.8

# Dividir datos
izquierda = df[df['petal_width'] <= split]
derecha = df[df['petal_width'] > split]

# Calcular MSEs
mse_izq = calcular_mse(izquierda['petal_length'])
mse_der = calcular_mse(derecha['petal_length'])
n_izq = len(izquierda)
n_der = len(derecha)
total = n_izq + n_der
mse_media = (n_izq / total) * mse_izq + (n_der / total) * mse_der

# Colores por especie
colores = {
    'Iris-setosa': 'blue',
    'Iris-versicolor': 'green',
    'Iris-virginica': 'red'
}

# Dibujar puntos
for especie, grupo in df.groupby('species'):
    plt.scatter(grupo['petal_width'], grupo['petal_length'],
                label=especie, color=colores[especie])

# Línea vertical en el mejor split
plt.axvline(x=split, color='black', linestyle='--', linewidth=2.5)

# Texto con MSE
texto = (
    f"MSE izquierda (≤ {split}): {mse_izq:.4f}\n"
    f"MSE derecha (> {split}): {mse_der:.4f}\n"
    f"MSE medio: {mse_media:.4f}"
)
plt.text(0.05, 7.5, texto, fontsize=10, bbox=dict(facecolor='white', edgecolor='black'))

# Detalles del gráfico
plt.xlabel('Petal Width')
plt.ylabel('Petal Length')
plt.title(f'Petal Width vs Petal Length con MSE en x = {split}')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.show()
