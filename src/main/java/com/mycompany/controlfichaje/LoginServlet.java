package com.mycompany.controlfichaje;

<<<<<<< HEAD

=======
>>>>>>> origin/andrea
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import Autenticacion.Autenticacion;


@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        // Usar la clase Autenticacion para validar y crear sesión
        boolean loginExitoso = Autenticacion.hacerLogin(request, usuario, contrasena);
        if (loginExitoso) {
            // Si el usuario tiene un fichaje activo, ir directamente a perfil.jsp
            HttpSession session = request.getSession();
            String usuarioSesion = Autenticacion.obtenerUsuarioActual(request);

            // Preferir datos reales del usuario desde BD; si no, fallback por espacios
            String nombre;
            String apellido;
            com.mycompany.controlfichaje.dao.Usuario u = com.mycompany.controlfichaje.dao.UsuarioDAO.obtenerUsuario(usuarioSesion);
            if (u != null) {
                nombre = (u.getUsuario() != null) ? u.getUsuario() : usuarioSesion;
                apellido = (u.getApellido() != null) ? u.getApellido() : "";
            } else {
                String[] partes = usuarioSesion.split(" ", 2);
                nombre = partes[0];
                apellido = partes.length > 1 ? partes[1] : "";
            }

            com.mycompany.controlfichaje.dao.FichajeDAO dao = new com.mycompany.controlfichaje.dao.FichajeDAO();
            com.mycompany.controlfichaje.FichajeMock activo = dao.obtenerFichajeActivo(nombre, apellido);
            if (activo != null) {
                session.setAttribute("horaEntrada", java.time.LocalDateTime.of(activo.fecha, activo.entrada));
                session.setAttribute("horaSalida", null);
                session.setAttribute("fichajeEntrada", true);
                session.setAttribute("fichajeSalida", false);
                response.sendRedirect("perfil.jsp");
                return;
            }

<<<<<<< HEAD
            // Si no hay fichaje activo, ir al panel correspondiente
            if ("admin".equals(Autenticacion.obtenerRol(request))) {
                response.sendRedirect("admin.jsp");
            } else {
                response.sendRedirect("bienvenido.jsp");
            }
        } else {
            response.sendRedirect("login.jsp?error=1");
        }
    }  
=======

    // Login para externo
    } else if ("externo".equals(usuario) && "externo123".equals(contrasena)) {
        // Guardar como 'usuario' para mantener el contrato con otros servlets/JSP
        session.setAttribute("usuario", usuario);
        session.setAttribute("rol", "externo");
        response.sendRedirect("externo.jsp");

    } else {
        response.sendRedirect("login.jsp?error=1");
    }
>>>>>>> origin/andrea
}
