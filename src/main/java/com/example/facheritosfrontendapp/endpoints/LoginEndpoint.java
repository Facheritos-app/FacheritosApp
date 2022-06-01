package com.example.facheritosfrontendapp.endpoints;

import com.example.facheritosfrontendapp.dto.LoginDTO;
import com.example.facheritosfrontendapp.dto.WorkerDTO;
import com.google.gson.Gson;
import okhttp3.*;


public class LoginEndpoint {

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient = new OkHttpClient();

    public WorkerDTO sendCredentials(String loginDTO) throws Exception {

        RequestBody body = RequestBody.create(loginDTO, JSON);

        Request loginRequest = new Request.Builder().url("http://localhost:8092/facheritosapp/api/login/auth").post(body).build();

        Response response = httpClient.newCall(loginRequest).execute();

        Gson gson = new Gson();

        WorkerDTO responseObject = gson.fromJson(response.body().string(), WorkerDTO.class);

        return responseObject;
    }

}
