package com.mycompany.controlfichaje;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import com.mycompany.controlfichaje.dao.FichajeDAO;

/**
 * Servlet para actualizar un fichaje existente desde admin.jsp.
 * Recibe todos los campos del fichaje por POST, incluido el id.
 */
@WebServlet("/ActualizarFichaje")
public class ActualizarFichajeServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String rol = request.getParameter("rol");
            LocalDate fecha = LocalDate.parse(request.getParameter("fecha"));
            LocalTime entrada = LocalTime.parse(request.getParameter("entrada"));
            LocalTime salida = LocalTime.parse(request.getParameter("salida"));
            int descanso = Integer.parseInt(request.getParameter("descanso"));
            int comida = Integer.parseInt(request.getParameter("comida"));
            int horasSemanales = Integer.parseInt(request.getParameter("horasSemanales"));
            boolean estado = request.getParameter("aprobarHorasExtra") != null;

            FichajeModel f = new FichajeModel(id, nombre, apellido, rol, fecha, entrada, salida, descanso, comida, horasSemanales, estado);

            FichajeDAO dao = new FichajeDAO();
            dao.actualizar(f);

            response.sendRedirect("admin.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("admin.jsp?error=1");
        }
    }
}
