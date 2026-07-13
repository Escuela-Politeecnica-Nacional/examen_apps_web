<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.ResultadoEvaluacionCredito" %>
<%@ page import="modelo.EstadoCredito" %>
<%@ page import="java.math.BigDecimal" %>
<%
    ResultadoEvaluacionCredito resultado = (ResultadoEvaluacionCredito) request.getAttribute("resultado");
    BigDecimal montoSolicitado = (BigDecimal) request.getAttribute("montoSolicitado");
    Integer plazoMeses = (Integer) request.getAttribute("plazoMeses");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Crédito a Plazos</title>
    <style>
        :root {
            --bg: #0f172a;
            --panel: #111827;
            --card: #1f2937;
            --text: #e5e7eb;
            --muted: #94a3b8;
            --accent: #22c55e;
            --danger: #ef4444;
            --warning: #f59e0b;
            --border: rgba(255,255,255,.08);
        }
        * { box-sizing: border-box; }
        body {
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
            background: radial-gradient(circle at top, #1e293b 0, var(--bg) 55%);
            color: var(--text);
            min-height: 100vh;
            padding: 32px 16px;
        }
        .wrap { max-width: 920px; margin: 0 auto; }
        .hero {
            background: linear-gradient(135deg, rgba(34,197,94,.18), rgba(59,130,246,.08));
            border: 1px solid var(--border);
            border-radius: 24px;
            padding: 28px;
            margin-bottom: 20px;
        }
        h1 { margin: 0 0 8px; font-size: 2rem; }
        .sub { margin: 0; color: var(--muted); }
        .grid { display: grid; grid-template-columns: 1.1fr .9fr; gap: 20px; }
        .panel {
            background: rgba(17,24,39,.92);
            border: 1px solid var(--border);
            border-radius: 20px;
            padding: 22px;
            box-shadow: 0 24px 60px rgba(0,0,0,.25);
        }
        .field { margin-bottom: 14px; }
        label { display: block; margin-bottom: 6px; font-weight: 700; }
        input, select {
            width: 100%;
            padding: 12px 14px;
            border-radius: 12px;
            border: 1px solid var(--border);
            background: #0b1220;
            color: var(--text);
        }
        button, .link-btn {
            display: inline-block;
            border: 0;
            border-radius: 12px;
            padding: 12px 18px;
            text-decoration: none;
            font-weight: 700;
            cursor: pointer;
        }
        button { background: var(--accent); color: #06250f; }
        .link-btn { background: #334155; color: var(--text); }
        .result {
            padding: 18px;
            border-radius: 16px;
            margin-top: 16px;
            border: 1px solid var(--border);
            background: var(--card);
        }
        .approved { color: var(--accent); }
        .review { color: var(--warning); }
        .rejected { color: var(--danger); }
        .hint { color: var(--muted); font-size: .95rem; }
        .actions { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 16px; }
        @media (max-width: 800px) {
            .grid { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
<div class="wrap">
    <div class="hero">
        <h1>Solicitud de crédito a plazos</h1>
        <p class="sub">Evalúa elegibilidad, registra la orden y deriva el pago según el resultado.</p>
    </div>

    <div class="grid">
        <div class="panel">
            <form action="gestionar" method="post">
                <input type="hidden" name="accion" value="evaluarCredito">
                <div class="field">
                    <label for="edad">Edad</label>
                    <input id="edad" name="edad" type="number" min="1" required>
                </div>
                <div class="field">
                    <label for="ingresosDeclarados">Ingresos declarados</label>
                    <input id="ingresosDeclarados" name="ingresosDeclarados" type="number" step="0.01" min="0" required>
                </div>
                <div class="field">
                    <label for="montoSolicitado">Monto solicitado</label>
                    <input id="montoSolicitado" name="montoSolicitado" type="number" step="0.01" min="0" required>
                </div>
                <div class="field">
                    <label for="plazoMeses">Plazo en meses</label>
                    <select id="plazoMeses" name="plazoMeses" required>
                        <option value="6">6 meses</option>
                        <option value="12">12 meses</option>
                        <option value="18">18 meses</option>
                        <option value="24">24 meses</option>
                    </select>
                </div>
                <button type="submit">Evaluar crédito</button>
            </form>

            <% if (resultado != null) { %>
            <div class="result">
                <h2 class="<%= resultado.getEstado() == EstadoCredito.APROBADO ? "approved" : resultado.getEstado() == EstadoCredito.EN_REVISION ? "review" : "rejected" %>">
                    <%= resultado.getEstado() %>
                </h2>
                <p><%= resultado.getMensaje() %></p>
                <p class="hint">Puntaje: <%= resultado.getSolicitudCredito().getPuntaje() %> | Cuota mensual: <%= resultado.getSolicitudCredito().getCuotaMensual() %></p>
            </div>
            <% } %>
        </div>

        <div class="panel">
            <h2>Flujo del caso de uso</h2>
            <p class="hint">Cliente logeado, compra a plazos y validación automática con reglas de negocio.</p>
            <div class="actions">
                <a class="link-btn" href="gestionar?accion=solicitar">Solicitar crédito a plazos</a>
                <a class="link-btn" href="metodosTradicionales.jsp">Métodos tradicionales</a>
            </div>
            <div class="result">
                <strong>Reglas</strong>
                <p class="hint">Aprobado desde 75. En revisión entre 50 y 74. Rechazado si es menor de 50, si es menor de 18 o si la cuota supera el 40% de los ingresos declarados.</p>
            </div>
            <% if (resultado != null && resultado.getEstado() == EstadoCredito.RECHAZADO) { %>
            <div class="result">
                <strong>Derivación</strong>
                <p class="hint">La solicitud fue enviada a tarjeta o transferencia como alternativas de pago.</p>
                <a class="link-btn" href="metodosTradicionales.jsp">Ir a métodos tradicionales</a>
            </div>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>
