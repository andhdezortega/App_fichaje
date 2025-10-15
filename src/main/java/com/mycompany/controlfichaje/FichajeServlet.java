package com.mycompany.controlfichaje;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import Autenticacion.Autenticacion;

@WebServlet(name = "FichajeServlet", urlPatterns = {"/FichajeServlet"})
public class FichajeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar si el usuario está autenticado
        if (!Autenticacion.estaAutenticado(request)) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String usuario = Autenticacion.obtenerUsuarioActual(request);
        String accion = request.getParameter("accion");

        if (accion == null) {
            response.sendRedirect("perfil.jsp");
            return;
        }

        HttpSession session = request.getSession();

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
