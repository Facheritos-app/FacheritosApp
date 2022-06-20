package backend.endpoints.inventoryEndpoint;

import backend.connectionBD.ConnectionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InventoryEndpoint {


    public Map<Boolean, ResultSet> getVehiclesForTableView() {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("SELECT * FROM car JOIN car_headquarter USING (id_car)" +
                    "JOIN headquarter USING (id_headquarter)" +
                    "JOIN model USING (id_model)");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }

        return response;
    }

    public Map<Boolean, ResultSet> getVehicleById(Integer idCar){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT * FROM car JOIN car_headquarter USING (id_car)" +
                            "JOIN headquarter USING (id_headquarter) JOIN color USING (id_color)" +
                            "JOIN model USING (id_model) WHERE id_car = ?");
            preparedStatement.setInt(1, idCar);
            resultSet = preparedStatement.executeQuery();
            resultSet.next(); //show the row data
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }
    public Map<Boolean, ResultSet> getPartsForTableView() {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("SELECT *, headquarter.name AS hq FROM part JOIN part_inventory USING(id_part)" +
                    "JOIN headquarter USING(id_headquarter)" +
                    "JOIN city USING(id_city)");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    public Map<Boolean, ResultSet> getPartById(Integer idPart){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT *, headquarter.name AS hq FROM part JOIN part_inventory USING(id_part)" +
                    "JOIN headquarter USING(id_headquarter)" +
                    "JOIN city USING(id_city) WHERE id_part = ?");
            preparedStatement.setInt(1, idPart);
            resultSet = preparedStatement.executeQuery();
            resultSet.next(); //show the row data
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }
}
