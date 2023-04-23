package com.wenjin.neo4j.repository;

import cn.hutool.core.date.DateUtil;
import com.wenjin.neo4j.ChatGptApplication;
import com.wenjin.neo4j.bean.Animal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ChatGptApplication.class)
@Slf4j
class PersonRepositoryTest {
    @Autowired
    private AnimalRepository animalRepository;


    @Test
    public void queryByFiled() {
        Animal factor = new Animal();
        factor.setEntity_id("a4faf10d-7dc2-40bd-80f8-e8afb3c1b942");
        factor.setEntity_name("性别");
        factor.setCreate_time(DateUtil.now());
        factor.setCreate_user("人工");
        long start = System.currentTimeMillis();

        // 测试save
        animalRepository.save(factor);
        // 测试saveall
//            List<Factor> factors = Lists.newArrayList(factor, factor);
//            factorRepository.saveAll(factors);

        // 测试saveall
        animalRepository.findById("a4faf10d-7dc2-40bd-80f8-e8afb3c1b942");

        // 测试existsById
//            boolean b = factorRepository.existsById("1111");

        // 测试findAll
//            List<Factor> all = factorRepository.findAll();

        // 测试findAll page
//            BaseParamType.PageParam pageParam = SysUtil.getInstance(BaseParamType.PageParam.class);
//            pageParam.setCurPage(1);
//            pageParam.setPageSize(5);
//            List<Factor> all = factorRepository.findAll(pageParam);

        // 测试findAll byIds
//            List<Object> ids = Lists.newArrayList("b0e9847e-ad98-446d-bd16-840a2b69c65e", "9fbb2f43-7a29-4a78-be2b-8fda10ec16d5");
//            List<Factor> all = factorRepository.findAllById(ids);

        // 测试count
//            long count = factorRepository.count();

        // 测试deleteById
//             factorRepository.deleteById("3559f1e9-6e78-4510-9ee1-7362f8838eb2");

        // 测试deleteById
//            List<Object> ids = Lists.newArrayList("3ddfe19a-1f57-44e0-8ad5-0f236171bb7b", "b0e9847e-ad98-446d-bd16-840a2b69c65e");
//            factorRepository.deleteAllById(ids);

        // 测试deleteById
//            List<Object> ids = Lists.newArrayList("3ddfe19a-1f57-44e0-8ad5-0f236171bb7b", "b0e9847e-ad98-446d-bd16-840a2b69c65e");
//            factorRepository.deleteAllById(ids);

        long end = System.currentTimeMillis();
        log.info("耗时: {}毫秒", (end - start));
    }
}