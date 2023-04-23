package com.wenjin.neo4j.config;

import com.wenjin.neo4j.context.Neo4jMappingContext;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {
    @Bean
    public Neo4jMappingContext neo4jMappingContext() {
        return new Neo4jMappingContext();
    }

    @Bean
    public Driver neo4jDriver() {
        return GraphDatabase.driver("bolt://xx.xx.x.xxx:7687", AuthTokens.basic("username", "password"));
    }
}
