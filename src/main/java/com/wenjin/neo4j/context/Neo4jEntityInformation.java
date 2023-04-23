package com.wenjin.neo4j.context;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * 存储实体的相关信息
 *
 * @param <T>
 * @param <ID>
 */
public class Neo4jEntityInformation<T, ID> {
    //实体类型
    private Class<T> type;

    // ID字段
    private Field fieldId;

    public Neo4jEntityInformation(Class<T> type, Field fieldId) {
        this.type = type;
        this.fieldId = fieldId;
    }

    /**
     * 获取实例中的id字段值
     *
     * @param entity 对象实例
     * @return id value
     */
    @SuppressWarnings("unchecked")
    public ID getId(T entity) {
        return (ID) ReflectionUtils.getField(fieldId, entity);
    }

    /**
     * 获取节点类型
     *
     * @return class对象
     */
    public Class<T> getJavaType() {
        return this.type;
    }

}
