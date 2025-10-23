package com.mycompany.controlfichaje;

import com.mycompany.controlfichaje.dao.DatabaseConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Rechaza las horas extra de un fichaje poniendo horas_extra = 0.
 */
@WebServlet("/RechazarHorasExtra")
public class RechazarHorasExtra extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) { resp.sendRedirect("admin.jsp"); return; }
        try {
            int id = Integer.parseInt(idStr);
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE fichajes SET horas_extra = 0 WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            resp.sendRedirect("admin.jsp");
        } catch (Exception ex) {
            ex.printStackTrace();
            resp.sendRedirect("admin.jsp?error=rechazo");
        }
    }
}
