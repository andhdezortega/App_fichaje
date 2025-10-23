package com.mycompany.controlfichaje;

import com.mycompany.controlfichaje.dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/ActualizarUsuarioServlet")
public class ActualizarUsuarioServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String rolSession = (String) session.getAttribute("rol");
        if (!"admin".equals(rolSession)) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idParam = request.getParameter("id");
        String usuario = request.getParameter("usuario");
        String apellido = request.getParameter("apellido");
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");
        String rol = request.getParameter("rol");
        String descripcion = request.getParameter("descripcion");

        System.out.println("🔍 ActualizarUsuarioServlet - ID: " + idParam);
        System.out.println("🔍 ActualizarUsuarioServlet - Usuario: " + usuario);
        System.out.println("🔍 ActualizarUsuarioServlet - Rol: " + rol);

        // Normalizar rol a valores esperados en la app
        if (rol == null || rol.trim().isEmpty()) {
            rol = "usuario";
        } else {
            rol = rol.trim().toLowerCase();
            if ("user".equals(rol)) {
                rol = "usuario";
            } else if (!"admin".equals(rol) && !"usuario".equals(rol)) {
                // Cualquier otro valor inesperado pasa a "usuario"
                rol = "usuario";
            }
        }

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("usuarios.jsp?error=ID de usuario no especificado");
            return;
        }
        int id = Integer.parseInt(idParam);
        UsuarioDAO dao = new UsuarioDAO();
        boolean ok = dao.actualizarUsuarioPorId(id, usuario, apellido, correo, password, rol, descripcion);
        if (ok) {
            response.sendRedirect("usuarios.jsp?mensaje=Usuario actualizado correctamente");
        } else {
            response.sendRedirect("usuarios.jsp?error=No se pudo actualizar el usuario");
        }
    }
}


