package com.ztesoft.sca.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class GsonUtils {
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static Gson gson = new GsonBuilder()
			//.setDateFormat(DEFAULT_DATE_FORMAT)
								.registerTypeAdapter(Integer.class, new IntDefaultAdapter())
								.registerTypeAdapter(int.class, new IntDefaultAdapter())
								
								.registerTypeAdapter(Boolean.class, new BooleanDefaultAdapter())
								.registerTypeAdapter(boolean.class, new BooleanBaseTypeDefaultAdapter())
								
								.registerTypeAdapter(Byte.class, new ByteDefaultAdapter())
								.registerTypeAdapter(byte.class, new ByteBaseTypeDefaultAdapter())
								
								.registerTypeAdapter(Character.class, new CharacterDefault0Adapter())
								.registerTypeAdapter(char.class, new CharacterDefault0Adapter())
								
								.registerTypeAdapter(Double.class, new DoubleDefaultAdapter())
								.registerTypeAdapter(double.class, new DoubleBaseTypeDefaultAdapter())
								
								.registerTypeAdapter(Float.class, new FloatDefaultAdapter())
								.registerTypeAdapter(float.class, new FloatBaseTypeDefaultAdapter())
								
								.registerTypeAdapter(Long.class, new LongDefaultAdapter())
								.registerTypeAdapter(long.class, new LongBaseTypeDefaultAdapter())
								
								.registerTypeAdapter(Short.class, new ShortDefaultAdapter())
								.registerTypeAdapter(short.class, new ShortBaseTypeDefaultAdapter())
								
								.registerTypeAdapter(Date.class, new DefaultDateTypeAdapter(DateFormat.DEFAULT,DateFormat.DEFAULT))
								.create();


	public static String toJson(Object object) {
		return gson.toJson(object);
	}

	public static <T> T toObject(String paramJson, Class<T> clazz) {
		return gson.fromJson(paramJson, clazz);
	}
	
	public static Object toObject(String paramJson, Type clazz) {
		return gson.fromJson(paramJson, clazz);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> toObjects(String paramJson, Class<T> clazz) {
		return (List<T>) gson.fromJson(paramJson, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static  List toObjects(String paramJson, Type clazz) {
		return (List) gson.fromJson(paramJson, clazz);
	}
	
	public static Map<String,Object> toMap(String paramJson){
		return gson.fromJson(paramJson, new TypeToken<Map<String, Object>>(){}.getType());
	}
	
	public static Map toMap(String paramJson, Type clazz){
		return (Map)gson.fromJson(paramJson, clazz);
	}
	
	public static LinkedHashMap<String,Object> toLinkedHashMap(String paramJson){
		return gson.fromJson(paramJson, new TypeToken<LinkedHashMap<String, Object>>(){}.getType());
	}
}