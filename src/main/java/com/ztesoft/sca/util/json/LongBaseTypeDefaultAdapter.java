package com.ztesoft.sca.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class LongBaseTypeDefaultAdapter implements JsonSerializer<Long>, JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context)
                             throws JsonParseException {
        try {
            if (json.getAsString().equals("")){
                return 0l;
            }
        } catch (Exception ignore){
        }
        try {
            return json.getAsLong();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
    	if(src!=null&&src>9007199254740992l){
    		return new JsonPrimitive(src.toString());
    	}
        return new JsonPrimitive(src);
    }

}