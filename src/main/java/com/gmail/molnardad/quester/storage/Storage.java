package com.gmail.molnardad.quester.storage;

public interface Storage {
	
	public void save();
	
	public boolean load();
	
	public StorageKey getKey(String path);
	
}
