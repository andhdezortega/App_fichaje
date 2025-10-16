package com.mycompany.controlfichaje;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
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
                    // Si se pasa un parámetro opcional returnTo, redirige a esa página (por ejemplo "bienvenido.jsp");
                    String returnTo = request.getParameter("returnTo");
                    if (returnTo != null && !returnTo.trim().isEmpty()) {
                        // Usa sendRedirect para que el navegador cargue la página solicitada
                        response.sendRedirect(request.getContextPath() + "/" + returnTo);
                        return;
                    }

                    response.sendRedirect("perfil.jsp");
                return;

            case "salida":
                session.setAttribute("horaSalida", LocalDateTime.now());
                session.setAttribute("fichajeSalida", true);
                session.setAttribute("fichajeEntrada", false);
                // Si se pasa un parámetro opcional returnTo, redirige a esa página en lugar de la ruta por defecto
                String returnToSalida = request.getParameter("returnTo");
                if (returnToSalida != null && !returnToSalida.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/" + returnToSalida);
                    return;
                }

                response.sendRedirect(request.getContextPath() + "/bienvenido.jsp");
                return;

            default:
                // si no reconoce la acción
                response.sendRedirect("perfil.jsp");
        }
    }
}
