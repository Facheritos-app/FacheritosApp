package com.example.facheritosfrontendapp.endpoints.loginEndpoint;

import com.example.facheritosfrontendapp.dto.loginDTO.LoginDTO;
import com.example.facheritosfrontendapp.dto.personDTO.WorkerDTO;
import com.google.gson.Gson;
import okhttp3.*;


public class LoginEndpoint {

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient = new OkHttpClient();

    public WorkerDTO sendCredentials(LoginDTO loginDTO) throws Exception {

        Gson gson = new Gson();

        String loginDtoJson = gson.toJson(loginDTO); //From object to string value in json format

        RequestBody body = RequestBody.create(loginDtoJson, JSON); //Create the json from the string format

        Request loginRequest = new Request.Builder().url("http://localhost:8092/facheritosapp/api/login/auth").post(body).build(); //Set up the request

        Response response = httpClient.newCall(loginRequest).execute(); //Execute the request


        WorkerDTO responseObject = gson.fromJson(response.body().string(), WorkerDTO.class); //Cast the response from json string to object

        return responseObject;
    }

}
