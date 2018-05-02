package com.ztesoft.sca.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class DoubleBaseTypeDefaultAdapter implements JsonSerializer<Double>,
        JsonDeserializer<Double> {
	@Override
	public Double deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		try {
			if (json.getAsString().equals("")) {
				return null;
			}
		} catch (Exception ignore) {
		}
		try {
			return json.getAsDouble();
		} catch (NumberFormatException e) {
			throw new JsonSyntaxException(e);
		}
	}

	@Override
	public JsonElement serialize(Double src, Type typeOfSrc,
			JsonSerializationContext context) {
		if(src!=null&&src>9007199254740992.0){
    		return new JsonPrimitive(src.toString());
    	}
		return new JsonPrimitive(src);
	}

}
