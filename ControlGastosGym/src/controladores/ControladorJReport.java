/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author nico
 */
public class ControladorJReport {

    private JasperReport reporte;
    private final String logo = "/Reporte/logo.png";

    public ControladorJReport(String jasper) throws JRException, ClassNotFoundException, SQLException {
        reporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/Reporte/" + jasper));//cargo el reporte
    }

    //listado de clientes productos y proveedores.
    public void mostrarReporte(Date desde, Date hasta) throws ClassNotFoundException, SQLException, JRException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/gestionGym", "root", "root");
        Map parametros = new HashMap();
        parametros.clear();
        parametros.put("logo", this.getClass().getResourceAsStream(logo));
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, connection);
        JasperViewer.viewReport(jasperPrint, false);
        connection.close();
    }


}