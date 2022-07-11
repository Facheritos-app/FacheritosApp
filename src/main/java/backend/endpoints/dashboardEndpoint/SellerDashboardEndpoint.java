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
    public Map<Boolean, ResultSet> amountOfSalesPerMonthChart() {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("SELECT date_part('month', sale_date) AS month, COUNT(*) AS total FROM sale GROUP BY month");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }
}
