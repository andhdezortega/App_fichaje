package com.mycompany.controlfichaje;

import com.mycompany.controlfichaje.dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet para actualizar los datos de un usuario existente (por id).
 * Requiere rol admin. Si password está vacío, no se modifica.
 */
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

        if (rol == null || rol.isEmpty()) {
            rol = "user";
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


