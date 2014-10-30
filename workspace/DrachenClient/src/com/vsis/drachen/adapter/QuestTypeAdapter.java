/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vsis.drachen.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.vsis.drachen.model.quest.Quest;

public final class QuestTypeAdapter implements // JsonSerializer<T>,
		JsonDeserializer<Quest> {

	public QuestTypeAdapter() {
	}

	private List<Field> getAllFields(Class<?> type) {
		Class<?> t = type;
		List<Field> fields = new LinkedList<Field>();
		do {
			// System.out.println(t.getName());
			for (Field f : t.getDeclaredFields())
				fields.add(f);
		} while ((t = t.getSuperclass()) != null && !t.equals(Object.class));

		return fields;
	}

	// safe because both Long.class and long.class are of type Class<Long>
	@SuppressWarnings("unchecked")
	private static <T> Class<T> wrap(Class<T> c) {
		return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
	}

	private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS;

	static {
		PRIMITIVES_TO_WRAPPERS = new HashMap<Class<?>, Class<?>>();
		PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
		PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
		PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
		PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
		PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
		PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
		PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
		PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
		PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);
	}

	public Quest deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		// System.out.println("Starting quest deserializer");
		JsonObject obj = json.getAsJsonObject();
		Quest result = new Quest();
		parseViaReflection(context, obj, result);

		result.updateQuestReference();
		return result;
	}

	private void parseViaReflection(JsonDeserializationContext context,
			JsonObject obj, Quest result) {
		for (Field f : getAllFields(Quest.class)) {
			// System.out.println(f.getName());
			JsonElement ele = obj.get(f.getName());
			if (ele != null) {
				try {
					f.setAccessible(true);
					Class<?> cls = wrap(f.getType());

					Type type = cls;// TypeToken.get(cls).getType();
					if (cls.getTypeParameters().length > 0) {
						type = f.getGenericType();
					}
					// System.out.println("Type1: " + cls.getName());
					// System.out.println("Type2: " + type);

					f.set(result, cls.cast(context.deserialize(ele, type)));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}