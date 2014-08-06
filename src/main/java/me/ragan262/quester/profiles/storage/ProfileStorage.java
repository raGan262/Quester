package me.ragan262.quester.profiles.storage;

import java.util.UUID;
import me.ragan262.quester.profiles.ProfileImage;

public interface ProfileStorage {
	
	public ProfileImage retrieve(UUID uid);
	
	public void store(ProfileImage image);
}
