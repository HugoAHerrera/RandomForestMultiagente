import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.tree import export_text
from math import log2
import time

# Leer el dataset (asegúrate de que la ruta del CSV sea correcta)
df = pd.read_csv("src_antiguo/data/dataset.csv")

# Definir las características (features) y la variable objetivo
X = df[["sepal_length", "sepal_width", "petal_length", "petal_width"]]
y = df["species"]

# Crear el modelo usando un RandomForestClassifier con un único árbol y criterio de entropía
modelo = RandomForestClassifier(n_estimators=1, criterion="entropy", max_depth=3)
modelo.fit(X, y)

# Extraer el único árbol generado
arbol = modelo.estimators_[0]

# Exportar la estructura del árbol a un formato textual
arbol_texto = export_text(arbol, feature_names=list(X.columns))
print(arbol_texto)
