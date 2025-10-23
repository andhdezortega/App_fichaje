<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%@ page import="java.time.LocalDateTime"%>
<%@ page import="java.time.Duration"%>
<%@ page import="com.mycompany.controlfichaje.dao.*" %>

<%!
    // Método auxiliar para capitalizar nombres de estados
    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
%>

<%
    // Validar sesión de usuario
    String usuario = (String) session.getAttribute("usuario");
    String correo = (String) session.getAttribute("correo");
    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Obtener datos del usuario
    Usuario usuarioObj = UsuarioDAO.obtenerUsuarioPorCorreo(correo);
    String apellido = usuarioObj != null && usuarioObj.getApellido() != null ? usuarioObj.getApellido() : "";

    Boolean fichajeEntrada = (Boolean) session.getAttribute("fichajeEntrada");
    if (fichajeEntrada == null || !fichajeEntrada) {
        response.sendRedirect("bienvenido.jsp");
        return;
    }

    LocalDateTime horaEntrada = (LocalDateTime) session.getAttribute("horaEntrada");
    LocalDateTime horaSalida = (LocalDateTime) session.getAttribute("horaSalida");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    String entradaFormateada = horaEntrada != null ? horaEntrada.format(formatter) : "Sin registrar";

    boolean fichajeSalida = horaEntrada != null && horaSalida == null;

    // Estado actual del usuario
    String estadoUsuario = (String) session.getAttribute("estadoUsuario");
    if (estadoUsuario == null || estadoUsuario.isEmpty()) {
        estadoUsuario = "produccion";
        session.setAttribute("estadoUsuario", estadoUsuario);
    }

    String claseEstado = "estado-msg " + ("produccion".equals(estadoUsuario) ? "estado-produccion" : "estado-no-produccion");

    String estado;
    switch (estadoUsuario) {
        case "break": estado = "Break"; break;
        case "comida": estado = "Comida"; break;
        case "asuntos_propios": estado = "Asuntos propios"; break;
        case "produccion": estado = "En producción"; break;
        default: estado = ""; break;
    }

    // Inicializar estados y minutos acumulados
    String[] estados = {"produccion", "break", "comida", "asuntos_propios"};
    for (String est : estados) {
        String estCap = capitalize(est);
        if (session.getAttribute("inicio" + estCap) == null && est.equals(estadoUsuario)) {
            session.setAttribute("inicio" + estCap, LocalDateTime.now());
        }
        if (session.getAttribute("minutos" + estCap) == null) {
            session.setAttribute("minutos" + estCap, 0L);
        }
    }

    // Calcular minutos acumulados
    long minutosProduccion = 0;
    long minutosNoProduccion = 0;

    for (String est : estados) {
        String estCap = capitalize(est);
        Long minutos = (Long) session.getAttribute("minutos" + estCap);
        minutos = minutos != null ? minutos : 0L;

        LocalDateTime inicio = (LocalDateTime) session.getAttribute("inicio" + estCap);
        if (inicio != null && est.equals(estadoUsuario)) {
            minutos += Duration.between(inicio, LocalDateTime.now()).toMinutes();
        }

        session.setAttribute("minutos" + estCap, minutos);

        if ("produccion".equals(est)) {
            minutosProduccion = minutos;
        } else {
            minutosNoProduccion += minutos;
        }
    }

    // Formato ISO para JS
    LocalDateTime inicioEstado = (LocalDateTime) session.getAttribute("inicio" + capitalize(estadoUsuario));
    String inicioEstadoStr = "";
    if (inicioEstado != null) {
        DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        inicioEstadoStr = inicioEstado.format(isoFormatter);
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><%= estado %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="css/styles.css">
    <link rel="icon" type="image/x-icon" href="favicon.ico">
</head>
<body class="bg-light">
<div class="container d-flex justify-content-center align-items-center min-vh-100">
    <div class="card shadow-sm w-100" style="max-width: 700px;">
        <div class="card-body">
            <h1 class="mb-4">Perfil de <%= usuario %> <%= apellido %></h1>
            <p><strong>Última entrada:</strong> <%= entradaFormateada %></p>

<p class="estado-msg <%= "produccion".equals(estadoUsuario) ? "estado-produccion" : "estado-no-produccion" %>">
    <strong>Tu estado:&nbsp; </strong> <%= estado %>
</p>


            <!-- Contadores -->
            <div class="mt-4">
                <p><strong>Tiempo en producción:</strong>
                    <span id="contador-produccion">
                        <%= String.format("%02dh %02dm %02ds", minutosProduccion / 60, minutosProduccion % 60, 0) %>

                    </span>
                </p>
                <p><strong>Tiempo en no producción:</strong>
                    <span id="contador-no-produccion">

                        <%= String.format("%02dh %02dm %02ds", minutosNoProduccion / 60, minutosNoProduccion % 60, 0) %>
                    </span>
                </p>
            </div>

<!-- Script para actualizar contadores -->
<script>
document.addEventListener("DOMContentLoaded", function() {
    // Variables de sesión
    const minutosProduccionJS = <%= minutosProduccion %>;
    const minutosNoProduccionJS = <%= minutosNoProduccion %>;
    const inicioEstadoStrJS = "<%= inicioEstadoStr %>";
    const estadoActualJS = "<%= estadoUsuario %>";


    function actualizarContadores() {
        if (!inicioEstadoStr) return;

        const inicio = new Date(inicioEstadoStr);
        const ahora = new Date();
        const diffMinutos = Math.floor((ahora - inicio) / 60000);
        const diffSegundos = Math.floor(((ahora - inicio) % 60000) / 1000);

        let h, m, s;

        if (estadoActual === "produccion") {
            let total = minutosProduccion + diffMinutos;
            h = String(Math.floor(total / 60)).padStart(2, '0');
            m = String(total % 60).padStart(2, '0');
            s = String(diffSegundos).padStart(2, '0');
            document.querySelectorAll(".contador-produccion").forEach(el => el.textContent = `${h}h ${m}m ${s}s`);
        } else {
            let total = minutosNoProduccion + diffMinutos;
            h = String(Math.floor(total / 60)).padStart(2, '0');
            m = String(total % 60).padStart(2, '0');
            s = String(diffSegundos).padStart(2, '0');
            document.querySelectorAll(".contador-no-produccion").forEach(el => el.textContent = `${h}h ${m}m ${s}s`);
        }
    }

    actualizarContadores();
    setInterval(actualizarContadores, 1000);
});
</script>



            <hr class="my-4">

            <!-- Formulario cambiar estado -->
            <form action="ActualizarEstadoServlet" method="post" class="mb-3">
                <div class="row g-2 align-items-center">
                    <div class="col-md-4">
                        <select name="estado" class="form-select" required>
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

            <!-- Formulario fichar salida -->
            <form action="FichajeServlet" method="post" class="mb-2">
                <input type="hidden" name="accion" value="salida">
                <input type="hidden" name="returnTo" value="bienvenido.jsp">
                <button type="submit" class="btn btn-danger" <%= !fichajeSalida ? "disabled" : "" %>>Fichar Salida</button>
                </form>

                <!-- Botón de cerrar sesión -->
                <form action="LogoutServlet" method="post">
                    <button type="submit" class="btn btn-secondary">Cerrar sesión</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>