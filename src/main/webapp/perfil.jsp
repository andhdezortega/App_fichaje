<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%@ page import="java.time.LocalDateTime"%>
<%@ page import="java.time.Duration"%>
<%@ page import="com.mycompany.controlfichaje.dao.FichajeDAO"%>
<%@ page import="com.mycompany.controlfichaje.FichajeMock"%>
<%@ page import="java.util.List"%>

<%
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    // Obtener nombre y apellido del usuario
    String[] partes = usuario.split(" ");
    String nombre = partes[0];
    String apellido = partes.length > 1 ? partes[1] : "";
    LocalDateTime horaEntrada = (LocalDateTime) session.getAttribute("horaEntrada");
    LocalDateTime horaSalida = (LocalDateTime) session.getAttribute("horaSalida");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    String entradaFormateada = (horaEntrada != null) ? horaEntrada.format(formatter) : "Sin registrar";
    boolean fichajeSalida = (horaEntrada != null && horaSalida == null);
    String estadoUsuario = (String) session.getAttribute("estadoUsuario");
    if (estadoUsuario == null || estadoUsuario.isEmpty()) {
        estadoUsuario = "produccion";
        session.setAttribute("estadoUsuario", estadoUsuario);
    }
    String claseEstado = "estado-msg";
    if ("produccion".equals(estadoUsuario)) {
        claseEstado += " estado-produccion";
    } else {
        claseEstado += " estado-no-produccion";
    }
    String estadoBarra = "";
    if ("break".equals(estadoUsuario)) {
        estadoBarra = "Break";
    } else if ("comida".equals(estadoUsuario)) {
        estadoBarra = "Comida";
    } else if ("asuntos_propios".equals(estadoUsuario)) {
        estadoBarra = "Asuntos propios";
    } else if ("produccion".equals(estadoUsuario)) {
        estadoBarra = "En producción";
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><%= estadoBarra %></title>
    <link rel="stylesheet" href="css/styles.css">
    <link rel="stylesheet" href="css/fichajes.css">
    <link rel="icon" type="image/x-icon" href="favicon.ico">
    <link rel="icon" href="<%=request.getContextPath()%>/favicon.ico" type="image/x-icon">
</head>
<body>
    <div class="fichaje-container">
        <div class="fichaje-box">
            <h1>Perfil de <%= usuario %></h1>

            <p><strong>Última entrada:</strong> <%= entradaFormateada %></p>
            
            <% 
            // Calcular tiempo transcurrido
            String tiempoTranscurrido = "";
            if (horaEntrada != null) {
                Duration duracion;
                if (horaSalida != null) {
                    duracion = Duration.between(horaEntrada, horaSalida);
                } else {
                    duracion = Duration.between(horaEntrada, LocalDateTime.now());
                }
                long horas = duracion.toHours();
                long minutos = duracion.toMinutesPart();
                tiempoTranscurrido = String.format("%d horas y %d minutos", horas, minutos);
            }
            if (!tiempoTranscurrido.isEmpty()) { 
            %>
                <p><strong>Tiempo transcurrido:</strong> <%= tiempoTranscurrido %></p>
            <% } %>

            <% if (estadoUsuario != null && !estadoUsuario.isEmpty()) { %>
                <p class="<%= claseEstado %>"><strong>Tu estado es:&nbsp;</strong><%= estadoBarra %></p>
            <% } %>

            <form action="ActualizarEstadoServlet" method="post" style="margin-bottom: 20px;">
                <select name="estado" id="estado" required>
                    <option value="produccion" <%= "produccion".equals(estadoUsuario) ? "selected" : "" %>>En producción</option>
                    <option value="break" <%= "break".equals(estadoUsuario) ? "selected" : "" %>>Break</option>
                    <option value="comida" <%= "comida".equals(estadoUsuario) ? "selected" : "" %>>Comida</option>
                    <option value="asuntos_propios" <%= "asuntos_propios".equals(estadoUsuario) ? "selected" : "" %>>Asuntos propios</option>
                </select>
                <button type="submit">Actualizar estado</button>
            </form>
                
            <!-- Enviar la acción de salida al servlet de fichaje para que registre la hora de salida -->
            <form action="FichajeServlet" method="post">
                <input type="hidden" name="accion" value="salida">
                <input type="hidden" name="nombre" value="<%= nombre %>">
                <input type="hidden" name="apellido" value="<%= apellido %>">
                <input type="hidden" name="descanso" value="<%= "break".equals(estadoUsuario) ? 1 : 0 %>">
                <input type="hidden" name="comida" value="<%= "comida".equals(estadoUsuario) ? 1 : 0 %>">
                <button type="submit" class="btn-rojo" <%= (!fichajeSalida) ? "disabled" : "" %>>Fichar Salida</button>
            </form>
            <form action="LogoutServlet" method="post" style="margin-top: 10px;">
                <button class="cerrar-btn" type="submit">Cerrar sesión</button>
            </form>
        </div>
    </div>
</body>
</html>

