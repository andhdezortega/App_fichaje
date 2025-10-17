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
    LocalDateTime inicioProduccion = (LocalDateTime) session.getAttribute("inicioProduccion");

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

   
String estadoBarra;
switch (estadoUsuario) {
    case "break":
        estadoBarra = "Break";
        break;
    case "comida":
        estadoBarra = "Comida";
        break;
    case "asuntos_propios":
        estadoBarra = "Asuntos propios";
        break;
    case "produccion":
        estadoBarra = "En producción";
        break;
    default:
        estadoBarra = "";
}
%>

    


<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><%= estadoBarra %></title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

    <link rel="stylesheet" href="css/styles.css">
    <link rel="icon" href="<%=request.getContextPath()%>/favicon.ico" type="image/x-icon">
</head>

<body class="bg-light">
    <div class="container d-flex justify-content-center align-items-center min-vh-100">
        <div class="card shadow-sm w-100" style="max-width: 700px;">
            <div class="card-body">
                
                <h1 class="mb-4">Perfil de <%= usuario %></h1>

                <p><strong>Última entrada:</strong> <%= entradaFormateada %></p>

                <% if (estadoUsuario != null && !estadoUsuario.isEmpty()) { %>
                    <p class="<%= claseEstado %>">
                        <strong>Tu estado es:&nbsp;</strong><%= estadoBarra %>
                    </p>
                <% } %>

                <% if ("produccion".equals(estadoUsuario) && inicioProduccion != null) { %>
                    <div class="mt-4">
                        <p><strong>Tiempo en producción:</strong> <span id="contador-produccion">00:00:00</span></p>

                        <!-- Barra de progreso con Bootstrap -->
                        <div class="progress">
                            <div id="barra-produccion" class="progress-bar bg-success progress-bar-striped" role="progressbar"
                                 style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
                                0%
                            </div>
                        </div>
                    </div>

                    <script>
                        const inicioProduccion = new Date("<%= inicioProduccion.toString() %>");
                        const jornadaMs = 8 * 60 * 60 * 1000; // 8 horas de jornada

                        function actualizarContadorYBarra() {
                            const ahora = new Date();
                            const diffMs = ahora - inicioProduccion;
                            const horas = Math.floor(diffMs / 3600000);
                            const minutos = Math.floor((diffMs % 3600000) / 60000);
                            const segundos = Math.floor((diffMs % 60000) / 1000);

                            // Mostrar tiempo
                            document.getElementById('contador-produccion').textContent =
                                `${horas.toString().padStart(2, '0')}h ${minutos.toString().padStart(2, '0')}m ${segundos.toString().padStart(2, '0')}s`;

                            // Calcular porcentaje
                            let porcentaje = (diffMs / jornadaMs) * 100;
                            porcentaje = Math.min(porcentaje, 100); // No pasar de 100%

                            // Actualizar barra
                            const barra = document.getElementById('barra-produccion');
                            barra.style.width = porcentaje + '%';
                            barra.textContent = Math.floor(porcentaje) + '%';
                            barra.setAttribute('aria-valuenow', Math.floor(porcentaje));
                        }

                        setInterval(actualizarContadorYBarra, 1000);
                        actualizarContadorYBarra();
                    </script>
                <% } %>

                <hr class="my-4"/>

                <!-- Formulario para cambiar estado -->
                <form action="ActualizarEstadoServlet" method="post" class="mb-3">
                    <div class="row g-2 align-items-center">
                        <div class="col-md-4">
                            <select name="estado" id="estado" class="form-select" required>
                                <option value="produccion" <%= "produccion".equals(estadoUsuario) ? "selected" : "" %>>En producción</option>
                                <option value="break" <%= "break".equals(estadoUsuario) ? "selected" : "" %>>Break</option>
                                <option value="comida" <%= "comida".equals(estadoUsuario) ? "selected" : "" %>>Comida</option>
                                <option value="asuntos_propios" <%= "asuntos_propios".equals(estadoUsuario) ? "selected" : "" %>>Asuntos propios</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <button type="submit" class="btn btn-primary">Actualizar estado</button>
                        </div>
                    </div>
                </form>

                <!-- Formulario para fichar salida -->
                <form action="FichajeServlet" method="post">
                    <input type="hidden" name="accion" value="salida">
                        <input type="hidden" name="returnTo" value="bienvenido.jsp">
                        <button type="submit" class="btn btn-danger" <%= (!fichajeSalida) ? "disabled" : "" %>>Fichar Salida</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
