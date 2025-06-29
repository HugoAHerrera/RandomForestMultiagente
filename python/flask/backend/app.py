from flask import Flask, request, jsonify, send_from_directory
from flask_cors import CORS
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import accuracy_score
import numpy as np
from sklearn.tree import export_text
import os

app = Flask(__name__)
CORS(app)

BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'frontend'))
HTML_FOLDER = os.path.join(BASE_DIR, 'html')
CSS_FOLDER = os.path.join(BASE_DIR, 'css')
JS_FOLDER = os.path.join(BASE_DIR, 'js')

column_types = {}
stored_rows = []
predictions_db = []

@app.route("/api/file/header", methods=["POST"])
def set_column_types():
    global column_types
    data = request.get_json()
    column_types = data.get("types", {})
    stored_rows.clear()
    return "Header recibido", 200

@app.route("/api/file/chunk", methods=["POST"])
def receive_chunk():
    data = request.get_json()
    rows = data.get("rows", [])
    stored_rows.extend(rows)
    return "Chunk recibido", 200

@app.route("/api/prediction", methods=["POST"])
def make_predictions():
    data = request.get_json()
    for item in data:
        features = item.get("features", {})
        task = item.get("task")
        target_col = item.get("target")
        username = item.get("userName")
        file_name = item.get("fileName")
        name = item.get("name")

        train_data = []
        target_data = []

        encoders = {}
        feature_types = [column_types.get(k, "Categórica") for k in features.keys()]

        for row in stored_rows:
            if all(k in row for k in features.keys()) and target_col in row and row[target_col] != "":
                try:
                    train_row = []
                    for k, f_type in zip(features.keys(), feature_types):
                        val = row[k]
                        if task == "clasificacion":
                            val_conv = convert_value(val, column_types.get(k, "Categórica"))
                        else:
                            if f_type == "Categórica":
                                if k not in encoders:
                                    encoders[k] = LabelEncoder()
                                    col_vals = [r[k] for r in stored_rows if k in r and r[k] != ""]
                                    encoders[k].fit(col_vals)
                                val_conv = encoders[k].transform([val])[0]
                            else:
                                val_conv = convert_value(val, "Continua")
                        train_row.append(val_conv)
                    train_data.append(train_row)

                    if task == "clasificacion":
                        target_data.append(convert_value(row[target_col], column_types.get(target_col, "Categórica")))
                    else:
                        target_data.append(float(row[target_col]))

                except Exception:
                    continue

        if not train_data:
            print(f"{name}: No hay datos válidos para entrenar.")
            continue

        X = np.array(train_data)

        if task == "clasificacion":
            y_raw = target_data
            le = LabelEncoder()
            y = le.fit_transform(y_raw)
            model = RandomForestClassifier()
            model.fit(X, y)
            sample = [convert_value(features[k], column_types.get(k, "Categórica")) for k in features.keys()]
            pred_encoded = model.predict([sample])[0]
            pred = le.inverse_transform([pred_encoded])[0]

            y_pred = model.predict(X)
            accuracy = accuracy_score(y, y_pred)
            print(f"Precisión para {name}: {accuracy:.2f}")

            tree_rules = export_text(model.estimators_[0], feature_names=list(features.keys()))
            print(f"Árbol de decisión:\n{tree_rules}")

        else:
            y = np.array(target_data, dtype=float)
            model = RandomForestRegressor()
            model.fit(X, y)

            sample = []
            for k, f_type in zip(features.keys(), feature_types):
                val = features[k]
                if f_type == "Categórica":
                    val_conv = encoders[k].transform([val])[0]
                else:
                    val_conv = convert_value(val, "Continua")
                sample.append(val_conv)

            pred = model.predict([sample])[0]
            pred = round(float(pred), 3)

            r2 = model.score(X, y)
            print(f"R^2 para {name}: {r2:.3f}")

            tree_rules = export_text(model.estimators_[0], feature_names=list(features.keys()))
            print(f"Árbol de decisión:\n{tree_rules}")

        print(f"Predicción para {name}: {pred}")

    return "", 204

@app.route("/")
def serve_index():
    return send_from_directory(HTML_FOLDER, "index.html")

@app.route("/css/<path:path>")
def serve_css(path):
    return send_from_directory(CSS_FOLDER, path)

@app.route("/js/<path:path>")
def serve_js(path):
    return send_from_directory(JS_FOLDER, path)

def convert_value(value, col_type):
    if col_type == "Continua":
        return float(value)
    else:
        return str(value)

if __name__ == "__main__":
    app.run(debug=True)
