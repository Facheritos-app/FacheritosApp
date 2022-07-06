package backend.endpoints.loginEndpoint;

import backend.connectionBD.ConnectionBD;
import backend.dto.loginDTO.LoginDTO;
import backend.dto.personDTO.WorkerDTO;
import backend.mapper.loginMapper.WorkerMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class LoginEndpoint {

    public Map<Boolean, WorkerDTO> getCredentials(LoginDTO loginDTO) {

        //Pura chachara
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, WorkerDTO> response = new HashMap<>();
        WorkerDTO workerDTO = new WorkerDTO();
        try(Connection conn = ConnectionBD.connectDB().getConnection();){
            preparedStatement = conn.prepareStatement("SELECT *, (SELECT name FROM headquarter JOIN worker USING(id_headquarter)" +
                    "JOIN person USING(id_person) WHERE cc=?) AS hq FROM person JOIN worker USING(id_person) JOIN type_person USING(id_type_person) WHERE person.cc = ?");
            preparedStatement.setString(1, loginDTO.getCc());
            preparedStatement.setString(2, loginDTO.getCc());
            resultSet = preparedStatement.executeQuery();
            resultSet.next();//there must be a call to next for every row
            if(resultSet.getString("password").equals(loginDTO.getPassword())){
                workerDTO = WorkerMapper.map(resultSet);
                response.put(true, workerDTO);
            }else{
                response.put(false, workerDTO);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return response;

    }

}
