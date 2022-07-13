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
    public Map<Boolean, ResultSet> amountOfSalesPerMonthChart(String category, Integer year) {

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
}
