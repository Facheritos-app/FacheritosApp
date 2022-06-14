package backend.endpoints.workerEndpoint;

import backend.connectionBD.ConnectionBD;
import backend.dto.personDTO.PersonDTO;
import backend.dto.personDTO.WorkerDTO;
import backend.endpoints.personEndpoint.PersonEndpoint;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class WorkerEndpoint {

    private PersonEndpoint personEndpoint = new PersonEndpoint();

    /**
     * createWorker: WorkerDTO -> Boolean
     * Purpose: This method connects to the DB and saves a worker,
     * if successful, it returns true, if not it returns false
     */
    public Boolean createWorker(WorkerDTO worker){
        PreparedStatement preparedStatement = null;
        HashMap<Boolean, Integer> responsePerson = (HashMap<Boolean, Integer>) personEndpoint.createPerson(worker);
        if(responsePerson.containsKey(true)){
            worker.setId_person(responsePerson.get(true));
            try(Connection conn = ConnectionBD.connectDB().getConnection()){
                preparedStatement = conn.prepareStatement("INSERT INTO worker(id_person, id_headquarter, password, state, salary)" +
                        "VALUES (?,?,?,?,?)");
                preparedStatement.setInt(1, worker.getId_person());
                preparedStatement.setInt(2, worker.getId_headquarter());
                preparedStatement.setString(3, "hola123");
                preparedStatement.setBoolean(4, worker.getState());
                preparedStatement.setDouble(5, worker.getSalary());
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            System.out.println("No se ha podido crear la persona en la BD");
            return false;
        }
    }

    /**
     * getWorkers: void -> Map<Boolean, ResultSet>
     * Purpose: This method contains all the logic to connect the DB and get all the workers,
     * if everything went alright, it returns a hashmap with true and the required ResultSet,
     * if not, it returns a hashmap with false and a ResultSet with null
     */
    public Map<Boolean, ResultSet> getWorkersForTableView(){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT * FROM person JOIN worker USING(id_person) JOIN headquarter USING(id_headquarter)" +
                    "JOIN type_person USING(id_type_person)");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    public Map<Boolean, ResultSet> getWorkerById(Integer idPerson){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT * FROM person JOIN worker USING(id_person) JOIN headquarter USING(id_headquarter)" +
                    "JOIN city USING(id_city)" +
                    "WHERE id_person = ?");
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
}
