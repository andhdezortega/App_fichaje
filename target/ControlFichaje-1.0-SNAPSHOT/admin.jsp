<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%@ page import="java.util.*, java.time.*" %>

<%
    String usuario = (String) session.getAttribute("usuario");
    String rol = (String) session.getAttribute("rol");

    if (usuario == null || !"admin".equals(rol)) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Simulación temporal de fichajes
    class FichajeMock {
        String nombre, apellido, rol;
        LocalDate fecha;
        LocalTime entrada, salida, descanso, comida;
        int horasSemanales;

        FichajeMock(String n, String a, String r, String f, String e, String s, String d, String c, int hs) {
            nombre = n;
            apellido = a;
            rol = r;
            fecha = LocalDate.parse(f);
            entrada = LocalTime.parse(e);
            salida = LocalTime.parse(s);
            descanso = LocalTime.parse(d);
            comida = LocalTime.parse(c);
            horasSemanales = hs;
        }
    }

    List<FichajeMock> fichajes = new ArrayList<>();
    fichajes.add(new FichajeMock("Juan", "Pérez", "usuario", "2025-10-14", "08:00", "17:00", "00:15", "01:00", 40));
    fichajes.add(new FichajeMock("Lucía", "Gómez", "usuario", "2025-10-14", "09:00", "18:00", "00:20", "01:00", 38));
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Panel Administrador</title>
     <link rel="stylesheet" href="css/styles.css">
    <link rel="icon" type="image/x-icon" href="icon.ico">
    <link rel="icon" href="<%=request.getContextPath()%>/favicon.ico" type="image/x-icon">
    
</head>
<body>

<div class="container">
    <!-- BARRA LATERAL: formulario -->
    <div class="sidebar">
        <h2>Nuevo Fichaje</h2>
        <form method="post" action="InsertarFichaje">
            <label>Nombre:</label>
            <input type="text" name="nombre" required>

            <label>Apellido:</label>
            <input type="text" name="apellido" required>

            <label>Rol:</label>
            <input type="text" name="rol" required>

            <label>Fecha:</label>
            <input type="date" name="fecha" required>

            <label>Entrada:</label>
            <input type="time" name="entrada" required>

            <label>Salida:</label>
            <input type="time" name="salida" required>

            <label>Descanso:</label>
            <input type="time" name="descanso">

            <label>Comida:</label>
            <input type="time" name="comida">

            <label>Horas semanales:</label>
            <input type="number" name="horasSemanales" required>

            <input type="submit" value="Guardar fichaje">
        </form>
    </div>

    <!-- PANEL CENTRAL: tabla -->
    <div class="main">
        <h1>Fichajes registrados</h1>
        <table>
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
                    <th>Horas Semanales</th>
                </tr>
            </thead>
            <tbody>
                <% for (FichajeMock f : fichajes) { %>
                    <tr>
                        <td><%= f.nombre %></td>
                        <td><%= f.apellido %></td>
                        <td><%= f.rol %></td>
                        <td><%= f.fecha %></td>
                        <td><%= f.entrada %></td>
                        <td><%= f.salida %></td>
                        <td><%= f.descanso %></td>
                        <td><%= f.comida %></td>
                        <td><%= f.horasSemanales %> h</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
