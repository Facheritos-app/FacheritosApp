package com.example.facheritosfrontendapp.endpoints.typePersonEndpoint;

import com.example.facheritosfrontendapp.dto.personDTO.TypePersonDTO;
import com.google.gson.Gson;
import okhttp3.*;


public class TypePersonEndpoint {

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient = new OkHttpClient();

    public TypePersonDTO getTypePerson (String id) throws Exception {

        Gson gson = new Gson();

        Request loginRequest = new Request.Builder().url("http://localhost:8092/facheritosapp/api/type-person/get/"+id).build(); //Set up the request

        Response response = httpClient.newCall(loginRequest).execute(); //Execute the request

        TypePersonDTO responseObject = gson.fromJson(response.body().string(), TypePersonDTO.class); //Cast the response from json string to object

        return responseObject;
    }
}
