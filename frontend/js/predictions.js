document.addEventListener("DOMContentLoaded", () => {
  const userNameSpan = document.getElementById("userName");
  const csvInput = document.getElementById("csvInput");
  const columnPreview = document.getElementById("columnPreview");
  const historyBtn = document.getElementById("historyBtn");
  const logoutBtn = document.getElementById("logoutBtn");
  const samplesDiv = document.getElementById("samplesDiv");

  var targetCol
  var selectedColumns;
  var allColumns;

  var uploadedCSVFile = null;
  var previousCount = 0;

  const username = sessionStorage.getItem("username");
  if (!username) {
    window.location.href = "/";
    return;
  }
  userNameSpan.textContent = username;

  function goToHistory() {
    window.location.href = "/historial";
  }

  function logout() {
    sessionStorage.clear();
    window.location.href = "/";
  }

  historyBtn.addEventListener("click", goToHistory);
  logoutBtn.addEventListener("click", logout);

  const initPreviousCount = async () => {
    try {
      const response = await fetch(`/api/prediction/${encodeURIComponent(username)}`, {
        method: "GET"
      });
      if (!response.ok) throw new Error("Error");
      const data = await response.json();
      previousCount = data.length;
    } catch (err) {
      console.error("Error", err);
    }
  };

  initPreviousCount();

  csvInput.addEventListener("change", e => {
    document.getElementById("welcomeTitle").style.display = "none";
    const file = e.target.files[0];
    if (!file) return;
    uploadedCSVFile = file;
    processCSV(file);
  });

  function processCSV(file) {
    let headers = [];
    let firstRow = null;

    Papa.parse(file, {
      header: true,
      skipEmptyLines: true,
      step: function(results, parser) {
        const row = results.data;
        if (!firstRow) {
          headers = Object.keys(row);
          allHeaders = [...headers];
          firstRow = row;
          parser.abort();
        }
      },
      complete: function() {
        const types = headers.map(h => {
          const value = firstRow[h];
          const num = parseFloat(value);
          return !isNaN(num) && isFinite(num) ? "Continua" : "Categórica";
        });

        document.getElementById("csvNote").style.display = "inline-block";
        renderColumnTable(headers, types);
        updateSamplesDiv();
      }
    });
  }

  function renderColumnTable(headers, types) {
    let html = "<table><thead><tr>";
    for (const h of headers) {
      html += `<th>${h}</th>`;
    }
    html += "</tr></thead><tbody><tr>";
    for (let i = 0; i < types.length; i++) {
      html += `<td>
       <input type="checkbox" data-checkbox-index="${i}" checked />
        <select data-index="${i}">
          <option value='Categórica' ${types[i] === "Categórica" ? "selected" : ""}>Categórica</option>
          <option value='Continua' ${types[i] === "Continua" ? "selected" : ""}>Continua</option>
        </select>
      </td>`;
    }
    html += "</tr></tbody></table>";
    columnPreview.innerHTML = html;
  }

  function getCheckedColumns() {
    const checkboxes = columnPreview.querySelectorAll("input[type='checkbox']");
    let columns = [];
    checkboxes.forEach((cb, i) => {
      if (cb.checked) {
        const header = columnPreview.querySelectorAll("table thead th")[i].textContent;
        columns.push(header);
      }
    });
    return columns;
  }

  function generateSampleInputs(sampleIndex) {
    const checkedColumns = getCheckedColumns();
    targetCol = document.getElementById("targetColumn").value;
    const filteredColumns = checkedColumns.filter(col => col !== targetCol);

    let html = `<div class="sample-row"><strong>Muestra ${sampleIndex}:</strong><div class="sample-grid">`;
    filteredColumns.forEach(col => {
      html += `
        <div class="sample-cell">
          <label>${col}:</label>
          <input type="text" name="sample${sampleIndex}-${col}" placeholder="Rellenar" />
        </div>`;
    });
    html += `</div></div>`;
    return html;
  }

  function updateSampleRows(count) {
    const container = document.getElementById("samplesContainer");
    let html = "";
    for (let i = 1; i <= count; i++) {
      html += generateSampleInputs(i);
    }
    container.innerHTML = html;
    attachInputListeners();
    checkFormValidity();
  }

  function updateSamplesDiv() {
    samplesDiv.style.display = "block";
    let html = `<div>
        <label for="numPredictions">Número de predicciones:</label>
        <input type="number" id="numPredictions" min="1" value="1" />
        <label for="targetColumn">Columna a predecir:</label>
        <select id="targetColumn">
          ${getCheckedColumns().map(col => `<option value="${col}">${col}</option>`).join('')}
        </select>
        <label for="functionType">Función:</label>
        <select id="functionType">
          <option value="clasificacion">Clasificación</option>
          <option value="regresion">Regresión</option>
        </select>
      </div>`;
    html += `<div id="samplesContainer"></div>`;
    html += `<div id="predictBtnContainer" style="text-align: center; margin-top: 20px;"></div>`;
    samplesDiv.innerHTML = html;

    updateSampleRows(1);

    document.getElementById("numPredictions").addEventListener("input", e => {
      let n = parseInt(e.target.value);
      if (isNaN(n) || n < 1) {
        n = 1;
        e.target.value = 1;
      }
      updateSampleRows(n);
    });

    document.getElementById("targetColumn").addEventListener("change", () => {
      const n = parseInt(document.getElementById("numPredictions").value) || 1;
      updateSampleRows(n);
    });
  }

  function attachInputListeners() {
    document.querySelectorAll("#samplesContainer input").forEach(input => {
      input.addEventListener("input", checkFormValidity);
    });
  }

  function checkFormValidity() {
    const checkedColumns = getCheckedColumns();
    targetCol = document.getElementById("targetColumn")?.value;
    const filtered = checkedColumns.filter(col => col !== targetCol);
    const inputs = document.querySelectorAll("#samplesContainer input");
    const allFilled = Array.from(inputs).every(input => input.value.trim() !== "");

    const predictContainer = document.getElementById("predictBtnContainer");
    predictContainer.innerHTML = "";

    if (filtered.length === 0 || !allFilled) return;

    const btn = document.createElement("button");
    btn.textContent = "Predecir muestras";
    btn.classList.add("predict-button");
    predictContainer.appendChild(btn);

    btn.addEventListener("click", () => {
      btn.disabled = true;

      const selects = columnPreview.querySelectorAll("select");
      const checkboxes = columnPreview.querySelectorAll("input[type='checkbox']");
      const columnTypes = {};

      selects.forEach((sel, i) => {
        if (checkboxes[i].checked) {
          const colName = columnPreview.querySelectorAll("thead th")[i].textContent;
          columnTypes[colName] = sel.value;
        }
      });

      fetch("/api/file/header", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ types: columnTypes }),
      })
        .then(res => res.ok ? res.text() : res.text().then(t => { throw new Error(t); }))
        .then(async data => {
          targetCol = document.getElementById("targetColumn").value;
          selectedColumns = Object.keys(columnTypes);
          allColumns = selectedColumns.includes(targetCol) ? selectedColumns : [...selectedColumns, targetCol];

          const buffer = [];
          let headers = [];
          let leftover = "";

          const sendChunk = async (chunk) => {
            try {
              const res = await fetch("/api/file/chunk", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ rows: chunk })
              });
              if (!res.ok) throw new Error(await res.text());
            } catch (err) {
              console.error("Error enviando chunk:", err.message);
            }
          };

          const reader = uploadedCSVFile.stream()
            .pipeThrough(new TextDecoderStream())
            .getReader();

          const process = async () => {
            while (true) {
              const { value, done } = await reader.read();
              if (done) break;

              const lines = (leftover + value).split(/\r?\n/);
              leftover = lines.pop();

              for (const line of lines) {
                if (!headers.length) {
                  headers = line.split(",").map(h => h.trim());
                  continue;
                }

                const values = line.split(",").map(v => v.trim());
                if (values.length !== headers.length) continue;

                const row = {};
                headers.forEach((col, idx) => {
                  if (allColumns.includes(col)) {
                    row[col] = values[idx];
                  }
                });

                buffer.push(row);
                if (buffer.length === 10) {
                  await sendChunk(buffer.slice());
                  buffer.length = 0;
                }
              }
            }

            if (leftover.trim() !== "") {
              const values = leftover.split(",");
              if (values.length === headers.length) {
                const row = {};
                headers.forEach((col, idx) => {
                  if (allColumns.includes(col)) {
                    row[col] = (col === targetCol) ? "" : values[idx];
                  }
                });
                buffer.push(row);
              }
            }

            if (buffer.length > 0) {
              await sendChunk(buffer);
            }
          };

          await process();

          const predictions = [];
          const numSamples = parseInt(document.getElementById("numPredictions").value);
          const task = document.getElementById("functionType").value;

          for (let i = 1; i <= numSamples; i++) {
            const features = {};
            let predictedCol = "";
            const predictedLabel = `Muestra ${i}`;

            for (const col of allColumns) {
              const input = document.querySelector(`input[name="sample${i}-${col}"]`);
              const value = input?.value?.trim() ?? "";

              if (value === "") {
                predictedCol = col;
              } else {
                features[col] = value;
              }
            }

            predictions.push({
              name: predictedLabel,
              target: predictedCol,
              task: task,
              userName: username,
              fileName: uploadedCSVFile.name,
              features: features
            });
          }
          try {
            const res = await fetch("/api/prediction", {
              method: "POST",
              headers: {
                "Content-Type": "application/json"
              },
              body: JSON.stringify(predictions)
            });

            if (!res.ok) throw new Error("Error al predecir");
            const intervalId = setInterval(async () => {
              try {
                const response = await fetch(`/api/prediction/${encodeURIComponent(username)}`, {
                  method: "GET"
                });
                if (!response.ok) throw new Error("Error");

                const data = await response.json();
                if (data.length !== previousCount) {
                  clearInterval(intervalId);
                  window.location.href = "/historial";
                }
              } catch (pollErr) {
                console.error("Error:", pollErr);
              }
            }, 1000);
          } catch (err) {
            console.error("Error en la predicción:", err);
          }
        })
        .catch(err => alert("Error: " + err.message));
      
    });

  }


  columnPreview.addEventListener("change", e => {
    if (e.target.type === "checkbox") {
      if (samplesDiv.style.display !== "none") {
        const numPred = parseInt(document.getElementById("numPredictions").value) || 1;
        updateSamplesDiv();
        updateSampleRows(numPred);
      }
    }
  });
});
