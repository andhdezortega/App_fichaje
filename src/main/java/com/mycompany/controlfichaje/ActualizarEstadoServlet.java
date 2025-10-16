package com.mycompany.controlfichaje;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;



import java.io.IOException;

@WebServlet("/ActualizarEstadoServlet")
public class ActualizarEstadoServlet extends HttpServlet {

@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession session = request.getSession(); // ✅ Primero obtenemos la sesión

    String estado = request.getParameter("estado");

    if (estado != null && !estado.trim().isEmpty()) {
        session.setAttribute("estadoUsuario", estado);

        if ("produccion".equals(estado)) {
            // Si no hay hora de inicio guardada, la registramos
            if (session.getAttribute("inicioProduccion") == null) {
                session.setAttribute("inicioProduccion", java.time.LocalDateTime.now());
            }
        } else {
            // Si sale de producción, quitamos el inicio
            session.removeAttribute("inicioProduccion");
        }
    }

    request.getRequestDispatcher("perfil.jsp").forward(request, response);
}

}