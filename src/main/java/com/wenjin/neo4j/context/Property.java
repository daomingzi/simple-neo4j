package com.wenjin.neo4j.context;

import lombok.Data;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


@Data
public class Property {
    private Field field;
    private PropertyDescriptor descriptor;
    private Class<?> rawType; // 属性类型
    private String name;      // 属性名称
    private Integer hashCode;
    private Method getter;
    private Method setter;

    public Property() {
    }

    public Property(Field field, PropertyDescriptor descriptor, Class<?> rawType, Integer hashCode, Method getter, Method setter, String name) {
        this.field = field;
        this.descriptor = descriptor;
        this.rawType = rawType;
        this.hashCode = hashCode;
        this.getter = getter;
        this.setter = setter;
        this.name = name;
    }

    /**
     * 注册一个新节点的字段数据实体
     * ps: 传入字段CLass对象, 自动填充相关数据, 目前先用get, set方法代替 TODO
     */
    public void register() {

    }
}
