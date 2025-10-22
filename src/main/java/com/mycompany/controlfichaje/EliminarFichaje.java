package com.mycompany.controlfichaje;

import com.mycompany.controlfichaje.dao.FichajeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet para eliminar un fichaje por id desde admin.jsp.
 */
@WebServlet("/EliminarFichaje")
public class EliminarFichaje extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            FichajeDAO dao = new FichajeDAO();
            boolean ok = dao.eliminar(id);
            if (ok) {
                response.sendRedirect("admin.jsp?mensaje=Fichaje eliminado");
            } else {
                response.sendRedirect("admin.jsp?error=No se pudo eliminar");
            }
        } catch (Exception e) {
            response.sendRedirect("admin.jsp?error=Solicitud inválida");
        }
    }
}


