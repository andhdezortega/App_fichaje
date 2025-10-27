package com.mycompany.controlfichaje;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import Autenticacion.Autenticacion;
import com.mycompany.controlfichaje.dao.FichajeDAO;
import com.mycompany.controlfichaje.dao.UsuarioDAO;
import com.mycompany.controlfichaje.dao.Usuario;

@WebServlet(name = "FichajeServlet", urlPatterns = {"/FichajeServlet"})
public class FichajeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // 1️⃣ Verificar autenticación
        if (!Autenticacion.estaAutenticado(request)) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2️⃣ Obtener correo de sesión
        String correoUsuario = (String) session.getAttribute("correo");
        if (correoUsuario == null || correoUsuario.isEmpty()) {
            response.sendRedirect("login.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            response.sendRedirect("perfil.jsp");
            return;
        }

        // 3️⃣ Obtener datos del usuario
        Usuario usuario = UsuarioDAO.obtenerUsuarioPorCorreo(correoUsuario);
        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String nombre = usuario.getUsuario();
        String apellido = usuario.getApellido() != null ? usuario.getApellido() : "";

        FichajeDAO fichajeDAO = new FichajeDAO();
        LocalDateTime ahora = LocalDateTime.now();

        switch (accion) {
            case "entrada":
                // Evitar duplicados
                FichajeModel activo = fichajeDAO.obtenerFichajeActivo(nombre, apellido);
                if (activo != null) {
                    session.setAttribute("horaEntrada", LocalDateTime.of(activo.getFecha(), activo.getEntrada()));
                    session.setAttribute("horaSalida", null);
                    session.setAttribute("fichajeEntrada", true);
                    session.setAttribute("fichajeSalida", false);
                    response.sendRedirect("perfil.jsp");
                    return;
                }

                // Crear nuevo fichaje
                FichajeModel fichaje = new FichajeModel();
                fichaje.setNombre(nombre);
                fichaje.setApellido(apellido);
                fichaje.setRol(usuario.getRol());
                fichaje.setFecha(LocalDate.now());
                fichaje.setEntrada(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));

                boolean entradaRegistrada = fichajeDAO.crear(fichaje);
                if (entradaRegistrada) {
                    session.setAttribute("horaEntrada", ahora);
                    session.setAttribute("horaSalida", null);
                    session.setAttribute("fichajeEntrada", true);
                    session.setAttribute("fichajeSalida", false);
                    session.setAttribute("inicioProduccion", ahora);
                    session.setAttribute("estadoUsuario", "produccion");
                    response.sendRedirect("perfil.jsp");
                } else {
                    request.setAttribute("error", "Error al registrar la entrada");
                    request.getRequestDispatcher("perfil.jsp").forward(request, response);
                }
                return;

            case "salida":
                FichajeModel fichajeActivo = fichajeDAO.obtenerFichajeActivo(nombre, apellido);
                if (fichajeActivo != null) {
                    fichajeActivo.setSalida(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
                    fichajeActivo.setEstado(true);
                    String estado = request.getParameter("estado");
                    fichajeActivo.setDescanso("break".equals(estado) ? 1 : 0);
                    fichajeActivo.setComida("comida".equals(estado) ? 1 : 0);

                    boolean salidaRegistrada = fichajeDAO.actualizar(fichajeActivo);
                    if (salidaRegistrada) {
                        session.setAttribute("horaSalida", ahora);
                        session.setAttribute("fichajeSalida", true);
                        session.setAttribute("fichajeEntrada", false);
                        session.removeAttribute("inicioProduccion");
                        session.removeAttribute("estadoUsuario");
                        response.sendRedirect("bienvenido.jsp");
                    } else {
                        request.setAttribute("error", "No se pudo registrar la salida");
                        request.getRequestDispatcher("perfil.jsp").forward(request, response);
                    }
                } else {
                    request.setAttribute("error", "No se encontró un fichaje activo");
                    request.getRequestDispatcher("perfil.jsp").forward(request, response);
                }
                return;

            default:
                response.sendRedirect("perfil.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
