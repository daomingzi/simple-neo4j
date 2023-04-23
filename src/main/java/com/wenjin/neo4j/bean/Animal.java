package com.wenjin.neo4j.bean;

import com.wenjin.neo4j.annotation.Id;
import com.wenjin.neo4j.annotation.Node;
import lombok.Data;

/**
 * @author huangwj.
 * @date 2023/3/24
 */
@Node(label = "animal")
@Data
public class Animal {
    /**
     * 节点名称
     */
    private String entity_name;
    /**
     * 节点主键
     */
    @Id
    private String entity_id;
    /**
     * 创建时间
     */
    private String create_time;
    /**
     * 创建用户: 移动端获取不到user_id只能靠前端传, 手动更新
     */
    private String create_user;
    /**
     * 更新时间
     */
    private String update_time;
    /**
     * 更新用户: 移动端获取不到user_id只能靠前端传, 手动更新
     */
    private String update_user;
    private String sex;
    private Long age;


}
