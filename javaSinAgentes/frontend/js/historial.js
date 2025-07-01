document.addEventListener("DOMContentLoaded", async () => {
  const predictionBtn = document.getElementById("predictionBtn");
  const username = sessionStorage.getItem("username");
  const logoutBtn = document.getElementById("logoutBtn");

  if (!username) return;

  try {
    const response = await fetch(`/api/prediction/${encodeURIComponent(username)}`, {
      method: "GET"
    });

    if (!response.ok) throw new Error("Error al obtener historial");

    const data = await response.json();
    renderHistorial(data);
  } catch (err) {
    console.error(err);
  }

  function goToPrediction() {
    window.location.href = "/predictions";
  }

  function logout() {
    sessionStorage.clear();
    window.location.href = "/";
  }

  predictionBtn.addEventListener("click", goToPrediction);
  logoutBtn.addEventListener("click", logout);
});

function renderHistorial(predictions) {
  const container = document.getElementById("historialDiv");
  container.style.display = "block"; 

  const grouped = {};

  predictions.forEach(p => {
    if (!grouped[p.fileName]) grouped[p.fileName] = [];
    grouped[p.fileName].push(p);
  });

  for (const [fileName, predList] of Object.entries(grouped)) {
    const fileSection = document.createElement("div");
    fileSection.className = "file-section";

    const title = document.createElement("h2");
    title.textContent = `Predicciones: ${fileName}`;
    fileSection.appendChild(title);

    predList.forEach((pred, i) => {
      const sampleIndex = i + 1;
      const parameters = JSON.parse(pred.parameters);
      const result = JSON.parse(pred.result);

      const allCols = [...new Set([...Object.keys(parameters), ...Object.keys(result)])];

      let html = `<div class="sample-row"><strong>Predicción ${sampleIndex} (${pred.type}):</strong><div class="sample-grid">`;

      allCols.forEach(col => {
        const isPredicted = col in result;
        const value = isPredicted ? result[col] : parameters[col];

        html += `
          <div class="sample-cell">
            <label>${col}:</label>
            <div style="${isPredicted ? 'color:red' : ''}">
              ${value}
            </div>
          </div>`;
      });

      html += `
          <div class="sample-cell">
            <label>Accuracy:</label>
            <div style="color:blue;">
              ${pred.accuracy || 'N/A'}
            </div>
          </div>`;

      html += `</div></div>`;
      fileSection.innerHTML += html;
    });

    container.appendChild(fileSection);
  }
}
