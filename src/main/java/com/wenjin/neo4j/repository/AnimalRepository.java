package com.wenjin.neo4j.repository;

import com.wenjin.neo4j.component.SimpleNeo4jRepository;
import com.wenjin.neo4j.bean.Animal;
import org.springframework.stereotype.Repository;

@Repository
public class AnimalRepository extends SimpleNeo4jRepository<Animal, String> {


//    Person findByEntity_name(String name);
}