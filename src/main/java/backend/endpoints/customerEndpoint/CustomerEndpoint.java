package backend.endpoints.customerEndpoint;


import backend.connectionBD.ConnectionBD;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CustomerEndpoint {

    /**
     * getCustomerForTableView: void -> Map<Boolean, ResultSet>
     * Purpose: this method obtains customer data from the database to fill the table in the customers tab.
     */
    public Map<Boolean, ResultSet> getCustomersForTableView(){
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement(
                    "SELECT * FROM person WHERE id_type_person = 4");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

}
