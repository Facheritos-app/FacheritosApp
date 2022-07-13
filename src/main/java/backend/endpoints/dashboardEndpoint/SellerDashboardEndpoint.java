package backend.endpoints.dashboardEndpoint;

import backend.connectionBD.ConnectionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SellerDashboardEndpoint {

    /**
     * amountOfSalesPerMonthChart: void -> Map<Boolean, Integer>
     * Purpose: This method connects to the DB and returns the total number of sales per month, two columns
     */
    public Map<Boolean, ResultSet> amountOfSalesChart(String category, Integer year) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            if (category.equals("year")) {
                preparedStatement = conn.prepareStatement("SELECT date_part(?, sale_date) AS cat, COUNT(*) AS total FROM sale GROUP BY cat ");
                preparedStatement.setString(1, category);
            } else if (category.equals("month")) {
                preparedStatement = conn.prepareStatement("SELECT date_part(?, sale_date) AS cat, COUNT(*) AS total FROM sale WHERE date_part('year', sale_date) = ? GROUP BY cat ");
                preparedStatement.setString(1, category);
                preparedStatement.setInt(2, year);
            }
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * amountOfSalesPerMonthChart: void -> Map<Boolean, Integer>
     * Purpose: This method connects to the DB and returns the total number of sales per month, two columns
     */
    public Map<Boolean, ResultSet> customersChart(Integer selectionType, Integer year, Integer id) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            //If the yAxis is about the amount of sales
            if (selectionType.equals(0)) {
                //If the id is the id number (cc)
                if(id == 0) {
                    preparedStatement = conn.prepareStatement("SELECT cc,COUNT(*) AS total FROM sale " +
                            "JOIN person ON person.id_person=sale.id_customer " +
                            "WHERE DATE_PART('year', sale_date) = ? GROUP BY cc ORDER BY total DESC LIMIT 10");
                }
                //if the id is the name of the customer
                else if(id == 1) {
                    preparedStatement = conn.prepareStatement("SELECT CONCAT(last_name, ' ', first_name) AS name, COUNT(*) AS total FROM sale " +
                            "JOIN person ON person.id_person=sale.id_customer " +
                            "WHERE DATE_PART('year', sale_date) = ? GROUP BY name ORDER BY total DESC LIMIT 10");
                }
                preparedStatement.setInt(1, year);
            }
            //If the yAxis is about the total (price) sold
            else if (selectionType.equals(1)) {
                preparedStatement = conn.prepareStatement("");
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
