package me.ragan262.quester.storage;

public interface Storage {
	
	public void save();
	
	public boolean load();
	
	public StorageKey getKey(String path);
	
}
