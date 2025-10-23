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
        String rol = (String) session.getAttribute("rol");
        if (!"admin".equals(rol)) {
            response.sendRedirect("login.jsp");
            return;
        }

        String usuario = request.getParameter("usuario");
        String apellido = request.getParameter("apellido");
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");
        String descripcion = request.getParameter("descripcion");

        UsuarioDAO dao = new UsuarioDAO();

        // Verificar si el usuario ya existe
        if (dao.existeUsuario(usuario)) {
            response.sendRedirect("usuarios.jsp?error=El usuario ya existe");
            return;
        }

        // Crear el nuevo usuario (siempre con rol 'user') incluyendo el correo
        boolean creado = dao.crearUsuario(usuario, apellido, correo, password, "user", descripcion);

        if (creado) {
            response.sendRedirect("usuarios.jsp?mensaje=Usuario creado correctamente");
        } else {
            response.sendRedirect("usuarios.jsp?error=Error al crear el usuario");
        }
    }
}