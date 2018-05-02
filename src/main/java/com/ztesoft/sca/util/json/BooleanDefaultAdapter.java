package com.ztesoft.sca.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class BooleanDefaultAdapter implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context)
                             throws JsonParseException {
        try {
            if (json.getAsString().equals("")){
                return null;
            }
        } catch (Exception ignore){
        }
        try {
            return json.getAsBoolean();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(Boolean src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }

}
