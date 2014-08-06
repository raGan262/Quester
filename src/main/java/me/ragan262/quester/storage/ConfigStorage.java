package me.ragan262.quester.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class ConfigStorage implements Storage {
	
	private final Utf8YamlConfiguration config;
	private final String fileName;
	private final File conFile;
	private final Logger logger;
	
	public ConfigStorage(final File file, final Logger logger, final InputStream defaultStream) {
		if(logger == null) {
			this.logger = Bukkit.getLogger();
		}
		else {
			this.logger = logger;
		}
		config = new Utf8YamlConfiguration();
		conFile = file;
		final int id = file.getName().lastIndexOf('.');
		fileName = id > 0 ? file.getName().substring(0, id) : file.getName();
		if(defaultStream != null && !file.exists()) {
			create(defaultStream);
			save();
		}
	}
	
	private void create(final InputStream defaultStream) {
		createFile();
		if(defaultStream != null) {
			try {
				config.load(defaultStream);
				logger.info("Loaded default file: " + conFile.getName() + " !");
			}
			catch(final IOException e) {
				logger.severe("Could not load default file: " + conFile.getName() + " !");
				e.printStackTrace();
			}
			catch(final InvalidConfigurationException e) {
				logger.severe("Could not load default file: " + conFile.getName() + " !");
				e.printStackTrace();
			}
		}
	}
	
	private void createFile() {
		try {
			conFile.getParentFile().mkdirs();
			conFile.createNewFile();
		}
		catch(final IOException ex) {
			logger.severe("Could not create file: " + conFile.getName() + " !");
		}
	}
	
	private boolean hasPath(final String path) {
		return config.get(path) != null;
	}
	
	@Override
	public void save() {
		saveToFile(conFile);
	}
	
	public void saveToFile(final File file) {
		if(!conFile.exists()) {
			createFile();
		}
		try {
			config.save(file);
		}
		catch(final IOException ex) {
			logger.severe("Can't write to file '" + conFile.getName() + "'!");
		}
	}
	
	@Override
	public boolean load() {
		if(!conFile.exists()) {
			createFile();
		}
		try {
			config.load(conFile);
			return true;
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	@Override
	public StorageKey getKey(String root) {
		if(root == null) {
			root = "";
		}
		final StorageKey key = new ConfigKey(root);
		if(root.isEmpty()) {
			key.name = fileName;
		}
		return key;
	}
	
	public class ConfigKey extends StorageKey {
		
		protected ConfigKey(final String root) {
			super(root);
		}
		
		@Override
		public String getStorageType() {
			return "YAML";
		}
		
		@Override
		public boolean keyExists(final String key) {
			return hasPath(createRelativeKey(key));
		}
		
		@Override
		public void removeKey(final String key) {
			config.set(createRelativeKey(key), null);
		}
		
		@Override
		public StorageKey getSubKey(final String key) {
			return new ConfigKey(createRelativeKey(key));
		}
		
		@Override
		public List<StorageKey> getSubKeys() {
			Set<String> keySet = null;
			if(path == "") {
				keySet = config.getKeys(false);
			}
			else {
				final ConfigurationSection section = config.getConfigurationSection(path);
				if(section != null) {
					keySet = section.getKeys(false);
				}
			}
			if(keySet != null) {
				final List<StorageKey> result = new ArrayList<StorageKey>();
				for(final String key : keySet) {
					result.add(new ConfigKey(createRelativeKey(key)));
				}
				return result;
			}
			else {
				return Collections.emptyList();
			}
		}
		
		@Override
		public boolean hasSubKeys() {
			if(path == "") {
				return !config.getKeys(false).isEmpty();
			}
			else {
				return config.getConfigurationSection(path) != null;
			}
		}
		
		@Override
		public boolean getBoolean(final String key, final boolean value) {
			final String path = createRelativeKey(key);
			return config.getBoolean(path, value);
		}
		
		@Override
		public void setBoolean(final String key, final boolean value) {
			config.set(createRelativeKey(key), value);
		}
		
		@Override
		public int getInt(final String key, final int value) {
			final String path = createRelativeKey(key);
			return config.getInt(path, value);
		}
		
		@Override
		public void setInt(final String key, final int value) {
			config.set(createRelativeKey(key), value);
		}
		
		@Override
		public long getLong(final String key, final long value) {
			final String path = createRelativeKey(key);
			return config.getLong(path, value);
		}
		
		@Override
		public void setLong(final String key, final long value) {
			config.set(createRelativeKey(key), value);
		}
		
		@Override
		public double getDouble(final String key, final double value) {
			final String path = createRelativeKey(key);
			return config.getDouble(path, value);
		}
		
		@Override
		public void setDouble(final String key, final double value) {
			config.set(createRelativeKey(key), value);
		}
		
		@Override
		public String getString(final String key, final String value) {
			final String path = createRelativeKey(key);
			return config.getString(path, value);
		}
		
		@Override
		public void setString(final String key, final String value) {
			config.set(createRelativeKey(key), value);
		}
		
		@Override
		public Object getRaw(final String key, final Object value) {
			final String path = createRelativeKey(key);
			return config.get(path, value);
		}
		
		@Override
		public void setRaw(final String key, final Object value) {
			config.set(createRelativeKey(key), value);
		}
		
	}
	
}
