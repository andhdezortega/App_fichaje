package com.mycompany.controlfichaje;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.mycompany.controlfichaje.dao.UsuarioDAO;

@WebServlet("/CrearUsuarioServlet")
public class CrearUsuarioServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar si el usuario es admin
        HttpSession session = request.getSession();
        String rolSession = (String) session.getAttribute("rol");
        if (!"admin".equals(rolSession)) {
            response.sendRedirect("login.jsp");
            return;
        }

        String usuario = request.getParameter("usuario");
        String apellido = request.getParameter("apellido");
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");
        String rol = request.getParameter("rol");
        String descripcion = request.getParameter("descripcion");

        // Validar que el rol sea válido
        if (rol == null || rol.isEmpty()) {
            rol = "usuario";
        }

        UsuarioDAO dao = new UsuarioDAO();

        // Verificar si el usuario ya existe
        if (dao.existeUsuario(usuario)) {
            response.sendRedirect("usuarios.jsp?error=El usuario ya existe");
            return;
        }

        // Crear el nuevo usuario con el rol especificado
        boolean creado = dao.crearUsuario(usuario, apellido, correo, password, rol, descripcion);

        if (creado) {
            response.sendRedirect("usuarios.jsp?mensaje=Usuario creado correctamente");
        } else {
            response.sendRedirect("usuarios.jsp?error=Error al crear el usuario");
        }
    }
}