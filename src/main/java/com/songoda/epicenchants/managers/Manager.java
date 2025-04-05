package com.songoda.epicenchants.managers;

import com.songoda.epicenchants.EpicEnchants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Manager<K, V> {
    final EpicEnchants instance;
    private final Map<K, V> map;

    public Manager(EpicEnchants instance) {
        this.instance = instance;
        this.map = new HashMap<>();
    }

    public Optional<V> getValue(K key) {
        for (Object k : this.map.keySet()) {
            if (k.toString().equalsIgnoreCase(key.toString())) {
                return Optional.ofNullable(this.map.get(k));
            }
        }
        return Optional.empty();
    }

    public void add(K key, V value) {
        this.map.put(key, value);
    }

    public V getValueUnsafe(K key) {
        return getValue(key).orElse(null);
    }

    public Collection<V> getValues() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    public Collection<K> getKeys() {
        return Collections.unmodifiableCollection(this.map.keySet());
    }

    public void clear() {
        this.map.clear();
    }
}
