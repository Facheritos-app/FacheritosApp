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
}
