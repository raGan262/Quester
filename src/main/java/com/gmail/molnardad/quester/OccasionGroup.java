package com.gmail.molnardad.quester;

import java.util.HashSet;

public class OccasionGroup {

	/**
	 * @uml.property  name="occasions"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.Integer"
	 */
	private HashSet<Integer> occasions = null;
	
	public OccasionGroup() {
		occasions = new HashSet<Integer>();
	}
	
	public boolean add(int occasion) {
		return occasions.add(occasion);
	}
	
	public int add(int[] occasions) {
		int result = 0;
		for (int i=0; i<occasions.length; i++) {
			if(this.occasions.add(occasions[i])) {
				result++;
			}
		}
		return result;
	}
	
	public boolean remove(int occasion) {
		return occasions.remove(occasion);
	}
	
	public int remove(int[] occasions) {
		int result = 0;
		for (int i=0; i<occasions.length; i++) {
			if(this.occasions.remove(occasions[i])) {
				result++;
			}
		}
		return result;
	}
	
	public boolean contains(int occasion) {
		return occasions.contains(occasion);
	}
	
	public int size() {
		return occasions.size();
	}
	
	@Override
	public boolean equals(Object occasionGroup) {
		if(this == occasionGroup) {
			return true;
		}
		if(occasionGroup == null) {
			return false;
		}
		if(occasionGroup instanceof OccasionGroup) {
			OccasionGroup grp = (OccasionGroup) occasionGroup;
			if(grp.size() != size()) {
				return false;
			}
			for(int i : occasions) {
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
