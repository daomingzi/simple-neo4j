package com.wenjin.neo4j.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * neo4j操作上下文
 *
 * @param <T>
 */
public class Neo4jMappingContext<T> {

    // id生成器, 默认UUID生成器
//    private final Map<Class<? extends IdGenerator<?>>, IdGenerator<?>> idGenerators = new ConcurrentHashMap<>();

    /**
     * node节点class对象为key, Neo4jPersistentEntity为value的map
     */
    private final Map<Class<T>, Neo4jPersistentEntity<T>> neo4jPersistentEntityMap = new ConcurrentHashMap<>();

    public Neo4jPersistentEntity<T> getRequiredPersistentEntity(Class<T> domainType) {
        return neo4jPersistentEntityMap.get(domainType);
    }

    public void register(Class<T> clazz, Neo4jPersistentEntity<T> entityMetaData) {
        neo4jPersistentEntityMap.put(clazz, entityMetaData);
    }
}

