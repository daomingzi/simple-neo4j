package com.wenjin.neo4j.context;

import lombok.Data;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * 元数据持久化实体, 存储node相关信息(label, fields等), 通过反射获取实例的相关数据
 *
 * @param <T>
 */
@Data
public class Neo4jPersistentEntity<T> {

    /**
     * node节点的label
     */
    private String primaryLabel;

    /**
     * node类型
     */
    private Class<T> clazz;

    /**
     * node的属性列表
     */
    private List<Property> properties;

    /**
     * id字段
     */
    private Field idField;
    /**
     * id字段名称
     */
    private String idName;

    /** 关系列表, 存储节点关系列表, 方便多节点关联操作 TODO*/

    /**
     * 注册一个新的节点元数据实体,
     * ps: 传入节点CLass对象, 自动填充相关数据, 目前先用get, set方法代替 TODO
     */
    public void register() {

    }

    public boolean isNew(Object bean) {
        idField.setAccessible(true);
        return Objects.isNull(ReflectionUtils.getField(idField, bean));
    }
}
