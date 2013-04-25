package com.gmail.molnardad.quester.storage;

import java.util.List;

/**
 * @author  raGan
 */
public abstract class StorageKey {
	
	/**
	 * @uml.property  name="path"
	 */
	protected String path = "";
	
	public StorageKey(String path) {
		this.path = path;
	}
	
	protected String createRelativeKey(String from) {
		if (from.isEmpty()) {
			return this.path;
		}
		if (from.charAt(0) == '.') {
			return this.path + from;
		}
		return this.path + "." + from;
	}
	
	public String getName() {
		return path.substring(path.lastIndexOf(46) + 1);
	}
	
	/**
	 * @uml.property  name="storageType"
	 */
	public abstract String getStorageType();
	
	public abstract boolean keyExists(String key);
	
	public abstract void removeKey(String key);
	
	public abstract StorageKey getSubKey(String key);
	
	public abstract List<StorageKey> getSubKeys();
	
	public abstract boolean hasSubKeys();
	
	public abstract boolean getBoolean(String key);

	public abstract boolean getBoolean(String key, boolean value);
	
	public abstract void setBoolean(String key, boolean value);
	
	public abstract int getInt(String key);

	public abstract int getInt(String key, int value);
	
	public abstract void setInt(String key, int value);
	
	public abstract long getLong(String key);

	public abstract long getLong(String key, long value);
	
	public abstract void setLong(String key, long value);
	
	public abstract double getDouble(String key);

	public abstract double getDouble(String key, double value);
	
	public abstract void setDouble(String key, double value);
	
	public abstract String getString(String key);

	public abstract String getString(String key, String value);
	
	public abstract void setString(String key, String value);
	
	public abstract Object getRaw(String key);

	public abstract Object getRaw(String key, Object value);
	
	public abstract void setRaw(String key, Object value);
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
	    }
	    if ((obj == null) || (getClass() != obj.getClass())) {
	    	return false;
	    }
	    StorageKey other = (StorageKey)obj;
	    if (this.path == null) {
	      if (other.path != null)
	        return false;
	    }
	    else if (!this.path.equals(other.path)) {
	      return false;
	    }
	    return true;
	}
	
	@Override
	public int hashCode() {
		return (path + getStorageType()).hashCode();
	}
}
