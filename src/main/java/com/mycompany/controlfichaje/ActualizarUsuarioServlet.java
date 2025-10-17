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

        String originalUsuario = request.getParameter("originalUsuario");
        String usuario = request.getParameter("usuario");
        String apellido = request.getParameter("apellido");
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");
        String rol = request.getParameter("rol");
        String descripcion = request.getParameter("descripcion");

        if (rol == null || rol.isEmpty()) {
            rol = "user";
        }

        UsuarioDAO dao = new UsuarioDAO();
        boolean ok = dao.actualizarUsuario(originalUsuario != null && !originalUsuario.isEmpty() ? originalUsuario : usuario,
                                           usuario, apellido, correo, password, rol, descripcion);
        if (ok) {
            response.sendRedirect("usuarios.jsp?mensaje=Usuario actualizado correctamente");
        } else {
            response.sendRedirect("usuarios.jsp?error=No se pudo actualizar el usuario");
        }
    }
}


