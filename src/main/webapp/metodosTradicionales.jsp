<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Métodos Tradicionales</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
            background: #111827;
            color: #f9fafb;
            min-height: 100vh;
            display: grid;
            place-items: center;
            padding: 24px;
        }
        .card {
            max-width: 720px;
            width: 100%;
            background: #1f2937;
            border: 1px solid rgba(255,255,255,.08);
            border-radius: 24px;
            padding: 28px;
        }
        .pill {
            display: inline-block;
            padding: 8px 12px;
            border-radius: 999px;
            background: #374151;
            margin-right: 8px;
            margin-bottom: 8px;
        }
        a {
            display: inline-block;
            margin-top: 18px;
            color: #22c55e;
            text-decoration: none;
            font-weight: 700;
        }
    </style>
</head>
<body>
    <div class="card">
        <h1>Pago no aprobado para crédito a plazos</h1>
        <p>No es posible otorgar el crédito en este momento. Puedes continuar con estos métodos tradicionales:</p>
        <div>
            <span class="pill">Tarjeta</span>
            <span class="pill">Transferencia</span>
        </div>
        <a href="gestionar?accion=solicitar">Volver a solicitar crédito</a>
    </div>
</body>
</html>
