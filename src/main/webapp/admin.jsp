<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%@ page import="java.util.*, java.time.*, java.time.format.DateTimeFormatter" %>
<%@ page import="com.mycompany.controlfichaje.FichajeMock" %>

<%
    String usuario = (String) session.getAttribute("usuario");
    String rol = (String) session.getAttribute("rol");

    if (usuario == null || !"admin".equals(rol)) {
        response.sendRedirect("login.jsp");
        return;
    }

   
    // Recuperar o inicializar la lista de fichajes en sesión
    List<FichajeMock> fichajes = (List<FichajeMock>) session.getAttribute("fichajes");
    if (fichajes == null) {
        fichajes = new ArrayList<>();
        fichajes.add(new FichajeMock(1, "Juan", "Pérez", "usuario", "2025-10-14", "08:00", "17:00", "15", "60", 40, true));
        fichajes.add(new FichajeMock(2, "Lucía", "Gómez", "usuario", "2025-10-14", "09:00", "18:00", "20", "60", 38, false));
        fichajes.add(new FichajeMock(3, "Carlos", "López", "usuario", "2025-10-14", "07:30", "16:30", "10", "45", 40, true));
        // ... puedes añadir más si lo necesitas ...
        session.setAttribute("fichajes", fichajes);
    }

    // Buscar fichaje para edición
    String paramId = request.getParameter("id");
    FichajeMock fichajeSeleccionado = null;
    if (paramId != null) {
        int id = Integer.parseInt(paramId);
        for (FichajeMock f : fichajes) {
            if (f.id == id) {
                fichajeSeleccionado = f;
                break;
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Panel Administrador</title>
    <link rel="stylesheet" href="css/styles.css">
    <style>
        .estado-si {
            background-color: #d4edda; /* verde claro */
            color: #155724;
            font-weight: bold;
            text-align: center;
        }
        .estado-no {
            background-color: #fff3cd; /* amarillo claro */
            color: #856404;
            font-weight: bold;
            text-align: center;
        }
    </style>
</head>
<body>

<div class="container">
    <!-- BARRA LATERAL -->
    <div class="sidebar">
        <h2><%= (fichajeSeleccionado != null) ? "Editar Fichaje" : "Nuevo Fichaje" %></h2>
        <form method="post" action="<%= (fichajeSeleccionado != null) ? "ActualizarFichaje" : "InsertarFichaje" %>">
            <% if (fichajeSeleccionado != null) { %>
                <input type="hidden" name="id" value="<%= fichajeSeleccionado.id %>">
            <% } %>

            <label>Nombre:</label>
            <input type="text" name="nombre" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.nombre : "" %>">

            <label>Apellido:</label>
            <input type="text" name="apellido" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.apellido : "" %>">

            <label>Rol:</label>
            <input type="text" name="rol" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.rol : "" %>">

            <label>Fecha:</label>
            <input type="date" name="fecha" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.fecha.toString() : "" %>">

            <label>Entrada:</label>
            <input type="time" name="entrada" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.entrada.toString() : "" %>">

            <label>Salida:</label>
            <input type="time" name="salida" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.salida.toString() : "" %>">

            <label>Descanso (min):</label>
            <input type="number" name="descanso" value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.descanso : "" %>">

            <label>Comida (min):</label>
            <input type="number" name="comida" value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.comida : "" %>">

            <label>Horas semanales:</label>
            <input type="number" name="horasSemanales" required value="<%= (fichajeSeleccionado != null) ? fichajeSeleccionado.horasSemanales : "" %>">

            <label>En producción:</label>
            <input type="checkbox" name="estado" <%= (fichajeSeleccionado != null && fichajeSeleccionado.estado) ? "checked" : "" %>>

            <input type="submit" value="<%= (fichajeSeleccionado != null) ? "Actualizar" : "Guardar fichaje" %>">
        </form>
    </div>

    <!-- PANEL CENTRAL: tabla -->
    <div class="main">
        <h1>Fichajes registrados</h1>
        <table class="admin-table">
            <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>Rol</th>
                    <th>Fecha</th>
                    <th>Entrada</th>
                    <th>Salida</th>
                    <th>Descanso</th>
                    <th>Comida</th>
                    <th>Horas</th>
                    <th>En producción</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
            <% for (FichajeMock f : fichajes) { %>
                <tr>
                    <td><%= f.nombre %></td>
                    <td><%= f.apellido %></td>
                    <td><%= f.rol %></td>
                    <td><%= f.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) %></td>
                    <td><%= f.entrada %></td>
                    <td><%= f.salida %></td>
                    <td><%= f.descanso %></td>
                    <td><%= f.comida %></td>
                    <td><%= f.horasSemanales %></td>
                    <td class="<%= f.estado ? "estado-si" : "estado-no" %>">
                        <%= f.estado ? "Sí" : "No" %>
                    </td>
                    <td>
                        <form method="get" action="admin.jsp" style="display:inline;">
                            <input type="hidden" name="id" value="<%= f.id %>">
                            <input type="submit" value="Editar" class="btn-accion">
                        </form>
                        <span style="margin: 0 5px;">|</span>
                        <form method="post" action="EliminarFichaje" style="display:inline;">
                            <input type="hidden" name="id" value="<%= f.id %>">
                            <input type="submit" value="Eliminar" class="btn-accion" onclick="return confirm('¿Seguro que quieres eliminar este fichaje?');">
                                                </form>
                    </td>
                </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>

               
