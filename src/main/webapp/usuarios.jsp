<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%@ page import="java.util.*, com.mycompany.controlfichaje.dao.*" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    String rol = (String) session.getAttribute("rol");

    // Verificar si el usuario es admin
    if (usuario == null || !"admin".equals(rol)) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Obtener mensaje de éxito o error si existe
    String mensaje = request.getParameter("mensaje");
    String error = request.getParameter("error");

    // Si venimos de EditarUsuarioServlet, habrá un usuarioObj con datos a editar
    Usuario usuarioObj = (Usuario) request.getAttribute("usuarioObj");
    boolean editMode = usuarioObj != null;
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Usuarios</title>
    <link rel="stylesheet" href="css/styles.css">
    <link rel="icon" type="image/x-icon" href="favicon.ico">
</head>
<body>
    <div class="container">
        <!-- BARRA LATERAL -->
        <div class="sidebar">
            
            <h2><%= editMode ? "Editar Usuario" : "Crear Nuevo Usuario" %></h2>
            <% if (mensaje != null) { %>
                <p class="mensaje-exito"><%= mensaje %></p>
            <% } %>
            <% if (error != null) { %>
                <p class="mensaje-error"><%= error %></p>
            <% } %>
            <% if (editMode) { %>
                <p class="mensaje-info">Editando al usuario <strong><%= usuarioObj.getUsuario() %></strong></p>
            <% } %>
            <form method="post" action="<%= editMode ? "ActualizarUsuarioServlet" : "CrearUsuarioServlet" %>">
                <% if (editMode) { %>
                    <input type="hidden" name="originalUsuario" value="<%= usuarioObj.getUsuario() %>">
                <% } %>
                <label>Usuario:</label>
                <input type="text" name="usuario" value="<%= editMode ? usuarioObj.getUsuario() : "" %>" required>

                <label>Apellido:</label>
                <input type="text" name="apellido" value="<%= editMode && usuarioObj.getApellido() != null ? usuarioObj.getApellido() : "" %>" required>

                <label>Correo electrónico:</label>
                <input type="email" name="correo" value="<%= editMode && usuarioObj.getCorreo() != null ? usuarioObj.getCorreo() : "" %>" required>

                <label>Contraseña<%= editMode ? " (dejar en blanco para no cambiarla)" : "" %>:</label>
                <input type="password" name="password" <%= editMode ? "" : "required" %>>

                <label>Descripción:</label>
                <input type="text" name="descripcion" value="<%= editMode && usuarioObj.getDescripcion() != null ? usuarioObj.getDescripcion() : "" %>" required>

                <% if (editMode) { %>
                    <!-- Mantener el rol actual del usuario para no perderlo -->
                    <input type="hidden" name="rol" value="<%= usuarioObj.getRol() %>">
                <% } %>

                <input type="submit" value="<%= editMode ? "Guardar cambios" : "Crear Usuario" %>">
                <% if (editMode) { %>
                    <a href="usuarios.jsp" class="boton-link" style="margin-left:8px;">Cancelar</a>
                <% } %>
            </form>
            
            

            <div style="margin-top: 20px;">
                <a href="admin.jsp" class="boton-link">Volver al Panel de Fichajes</a>
            </div>
        </div>

        <!-- PANEL CENTRAL -->
        <div class="main">
            <h1>Usuarios del Sistema</h1>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Usuario</th>
                        <th>Apellido</th>
                        <th>Correo</th>
                        <th>Rol</th>
                        <th>Descripción</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                    UsuarioDAO usuarioDAO = new UsuarioDAO();
                    List<Map<String, String>> usuarios = usuarioDAO.obtenerTodos();
                    for (Map<String, String> user : usuarios) { 
                    %>
                        <tr>
                            <td><%= user.get("usuario") %></td>
                            <td><%= user.get("apellido") != null ? user.get("apellido") : "" %></td>
                            <td><%= user.get("correo") != null ? user.get("correo") : "" %></td>
                            <td><%= user.get("rol") %></td>
                            <td><%= user.get("descripcion") %></td>
                            <td>
                                <div class="actions-inline">
                                    <form method="get" action="EditarUsuarioServlet" style="display:inline;">
                                        <input type="hidden" name="usuario" value="<%= user.get("usuario") %>">
                                        <input type="submit" value="Editar" class="btn-accion">
                                    </form>
                                    <% if (!"admin".equals(user.get("usuario"))) { %>
                                        <form method="post" action="EliminarUsuarioServlet" style="display:inline;">
                                            <input type="hidden" name="usuario" value="<%= user.get("usuario") %>">
                                            <input type="submit" value="Eliminar" class="btn-accion btn-rojo" 
                                                   onclick="return confirm('¿Seguro que quieres eliminar este usuario?');">
                                        </form>
                                    <% } %>
                                </div>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>