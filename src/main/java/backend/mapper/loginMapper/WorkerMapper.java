package backend.mapper.loginMapper;


import backend.dto.personDTO.WorkerDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class WorkerMapper {

    public static WorkerDTO map(ResultSet resultSet) throws SQLException {
        WorkerDTO workerDTO = new WorkerDTO();

        workerDTO.setId_person(resultSet.getInt("id_person"));
        workerDTO.setId_worker(resultSet.getInt("id_worker"));
        workerDTO.setFirst_name(resultSet.getString("first_name"));
        workerDTO.setLast_name(resultSet.getString("last_name"));
        workerDTO.setCc(resultSet.getString("cc"));
        workerDTO.setBirthday(resultSet.getDate("birthday").toLocalDate());
        workerDTO.setEmail(resultSet.getString("email"));
        workerDTO.setId_headquarter(resultSet.getInt("id_headquarter"));
        workerDTO.setState(resultSet.getBoolean("state"));
        workerDTO.setSalary(resultSet.getDouble("salary"));
        workerDTO.setPassword(resultSet.getString("password"));
        workerDTO.setId_rol(resultSet.getInt("id_type_person"));
        workerDTO.setRol(resultSet.getString("rol_person"));
        workerDTO.setHired_date(resultSet.getDate("hired_date"));

        return workerDTO;
    }

}
