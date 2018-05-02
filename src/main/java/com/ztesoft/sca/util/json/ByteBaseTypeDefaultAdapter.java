package com.ztesoft.sca.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ByteBaseTypeDefaultAdapter implements JsonSerializer<Byte>, JsonDeserializer<Byte> {
    @Override
    public Byte deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context)
                             throws JsonParseException {
        try {
            if (json.getAsString().equals("")){
                return 0;
            }
        } catch (Exception ignore){
        }
        try {
            return json.getAsByte();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(Byte src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }

}
