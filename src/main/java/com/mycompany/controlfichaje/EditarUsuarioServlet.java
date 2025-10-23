package com.mycompany.controlfichaje;

import com.mycompany.controlfichaje.dao.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/EditarUsuarioServlet")
public class EditarUsuarioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String rol = (String) session.getAttribute("rol");
        if (!"admin".equals(rol)) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idParam = request.getParameter("id");
        System.out.println("🔍 EditarUsuarioServlet - ID recibido: " + idParam);
        System.out.println("🔍 EditarUsuarioServlet - Todos los parámetros: " + request.getParameterMap());
        
        if (idParam == null || idParam.isEmpty()) {
            System.out.println("❌ Error: ID no especificado");
            response.sendRedirect("usuarios.jsp?error=Usuario no especificado - ID: " + idParam);
            return;
        }
        
        int id = Integer.parseInt(idParam);
        System.out.println("🔍 Buscando usuario con ID: " + id);
        
        Usuario existente = com.mycompany.controlfichaje.dao.UsuarioDAO.obtenerUsuarioPorId(id);
        if (existente == null) {
            System.out.println("❌ Error: Usuario no encontrado con ID: " + id);
            response.sendRedirect("usuarios.jsp?error=Usuario no encontrado");
            return;
        }
        
        System.out.println("✅ Usuario encontrado: " + existente.getUsuario() + " (ID: " + existente.getId() + ")");
        request.setAttribute("usuarioObj", existente);
        request.getRequestDispatcher("usuarios.jsp").forward(request, response);
    }
}


