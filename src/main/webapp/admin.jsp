<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%@ page import="java.util.*, java.time.*, java.time.format.DateTimeFormatter" %>
<%@ page import="com.mycompany.controlfichaje.*" %>
<%@ page import="com.mycompany.controlfichaje.dao.FichajeDAO" %>

<%
    String usuario = (String) session.getAttribute("usuario");
    String rol = (String) session.getAttribute("rol");

    if (usuario == null || !"admin".equals(rol)) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    FichajeDAO fichajeDAO = new FichajeDAO();
    List<FichajeModel> fichajes = fichajeDAO.obtenerTodos();

    String paramId = request.getParameter("id");
    FichajeModel fichajeSeleccionado = null;
    if (paramId != null) {
        try {
            fichajeSeleccionado = fichajeDAO.obtenerPorId(Integer.parseInt(paramId));
        } catch (NumberFormatException ex) {
            fichajeSeleccionado = null;
        }
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Panel Administrador</title>
    <link rel="stylesheet" href="css/styles.css">
    <link rel="icon" type="image/x-icon" href="favicon.ico?v=2">
    <meta charset="UTF-8">
    <title>Panel Administrador</title>
    <!-- Enlace al CSS de DataTables -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.12.1/css/jquery.dataTables.min.css">
    
    <!-- Enlace a jQuery (requerido por DataTables) -->
    <script type="text/javascript" charset="utf8" src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <!-- Enlace a JS de DataTables -->
    <script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.12.1/js/jquery.dataTables.min.js"></script>


    <!--Mover ancho columnas-->
    <script src="https://code.jquery.com/jquery-2.2.4.min.js"></script>


    <style>
        .estado-si {
            background-color: #d4edda; 
            color: #155724;
            font-weight: bold;
            text-align: center;
        }
        .estado-no {
            background-color: #fff3cd; 
            color: #856404;
            font-weight: bold;
            text-align: center;
        }
    </style>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sorttable/2.1.2/sorttable.min.js"></script>

</head>
<body>

            
        <div class="top-right-controls">
            <form method="post" action="LogoutServlet">
                <button type="submit" class="cerrar-btn">Cerrar sesión</button>
            </form>
        </div>
<div class="container">

    <!---------------------- BARRA LATERAL----------------------- -->
    <div class="sidebar">
        <h2><%= (fichajeSeleccionado != null) ? "Editar Fichaje" : "Nuevo Fichaje" %></h2>
        
        <form id="formFichaje" method="post" action="<%= (fichajeSeleccionado != null) ? "ActualizarFichaje" : "InsertarFichaje" %>">
            <% if (fichajeSeleccionado != null) { %>
                <input type="hidden" name="id" value="<%= fichajeSeleccionado.getId() %>">
                <p><strong>ID:</strong> <%= fichajeSeleccionado.getId() %></p>
            <% } %>

            <label>Nombre:</label>
            <input type="text" name="nombre" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.getNombre() : "" %>">

            <label>Apellido:</label>
            <input type="text" name="apellido" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.getApellido() : "" %>">

            <label>Rol:</label>
            <input type="text" name="rol" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.getRol() : "" %>">

            <label>Fecha:</label>
            <input type="date" name="fecha" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.getFecha().toString() : "" %>">

            <%
                String entradaVal = "";
                String salidaVal = "";
                if (fichajeSeleccionado != null) {
                    if (fichajeSeleccionado.getEntrada() != null) {
                        String s = fichajeSeleccionado.getEntrada().toString();
                        entradaVal = s.length() >= 5 ? s.substring(0,5) : s;
                    }
                    if (fichajeSeleccionado.getSalida() != null) {
                        String s2 = fichajeSeleccionado.getSalida().toString();
                        salidaVal = s2.length() >= 5 ? s2.substring(0,5) : s2;
                    }
                }
            %>

            <label>Entrada:</label>
            <input type="time" name="entrada" required value="<%= entradaVal %>">

            <label>Salida:</label>
            <input type="time" name="salida" required value="<%= salidaVal %>">

            <label>Descanso (min):</label>
            <input type="number" name="descanso" value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.getDescanso() : "" %>">

            <label>Comida (min):</label>
            <input type="number" name="comida" value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.getComida() : "" %>">

            <label>Horas semanales:</label>
            <input type="number" name="horasSemanales" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.getHorasSemanales() : "" %>">


            <% if (fichajeSeleccionado != null) { %>
                <div class="panel-overtime" style="margin-bottom:16px; padding:12px; background:#1f2a44; border-radius:6px;">
                    <p style="margin:0 0 8px 0;">Horas extra actuales: <strong><%= fichajeSeleccionado.getHorasExtra() %> min</strong></p>
                    <div style="display:flex; gap:8px;">
                        <button type="button" onclick="denegarHorasExtra(<%= fichajeSeleccionado.getId() %>)" class="btn-accion" style="background:#dc3545;">
                            Denegar horas extra (poner a 0)
                        </button>
                    </div>
                </div>
            <% } %>
            <input type="submit" value="<%= (fichajeSeleccionado != null) ? "Actualizar" : "Guardar" %>">
        </form>
    </div>

    <!------------------- PANEL CENTRAL: tabla ------------->
    <div class="main">
        <h1>Control de Fichajes</h1>
        <h2>Administrador</h2>
        <div style="margin-bottom: 20px;">
            <a href="usuarios.jsp" class="boton-link">Gestionar Usuarios</a>
        </div>
        <table id="tablaFichajes" class="admin-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>Rol</th>
                    <th>Fecha</th>
                    <th>Entrada</th>
                    <th>Salida</th>
                    <th>Descanso</th>
                    <th>Comida</th>
                    <th>Horas trabajadas</th>
                    <th>Extra (min)</th>
                    <th>Horas/semana</th>
                    <th>En producción</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
            <% for (FichajeModel f : fichajes) { %>
                <tr>
                    <td><%= f.getId() %></td>
                    <td><%= f.getNombre() %></td>
                    <td><%= f.getApellido() %></td>
                    <td><%= f.getRol() %></td>
                    <td><%= f.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) %></td>
                    <td><%= f.getEntrada() %></td>
                    <td><%= f.getSalida() %></td>
                    <td><%= f.getDescanso() %></td>
                    <td><%= f.getComida() %></td>
                    <td>
                        <%
                            String hhmm = "00:00";
                            if (f.getEntrada() != null && f.getSalida() != null) {
                                long minutos = java.time.Duration.between(f.getEntrada(), f.getSalida()).toMinutes();
                                int descuentos = Math.max(0, f.getDescanso()) + Math.max(0, f.getComida());
                                long minutosEfectivos = Math.max(0, minutos - descuentos);
                                long horas = minutosEfectivos / 60;
                                long mins = minutosEfectivos % 60;
                                hhmm = String.format("%02d:%02d", horas, mins);
                            }
                        %>
                        <%= hhmm %>
                    </td>
                    <td>
                        <%
                            // Calcular minutos que superan las 8 horas
                            int minutosExtra = 0;
                            if (f.getEntrada() != null && f.getSalida() != null) {
                                long minutosTrabajados = java.time.Duration.between(f.getEntrada(), f.getSalida()).toMinutes();
                                int descuentos = Math.max(0, f.getDescanso()) + Math.max(0, f.getComida());
                                long minutosEfectivos = Math.max(0, minutosTrabajados - descuentos);
                                long minutosBase = 8 * 60; // 8 horas en minutos
                                minutosExtra = (int) Math.max(0, minutosEfectivos - minutosBase);
                            }
                            // Si horas_extra en BD es 0, significa que fueron rechazadas
                            int horasExtraBD = f.getHorasExtra();
                            int mostrar = (horasExtraBD == 0) ? 0 : minutosExtra;
                        %>
                        <%= mostrar %>
                    </td>
                    <td><%= f.getHorasSemanales() %></td>
                    <td class="<%= (f.getEntrada() != null && f.getSalida() == null) ? "estado-si" : "estado-no" %>">
                        <%= (f.getEntrada() != null && f.getSalida() == null) ? "Sí" : "No" %>
                    </td>
                    <td>
                        <div class="actions-inline">
                            <form method="get" action="admin.jsp">
                                <input type="hidden" name="id" value="<%= f.getId() %>">
                                <input type="submit" value="Editar" class="btn-accion small-action">
                            </form>

                            <form method="post" action="EliminarFichaje">
                                <input type="hidden" name="id" value="<%= f.getId() %>">
                                <input type="submit" value="Eliminar" class="btn-accion small-action" onclick="return confirm('¿Seguro que quieres eliminar este fichaje?');">
                            </form>
                            
                        </div>
                    </td>
                </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>
<script>
$(document).ready(function() {
    $('#tablaFichajes').DataTable({
        paging: true,
        searching: true,
        ordering: true,
        info: true,
        pageLength: 100,
        language: {
            sSearch: "Buscar:",
            sLengthMenu: "Mostrar _MENU_ registros por página",
            sInfo: "Mostrando _START_ a _END_ de _TOTAL_ registros",
            sInfoEmpty: "Mostrando 0 a 0 de 0 registros",
            sZeroRecords: "No se han encontrado registros",
            sInfoFiltered: "(filtrado de _MAX_ registros totales)"
        }
    });

    // Insertar divisor visual después del control de cantidad de registros
    setTimeout(function() {
        $('.dataTables_length').after('<div class="divisor"></div>');
    }, 50);
});
</script>

<script>
// Alinea el botón de logout con el h1 dentro de .main
function alignLogoutButton() {
    var h1 = document.querySelector('.main h1');
    var wrapper = document.querySelector('.top-right-controls');
    if (!h1 || !wrapper) return;
    var h1Rect = h1.getBoundingClientRect();
    var btn = wrapper.querySelector('.cerrar-btn');
    var btnHeight = btn ? btn.offsetHeight : wrapper.offsetHeight;
    // calcular top para centrar el botón con el H1
    var top = h1Rect.top + (h1Rect.height - btnHeight) / 2;
    if (top < 6) top = 6;
    wrapper.style.top = top + 'px';
}

function denegarHorasExtra(id) {
    if (confirm('¿Estás seguro de que quieres denegar las horas extra? Se pondrán a 0.')) {
        var form = document.createElement('form');
        form.method = 'POST';
        form.action = 'RechazarHorasExtra';
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'id';
        input.value = id;
        form.appendChild(input);
        document.body.appendChild(form);
        form.submit();
    }
}

window.addEventListener('load', alignLogoutButton);
window.addEventListener('resize', alignLogoutButton);
</script>


</body>
</html>