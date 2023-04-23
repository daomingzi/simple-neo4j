package com.wenjin.neo4j.config;

import com.wenjin.neo4j.component.SimpleNeo4jRepository;
import com.wenjin.neo4j.annotation.Id;
import com.wenjin.neo4j.annotation.Node;
import com.wenjin.neo4j.context.Neo4jEntityInformation;
import com.wenjin.neo4j.context.Neo4jMappingContext;
import com.wenjin.neo4j.context.Neo4jPersistentEntity;
import com.wenjin.neo4j.context.Property;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 对neo4j节点元输出前置处理
 * 在spring启动时, 扫描到实现Neo4jRepository的持久层, 处理节点存储元数据, 留作之后调用操作方法使用
 *
 * 需要使用时再业务服务引入配置
 */
@Component
public class BeanPostProcessor implements org.springframework.beans.factory.config.BeanPostProcessor {
    @Autowired
    private Neo4jMappingContext neo4jMappingContext;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SimpleNeo4jRepository) {
            Type type = bean.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                Class<?> nodeClazz = (Class<?>) typeArguments[0];
//                Type idType = typeArguments[1];
                Class<?> idClazz = (Class<?>) typeArguments[1];

                Neo4jPersistentEntity entityMetaData = new Neo4jPersistentEntity<>();

                /**
                 * 临时实现
                 */
                Node node = null;
                node = AnnotatedElementUtils.getMergedAnnotation(nodeClazz, Node.class);
                String label = node.label();
                Field[] fields = nodeClazz.getDeclaredFields();
//                Field field1 = fields[0];
//                ReflectionUtils.getField(field1, field1 -> field1.isAnnotationPresent(Id.class));
                List<Property> properties = Stream.of(fields).map(field -> {
                    Property property = new Property();
                    property.setField(field);
                    property.setName(field.getName());

                    if (field.isAnnotationPresent(Id.class)) {
                        entityMetaData.setIdField(field);
                        entityMetaData.setIdName(field.getName());
                    }

                    return property;
                }).collect(Collectors.toList());

                System.out.println(typeArguments);

                // typeArguments数组中的第一个元素表示T的类型，第二个元素表示ID的类型

                entityMetaData.setPrimaryLabel(label);
                entityMetaData.setClazz(nodeClazz);
                entityMetaData.setProperties(properties);

                neo4jMappingContext.register(nodeClazz, entityMetaData);

                Neo4jEntityInformation neo4jEntityInformation = new Neo4jEntityInformation(nodeClazz, entityMetaData.getIdField());

                ((SimpleNeo4jRepository<?, ?>) bean).register(neo4jEntityInformation);

            }
        }
        return bean;
    }
}

