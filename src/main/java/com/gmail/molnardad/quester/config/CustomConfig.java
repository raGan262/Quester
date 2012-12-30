package com.gmail.molnardad.quester.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CustomConfig {
	
		JavaPlugin plugin = null;
		YamlConfiguration config = null;
		File conFile = null;
		
		public CustomConfig(JavaPlugin plugin, String fileName) {
			
			this.plugin = plugin;
			conFile = new File(plugin.getDataFolder(), fileName);
			config = loadConfig();
			initialize();
			if(!validate()){
				if(resetConfig()){
					this.plugin.getLogger().severe("Config invalid, reseting.");
				}
			}
		}
		
		public YamlConfiguration getConfig() {
			return this.config;
		}
		
		public YamlConfiguration loadConfig() {
			
			if (!conFile.exists()) {
				InputStream defaultConfigStream = plugin.getResource(conFile.getName());
				if (defaultConfigStream != null) {
					YamlConfiguration customConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
					try {
						customConfig.save(conFile);
						plugin.getLogger().info("Created default file " + conFile.getName() + " !");
					} catch (IOException ex) {
						plugin.getLogger().severe("Can't save file " + conFile.getName() + " !");
					}
					config = customConfig;
				} else {
					try {
						conFile.createNewFile();
						plugin.getLogger().info("Created empty file " + conFile.getName() + " !");
					} catch (IOException ex) {
						plugin.getLogger().severe("Can't create file " + conFile.getName() + " !");
					}
					config = YamlConfiguration.loadConfiguration(conFile);
				}
			} else {
				config = YamlConfiguration.loadConfiguration(conFile);
			}
			return config;
		}
		
		public void saveConfig() {
			
			try {
				config.save(conFile);
			} catch (IOException ex) {
				plugin.getLogger().severe("Can't Write To File '" + conFile.getName() + "'!");
		    }
		}
		
		public boolean resetConfig() {
			InputStream defaultConfigStream = plugin.getResource(conFile.getName());
			if (defaultConfigStream != null) {
				config = YamlConfiguration.loadConfiguration(defaultConfigStream);
				saveConfig();
				return true;
			} else {
				plugin.getLogger().severe("Resetting cofiguration failed.");
				return false;
			}
		}
		
		public void initialize() {
			
		}
		
		public boolean validate() {
			return true;
		}
}
