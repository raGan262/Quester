package com.gmail.molnardad.quester.storage;

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
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigStorage implements Storage {


	private final YamlConfiguration config;
	private final File conFile;
	private final Logger logger;
	
	public ConfigStorage(File file, Logger logger, InputStream defaultStream) {
		if(logger == null) {
			this.logger = Bukkit.getLogger();
		}
		else {
			this.logger = logger;
		}
	    this.config = new YamlConfiguration();
	    this.conFile = file;
	    if (!file.exists()) {
	      create(defaultStream);
	      save();
	    }
	}
	
	private void create(InputStream defaultStream) {
		try {
		    conFile.getParentFile().mkdirs();
			conFile.createNewFile();
		} catch (IOException ex) {
			logger.severe("Could not create file: " + conFile.getName() + " !");
		}
		if (defaultStream != null) {
			try {
				config.load(defaultStream);
				logger.info("Loaded default file: " + conFile.getName() + " !");
			}
			catch (IOException e) {
				logger.severe("Could not load default file: " + conFile.getName() + " !");
				e.printStackTrace();
			}
			catch (InvalidConfigurationException e) {
				logger.severe("Could not load default file: " + conFile.getName() + " !");
				e.printStackTrace();
			}
		}
		else {
			logger.info("Created empty file: " + conFile.getName() + " !");
		}
	}
	
	private boolean hasPath(String path) {
		return config.get(path) != null;
	}
	
	@Override
	public void save() {
		try {
			config.save(conFile);
		} catch (IOException ex) {
			logger.severe("Can't write to file '" + conFile.getName() + "'!");
	    }
	}

	@Override
	public boolean load() {
		try {
			this.config.load(conFile);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;	
	}

	@Override
	public StorageKey getKey(String root) {
		return new ConfigKey(root);
	}
	
	public class ConfigKey extends StorageKey {

		protected ConfigKey(String root) {
			super(root);
		}

		@Override
		public String getStorageType() {
			return "YAML";
		}

		@Override
		public boolean keyExists(String key) {
			return hasPath(createRelativeKey(key));
		}

		@Override
		public void removeKey(String key) {
			ConfigStorage.this.config.set(createRelativeKey(key), null);
		}
		
		@Override
		public StorageKey getSubKey(String key) {
			return new ConfigKey(createRelativeKey(key));
		}
		
		@Override
		public Iterable<StorageKey> getSubKeys() {
			Set<String> keySet = null;
			if(path == "") {
				ConfigStorage.this.config.getKeys(false);
			}
			else {
				ConfigurationSection section = ConfigStorage.this.config.getConfigurationSection(path);
				if(section != null) {
					keySet = section.getKeys(false);
				}
			}
			if(keySet != null) {
				List<StorageKey> result = new ArrayList<StorageKey>();
				for(String key : keySet) {
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
				return !ConfigStorage.this.config.getKeys(false).isEmpty();
			}
			else {
				return ConfigStorage.this.config.getConfigurationSection(path) != null;
			}
		}
		
		@Override
		public boolean getBoolean(String key) {
			return getBoolean(key, false);
		}

		@Override
		public boolean getBoolean(String key, boolean value) {
			String path = createRelativeKey(key);
			return ConfigStorage.this.config.getBoolean(path, value);
		}

		@Override
		public void setBoolean(String key, boolean value) {
			ConfigStorage.this.config.set(createRelativeKey(key), value);
		}

		@Override
		public int getInt(String key) {
			return getInt(key, 0);
		}

		@Override
		public int getInt(String key, int value) {
			String path = createRelativeKey(key);
			return ConfigStorage.this.config.getInt(path, value);
		}

		@Override
		public void setInt(String key, int value) {
			ConfigStorage.this.config.set(createRelativeKey(key), value);
		}

		@Override
		public long getLong(String key) {
			return getLong(key, 0L);
		}

		@Override
		public long getLong(String key, long value) {
			String path = createRelativeKey(key);
			return ConfigStorage.this.config.getLong(path, value);
		}

		@Override
		public void setLong(String key, long value) {
			ConfigStorage.this.config.set(createRelativeKey(key), value);
		}

		@Override
		public double getDouble(String key) {
			return getDouble(key, 0.0D);
		}

		@Override
		public double getDouble(String key, double value) {
			String path = createRelativeKey(key);
			return ConfigStorage.this.config.getDouble(path, value);
		}

		@Override
		public void setDouble(String key, double value) {
			ConfigStorage.this.config.set(createRelativeKey(key), value);
		}

		@Override
		public String getString(String key) {
			return getString(key, null);
		}

		@Override
		public String getString(String key, String value) {
			String path = createRelativeKey(key);
			return ConfigStorage.this.config.getString(path, value);
		}

		@Override
		public void setString(String key, String value) {
			ConfigStorage.this.config.set(createRelativeKey(key), value);
		}

		@Override
		public Object getRaw(String key) {
			return getRaw(key, null);
		}

		@Override
		public Object getRaw(String key, Object value) {
			String path = createRelativeKey(key);
			return ConfigStorage.this.config.get(path, value);
		}

		@Override
		public void setRaw(String key, Object value) {
			ConfigStorage.this.config.set(createRelativeKey(key), value);
		}
		
		
	}
	
}
