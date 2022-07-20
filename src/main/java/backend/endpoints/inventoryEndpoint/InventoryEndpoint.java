package backend.endpoints.inventoryEndpoint;

import backend.connectionBD.ConnectionBD;
import backend.dto.inventoryDTO.PartDTO;
import backend.dto.inventoryDTO.VehicleDTO;
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
                    "JOIN model USING (id_model) WHERE car.status='Activo'");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }

        return response;
    }

    public Map<Boolean, ResultSet> getVehicleById(Integer idCar, Integer idHeadquarter){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT * FROM car JOIN car_headquarter USING (id_car)" +
                            "JOIN headquarter USING (id_headquarter) JOIN color USING (id_color)" +
                            "JOIN model USING (id_model) WHERE id_car = ? AND id_headquarter = ?");
            preparedStatement.setInt(1, idCar);
            preparedStatement.setInt(2, idHeadquarter);
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
     * createPart: PartDTO -> Map<Boolean, Integer>
     * Purpose: This method connects to the DB and saves a part, it makes the first
     * necessary query in order to save the part in the DB,
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
    /**
     * completeCreatePart: PartDTO -> Boolean
     * Purpose: This method connects to the DB and saves a part,
     * if successful, it returns true, if not it returns false
     */
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

    /**
     * createVehicle: VehicleDTO -> Map<Boolean, Integer>
     * Purpose: This method connects to the DB and saves a vehicle, it makes the first
     * necessary query in order to save the part in the DB,
     */
    public Map<Boolean, Integer> createVehicle(VehicleDTO vehicle){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, Integer> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("INSERT INTO car(id_model, assemble_year, id_color, image) VALUES(?,?,?,?) RETURNING id_car");
            preparedStatement.setInt(1, vehicle.getIdModel());
            preparedStatement.setString(2, vehicle.getAssembleYear());
            preparedStatement.setInt(3, vehicle.getIdColor());
            preparedStatement.setString(4, vehicle.getImageLink());
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet.getInt("id_car"));
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, -1);
        }
        return response;
    }

    /**
     * completeCreateVehicle: VehicleDTO -> Boolean
     * Purpose: This method connects to the DB and saves a vehicle,
     * if successful, it returns true, if not it returns false
     */
    public Boolean completeCreateVehicle(VehicleDTO vehicle){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Integer idCar = null;
        Integer idHeadquarter = null;
        HashMap<Boolean, Integer> responseIdPart = (HashMap<Boolean, Integer>) createVehicle(vehicle);
        if(responseIdPart.containsKey(true)){
            idCar = responseIdPart.get(true);
            try(Connection conn = ConnectionBD.connectDB().getConnection()){
                preparedStatement = conn.prepareStatement("INSERT INTO car_headquarter(id_car, id_headquarter, quantity) VALUES(?,?,?)");
                preparedStatement.setInt(1,idCar);
                preparedStatement.setInt(2, vehicle.getIdHeadquarter());
                preparedStatement.setInt(3, vehicle.getQuantity());
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            System.out.print("Algo salió mal creando el vehículo :(");

            return false;
        }
    }

    /**
     * editPart: PartDTO -> Map<Boolean, Integer>
     * Purpose: This method connects to the DB and edits a part, it makes the first
     * necessary query in order to edit the part in the DB,
     */
    public Map<Boolean, Integer> editPart(PartDTO part){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, Integer> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("UPDATE part SET name=?, price=?, description=? WHERE id_part=? RETURNING id_part");
            preparedStatement.setString(1, part.getName());
            preparedStatement.setDouble(2, part.getPrice());
            preparedStatement.setString(3, part.getDescription());
            preparedStatement.setInt(4, part.getIdPart());

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet.getInt("id_part"));
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, -1);
        }
        return response;
    }

    /**
     * checkPartElements: PartDTO -> Map<Boolean, ResultSet>
     * Purpose: This method connects to the DB and checks if there are elements in the part_inventory table
     * with specific conditions
     */
    public Map<Boolean, ResultSet> checkPartElements(PartDTO part){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT * FROM part_inventory WHERE id_part = ? AND id_headquarter = ?");
            preparedStatement.setInt(1, part.getIdPart());
            preparedStatement.setInt(2, part.getId_headquarter());
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.isBeforeFirst()){ //if there are no rows
                response.put(true, resultSet);
                return response;
            }
            resultSet.next();
            response.put(false, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }
    /**
     * complteteEditPart: VehicleDTO -> Boolean
     * Purpose: This method connects to the DB and saves a vehicle,
     * if successful, it returns true, if not it returns false
     */
    public Boolean completeEditPart(PartDTO part, Integer oldIdHearquater) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Integer idPart = null;
        Integer idHeadquarter = null;
        HashMap<Boolean, Integer> responseIdPart = (HashMap<Boolean, Integer>) editPart(part);
        HashMap<Boolean, ResultSet> responseElements = (HashMap<Boolean, ResultSet>) checkPartElements(part);
        if(responseIdPart.containsKey(true) && responseElements.containsKey(true)){
           idPart = responseIdPart.get(true);
            try(Connection conn = ConnectionBD.connectDB().getConnection()){
                preparedStatement = conn.prepareStatement("UPDATE part_inventory SET quantity=?, id_headquarter=? WHERE id_part=? AND id_headquarter=?");
                preparedStatement.setInt(1, part.getQuantity());
                preparedStatement.setInt(2, part.getId_headquarter());
                preparedStatement.setInt(3, part.getIdPart());
                preparedStatement.setInt(4, oldIdHearquater);
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            throw new Exception("ERROR: part_inventory row already exists");
        }
    }
    /**
     * changeOnlyPartQuantity: PartDTO -> Boolean
     * Purpose: This method connects to the DB, it changes the quantity of a car part when the headquarter
     *  stays the same.
     */
    public Boolean changeOnlyPartQuantity(PartDTO part){
        PreparedStatement preparedStatement = null;
        Integer idPart = null;
        HashMap<Boolean, Integer> responseIdPart = (HashMap<Boolean, Integer>) editPart(part);
        if(responseIdPart.containsKey(true)){
            idPart = responseIdPart.get(true);
            try(Connection conn = ConnectionBD.connectDB().getConnection()) {
                preparedStatement = conn.prepareStatement("UPDATE part_inventory SET quantity=? WHERE id_part=? AND id_headquarter=?");
                preparedStatement.setInt(1, part.getQuantity());
                preparedStatement.setInt(2, part.getIdPart());
                preparedStatement.setInt(3, part.getId_headquarter());
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            System.out.print("Algo salio mal modificando el repuesto :(");
            return false;
        }
    }
    /**
     * changeOnlyVehicleQuantity: VehicleDTO -> Boolean
     * Purpose: This method connects to the DB, it changes the quantity of a car when the headquarter
     *  stays the same.
     */
    public Boolean changeOnlyVehicleQuantity(VehicleDTO vehicle){
        PreparedStatement preparedStatement = null;
        Integer idPart = null;
        HashMap<Boolean, Integer> responseIdPart = (HashMap<Boolean, Integer>) editVehicle(vehicle);
        if(responseIdPart.containsKey(true)){
            idPart = responseIdPart.get(true);
            try(Connection conn = ConnectionBD.connectDB().getConnection()) {
                preparedStatement = conn.prepareStatement("UPDATE car_headquarter SET quantity=? WHERE id_car=? AND id_headquarter=?");
                preparedStatement.setInt(1, vehicle.getQuantity());
                preparedStatement.setInt(2, vehicle.getIdCar());
                preparedStatement.setInt(3, vehicle.getIdHeadquarter());
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            System.out.print("Algo salio mal modificando el repuesto :(");
            return false;
        }
    }
    /**
     * checkVehicleElements: PartDTO -> Map<Boolean, ResultSet>
     * Purpose: This method connects to the DB and checks if there are elements in the part_inventory table
     * with specific conditions
     */
    public Map<Boolean, ResultSet> checkVehicleElements(VehicleDTO vehicle){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT * FROM car_headquarter WHERE id_car = ? AND id_headquarter = ?");
            preparedStatement.setInt(1, vehicle.getIdCar());
            preparedStatement.setInt(2, vehicle.getIdHeadquarter());
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.isBeforeFirst()){ //if there are no rows
                response.put(true, resultSet);
                return response;
            }
            resultSet.next();
            response.put(false, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }
    /**
     * editVehicle: PartDTO -> Map<Boolean, Integer>
     * Purpose: This method connects to the DB and edits a vehicle, it makes the first
     * necessary query in order to edit the vehicle in the DB,
     */
    public Map<Boolean, Integer> editVehicle(VehicleDTO vehicle){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, Integer> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("UPDATE car SET id_model=?, assemble_year=?, id_color=?, image=? WHERE id_car=? RETURNING id_car");
            preparedStatement.setInt(1, vehicle.getIdModel());
            preparedStatement.setString(2, vehicle.getAssembleYear());
            preparedStatement.setInt(3, vehicle.getIdColor());
            preparedStatement.setString(4, vehicle.getImageLink());
            preparedStatement.setInt(5, vehicle.getIdCar());

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet.getInt("id_car"));
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, -1);
        }
        return response;
    }
    /**
     * completeEditVehicle: VehicleDTO -> Boolean
     * Purpose: This method connects to the DB and modifies a vehicle
     * if successful, it returns true, if not it returns false
     */
    public Boolean completeEditVehicle(VehicleDTO vehicle, Integer oldIdHearquater) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Integer idPart = null;
        Integer idHeadquarter = null;
        HashMap<Boolean, ResultSet> responseElements = (HashMap<Boolean, ResultSet>) checkVehicleElements(vehicle);
        HashMap<Boolean, Integer> responseIdPart = (HashMap<Boolean, Integer>) editVehicle(vehicle);
        if(responseIdPart.containsKey(true) && responseElements.containsKey(true)){
            idPart = responseIdPart.get(true);
            try(Connection conn = ConnectionBD.connectDB().getConnection()){
                preparedStatement = conn.prepareStatement("UPDATE car_headquarter SET quantity=?, id_headquarter=? WHERE id_car=? AND id_headquarter=?");
                preparedStatement.setInt(1, vehicle.getQuantity());
                preparedStatement.setInt(2, vehicle.getIdHeadquarter());
                preparedStatement.setInt(3, vehicle.getIdCar());
                preparedStatement.setInt(4, oldIdHearquater);
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            throw new Exception("ERROR: car_headquarter row already exists");
        }
    }
    /**
     * deleteVehicle: Integer -> Boolean
     * Purpose: sets the state of the vehicle as inactive, but it does not delete it definitively.
     */
    public Boolean deleteVehicle(Integer idVehicle) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
            try(Connection conn = ConnectionBD.connectDB().getConnection()){
                preparedStatement = conn.prepareStatement("UPDATE car SET status='Inactivo' WHERE id_car = ?");
                preparedStatement.setInt(1, idVehicle);
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

    }
}
