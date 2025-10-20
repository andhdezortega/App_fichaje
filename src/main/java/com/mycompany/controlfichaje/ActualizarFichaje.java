package com.mycompany.controlfichaje;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import com.mycompany.controlfichaje.dao.FichajeDAO;

@WebServlet("/ActualizarFichaje")
public class ActualizarFichaje extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String rol = request.getParameter("rol");
            String fecha = request.getParameter("fecha");
            String entrada = request.getParameter("entrada");
            String salida = request.getParameter("salida");
            int descanso = Integer.parseInt(request.getParameter("descanso"));
            int comida = Integer.parseInt(request.getParameter("comida"));
            int horasSemanales = Integer.parseInt(request.getParameter("horasSemanales"));
            boolean estado = request.getParameter("estado") != null;

            LocalDate ld = LocalDate.parse(fecha);
            LocalTime ltEntrada = LocalTime.parse(entrada);
            LocalTime ltSalida = LocalTime.parse(salida);

            FichajeModel fichaje = new FichajeModel(id, nombre, apellido, rol, ld, ltEntrada, ltSalida, descanso, comida, horasSemanales, estado);

            FichajeDAO dao = new FichajeDAO();
            boolean ok = dao.actualizar(fichaje);

            if (ok) {
                response.sendRedirect("admin.jsp?success=1");
            } else {
                response.sendRedirect("admin.jsp?error=1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("admin.jsp?error=1");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
