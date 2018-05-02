package com.ztesoft.sca.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CharacterDefault0Adapter implements JsonSerializer<Character>, JsonDeserializer<Character> {
    @Override
    public Character deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context)
                             throws JsonParseException {
        try {
            if (json.getAsString().equals("")){
                return '\0';
            }
        } catch (Exception ignore){
        }
        try {
            return json.getAsCharacter();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(Character src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }

}