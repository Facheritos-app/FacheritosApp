package backend.endpoints.dashboardEndpoint;

import backend.connectionBD.ConnectionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MechanicDashboardEnpoint {

    /**
     * modelsChart:  -> Map<Boolean, ResultSet>
     * Purpose: This method connects to the DB and returns the data for the models chart
     */
    public Map<Boolean, ResultSet> partsChart(Integer idPart, Integer selectionType, Integer year) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            //Quantity of parts sold
            if(selectionType == 0) {
                preparedStatement = conn.prepareStatement(" SELECT DATE_PART('month', job_order.created_at) AS data, SUM(job_order_part.quantity) AS total " +
                        " FROM job_order JOIN job_order_part USING(id_job_order) JOIN part USING(id_part) " +
                        " WHERE job_order_part.id_part = ? AND DATE_PART('year', job_order.created_at) = ?" +
                        "GROUP BY data");
            }
            //Price of parts sold
            else if(selectionType == 1){
                preparedStatement = conn.prepareStatement("SELECT DATE_PART('month', job_order.created_at) AS data, SUM(part.price.) AS total " +
                        " FROM job_order JOIN job_order_part USING(id_job_order) " +
                        " JOIN part USING(id_part)" +
                        " WHERE job_order_part.id_part = ? AND DATE_PART('year', job_order.created_at) = ?" +
                        "GROUP BY data");
            }

            preparedStatement.setInt(1, idPart);
            preparedStatement.setInt(2, year);
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }
}
