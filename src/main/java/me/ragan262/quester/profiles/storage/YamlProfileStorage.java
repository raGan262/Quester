package me.ragan262.quester.profiles.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import me.ragan262.quester.profiles.ProfileImage;
import me.ragan262.quester.storage.ConfigStorage;
import me.ragan262.quester.storage.Storage;
import me.ragan262.quester.storage.StorageKey;
import org.apache.commons.lang.Validate;

public class YamlProfileStorage implements ProfileStorage {
	
	private final File storageFolder;
	private final Logger logger;
	
	public YamlProfileStorage(final File storageFolder, final Logger logger) {
		Validate.notNull(storageFolder, "Profile storage folder can't be null.");
		this.storageFolder = storageFolder;
		if(logger != null) {
			this.logger = logger;
		}
		else {
			this.logger = Logger.getGlobal();
		}
	}
	
	@Override
	public ProfileImage retrieve(final UUID uid) {
		if(uid == null) {
			return null;
		}
		final Storage s = getPlayerStorage(uid, true);
		if(!s.getKey("").hasSubKeys()) {
			return null;
		}
		return StorageKeyProfileLoader.loadProfile(s.getKey(""));
	}
	
	@Override
	public void store(final ProfileImage image) {
		if(image == null) {
			return;
		}
		final Storage s = getPlayerStorage(image.uid, false);
		StorageKeyProfileLoader.saveProfile(image, s.getKey(""));
		s.save();
	}
	
	private Storage getPlayerStorage(final UUID uid, final boolean load) {
		final File f = new File(storageFolder, uid.toString() + ".yml");
		final ConfigStorage storage = new ConfigStorage(f, logger, null);
		if(load && f.exists()) {
			storage.load();
		}
		return storage;
	}
	
	public List<ProfileImage> retrieveAllFromFile(final File file) {
		final List<ProfileImage> result = new ArrayList<>();
		if(file.exists()) {
			final ConfigStorage storage = new ConfigStorage(file, logger, null);
			storage.load();
			ProfileImage image = null;
			int count = 0;
			for(final StorageKey subKey : storage.getKey("").getSubKeys()) {
				if(!subKey.hasSubKeys()) {
					continue;
				}
				count++;
				image = StorageKeyProfileLoader.loadProfile(subKey);
				if(image != null) {
					result.add(image);
				}
				if(count % 100 == 0) {
					logger.info("Loaded " + count + " profiles.");
				}
			}
			if(count % 100 != 0) {
				logger.info("Loaded " + count + " profiles.");
			}
		}
		return result;
	}
}
