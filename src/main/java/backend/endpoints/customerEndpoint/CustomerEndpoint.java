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


    /**
     * getCustomerById: Integer -> Map<Boolean, ResultSet>
     * Purpose: gets a customer from the database according to an input Id.
     */
    public Map<Boolean, ResultSet> getCustomerById(Integer idPerson){
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT * FROM person WHERE id_person = ?");
            preparedStatement.setInt(1,idPerson);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    public Map<Boolean, ResultSet> getCustomerByCc(String cc){
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement(
                    "SELECT * FROM person WHERE id_type_person = 4" +
                            "AND cc = ?");
            preparedStatement.setString(1, cc);
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.isBeforeFirst()){
                response.put(false, resultSet);
                return response;
            }
            resultSet.next();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

}
