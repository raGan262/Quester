package me.ragan262.quester.storage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

public class MemoryStorageKey extends StorageKey {
	
	private final ConfigurationSection root;
	
	public MemoryStorageKey() {
		super("");
		root = new MemoryConfiguration();
	}
	
	private MemoryStorageKey(final ConfigurationSection root, final String path) {
		super(path);
		this.root = root;
	}
	
	@Override
	public String getStorageType() {
		return "MEMORY";
	}
	
	@Override
	public boolean keyExists(final String key) {
		return root.isSet(createRelativeKey(key));
	}
	
	@Override
	public void removeKey(final String key) {
		root.set(createRelativeKey(key), null);
	}
	
	@Override
	public StorageKey getSubKey(final String key) {
		final String relative = createRelativeKey(key);
		return new MemoryStorageKey(root, relative);
	}
	
	@Override
	public List<StorageKey> getSubKeys() {
		final List<StorageKey> result = new ArrayList<StorageKey>();
		if(root.isConfigurationSection(path)) {
			for(final String key : root.getConfigurationSection(path).getKeys(false)) {
				result.add(new MemoryStorageKey(root, createRelativeKey(key)));
			}
		}
		return result;
	}
	
	@Override
	public boolean hasSubKeys() {
		return root.isConfigurationSection(path);
	}
	
	@Override
	public boolean getBoolean(final String key, final boolean value) {
		return root.getBoolean(createRelativeKey(key), value);
	}
	
	@Override
	public void setBoolean(final String key, final boolean value) {
		setRaw(key, value);
	}
	
	@Override
	public int getInt(final String key, final int value) {
		return root.getInt(createRelativeKey(key), value);
	}
	
	@Override
	public void setInt(final String key, final int value) {
		setRaw(key, value);
	}
	
	@Override
	public long getLong(final String key, final long value) {
		return root.getLong(createRelativeKey(key), value);
	}
	
	@Override
	public void setLong(final String key, final long value) {
		setRaw(key, value);
	}
	
	@Override
	public double getDouble(final String key, final double value) {
		return root.getDouble(createRelativeKey(key), value);
	}
	
	@Override
	public void setDouble(final String key, final double value) {
		setRaw(key, value);
	}
	
	@Override
	public String getString(final String key, final String value) {
		return root.getString(createRelativeKey(key), value);
	}
	
	@Override
	public void setString(final String key, final String value) {
		setRaw(key, value);
	}
	
	@Override
	public Object getRaw(final String key, final Object value) {
		return root.get(createRelativeKey(key), value);
	}
	
	@Override
	public void setRaw(final String key, final Object value) {
		root.set(createRelativeKey(key), value);
	}
	
}
