package backend.endpoints.loginEndpoint;

import backend.connectionBD.ConnectionBD;
import backend.dto.loginDTO.LoginDTO;
import backend.dto.otherDTO.ErrorDTO;
import backend.dto.personDTO.WorkerDTO;
import backend.mapper.loginMapper.WorkerMapper;
import facheritosfrontendapp.util.DotEnv;
import com.google.gson.Gson;
import okhttp3.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class LoginEndpoint {

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient = new OkHttpClient();

    public Map<Boolean, WorkerDTO> getCredentials(LoginDTO loginDTO) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, WorkerDTO> response = new HashMap<>();
        WorkerDTO workerDTO = new WorkerDTO();
        try(Connection conn = ConnectionBD.connectDB().getConnection();){
            preparedStatement = conn.prepareStatement("SELECT * FROM person JOIN worker USING(id_person) JOIN type_person USING(id_type_person)" +
                    "WHERE person.cc = ?");
            preparedStatement.setString(1, loginDTO.getCc());
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
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




    public Map<Integer, Object> sendCredentials(LoginDTO loginDTO) throws Exception {


        Gson gson = new Gson();



        String loginDtoJson = gson.toJson(loginDTO); //From object to string value in json format

        RequestBody body = RequestBody.create(loginDtoJson, JSON); //Create the json from the string format

        Request loginRequest = new Request.Builder().url(DotEnv.getEnv("URL_API")+"/login/auth").post(body).build(); //Set up the request

        Response response = httpClient.newCall(loginRequest).execute(); //Execute the request

        HashMap<Integer, Object> mapReturn = new HashMap<Integer, Object>();

        if(response.code() == 200){
            WorkerDTO responseObject = gson.fromJson(response.body().string(), WorkerDTO.class); //Cast the response from json string to object
            mapReturn.put(response.code(), responseObject);
            return mapReturn;
        }else{
            ErrorDTO responseError = gson.fromJson(response.body().string(), ErrorDTO.class);
            mapReturn.put(response.code(), responseError);
            return mapReturn;
        }
    }


}
