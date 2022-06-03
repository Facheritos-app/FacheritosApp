package facheritosfrontendapp.util;

import io.github.cdimascio.dotenv.Dotenv;

public class DotEnv {

    public static Dotenv dotenv = Dotenv.configure().directory("./src/main").filename("env").load();

    public static String getEnv(String envVarName){
        return dotenv.get(envVarName);
    }
}
