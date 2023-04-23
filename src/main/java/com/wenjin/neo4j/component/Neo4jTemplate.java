package com.wenjin.neo4j.component;

import cn.hutool.db.PageResult;
import com.wenjin.neo4j.context.Neo4jMappingContext;
import com.wenjin.neo4j.context.Neo4jPersistentEntity;
import com.wenjin.neo4j.util.CypherGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 若干调用neo4j的crud方法模板, 及将结果映射成对象
 */
@Component
@Slf4j
public class Neo4jTemplate<T, ID> {

    @Autowired
    private Neo4jMappingContext<T> neo4JMappingContext;

    @Autowired
    private Neo4jClient neo4jClient;

    public T findById(ID id, Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCql = CypherGenerator.build(entityMetaData).match().whereId(id).end().getStr();
        return neo4jClient.runAsObject(cypherCql, entityMetaData);
    }

    public T find(T entity, Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCql = CypherGenerator.build(entityMetaData).match().where(entity).end().getStr();
        return neo4jClient.runAsObject(cypherCql, entityMetaData);
    }

    public boolean existsById(ID id, Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCql = CypherGenerator.build(entityMetaData).match().whereId(id).count().getStr();
        return neo4jClient.runAsInt(cypherCql) > 0;
    }

    public T save(T entity, Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        if (entityMetaData.isNew(entity)) {
            return create(entity, entityMetaData);
        } else {
            return update(entity, entityMetaData);
        }
    }

    public List<String> saveAll(List<T> entities, Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCql = CypherGenerator.build(entityMetaData).createMultiple((List<Object>) entities).endNull().getStr();
        neo4jClient.runAsString(cypherCql);

        List<String> result = new ArrayList<>();
        for (T entity : entities) {
            String entityId = (String) ReflectionUtils.getField(entityMetaData.getIdField(), entity);
            result.add(entityId);
        }

        return result;
    }

    private T create(T entity, Neo4jPersistentEntity<T> entityMetaData) {
        String cypherCql = CypherGenerator.build(entityMetaData).create(entity).end().getStr();
        return neo4jClient.runCreateAsObject(cypherCql, entityMetaData);
    }

    private T update(T entity, Neo4jPersistentEntity<T> entityMetaData) {
        String cypherCql = CypherGenerator.build(entityMetaData).update(entity).end().getStr();
        return neo4jClient.runAsObject(cypherCql, entityMetaData);
    }

    public List<T> findAll(Class<T> domainType, List<String>... sort) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCql = CypherGenerator.build(entityMetaData).match().end().sort().getStr();
        return neo4jClient.runAsObjectList(cypherCql, entityMetaData);
    }

    public PageResult<T> findAll(Class<T> domainType, PageParam pageParam) {
        PageResult<T> result = new PageResult<>();

        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCqlCount = CypherGenerator.build(entityMetaData).match().count().getStr();
        int count = neo4jClient.runAsInt(cypherCqlCount);
        if (count > 0) {
            String cypherCql = CypherGenerator.build(entityMetaData).match().end().sort().page(pageParam).getStr();
            result.addAll(neo4jClient.runAsObjectList(cypherCql, entityMetaData));
            result.setPageResult(PageUtils.getPageResult(pageParam, count));
            return result;
        } else {
            result.empty(pageParam);
        }
        return result;
    }

    public List<T> findByIds(List<Object> ids, Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCql = CypherGenerator.build(entityMetaData).match().whereIds(ids).end().getStr();
        return neo4jClient.runAsObjectList(cypherCql, entityMetaData);
    }

    public int count(Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCqlCount = CypherGenerator.build(entityMetaData).match().count().getStr();
        return neo4jClient.runAsInt(cypherCqlCount);
    }

    public int deleteById(Object id, Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCql = CypherGenerator.build(entityMetaData).match().whereId(id).delete().endNull().getStr();
        return neo4jClient.runDelete(cypherCql);
    }

    public int delete(T entity, Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCql = CypherGenerator.build(entityMetaData).match().where(entity).delete().endNull().getStr();
        return neo4jClient.runDelete(cypherCql);
    }

    public void deleteByIds(List<Object> ids, Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCql = CypherGenerator.build(entityMetaData).match().whereIds(ids).delete().getStr();
        neo4jClient.runDelete(cypherCql);
    }

    public void deleteAll(Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        String cypherCql = CypherGenerator.build(entityMetaData).match().delete().getStr();
        neo4jClient.runDelete(cypherCql);
    }

    public T runAsObject(String cql, Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        return neo4jClient.runAsObject(cql, entityMetaData);
    }

    public List<T> runAsObjectList(String cql, Class<T> domainType) {
        Neo4jPersistentEntity<T> entityMetaData = neo4JMappingContext.getRequiredPersistentEntity(domainType);
        return neo4jClient.runAsObjectList(cql, entityMetaData);
    }
}
