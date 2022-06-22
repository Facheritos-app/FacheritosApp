package backend.endpoints.orderEndpoint;

import backend.connectionBD.ConnectionBD;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class OrderEndpoint {

    /**
     * getOrdersForTableView: void -> Map<Boolean, ResultSet>
     * Purpose: this method obtains orders data from the database to fill the orders table.
     */
    public Map<Boolean, ResultSet> getOrdersForTableView(){
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement(
                    "SELECT *, headquarter.name AS seat,  state.name AS status FROM person JOIN job_order ON person.id_person = job_order.id_customer JOIN state USING(id_state) JOIN headquarter USING(id_headquarter)");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

}