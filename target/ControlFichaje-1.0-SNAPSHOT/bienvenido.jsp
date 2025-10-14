<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Boolean fichajeEntrada = (Boolean) session.getAttribute("fichajeEntrada");
    Boolean fichajeSalida = (Boolean) session.getAttribute("fichajeSalida");

    if (fichajeEntrada == null) fichajeEntrada = false;
    if (fichajeSalida == null) fichajeSalida = false;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Bienvenido</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <div class="fichaje-container">
        <div class="fichaje-box">
            <h1>Hola, <%= usuario %></h1>
            <p>¿Qué deseas hacer?</p>

            <form action="FichajeServlet" method="post">
                <button type="submit" name="accion" value="entrada" <%= fichajeEntrada ? "disabled" : "" %>>Fichar Entrada</button>
                <button type="submit" name="accion" value="salida" <%= (!fichajeEntrada || fichajeSalida) ? "disabled" : "" %>>Fichar Salida</button>
            </form>

            <form action="LogoutServlet" method="post" style="margin-top: 20px;">
                <button class="cerrar-btn" type="submit">Cerrar sesión</button>
            </form>
            

            </form>

        </div>
    </div>
</body>
</html>
