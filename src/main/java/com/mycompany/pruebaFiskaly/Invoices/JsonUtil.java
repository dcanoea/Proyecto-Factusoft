package com.mycompany.pruebaFiskaly.Invoices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    // Serializar a String JSON
    public static String toJson(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    // Deserializar desde String JSON
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(json, clazz);
    }

    // Guardar objeto como archivo JSON
    public static void toJsonFile(Object obj, String filePath) throws IOException {
        mapper.writeValue(new File(filePath), obj);
    }

    // Leer objeto desde archivo JSON
    public static <T> T fromJsonFile(String filePath, Class<T> clazz) throws IOException {
        return mapper.readValue(new File(filePath), clazz);
    }
}
