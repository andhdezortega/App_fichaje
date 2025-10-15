<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%@ page import="java.time.LocalDateTime"%>

<%
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    LocalDateTime horaEntrada = (LocalDateTime) session.getAttribute("horaEntrada");
    LocalDateTime horaSalida = (LocalDateTime) session.getAttribute("horaSalida");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    String entradaFormateada = (horaEntrada != null) ? horaEntrada.format(formatter) : "Sin registrar";
    String salidaFormateada = (horaSalida != null) ? horaSalida.format(formatter) : "Sin registrar";

    String estadoUsuario = (String) session.getAttribute("estadoUsuario");

    String estadoBarra = "";
    if ("break".equals(estadoUsuario)) {
        estadoBarra = "Break";
    } else if ("comida".equals(estadoUsuario)) {
        estadoBarra = "Comida";
    } else if ("asuntos_propios".equals(estadoUsuario)) {
        estadoBarra = "Asuntos propios";
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Perfil de Usuario</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <div class="fichaje-container">
        <div class="fichaje-box">
            <h1>Perfil de <%= usuario %></h1>

            <p><strong>Última entrada:</strong> <%= entradaFormateada %></p>
            <p><strong>Última salida:</strong> <%= salidaFormateada %></p>

            <% if (estadoUsuario != null && !estadoUsuario.isEmpty()) { %>
                <p class="estado-msg"><strong>Tu estado es:</strong> <%= estadoBarra %></p>
            <% } %>

            <form action="ActualizarEstadoServlet" method="post" style="margin-bottom: 20px;">
               
                <select name="estado" id="estado" required>
                    <option value="" <%= (estadoUsuario == null || estadoUsuario.isEmpty()) ? "selected" : "" %>>Seleccionar estado</option>
                    <option value="break" <%= "Break".equals(estadoUsuario) ? "selected" : "" %>>Break</option>
                    <option value="comida" <%= "Comida".equals(estadoUsuario) ? "selected" : "" %>>Comida</option>
                    <option value="asuntos_propios" <%= "Asuntos propios".equals(estadoUsuario) ? "selected" : "" %>>Asuntos propios</option>
                </select>
                <button type="submit">Actualizar estado</button>
            </form>

            <form action="bienvenido.jsp" method="get">
                <button type="submit">Volver al menú</button>
            </form>
        </div>
    </div>
</body>
</html>
