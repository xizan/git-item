package com.ztesoft.sca.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This type adapter supports three subclasses of date: Date, Timestamp, and
 * java.sql.Date.
 * 
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class DefaultDateTypeAdapter implements JsonSerializer<Date>,
        JsonDeserializer<Date> {

	// TODO: migrate to streaming adapter

	private final DateFormat enUsFormat;
	private final DateFormat localFormat;
	private final DateFormat iso8601Format;
	private final DateFormat localFormat0;
	private final DateFormat localFormat1;
	private final DateFormat localFormat2;
	private final DateFormat localFormat3;

	DefaultDateTypeAdapter() {
		this(DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
				DateFormat.DEFAULT, Locale.CHINA), DateFormat.getDateTimeInstance(
				DateFormat.DEFAULT, DateFormat.DEFAULT));
	}

	DefaultDateTypeAdapter(String datePattern) {
		this(new SimpleDateFormat(datePattern, Locale.CHINA),
				new SimpleDateFormat(datePattern));
	}

	DefaultDateTypeAdapter(int style) {
		this(DateFormat.getDateInstance(style, Locale.CHINA), DateFormat
				.getDateInstance(style));
	}

	public DefaultDateTypeAdapter(int dateStyle, int timeStyle) {
		this(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.CHINA),
				DateFormat.getDateTimeInstance(dateStyle, timeStyle));
	}

	DefaultDateTypeAdapter(DateFormat enUsFormat, DateFormat localFormat) {
		this.enUsFormat = enUsFormat;
		this.localFormat = localFormat;
		this.iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
				Locale.CHINA);
		this.iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
		this.localFormat0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.localFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");

		this.localFormat2 = new SimpleDateFormat("yyyy-MM-dd hh");
		this.localFormat3 = new SimpleDateFormat("yyyy-MM-dd");
	}

	// These methods need to be synchronized since JDK DateFormat classes are
	// not thread-safe
	// See issue 162
	public JsonElement serialize(Date src, Type typeOfSrc,
			JsonSerializationContext context) {
		synchronized (localFormat) {
			String dateFormatAsString = localFormat0.format(src);
			return new JsonPrimitive(dateFormatAsString);
		}
	}

	public Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		if (!(json instanceof JsonPrimitive)) {
			throw new JsonParseException("The date should be a string value");
		}
		Date date = deserializeToDate(json);
		if (typeOfT == Date.class) {
			return date;
		} else if (typeOfT == Timestamp.class) {
			return new Timestamp(date.getTime());
		} else if (typeOfT == java.sql.Date.class) {
			return new java.sql.Date(date.getTime());
		} else {
			throw new IllegalArgumentException(getClass()
					+ " cannot deserialize to " + typeOfT);
		}
	}

	private Date deserializeToDate(JsonElement json) {
		synchronized (localFormat) {
			if ("".equals(json.getAsString())) {
				return null;
			}
			
			try {
				return localFormat0.parse(json.getAsString());
			} catch (ParseException ignored) {

			}

			try {
				return localFormat1.parse(json.getAsString());
			} catch (ParseException ignored) {

			}

			try {
				return localFormat2.parse(json.getAsString());
			} catch (ParseException ignored) {

			}
			
			try {
				return localFormat.parse(json.getAsString());
			} catch (ParseException ignored) {
			}
			try {
				return enUsFormat.parse(json.getAsString());
			} catch (ParseException ignored) {
			}
			try {
				return iso8601Format.parse(json.getAsString());
			} catch (ParseException ignored) {

			}
			
			

			try {
				return localFormat3.parse(json.getAsString());
			} catch (ParseException e) {
				throw new JsonSyntaxException(json.getAsString(), e);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DefaultDateTypeAdapter.class.getSimpleName());
		sb.append('(').append(localFormat.getClass().getSimpleName())
				.append(')');
		return sb.toString();
	}
}