package com.example.facheritosfrontendapp.endpoints.loginEndpoint;

import com.example.facheritosfrontendapp.dto.loginDTO.LoginDTO;
import com.example.facheritosfrontendapp.dto.otherDTO.ErrorDTO;
import com.example.facheritosfrontendapp.dto.personDTO.TypePersonDTO;
import com.example.facheritosfrontendapp.dto.personDTO.WorkerDTO;
import com.example.facheritosfrontendapp.util.DotEnv;
import com.google.gson.Gson;
import okhttp3.*;

import java.util.HashMap;
import java.util.Map;
import io.github.cdimascio.dotenv.Dotenv;

public class LoginEndpoint {

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient = new OkHttpClient();

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
