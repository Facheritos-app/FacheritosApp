package backend.endpoints.customerEndpoint;


import backend.connectionBD.ConnectionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CustomerEndpoint {

    /**
     * getCustomerForTableView: void -> Map<Boolean, ResultSet>
     * Purpose: obtains customer data from the database to fill the table in the customers tab.
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

    /**
     * updateCustomer: Integer, String, String, String, LocalDate, String, Integer, Integer, Boolean, Double -> void>
     * Purpose: updates a customer
     */
    public static void updateCustomer(Integer idPerson, String cc, String name, String lastname, String cellphone,
                                    LocalDate birthday, String email){
        PreparedStatement preparedStatement;
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("UPDATE person SET cc = ?, first_name = ?, last_name = ? , " +
                    "cellphone = ?, birthday = ?, email = ? WHERE id_person = ?;");
            preparedStatement.setString(1, cc);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, lastname);
            preparedStatement.setString(4, cellphone);
            preparedStatement.setDate(5, Date.valueOf(birthday));
            preparedStatement.setString(6, email);
            preparedStatement.setInt(7, idPerson);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

}
