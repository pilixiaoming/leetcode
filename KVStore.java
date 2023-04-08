package databricks;

public class KVStore {

}


/*
*
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KVBigLock {

    private final Lock m_lock = new ReentrantLock();
    private final Map<String, Integer> m_map = new HashMap<>();

    public void put(String key, int value) {
        m_lock.lock();
        try {
            m_map.put(key, value);
        } finally {
            m_lock.unlock();
        }
    }

    public Optional<Integer> get(String key) {
        m_lock.lock();
        try {
            if (m_map.containsKey(key)) {
                return Optional.of(m_map.get(key));
            } else {
                return Optional.empty();
            }
        } finally {
            m_lock.unlock();
        }
    }

    public boolean remove(String key) {
        m_lock.lock();
        try {
            return m_map.remove(key) != null;
        } finally {
            m_lock.unlock();
        }
    }
}
*
*
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class KVSharedLock {

    private final ReadWriteLock m_lock = new ReentrantReadWriteLock();
    private final Map<String, Integer> m_map = new HashMap<>();

    public void put(String key, int value) {
        m_lock.writeLock().lock();
        try {
            m_map.put(key, value);
        } finally {
            m_lock.writeLock().unlock();
        }
    }

    public Optional<Integer> get(String key) {
        m_lock.readLock().lock();
        try {
            if (m_map.containsKey(key)) {
                return Optional.of(m_map.get(key));
            } else {
                return Optional.empty();
            }
        } finally {
            m_lock.readLock().unlock();
        }
    }

    public boolean remove(String key) {
        m_lock.writeLock().lock();
        try {
            return m_map.remove(key) != null;
        } finally {
            m_lock.writeLock().unlock();
        }
    }
}
*
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KVSharded {

    private final int m_mask;
    private final List<KVBigLock> m_shards;

    public KVSharded(int num_shard) {
        m_mask = num_shard - 1;
        m_shards = new ArrayList<>(num_shard);

        if ((num_shard & m_mask) != 0) {
            throw new RuntimeException("num_shard must be a power of two");
        }

        for (int i = 0; i < num_shard; i++) {
            m_shards.add(new KVBigLock());
        }
    }

    private KVBigLock getShard(String key) {
        int h = key.hashCode();
        int index = h & m_mask;
        return m_shards.get(index);
    }

    public void put(String key, int value) {
        getShard(key).put(key, value);
    }

    public Optional<Integer> get(String key) {
        return getShard(key).get(key);
    }

    public boolean remove(String key) {
        return getShard(key).remove(key);
    }
}
*
*
*
*
* import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KVBigLock {

    private final Lock m_lock = new ReentrantLock();
    private final Map<String, Integer> m_map = new HashMap<>();

    public void put(String key, int value) {
        m_lock.lock();
        try {
            m_map.put(key, value);
        } finally {
            m_lock.unlock();
        }
    }

    public Integer get(String key) {
        if (m_map.containsKey(key)) {
            return m_map.get(key);
        } else {
            return null;
        }
    }

    public boolean remove(String key) {
        m_lock.lock();
        try {
            return m_map.remove(key) != null;
        } finally {
            m_lock.unlock();
        }
    }
}

*
*
** */
