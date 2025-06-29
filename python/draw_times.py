import re
import matplotlib.pyplot as plt
import math

# === Leer y parsear el archivo ===
ruta = r"C:\Users\hahdm\Downloads\winequality-synthetic-part1_benchmark_buffer.txt"

pattern = re.compile(r"Buffer:\s+(\d+)\s+bytes\s+.*?Tiempo:\s+([\d.]+)\s+ms", re.DOTALL)

buffers = []
tiempos = []

with open(ruta, 'r', encoding='utf-8') as f:
    texto = f.read()
    for match in pattern.finditer(texto):
        buffer_bytes = int(match.group(1))
        tiempo_ms = float(match.group(2))
        buffers.append(buffer_bytes)
        tiempos.append(tiempo_ms)

# === Convertir a unidad legible ===
def convertir_bytes(n):
    unidades = ['B', 'KB', 'MB', 'GB', 'TB', 'PB']
    i = 0
    while n >= 1024 and i < len(unidades) - 1:
        n /= 1024
        i += 1
    return f"{n:.2f} {unidades[i]}"

etiquetas_x = [convertir_bytes(b) for b in buffers]

# === Graficar ===
plt.figure(figsize=(12, 6))
plt.plot(buffers, tiempos, marker='o', color='dodgerblue', linewidth=2)

plt.xscale('log')
plt.xticks(buffers, etiquetas_x, rotation=45, ha='right')
plt.xlabel('Tamaño de buffer')
plt.ylabel('Tiempo (ms)')
plt.title('Tiempo de lectura vs Tamaño del buffer')
plt.grid(True, which='both', linestyle='--', alpha=0.5)
plt.tight_layout()
plt.show()
