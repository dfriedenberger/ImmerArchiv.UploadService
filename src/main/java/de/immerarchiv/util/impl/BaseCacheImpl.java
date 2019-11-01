package de.immerarchiv.util.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;



public class BaseCacheImpl<K extends KeyImpl,T> {
	
	private final Map<K,T> cache = new HashMap<K,T>();

	public T get(K key) {
		return cache.get(key);
	}

	public List<K> put(K key, T value) {
		
		
		cache.put(key, value);
		
		List<K> keysToDelete = cache.keySet().stream()
				.filter(k -> k.samePrimary(key))
				.filter(k -> !k.equals(key)).collect(Collectors.toList());
		
		for(K k : keysToDelete)
			cache.remove(k);
		
		return keysToDelete;
		
	}

	public Set<Entry<K,T>> entrySet() {
		return cache.entrySet();
	}

	public int size() {
		return cache.size();
	}

}
