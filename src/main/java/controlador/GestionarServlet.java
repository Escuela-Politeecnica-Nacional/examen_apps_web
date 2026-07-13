package controlador;

import java.io.IOException;
import java.math.BigDecimal;

import dao.SistemaDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Cliente;
import modelo.EstadoCredito;
import modelo.ResultadoEvaluacionCredito;

@WebServlet(name = "GestionarServlet", urlPatterns = { "/gestionar" })
public class GestionarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final SistemaDao sistemaDao = new SistemaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null || accion.isBlank() || "solicitar".equalsIgnoreCase(accion)) {
            solicitar(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if ("evaluarCredito".equalsIgnoreCase(accion)) {
            evaluarCredito(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    public void solicitar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/creditoplazos.jsp");
        dispatcher.forward(request, response);
    }

    public void evaluarCredito(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer edad = parseInteger(request.getParameter("edad"));
        BigDecimal ingresos = parseBigDecimal(request.getParameter("ingresosDeclarados"));
        BigDecimal montoSolicitado = parseBigDecimal(request.getParameter("montoSolicitado"));
        Integer plazoMeses = parseInteger(request.getParameter("plazoMeses"));

        Cliente cliente = new Cliente();
        cliente.setEdad(edad);
        cliente.setIngresosDeclarados(ingresos);

        ResultadoEvaluacionCredito resultado = sistemaDao.evaluarCredito(cliente, montoSolicitado, plazoMeses);

        request.setAttribute("resultado", resultado);
        request.setAttribute("cliente", cliente);
        request.setAttribute("montoSolicitado", montoSolicitado);
        request.setAttribute("plazoMeses", plazoMeses);

        if (resultado.getEstado() == EstadoCredito.RECHAZADO) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/metodosTradicionales.jsp");
            dispatcher.forward(request, response);
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/creditoplazos.jsp");
        dispatcher.forward(request, response);
    }

    private Integer parseInteger(String value) {
        try {
            return value == null || value.isBlank() ? null : Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            return value == null || value.isBlank() ? null : new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
