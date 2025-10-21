<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.time.format.DateTimeFormatter" %>
<%
    // Recuperar fichajes de sesión (misma estructura que admin.jsp)
    List<com.mycompany.controlfichaje.FichajeModel> fichajes = (List<com.mycompany.controlfichaje.FichajeModel>) session.getAttribute("fichajes");
    if (fichajes == null) {
        fichajes = new ArrayList<>();
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Perfil (solo lectura)</title>
    <link rel="stylesheet" href="css/styles.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.12.1/css/jquery.dataTables.min.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.datatables.net/1.12.1/js/jquery.dataTables.min.js"></script>
    <link rel="icon" type="image/x-icon" href="favicon.ico">
</head>

<body>
    <!-- Botón Cerrar sesión (fijo en la ventana) -->
    <div class="top-right-controls">
        <form method="post" action="LogoutServlet">
            <button type="submit" class="cerrar-btn">Cerrar sesión</button>
        </form>
    </div>

    <div class="container">

        <div class="sidebar">
            <h2>Consulta externa</h2>
                <p class="small-note" style="color: var(--blanco) !important; font-weight: 400 !important; font-size: 16px !important;">Vista de solo lectura de los fichajes. Puedes descargar un informe en CSV.</p>
            
                <!-- Botón de descarga en la barra lateral -->
            <form onsubmit="return false;">
                <button id="downloadCsv" class="btn-accion" style="width:100%;">Descargar CSV</button>
            </form>
        </div>

        <div class="main">
            <h1>Perfil público - Consulta</h1>
            <p>A continuación se muestra la tabla de fichajes en modo sólo lectura.</p>

            <table id="tablaExterna" class="admin-table" style="margin-top: 10px; width:100%;">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Apellido</th>
                        <th>Rol</th>
                        <th>Fecha</th>
                        <th>Entrada</th>
                        <th>Salida</th>
                        <th>Descanso</th>
                        <th>Comida</th>
                        <th>Horas/semana</th>
                    </tr>
                </thead>
                <tbody>
                <% for (com.mycompany.controlfichaje.FichajeModel f : fichajes) { %>
                    <tr>
                        <td><%= f.getId() %></td>
                        <td><%= f.getNombre() %></td>
                        <td><%= f.getApellido() %></td>
                        <td><%= f.getRol() %></td>
                        <td><%= f.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) %></td>
                        <td><%= f.getEntrada() %></td>
                        <td><%= f.getSalida() %></td>
                        <td><%= f.getDescanso() %></td>
                        <td><%= f.getComida() %></td>
                        <td><%= f.getHorasSemanales() %></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <script>
        $(document).ready(function() {
            $('#tablaExterna').DataTable({ paging: true, searching: true, ordering: true, pageLength: 100 });
                    

            $('#downloadCsv').on('click', function() {
                var rows = [];
                $('#tablaExterna thead th').each(function() { rows.push('"' + $(this).text().replace(/"/g, '""') + '"'); });
                var csv = rows.join(',') + '\n';
                $('#tablaExterna tbody tr').each(function() {
                    var cols = [];
                    $(this).find('td').each(function() { cols.push('"' + $(this).text().replace(/"/g, '""') + '"'); });
                    csv += cols.join(',') + '\n';
                });

                var BOM = '\uFEFF'; // BOM para forzar lectura UTF-8
                var blob = new Blob([BOM + csv], { type: 'text/csv;charset=utf-8;' });

                var url = URL.createObjectURL(blob);
                var a = document.createElement('a');
                a.href = url;
                a.download = 'fichajes_export.csv';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                URL.revokeObjectURL(url);
            });
        });
    </script>

    <script>
    function alignLogoutButton() {
        var h1 = document.querySelector('.main h1');
        var wrapper = document.querySelector('.top-right-controls');
        if (!h1 || !wrapper) return;
        var h1Rect = h1.getBoundingClientRect();
        var btn = wrapper.querySelector('.cerrar-btn');
        var btnHeight = btn ? btn.offsetHeight : wrapper.offsetHeight;
        var top = h1Rect.top + (h1Rect.height - btnHeight) / 2;
        if (top < 6) top = 6;
        wrapper.style.top = top + 'px';
    }
    window.addEventListener('load', alignLogoutButton);
    window.addEventListener('resize', alignLogoutButton);
    </script>
</body>
</html>
