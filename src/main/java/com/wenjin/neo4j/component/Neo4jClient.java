package com.wenjin.neo4j.component;

import cn.hutool.core.map.MapUtil;
import com.wenjin.neo4j.context.Neo4jPersistentEntity;
import com.wenjin.neo4j.context.Property;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.MapAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实际执行neo4j语句(调用neo4j), 并封装返回结果
 * ps: 自定调用方法, 可直接用session, 也可以用dsl等若干方式调用, 如有多种调用方式自行扩展
 */
@Component
@Slf4j
public class Neo4jClient {

    @Autowired
    private Driver driver;

    public Result run(String cypherCql) {
        try (Session session = driver.session()){
            Result result = session.run(cypherCql);
            return result.hasNext() ? result : null;
        }
    }

    public int runCreate(String cypherCql) {
        try (Session session = driver.session()) {
            return session.run(cypherCql).consume().counters().nodesDeleted();
        }
    }
    public <T> T runCreateAsObject(String cypherCql,  Neo4jPersistentEntity<T> entityMetaData) {
        try (Session session = driver.session()) {
            Result result = session.run(cypherCql);
            return result.hasNext() ? mapToClass(result.next().get(0).asMap(), entityMetaData) : null;
        }
    }
    public int runDelete(String cypherCql) {
        try (Session session = driver.session()) {
            return session.run(cypherCql).consume().counters().nodesDeleted();
        }
    }

    public String runAsString(String cypherCql) {
        try (Session session = driver.session()) {
            Result result = session.run(cypherCql);
            return result.hasNext() ? result.next().get(0).asString() : null;
        }
    }

    public Integer runAsInt(String cypherCql) {
        try (Session session = driver.session()) {
            Result result = session.run(cypherCql);
            return result.hasNext() ? result.next().get(0).asInt() : null;
        }
    }

    public Map<String, Object> runAsMap(String cypherCql) {
        try (Session session = driver.session()) {
            Result result = session.run(cypherCql);
            return result.hasNext() ? result.next().get(0).asMap() : null;
        }
    }

    /**
     * 查询返回一个map列表
     *
     * @param cypherCql
     * @return
     */
    public List<Map<String, Object>> runAsMapList(String cypherCql) {
        try (Session session = driver.session()) {
            Result result = session.run(cypherCql);
            return result.hasNext() ? result.next().get(0).asList(MapAccessor::asMap) : null;
        }
    }

    /**
     * 查询返回一个对象
     */
    public <T> T runAsObject(String cypherCql, Neo4jPersistentEntity<T> entityMetaData) {
        try (Session session = driver.session()) {
            Result result = session.run(cypherCql);
            return result.hasNext() ? mapToClass(result.next().get(0).asMap(), entityMetaData) : null;
        }
    }

    public <T> List<T> runAsObjectList(String cypherCql, Neo4jPersistentEntity<T> entityMetaData) {
        try (Session session = driver.session()) {
            Result result = session.run(cypherCql);
            if (result.hasNext()) {
                return result.list(x -> mapToClass(x.get(0).asMap(), entityMetaData));
            }
            return new ArrayList<>();
        }
    }


    public <T> T mapToClass(Map<String, Object> map, Neo4jPersistentEntity<T> entityMetaData) {
        try {
            long mapStart = System.currentTimeMillis();

            final T instance = entityMetaData.getClazz().getDeclaredConstructor().newInstance();
            entityMetaData.getProperties().stream().forEach(property -> {
                Object value = getValue(property, map);
                ReflectionUtils.setField(property.getField(), instance, value);
            });
            long mapStop = System.currentTimeMillis();
            log.info("map2Class耗时: {}", mapStop - mapStart);

            return instance;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getValue(Property property, Map<String, Object> map) {
        Object value;

        Field field = property.getField();
        field.setAccessible(true);

        if (field.getType().equals(Integer.class)) {
            value = MapUtil.getInt(map, property.getName());
        } else {
            value = map.get(property.getName());
        }
        return value;
    }

}
