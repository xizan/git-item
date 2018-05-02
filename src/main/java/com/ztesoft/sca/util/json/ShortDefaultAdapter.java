package com.ztesoft.sca.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ShortDefaultAdapter implements JsonSerializer<Short>, JsonDeserializer<Short> {
    @Override
    public Short deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context)
                             throws JsonParseException {
        try {
            if (json.getAsString().equals("")){
                return null;
            }
        } catch (Exception ignore){
        }
        try {
            return json.getAsShort();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(Short src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }

}