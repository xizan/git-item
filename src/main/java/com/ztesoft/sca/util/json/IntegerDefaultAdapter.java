package com.ztesoft.sca.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class IntegerDefaultAdapter implements JsonSerializer<Integer>, JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context)
                             throws JsonParseException {
        try {
            if (json.getAsString().equals("")){
                return null;
            }
        } catch (Exception ignore){
        }
        try {
            return json.getAsInt();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }
}