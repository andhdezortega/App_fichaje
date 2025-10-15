package com.mycompany.controlfichaje;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(name = "FichajeServlet", urlPatterns = {"/FichajeServlet"})
public class FichajeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String usuario = (String) session.getAttribute("usuario");
        String accion = request.getParameter("accion");

        if (usuario == null || accion == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        switch (accion) {
            case "entrada":
                session.setAttribute("horaEntrada", LocalDateTime.now());
                session.setAttribute("horaSalida", null);
                session.setAttribute("fichajeEntrada", true);
                session.setAttribute("fichajeSalida", false);
                // Redirige a perfil.jsp
                response.sendRedirect("perfil.jsp");
                return;

            case "salida":
                session.setAttribute("horaSalida", LocalDateTime.now());
                session.setAttribute("fichajeSalida", true);
                session.setAttribute("fichajeEntrada", false);
                response.sendRedirect("bienvenido.jsp");
                return;

            default:
                // si no reconoce la acción
                response.sendRedirect("perfil.jsp");
        }
    }
}
