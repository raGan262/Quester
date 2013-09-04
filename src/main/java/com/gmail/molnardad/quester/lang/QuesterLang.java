package com.gmail.molnardad.quester.lang;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.gmail.molnardad.quester.utils.Ql;

public class QuesterLang {
	
	private final Map<String, String> messages = new HashMap<String, String>(
			MessageRegistry.INITIAL_CAPACITY);
	
	private final Map<String, String> customMessages = new HashMap<String, String>(
			MessageRegistry.INITIAL_CAPACITY);
	
	private final File file;
	private final String fileName;
	private final MessageRegistry registry;
	
	QuesterLang(final File file, final MessageRegistry registry) {
		if(registry == null) {
			throw new IllegalArgumentException("Message Registry cannot be null.");
		}
		this.registry = registry;
		this.file = file;
		if(file != null) {
			fileName = file.getName();
		}
		else {
			fileName = "NULL";
		}
	}
	
	public File getFile() {
		return file;
	}
	
	public String get(final String key) {
		String result = messages.get(key);
		if(result == null) {
			result = registry.messages.get(key);
			if(result == null) {
				final StackTraceElement st = Thread.currentThread().getStackTrace()[2];
				Ql.debug("Class " + st.getClassName() + " requested unknown message '" + key
						+ "' on line " + st.getLineNumber() + ".");
				result = messages.get("MSG_UNKNOWN_MESSAGE");
			}
		}
		return result;
	}
	
	public String getCustom(final String key) {
		String result = customMessages.get(key);
		if(result == null) {
			result = registry.customMessages.get(key);
			if(result == null) {
				final StackTraceElement st = Thread.currentThread().getStackTrace()[2];
				Ql.debug("Class " + st.getClassName() + " requested unknown custom message '" + key
						+ "' on line " + st.getLineNumber() + ".");
				result = messages.get("MSG_UNKNOWN_MESSAGE");
			}
		}
		return result;
	}
	
	public Map<String, String> getCustomMessages() {
		return customMessages;
	}
	
	void put(final String key, final String message) {
		messages.put(key, message);
	}
	
	void putCustom(final String key, final String message) {
		customMessages.put(key, message);
	}
	
	Map<String, String> getMessages() {
		return messages;
	}
	
	int addDefaults() {
		int counter = 0;
		for(final Entry<String, String> entry : registry.messages.entrySet()) {
			if(messages.get(entry.getKey()) == null) { // intended
				messages.put(entry.getKey(), entry.getValue());
				if(file != null) {
					Ql.debug(entry.getKey() + " in " + fileName + " reset to default.");
				}
				counter++;
			}
		}
		for(final Entry<String, String> entry : registry.customMessages.entrySet()) {
			if(customMessages.get(entry.getKey()) == null) { // intended
				customMessages.put(entry.getKey(), entry.getValue());
				if(file != null) {
					Ql.debug(LanguageManager.CUSTOM_KEY + " " + entry.getKey() + " in " + fileName
							+ " reset to default.");
				}
				counter++;
			}
		}
		return counter;
	}
	
	void reset() {
		messages.clear();
		addDefaults();
	}
}
