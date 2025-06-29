import pandas as pd
import time

ruta_archivo = r"data/winequality-synthetic-part2.csv"
ruta_resultados = "pruebasRendimiento/pruebasPython/resultados.txt"

with open(ruta_resultados, "w", encoding="utf-8") as f:

    inicio = time.time()
    df = pd.read_csv(ruta_archivo)
    fin = time.time()
    f.write(f"Tiempo de carga: {(fin - inicio)*1000:.0f} ms\n")

    inicio = time.time()
    if len(df) > 14_999_999:
        fila = df.iloc[14_999_999].to_list()
        f.write(f"Fila 15 millones: {fila}\n")
    else:
        f.write("No hay 15 millones de filas\n")
    fin = time.time()
    f.write(f"Tiempo acceso fila 15M: {(fin - inicio)*1000:.0f} ms\n")

    inicio = time.time()
    valores_unicos = df.iloc[:, 0].dropna().astype(str).str.strip().unique()
    fin = time.time()
    f.write(f"Valores únicos primera columna: {len(valores_unicos)}\n")
    f.write(f"Tiempo valores únicos: {(fin - inicio)*1000:.0f} ms\n\n")
