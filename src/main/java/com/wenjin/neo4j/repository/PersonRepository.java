package com.wenjin.neo4j.repository;

import com.wenjin.neo4j.bean.Person;
import com.wenjin.neo4j.component.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, String> {
}