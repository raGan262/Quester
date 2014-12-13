package me.ragan262.quester;

import java.util.HashSet;

public class OccasionGroup {
	
	private HashSet<Integer> occasions = null;
	
	public OccasionGroup() {
		occasions = new HashSet<>();
	}
	
	public boolean add(final int occasion) {
		return occasions.add(occasion);
	}
	
	public int add(final int[] occasions) {
		int result = 0;
		for(int occasion : occasions) {
			if(this.occasions.add(occasion)) {
				result++;
			}
		}
		return result;
	}
	
	public boolean remove(final int occasion) {
		return occasions.remove(occasion);
	}
	
	public int remove(final int[] occasions) {
		int result = 0;
		for(int occasion : occasions) {
			if(this.occasions.remove(occasion)) {
				result++;
			}
		}
		return result;
	}
	
	public boolean contains(final int occasion) {
		return occasions.contains(occasion);
	}
	
	public int size() {
		return occasions.size();
	}
	
	@Override
	public boolean equals(final Object occasionGroup) {
		if(this == occasionGroup) {
			return true;
		}
		if(occasionGroup == null) {
			return false;
		}
		if(occasionGroup instanceof OccasionGroup) {
			final OccasionGroup grp = (OccasionGroup)occasionGroup;
			if(grp.size() != size()) {
				return false;
			}
			for(final int i : occasions) {
				if(!grp.contains(i)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return occasions.hashCode();
	}
}
