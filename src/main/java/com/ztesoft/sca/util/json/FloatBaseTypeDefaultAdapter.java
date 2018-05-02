package com.ztesoft.sca.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class FloatBaseTypeDefaultAdapter implements JsonSerializer<Float>, JsonDeserializer<Float> {
    @Override
    public Float deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context)
                             throws JsonParseException {
        try {
            if (json.getAsString().equals("")){
                return 0f;
            }
        } catch (Exception ignore){
        }
        try {
            return json.getAsFloat();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(Float src, Type typeOfSrc, JsonSerializationContext context) {
    	if(src!=null&&src>9007199254740992.0f){
    		return new JsonPrimitive(src.toString());
    	}
        return new JsonPrimitive(src);
    }

}

