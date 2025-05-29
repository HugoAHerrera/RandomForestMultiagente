import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.tree import export_text
from math import log2

df = pd.read_csv("src_antiguo/data/dataset.csv")
X = df[["sepal_length", "sepal_width", "petal_length", "petal_width"]]
y = df["species"]

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

numero_arboles = 20
modelo = RandomForestClassifier(n_estimators=numero_arboles, criterion="entropy")
modelo.fit(X_train, y_train)

y_pred = modelo.predict(X_test)
precision = accuracy_score(y_test, y_pred)
print(f"Precisión: {precision:.2%}")

muestra = pd.DataFrame([[5.4, 1.7, 6.0, 1.4]], columns=["sepal_length", "sepal_width", "petal_length", "petal_width"])
prediccion = modelo.predict(muestra)
print(f"Predicción: {prediccion[0]}")

arbol = modelo.estimators_[0]
texto_arbol = export_text(arbol, feature_names=list(X.columns))
print("\nÁrbol de decisión (primer árbol del Random Forest):")
print(texto_arbol)
print("\n\nClases predecir:")
print(modelo.classes_)

arbol = modelo.estimators_[0]
tree = arbol.tree_

print("\nDetalles del árbol:")
for i in range(tree.node_count):
    if tree.children_left[i] != tree.children_right[i]:
        feature = X.columns[tree.feature[i]]
        threshold = tree.threshold[i]
        
        samples = tree.n_node_samples[i]
        clases = tree.value[i][0]
        total = sum(clases)
        entropia = -sum((c / total) * log2(c / total) for c in clases if c > 0)

        print(f"Nodo {i}: split por '{feature}' <= {threshold:.2f} | muestras = {samples} | entropía = {entropia:.4f}")

"""
df = pd.read_csv("src_antiguo/data/winequality-red-categorical.csv")

X = df[["fixed acidity", "volatile acidity", "citric acid", "residual sugar", "chlorides",
        "free sulfur dioxide", "total sulfur dioxide", "density", "pH", "sulphates", "alcohol"]]
y = df["quality"]

# Entrenamiento (80%) y prueba (20%)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

numero_arboles = 20
modelo = RandomForestClassifier(n_estimators=numero_arboles)
modelo.fit(X_train, y_train)

y_pred = modelo.predict(X_test)
precision = accuracy_score(y_test, y_pred)
print(f"Precisión: {precision:.2%}")



# 1. Cargar datos
df = pd.read_csv("src_antiguo/data/winequality-red-categorical.csv")

# 2. Separar variables predictoras (X) y variable objetivo (y)
X = df[["fixed acidity", "volatile acidity", "citric acid", "residual sugar", "chlorides",
        "free sulfur dioxide", "total sulfur dioxide", "density", "pH", "sulphates", "alcohol"]]
y = df["quality"]

# 3. Dividir en datos de entrenamiento y prueba (80/20)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# 4. Entrenar modelo RandomForest con 20 árboles
numero_arboles = 20
modelo = RandomForestClassifier(n_estimators=numero_arboles, random_state=42)
modelo.fit(X_train, y_train)

# 5. Evaluar el modelo
y_pred = modelo.predict(X_test)
precision = accuracy_score(y_test, y_pred)
print(f"Precisión: {precision:.2%}")

# 6. Imprimir el primer árbol en texto
primer_arbol = modelo.estimators_[0]
texto_arbol = export_text(primer_arbol, feature_names=list(X.columns))
print("\nÁrbol de decisión #0 (texto):")
print(texto_arbol)

# 7. Visualizar el primer árbol gráficamente
plt.figure(figsize=(20, 10))
plot_tree(primer_arbol, feature_names=X.columns, filled=True, rounded=True, fontsize=10)
plt.title("Árbol de decisión #0")
plt.show()
"""