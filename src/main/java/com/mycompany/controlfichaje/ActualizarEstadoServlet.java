package com.mycompany.controlfichaje; 

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/ActualizarEstadoServlet")
public class ActualizarEstadoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String estado = request.getParameter("estado");

        if (estado != null && !estado.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("estadoUsuario", estado);
        }
        
        request.getRequestDispatcher("perfil.jsp").forward(request, response);
    }
}
