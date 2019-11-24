package de.immerarchiv.util.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;



public class BaseCacheImpl<K extends KeyImpl,T> {
	
	private final Map<K,T> cache = new HashMap<K,T>();
	
	private final Map<String,Set<K>> primaryCache = new HashMap<>();

	public T get(K key) {
		return cache.get(key);
	}

	public List<K> put(K key, T value) {
		
		
		cache.put(key, value);
		
		if(!primaryCache.containsKey(key.getPrimary()))
				primaryCache.put(key.getPrimary(), new HashSet<>());
		primaryCache.get(key.getPrimary()).add(key);
		
		List<K> keysToDelete = primaryCache.get(key.getPrimary()).stream()
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
