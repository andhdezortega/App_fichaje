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
    
    // Debug logs
    System.out.println("🔍 usuarios.jsp - editMode: " + editMode);
    System.out.println("🔍 usuarios.jsp - usuarioObj: " + (usuarioObj != null ? usuarioObj.getUsuario() : "null"));
    if (usuarioObj != null) {
        System.out.println("🔍 usuarios.jsp - ID: " + usuarioObj.getId() + ", Usuario: " + usuarioObj.getUsuario() + ", Rol: " + usuarioObj.getRol());
    }
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
    <div class="top-right-controls">
        <form method="post" action="LogoutServlet">
            <button type="submit" class="cerrar-btn">Cerrar sesión</button>
        </form>
    </div>
    
    <div class="container">
        <!-- BARRA LATERAL -->
        <div class="sidebar">
            <h2><%= editMode ? "Editar Usuario" : "Crear Nuevo Usuario" %></h2>
            <form method="post" action="<%= editMode ? "ActualizarUsuarioServlet" : "CrearUsuarioServlet" %>">
                <% if (editMode && usuarioObj != null) { %>
                    <input type="hidden" name="id" value="<%= usuarioObj.getId() %>">
                    <p><strong>ID:</strong> <%= usuarioObj.getId() %></p>
                <% } %>
                
                <label>Usuario:</label>
                <% 
                    String usuarioValue = "";
                    if (editMode && usuarioObj != null) {
                        usuarioValue = usuarioObj.getUsuario() != null ? usuarioObj.getUsuario() : "";
                    }
                %>
                <input type="text" name="usuario" value="<%= usuarioValue %>" required>

                <label>Apellido:</label>
                <% 
                    String apellidoValue = "";
                    if (editMode && usuarioObj != null) {
                        apellidoValue = usuarioObj.getApellido() != null ? usuarioObj.getApellido() : "";
                    }
                %>
                <input type="text" name="apellido" value="<%= apellidoValue %>" required>

                <label>Correo electrónico:</label>
                <% 
                    String correoValue = "";
                    if (editMode && usuarioObj != null) {
                        correoValue = usuarioObj.getCorreo() != null ? usuarioObj.getCorreo() : "";
                    }
                %>
                <input type="email" name="correo" value="<%= correoValue %>" required>

                <label>Contraseña<%= editMode ? " (dejar en blanco para no cambiarla)" : "" %>:</label>
                <input type="password" name="password" <%= editMode ? "" : "required" %>>

                <label>Rol:</label>
                <% 
                    String rolValue = "";
                    if (editMode && usuarioObj != null) {
                        rolValue = usuarioObj.getRol() != null ? usuarioObj.getRol() : "usuario";
                    }
                %>
                <select name="rol" required>
                    <option value="usuario" <%= "usuario".equals(rolValue) ? "selected" : "" %>>Usuario</option>
                    <option value="admin" <%= "admin".equals(rolValue) ? "selected" : "" %>>Administrador</option>
                </select>

                <label>Descripción:</label>
                <% 
                    String descripcionValue = "";
                    if (editMode && usuarioObj != null) {
                        descripcionValue = usuarioObj.getDescripcion() != null ? usuarioObj.getDescripcion() : "";
                    }
                %>
                <input type="text" name="descripcion" value="<%= descripcionValue %>" required>

                <input type="submit" value="<%= editMode ? "Actualizar" : "Crear Usuario" %>">
                <% if (editMode) { %>
                    <a href="usuarios.jsp" class="boton-link" style="margin-left:8px;">Cancelar</a>
                <% } %>
            </form>
            
            <% if (mensaje != null) { %>
                <p class="mensaje-exito"><%= mensaje %></p>
            <% } %>
            <% if (error != null) { %>
                <p class="mensaje-error"><%= error %></p>
            <% } %>
            <% if (editMode && usuarioObj != null) { %>
                <p class="mensaje-info">Editando al usuario <strong><%= usuarioObj.getUsuario() %></strong></p>
            <% } %>

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
                    System.out.println("🔍 usuarios.jsp - Total usuarios: " + usuarios.size());
                    for (Map<String, String> user : usuarios) { 
                        System.out.println("🔍 usuarios.jsp - Usuario: " + user.get("usuario") + ", ID: " + user.get("id"));
                    %>
                        <tr>
                            <td><%= user.get("usuario") %></td>
                            <td><%= user.get("apellido") != null ? user.get("apellido") : "" %></td>
                            <td><%= user.get("correo") != null ? user.get("correo") : "" %></td>
                            <td><%= user.get("rol") %></td>
                            <td><%= user.get("descripcion") %></td>
                            <td>
                                <form method="get" action="EditarUsuarioServlet" style="display:inline;">
                                    <input type="hidden" name="id" value="<%= user.get("id") %>">
                                    <input type="submit" value="Editar" class="btn-accion">
                                </form>
                                <% if (!"admin".equals(user.get("usuario"))) { %>
                                    <form method="post" action="EliminarUsuarioServlet" style="display:inline; margin-left:6px;">
                                        <input type="hidden" name="usuario" value="<%= user.get("usuario") %>">
                                        <input type="submit" value="Eliminar" class="btn-accion" 
                                            onclick="return confirm('¿Seguro que quieres eliminar este usuario?');">
                                    </form>
                                <% } %>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

<script>
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
window.addEventListener('load', alignLogoutButton);
window.addEventListener('resize', alignLogoutButton);
</script>

</body>
</html>