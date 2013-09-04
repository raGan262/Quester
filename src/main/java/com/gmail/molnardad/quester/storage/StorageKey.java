package com.gmail.molnardad.quester.storage;

import java.util.List;

public abstract class StorageKey {
	
	protected final String path;
	protected String name = null;
	
	public StorageKey(final String path) {
		this.path = path;
	}
	
	protected String createRelativeKey(final String from) {
		if(from.isEmpty()) {
			return path;
		}
		if(from.charAt(0) == '.') {
			return path + from;
		}
		return path + "." + from;
	}
	
	public String getName() {
		if(name == null) {
			name = path.substring(path.lastIndexOf(46) + 1);
		}
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public abstract String getStorageType();
	
	public abstract boolean keyExists(String key);
	
	public abstract void removeKey(String key);
	
	public abstract StorageKey getSubKey(String key);
	
	public abstract List<StorageKey> getSubKeys();
	
	public abstract boolean hasSubKeys();
	
	public boolean getBoolean(final String key) {
		return getBoolean(key, false);
	}
	
	public abstract boolean getBoolean(String key, boolean value);
	
	public abstract void setBoolean(String key, boolean value);
	
	public int getInt(final String key) {
		return getInt(key, 0);
	}
	
	public abstract int getInt(String key, int value);
	
	public abstract void setInt(String key, int value);
	
	public long getLong(final String key) {
		return getLong(key, 0);
	}
	
	public abstract long getLong(String key, long value);
	
	public abstract void setLong(String key, long value);
	
	public double getDouble(final String key) {
		return getDouble(key, 0.0D);
	}
	
	public abstract double getDouble(String key, double value);
	
	public abstract void setDouble(String key, double value);
	
	public String getString(final String key) {
		return getString(key, null);
	}
	
	public abstract String getString(String key, String value);
	
	public abstract void setString(String key, String value);
	
	public Object getRaw(final String key) {
		return getRaw(key, null);
	}
	
	public abstract Object getRaw(String key, Object value);
	
	public abstract void setRaw(String key, Object value);
	
	@Override
	public boolean equals(final Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final StorageKey other = (StorageKey) obj;
		if(path == null) {
			if(other.path != null) {
				return false;
			}
		}
		else if(!path.equals(other.path)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return (path + getStorageType()).hashCode();
	}
}
