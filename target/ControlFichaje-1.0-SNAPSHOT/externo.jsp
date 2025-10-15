<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Consulta Externa</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <h1>Consulta de Informes</h1>
    
    <!-- Filtros -->
    <form action="GenerarInforme" method="post">
        Usuario: <input type="text" name="usuario">
        Desde: <input type="date" name="desde">
        Hasta: <input type="date" name="hasta">
        <input type="submit" value="Generar Informe">
    </form>

    <!-- Resultado o tabla de informe (opcional) -->
</body>
</html>
