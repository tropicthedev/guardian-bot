package com.tropicoss.guardian.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Cache<K, V> {
    private static Cache<?, ?> instance;
    private final ConcurrentHashMap<K, V> cache;
    private final ScheduledExecutorService scheduler;

    private Cache() {
        this.cache = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public static synchronized <K, V> Cache<K, V> getInstance() {
        if (instance == null) {
            instance = new Cache<K, V>();
        }
        return (Cache<K, V>) instance;
    }

    public void put(K key, V value, long duration, TimeUnit unit) {
        cache.put(key, value);
        scheduler.schedule(() -> cache.remove(key), duration, unit);
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    public int size() {
        return cache.size();
    }

    public void clear() {
        cache.clear();
    }
}
