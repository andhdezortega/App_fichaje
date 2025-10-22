package com.mycompany.controlfichaje;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.mycompany.controlfichaje.dao.UsuarioDAO;

@WebServlet("/EliminarUsuarioServlet")
public class EliminarUsuarioServlet extends HttpServlet {
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


        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("usuarios.jsp?error=ID no especificado");
            return;
        }
        int id = Integer.parseInt(idParam);
        UsuarioDAO dao = new UsuarioDAO();
        boolean eliminado = dao.eliminarUsuarioPorId(id);

        if (eliminado) {
            response.sendRedirect("usuarios.jsp?mensaje=Usuario eliminado correctamente");
        } else {
            response.sendRedirect("usuarios.jsp?error=Error al eliminar el usuario");
        }
    }
}