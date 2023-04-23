package com.wenjin.neo4j.component;

import com.wenjin.neo4j.context.Neo4jEntityInformation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class SimpleNeo4jRepository<T, ID> implements Neo4jRepository<T, ID> {
    @Autowired
    protected Neo4jTemplate<T, ID> neo4jTemplate;
    @Autowired
    protected Neo4jClient neo4jClient;

    protected Neo4jEntityInformation<T, ID> entityInformation;

    public void register(Neo4jEntityInformation<T, ID> entityInformation) {
        this.entityInformation = entityInformation;
    }


    @Override
    public T save(T entity) {
        return neo4jTemplate.save(entity, entityInformation.getJavaType());
    }

    @Override
    public List<String> saveAll(List<T> entities) {
        return neo4jTemplate.saveAll(entities, entityInformation.getJavaType());
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(neo4jTemplate.findById(id, entityInformation.getJavaType()));
    }
    @Override
    public T find(T entity) {
        return neo4jTemplate.find(entity, entityInformation.getJavaType());
    }

    @Override
    public boolean existsById(ID id) {
        return neo4jTemplate.existsById(id, entityInformation.getJavaType());
    }

    @Override
    public List<T> findAll(List<String>... sort) {
        return neo4jTemplate.findAll(entityInformation.getJavaType(), sort);
    }

    @Override
    public PageResult<T> findAll(BaseParamType.PageParam pageParam) {
        return neo4jTemplate.findAll(entityInformation.getJavaType(), pageParam);
    }

    @Override
    public List<T> findAllById(List<Object> ids) {
        return neo4jTemplate.findByIds(ids, entityInformation.getJavaType());
    }

    @Override
    public long count() {
        return neo4jTemplate.count(entityInformation.getJavaType());
    }

    @Override
    public int deleteById(ID id) {
        return neo4jTemplate.deleteById(id, entityInformation.getJavaType());
    }

    @Override
    public int delete(T entity) {
        return neo4jTemplate.delete(entity, entityInformation.getJavaType());
    }

    @Override
    public void deleteAllById(List<Object> ids) {
        neo4jTemplate.deleteByIds(ids, entityInformation.getJavaType());
    }

    @Override
    public void deleteAll(List<? extends T> entities) {
        // TODO待实现
    }

    @Override
    public void deleteAll() {
        neo4jTemplate.deleteAll(entityInformation.getJavaType());
    }

}
