package backend.endpoints.personEndpoint;

import backend.connectionBD.ConnectionBD;
import backend.dto.personDTO.PersonDTO;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PersonEndpoint {


    public Map<Boolean, Integer> createPerson(PersonDTO person){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, Integer> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("INSERT INTO person(cc, first_name, last_name, cellphone, birthday, email, id_type_person)" +
                    "VALUES(?,?,?,?,?,?,?) RETURNING id_person");
            preparedStatement.setString(1,person.getCc());
            preparedStatement.setString(2, person.getFirst_name());
            preparedStatement.setString(3, person.getLast_name());
            preparedStatement.setString(4, person.getCellphone());
            preparedStatement.setDate(5, Date.valueOf(person.getBirthday()));
            preparedStatement.setString(6, person.getEmail());
            preparedStatement.setInt(7, person.getId_type_person());
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet.getInt("id_person"));
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, -1);
        }

        return response;
    }

}
