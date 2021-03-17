package com.eli.param.cache.cache;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author eli
 */
public class SegmentCache {

    private final ConcurrentHashMap<String, List<Object>> cache;

    private final Long version;

    public SegmentCache(ConcurrentHashMap<String, List<Object>> cache, long version) {
        this.cache = cache;
        this.version = version;
    }

    public List<Object> get(String key) {
        return cache.get(key);
    }


    public List<Object> getAll() {
        List<Object> list = new LinkedList<>();
        for (Map.Entry<String, List<Object>> entry : cache.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }

    public Long getVersion() {
        return version;
    }
}
