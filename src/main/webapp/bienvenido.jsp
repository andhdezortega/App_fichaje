<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Boolean fichajeEntrada = (Boolean) session.getAttribute("fichajeEntrada");
    if (fichajeEntrada == null) fichajeEntrada = false;

    // Si hay fichaje activo en sesión, redirigir a perfil.jsp directamente
    if (fichajeEntrada) {
        response.sendRedirect("perfil.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Bienvenido</title>
    <link rel="stylesheet" href="css/styles.css">
    <link rel="icon" type="image/x-icon" href="favicon.ico">
</head>
<body>
    <div class="fichaje-container">
        <div class="fichaje-box">
            <h1>Hola, <%= usuario %></h1>
            <p>Te damos la bienvenida al menú</p>

            <form action="FichajeServlet" method="post">
                <input type="hidden" name="returnTo" value="perfil.jsp">
                <button type="submit" name="accion" value="entrada" <%= fichajeEntrada ? "disabled" : "" %>>Fichar Entrada</button>
            </form>

            <form action="LogoutServlet" method="post" style="margin-top: 10px;">
                <button class="cerrar-btn" type="submit">Cerrar sesión</button>
            </form>
        </div>
    </div>
</body>
</html>
