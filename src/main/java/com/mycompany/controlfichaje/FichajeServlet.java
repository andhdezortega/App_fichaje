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
        System.out.println("FichajeServlet: accion = " + accion);

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lógica de fichaje
        if ("entrada".equals(accion)) {
            // Guarda la hora de entrada (opcional)
            session.setAttribute("horaEntrada", LocalDateTime.now());
            

            // Marca que fichó entrada
            session.setAttribute("fichajeEntrada", true);
            session.setAttribute("fichajeSalida", false);

        } else if ("salida".equals(accion)) {
            // Guarda la hora de salida (opcional)
            session.setAttribute("horaSalida", LocalDateTime.now());

            // Marca que fichó salida
            session.setAttribute("fichajeSalida", true);
            session.setAttribute("fichajeEntrada", false); // o true si quieres evitar que fiche entrada de nuevo
        }

        // Vuelve a la página principal
        request.getRequestDispatcher("perfil.jsp").forward(request, response);
        
    }
}
