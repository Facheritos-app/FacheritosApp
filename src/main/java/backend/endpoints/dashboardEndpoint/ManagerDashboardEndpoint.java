package backend.endpoints.dashboardEndpoint;

import backend.connectionBD.ConnectionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ManagerDashboardEndpoint {

    /**
     * sellersChart: Integer x3 -> Map<Boolean, ResultSet>
     * Purpose: This method connects to the DB and returns the data for the sellers chart
     */
    public Map<Boolean, ResultSet> sellersChart(Integer selectionType, Integer year, Integer id, Integer idHeadquarter) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            //If the yAxis has the amount of sales
            if(selectionType.equals(0)) {
                //If the id is the id number (cc)
                if(id == 0) {
                    preparedStatement = conn.prepareStatement("SELECT cc AS data,COUNT(*) AS total FROM sale " +
                            "JOIN person ON person.id_person=sale.id_worker " +
                            "WHERE DATE_PART('year', sale_date) = ? AND sale.id_headquarter = ? GROUP BY data ORDER BY total DESC LIMIT 10");
                }
                //if the id is the name of the customer
                else if(id == 1) {
                    preparedStatement = conn.prepareStatement("SELECT last_name AS data, COUNT(*) AS total FROM sale " +
                            "JOIN person ON person.id_person=sale.id_worker " +
                            "WHERE DATE_PART('year', sale_date) = ? AND sale.id_headquarter = ? GROUP BY data ORDER BY total DESC LIMIT 10");
                }
                preparedStatement.setInt(1, year);
                preparedStatement.setInt(2, idHeadquarter);
            }
            //If the yAxis has the total (price) sold
            else if(selectionType.equals(1)) {
                //If the id is the id number (cc)
                if(id == 0){
                    preparedStatement = conn.prepareStatement("SELECT cc AS data, SUM(price) AS total FROM sale " +
                            " JOIN person ON person.id_person=sale.id_worker " +
                            " WHERE DATE_PART('year', sale_date) = ? AND sale.id_headquarter = ? GROUP BY data ORDER BY total DESC LIMIT 10;");
                }
                //if the id is the name of the customer
                else if(id == 1){
                    preparedStatement = conn.prepareStatement("SELECT last_name AS data, SUM(price) AS total FROM sale " +
                            " JOIN person ON person.id_person=sale.id_worker " +
                            " WHERE DATE_PART('year', sale_date) = ? AND sale.id_headquarter = ? GROUP BY data ORDER BY total DESC LIMIT 10;");
                }
                preparedStatement.setInt(1, year);
                preparedStatement.setInt(2, idHeadquarter);
            }
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }
}
