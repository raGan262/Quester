package me.ragan262.quester.utils;

import java.util.HashSet;

public class CaseAgnosticSet extends HashSet<String> {
	
	private static final long serialVersionUID = 3378267763102001314L;
	
	@Override
	public boolean add(final String e) {
		return super.add(e.toLowerCase());
	}
	
	@Override
	public boolean contains(final Object o) {
		return o instanceof String && super.contains(((String) o).toLowerCase());
	}
}
