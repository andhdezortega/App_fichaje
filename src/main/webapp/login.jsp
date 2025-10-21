<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión</title>
    <link rel="stylesheet" href="css/styles.css">
    <link rel="icon" type="image/x-icon" href="favicon.ico">
</head>

<body>
    <div class ="login-container">
    <div class="login-box">
        <h2>Iniciar sesión</h2>
        <form action="<%= request.getContextPath() %>/LoginServlet" method="post">


            <label for="correo">Correo electrónico</label>
            <input type="text" name="correo" required autocomplete="username" style="width:100%;padding:10px;margin-bottom:20px;border:1px solid #cccccc;border-radius:5px;font-size:14px;">

            <label for="contrasena">Contraseña</label>
            <input type="password" name="contrasena" required>

            <button type="submit">Entrar</button>
        </form>

        <% if (request.getParameter("error") != null) { %>
            <p class="error">Usuario o contraseña incorrectos</p>
        <% } %>
    </div>
  </div>
</body>
</html>
