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

            <form action="bienvenido.jsp" method="get">
                <button type="submit">Volver al menú</button>
            </form>
        </div>
    </div>
</body>
</html>
