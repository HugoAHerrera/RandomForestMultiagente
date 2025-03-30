import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score

df = pd.read_csv("src/data/winequality-red-categorical.csv")

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
"""
muestra = [[5.4, 1.7, 6.0, 1.4]]
prediccion = modelo.predict(muestra)
print(f"Predicción: {prediccion[0]}")
"""