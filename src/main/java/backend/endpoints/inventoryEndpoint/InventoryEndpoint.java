package backend.endpoints.inventoryEndpoint;

import backend.connectionBD.ConnectionBD;
import backend.dto.inventoryDTO.PartDTO;
import backend.dto.personDTO.WorkerDTO;

import java.sql.*;
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
    /**
     * createPart: PartDTO -> Boolean
     * Purpose: This method connects to the DB and saves a part,
     * if successful, it returns true, if not it returns false
     */
    public Map<Boolean, Integer> createPart(PartDTO part){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, Integer> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("INSERT INTO part(name, price, description) VALUES(?,?,?) RETURNING id_part");
            preparedStatement.setString(1, part.getName());
            preparedStatement.setDouble(2, part.getPrice());
            preparedStatement.setString(3, part.getDescription());
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet.getInt("id_part"));
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, -1);
        }
        return response;
    }

    public Boolean completeCreatePart(PartDTO part){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Integer idPart = null;
        Integer idHeadquarter = null; //FALTAAAA
        HashMap<Boolean, Integer> responseIdPart = (HashMap<Boolean, Integer>) createPart(part);
        if(responseIdPart.containsKey(true)){
            idPart = responseIdPart.get(true);
            try(Connection conn = ConnectionBD.connectDB().getConnection()){
                preparedStatement = conn.prepareStatement("INSERT INTO part_inventory(id_part, id_headquarter,quantity) VALUES(?,?,?)");
                preparedStatement.setInt(1,idPart);
                preparedStatement.setInt(2, part.getId_headquarter());
                preparedStatement.setInt(3, part.getQuantity());
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            System.out.print("Algo salio mal creando el respuesto :(");
            return false;
        }
    }
}
