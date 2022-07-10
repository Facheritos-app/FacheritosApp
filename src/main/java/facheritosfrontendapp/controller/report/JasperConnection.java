package facheritosfrontendapp.controller.report;

import backend.connectionBD.ConnectionBD;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JasperConnection {

    private String reportPath;
    private JasperReport jasperReport;
    private JasperPrint jasperPrint;
    private JRDataSource dataSource;
    private Map<String, Object> parameters;


    public JasperConnection(String reportPath){
        jasperPrint = new JasperPrint();
        dataSource = new JREmptyDataSource();
        parameters = new HashMap<>();
        this.reportPath = reportPath;
        try {
            jasperReport = JasperCompileManager.compileReport(this.reportPath);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    public void addParameters(String key, Object value){
        parameters.put(key, value);
    }

    public void showReport(){
        try {
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, ConnectionBD.connectDB().getConnection());
            JasperViewer jasperViewer = new JasperViewer(jasperPrint);
            jasperViewer.setVisible(true);
        } catch (JRException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
