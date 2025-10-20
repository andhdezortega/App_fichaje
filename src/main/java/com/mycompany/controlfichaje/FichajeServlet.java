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

        // Verificar si el usuario está autenticado
        if (!Autenticacion.estaAutenticado(request)) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String nombreUsuario = Autenticacion.obtenerUsuarioActual(request);
        String accion = request.getParameter("accion");

        if (accion == null) {
            response.sendRedirect("perfil.jsp");
            return;
        }

        HttpSession session = request.getSession();
        LocalDateTime ahora = LocalDateTime.now();
        
        // Obtener usuario actual y sus datos
        Usuario usuario = UsuarioDAO.obtenerUsuario(nombreUsuario);
        
        // Forzar coherencia: usar siempre datos del usuario autenticado
        String nombre = (usuario != null && usuario.getUsuario() != null) ? usuario.getUsuario() : nombreUsuario;
        String apellido = (usuario != null && usuario.getApellido() != null) ? usuario.getApellido() : "";
        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Crear objeto FichajeModel para las operaciones
        FichajeModel fichaje = new FichajeModel();
        fichaje.nombre = nombre;
        fichaje.apellido = apellido;
        fichaje.rol = usuario.getRol();
    fichaje.fecha = LocalDate.now();
    fichaje.entrada = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        FichajeDAO fichajeDAO = new FichajeDAO();
        
        switch (accion) {
            case "entrada":
                // Evitar duplicados: si ya hay activo, no crear otro
                FichajeModel activo = fichajeDAO.obtenerFichajeActivo(nombre, apellido);
                if (activo != null) {
                    // Refrescar estado en sesión y redirigir
                    session.setAttribute("horaEntrada", LocalDateTime.of(activo.fecha, activo.entrada));
                    session.setAttribute("horaSalida", null);
                    session.setAttribute("fichajeEntrada", true);
                    session.setAttribute("fichajeSalida", false);
                    response.sendRedirect("perfil.jsp");
                    return;
                }

                // Registrar entrada nueva
                boolean entradaRegistrada = fichajeDAO.crear(fichaje);
                
                if (entradaRegistrada) {
                    session.setAttribute("horaEntrada", ahora);
                    session.setAttribute("horaSalida", null);
                    session.setAttribute("fichajeEntrada", true);
                    session.setAttribute("fichajeSalida", false);
                    
                    // Si se pasa un parámetro opcional returnTo, redirige a esa página
                    String returnTo = request.getParameter("returnTo");
                    if (returnTo != null && !returnTo.trim().isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/" + returnTo);
                        return;
                    }
                    response.sendRedirect("perfil.jsp");
                } else {
                    request.setAttribute("error", "Error al registrar la entrada");
                    request.getRequestDispatcher("perfil.jsp").forward(request, response);
                }
                return;

            case "salida":
                // Buscar fichaje activo directamente en la base de datos
                FichajeModel fichajeActivo = fichajeDAO.obtenerFichajeActivo(nombre, apellido);
                
                if (fichajeActivo != null) {
                    // Actualizar fichaje con la salida
                    fichajeActivo.salida = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
                    fichajeActivo.estado = true;
                    fichajeActivo.descanso = "break".equals(request.getParameter("estado")) ? 1 : 0;
                    fichajeActivo.comida = "comida".equals(request.getParameter("estado")) ? 1 : 0;
                    
                    boolean salidaRegistrada = fichajeDAO.actualizar(fichajeActivo);
                    
                    if (salidaRegistrada) {
                        session.setAttribute("horaSalida", ahora);
                        session.setAttribute("fichajeSalida", true);
                        session.setAttribute("fichajeEntrada", false);
                        
                        // Si se pasa un parámetro opcional returnTo, redirige a esa página
                        String returnToSalida = request.getParameter("returnTo");
                        if (returnToSalida != null && !returnToSalida.trim().isEmpty()) {
                            response.sendRedirect(request.getContextPath() + "/" + returnToSalida);
                            return;
                        }
                        response.sendRedirect("bienvenido.jsp");
                    } else {
                        request.setAttribute("error", "Error al registrar la salida");
                        request.getRequestDispatcher("perfil.jsp").forward(request, response);
                    }
                } else {
                    request.setAttribute("error", "No se encontró un fichaje activo");
                    request.getRequestDispatcher("perfil.jsp").forward(request, response);
                }
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
