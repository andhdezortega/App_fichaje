package com.mycompany.controlfichaje;

import com.mycompany.controlfichaje.dao.Usuario;
import com.mycompany.controlfichaje.dao.UsuarioDAO;
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
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("usuarios.jsp?error=Usuario no especificado");
            return;
        }
        int id = Integer.parseInt(idParam);
        Usuario existente = UsuarioDAO.obtenerUsuarioPorId(id);
        if (existente == null) {
            response.sendRedirect("usuarios.jsp?error=Usuario no encontrado");
            return;
        }
        request.setAttribute("usuarioObj", existente);
        request.getRequestDispatcher("usuarios.jsp").forward(request, response);
    }
}


