<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <div class="login-box">
        <h2>Iniciar sesión</h2>
        <form action="LoginServlet" method="post">
            <label for="usuario">Usuario</label>
            <input type="text" name="usuario" required>

            <label for="contrasena">Contraseña</label>
            <input type="password" name="contrasena" required>

            <button type="submit">Entrar</button>
        </form>

        <% if (request.getParameter("error") != null) { %>
            <p class="error">Usuario o contraseña incorrectos</p>
        <% } %>
    </div>
</body>
</html>
