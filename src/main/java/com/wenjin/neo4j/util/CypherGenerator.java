package com.wenjin.neo4j.util;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.wenjin.neo4j.context.Neo4jPersistentEntity;
import com.wenjin.neo4j.context.Property;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 组装Cypher语句, 封装了若干拼接cql的单元功能
 */
public class CypherGenerator {
    // 需考虑线程安全的问题
    private final StringBuilder cypherCql;
    private final String label;
    private final String idName;
    private final Field idField;
    private final Neo4jPersistentEntity<?> entityMetaData;

    private CypherGenerator(StringBuilder cypherCql, Neo4jPersistentEntity<?> entityMetaData) {
        this.cypherCql = cypherCql;
        this.entityMetaData = entityMetaData;
        this.label = entityMetaData.getPrimaryLabel();
        this.idName = entityMetaData.getIdName();
        this.idField = entityMetaData.getIdField();
    }

    public static CypherGenerator build(Neo4jPersistentEntity<?> entityMetaData) {
        return new CypherGenerator(new StringBuilder(), entityMetaData);
    }

    public CypherGenerator create(Object instance) {
        // 设置主键id值, 日后可扩展成id生成器生成, 目前简单实用UUID实现
        ReflectionUtils.setField(idField, instance, UUID.randomUUID().toString());

        this.cypherCql.append("CREATE ");
        this.getCreateStr(instance, false, false);
        return this;

    }

    public CypherGenerator createMultiple(List<Object> entities) {
        this.cypherCql.append("CREATE ");

        Iterator<Object> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Object entity = iterator.next();
            // 设置主键id值, 日后可扩展成id生成器生成, 目前简单实用UUID实现
            ReflectionUtils.setField(idField, entity, UUID.randomUUID().toString());
            this.getCreateStr(entity, iterator.hasNext(), true);
        }

        return this;
    }

    public void getCreateStr(Object instance, boolean hasNext, boolean isMultiple) {
        int hashCode = Math.abs(instance.hashCode());
        this.cypherCql.append("(").append(label);
        if (isMultiple) {
            this.cypherCql.append(hashCode);
        }
        this.cypherCql.append(": ").append(label).append("{");
        String collect = entityMetaData.getProperties().stream()
                .map(property -> {
                    Object value = getFieldValue(property.getField(), instance, true);
                    return value == null ? "" : property.getName() + ": " + value;
                })
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining(", "));
        this.cypherCql.append(collect);
        this.cypherCql.append("})");
        if (hasNext) {
            this.cypherCql.append(", ");
        }
    }

    public CypherGenerator update(Object instance) {
        // 设置主键id值, 日后可扩展成id生成器生成, 目前简单实用UUID实现
        Object idValue = ReflectionUtils.getField(idField, instance);
        match().whereId(idValue).getUpdateStr(instance);

        return this;

    }

    public void getUpdateStr(Object instance) {
        this.cypherCql.append(" set ");
        String collect = entityMetaData.getProperties().stream()
                .filter(property -> !idField.equals(property.getField()))
                .map(property -> {
                    Object value = getFieldValue(property.getField(), instance, false);
                    return value == null ? "" : label + "." + property.getName() + " = " + value;
                })
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining(", "));
        this.cypherCql.append(collect);
    }

    public CypherGenerator match() {
        this.cypherCql.append("match (").append(label).append(": ").append(label).append(") ");
        return this;
    }

    /**
     * 啥也不传, 默认遍历所有字段, 值为null则跳过
     */
    public CypherGenerator where(Object instance) {
        this.cypherCql.append("where 1=1 and ");

        String collect = entityMetaData.getProperties().stream()
                .filter(property -> !idField.equals(property.getField()))
                .map(property -> {
                    Object value = getFieldValue(property.getField(), instance);
                    return value == null ? "" : label + "." + property.getName() + " = " + value;
                })
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining(" and "));
        this.cypherCql.append(collect);
        return this;
    }

    public CypherGenerator whereId(Object id) {
        this.cypherCql.append(" where 1=1 and ").append(label).append(".").append(idName).append(" = ");
        if (idField.getType().equals(String.class)) {
            this.cypherCql.append("'").append(id).append("'");
        } else {
            this.cypherCql.append(id);
        }

        return this;
    }

    public CypherGenerator whereIds(List<Object> ids) {
        this.cypherCql.append(" where 1=1 and ").append(label).append(".").append(idName);
        in(ids);
        return this;
    }

    public CypherGenerator in(List<Object> params) {
        this.cypherCql.append(" in [");

        Iterator<Object> iterator = params.iterator();
        while (iterator.hasNext()) {
            Object id = iterator.next();
            if (idField.getType().equals(String.class)) {
                this.cypherCql.append("'").append(id).append("'");
            } else {
                this.cypherCql.append(id);
            }
            if (iterator.hasNext()) {
                cypherCql.append(", ");
            }
        }
        this.cypherCql.append("] ");
        return this;
    }

    public CypherGenerator and(Property property, Object instance) throws IllegalAccessException {
        this.cypherCql.append(" and ").append(property.getName());
        if (property.getRawType().equals(String.class)) {
            this.cypherCql.append("'").append(property.getField().get(instance)).append("'");
        } else {
            this.cypherCql.append(property.getField().get(instance));
        }
        return this;
    }

    public CypherGenerator end() {
        this.cypherCql.append(" return ").append(entityMetaData.getPrimaryLabel());
        return this;
    }

    public CypherGenerator endNull() {
        this.cypherCql.append(" return null");
        return this;
    }

    public CypherGenerator delete() {
        this.cypherCql.append(" detach delete ").append(entityMetaData.getPrimaryLabel());
        return this;
    }

    public CypherGenerator count() {
        this.cypherCql.append(" return count(").append(entityMetaData.getPrimaryLabel()).append(")");
        return this;
    }

    /**
     * 先只实现默认按照创建时间排序, 有需要可以创建sort类, 添加排序方式和用注解的方式提取排序字段
     *
     * @author huangwj.
     * @date 2023/4/18 7:02 PM
     */
    public CypherGenerator sort() {
        this.cypherCql.append(" order by ").append(entityMetaData.getPrimaryLabel()).append(".create_time desc ");
        return this;
    }

    public CypherGenerator page(BaseParamType.PageParam pageParam) {
        this.cypherCql.append(" skip ").append(PageUtils.getPageStart(pageParam))
                .append(" limit ").append(pageParam.getPageSize());
        return this;
    }

    public Object getFieldValue(Field field, Object instance, boolean isCreate) {
        field.setAccessible(true);

        if (isCreate) {
            if ("create_time".equals(field.getName())) {
                if (ReflectionUtils.getField(field, instance) != null) {
                    return getFieldValue(field, instance);
                }
                String now = DateUtil.now();
                ReflectionUtils.setField(field, instance, now);
                return "'" + now + "'";
            }
            if ("create_user".equals(field.getName())) {
                if (ReflectionUtils.getField(field, instance) != null) {
                    return getFieldValue(field, instance);
                }
                String userCode = CommTools.prcRunEnvs().getUser_code();
                ReflectionUtils.setField(field, instance, userCode);
                return "'" + userCode + "'";
            }
        }
        if (!isCreate) {
            if ("update_time".equals(field.getName())) {
                if (ReflectionUtils.getField(field, instance) != null) {
                    return getFieldValue(field, instance);
                }
                String now = DateUtil.now();
                ReflectionUtils.setField(field, instance, now);
                return "'" + now + "'";
            }
            if ("update_user".equals(field.getName())) {
                if (ReflectionUtils.getField(field, instance) != null) {
                    return getFieldValue(field, instance);
                }
                String userCode = CommTools.prcRunEnvs().getUser_code();
                ReflectionUtils.setField(field, instance, userCode);
                return "'" + userCode + "'";
            }
        }

        return getFieldValue(field, instance);
    }

    public Object getFieldValue(Field field, Object instance) {
        field.setAccessible(true);

        Object value = ReflectionUtils.getField(field, instance);
        if (value == null) {
            return null;
        }

        if (field.getType().equals(String.class)) {
            return "'" + value + "'";
        } else {
            return value;
        }

    }

    public String getStr() {
        return this.cypherCql.toString();
    }

}
