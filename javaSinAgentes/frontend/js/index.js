$(document).ready(function () {
    function renderLogin() {
        const loginHTML = `
            <form id="login-form">
                <label for="username">Nombre de usuario:</label><br>
                <input type="text" id="username" required>

                <label for="password">Contraseña:</label><br>
                <div class="input-group">
                    <input type="password" id="password" required>
                    <i class="bx bx-show-alt toggle-password" toggle="#password"></i>
                </div>

                <button type="submit">Iniciar sesión</button>
                <p id="invalid-credentials" style="color:red; display:none;"></p>
            </form>
            <div class="div-session-info">
                <a id="create-account">Registrar</a>
            </div>
        `;
        $('.login-box').html(loginHTML);
        $('#login-form').on('submit', handleLoginSubmit);
    }

    function renderRegister() {
        const registerHTML = `
            <form id="register-form">
                <label for="username">Nombre de usuario:</label><br>
                <input type="text" id="username" required>

                <label for="password">Contraseña:</label><br>
                <div class="input-group">
                    <input type="password" id="password" required>
                    <i class="bx bx-show-alt toggle-password" toggle="#password"></i>
                </div>

                <label for="repetir">Repetir contraseña:</label><br>
                <div class="input-group">
                    <input type="password" id="repetir" required>
                    <i class="bx bx-show-alt toggle-password" toggle="#repetir"></i>
                </div>

                <button type="submit">Registrar</button>
                <p id="register-error" style="color:red; display:none;"></p>
            </form>
            <div class="div-session-info">
                <a id="sign-in">Iniciar sesión</a>
            </div>
        `;
        $('.login-box').html(registerHTML);
        $('#register-form').on('submit', handleRegisterSubmit);
    }

    renderLogin();

    $(document).on('click', '#create-account', function () {
        renderRegister();
    });

    $(document).on('click', '#sign-in', function () {
        renderLogin();
    });

    $(document).on('click', '.toggle-password', function () {
        const input = $($(this).attr("toggle"));
        const type = input.attr("type") === "password" ? "text" : "password";
        input.attr("type", type);
        $(this).toggleClass("bx-show-alt bx-hide");
    });
});

async function hashPassword(password) {
    const encoder = new TextEncoder();
    const data = encoder.encode(password);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
    return hashHex;
}

async function handleLoginSubmit(e) {
    e.preventDefault();
    const username = document.querySelector("#username").value;
    const password = document.querySelector("#password").value;
    const hashedPassword = await hashPassword(password);

    const errorElement = document.querySelector("#invalid-credentials");

    fetch("/api/user/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, password: hashedPassword })
    })
    .then(res => {
        if (res.ok) {
            sessionStorage.setItem("username", username);
            window.location.href = "/predictions";
        } else {
            if (res.status === 401) {
                errorElement.innerText = "Nombre de usuario o constraseña incorrecta";
                errorElement.style.display = "block";
            }
        }
    })
    .catch(console.error);
}

async function handleRegisterSubmit(e) {
    e.preventDefault();
    const username = document.querySelector("#username").value;
    const password = document.querySelector("#password").value;
    const repeat = document.querySelector("#repetir").value;

    const errorElement = document.querySelector("#register-error");

    if (password !== repeat) {
        errorElement.innerText = "Las contraseñas no coinciden";
        errorElement.style.display = "block";
        return;
    }

    const hashedPassword = await hashPassword(password);

    fetch("/api/user/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, password: hashedPassword })
    })
    .then(res => {
        if (res.status === 409) {
            errorElement.innerText = "Nombre de usuario ya está en uso";
            errorElement.style.display = "block";
            throw new Error("Usuario ya existe");
        }

        if (res.ok) {
            sessionStorage.setItem("username", username);
            window.location.href = "/predictions";
        } else {
            throw new Error("Error desconocido en el registro");
        }
    })
    .catch(console.error);
}
