package com.wenjin.neo4j.bean;

import com.wenjin.neo4j.annotation.Id;
import com.wenjin.neo4j.annotation.Node;
import lombok.Data;

/**
 * @author huangwj.
 * @date 2023/3/24
 */
@Node(label = "person")
@Data
public class Person {
    @Id
    private String entity_id;

    private String entity_name;
    private String sex;

    private Long age;

}
