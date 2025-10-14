package com.mycompany.controlfichaje;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        if ("admin".equals(usuario) && "1234".equals(contrasena)) {
            // GUARDAR EL USUARIO EN LA SESIÓN
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);

            response.sendRedirect("bienvenido.jsp");
        } else {
            response.sendRedirect("login.jsp?error=1");
        }
    }
}
