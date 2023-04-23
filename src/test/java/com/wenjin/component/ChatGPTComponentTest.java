package com.wenjin.component;

import com.wenjin.neo4j.ChatGptApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest(classes = ChatGptApplication.class)
class ChatGPTComponentTest {
    @Autowired
    private ChatGPTComponent chatGPTComponent;

    @Test
    public void testAsk() {
        chatGPTComponent.chat("背一首出师表");
    }

    @Test
    public void testImageCreate() {
        chatGPTComponent.imagesCreate("英短蓝白猫咪");
    }

    @Test
    public void testimagesVariation() {
        chatGPTComponent.imagesVariation("/Volumes/data/file_storage/.png");
    }
    @Test
    public void testEmbeddings() {
        chatGPTComponent.embeddings(Arrays.asList("我的胃不舒服", "附近有没有三甲医院"));
    }

    @Test
    public void testimagesVariation100() {

//        String imagesPath = chatGPTComponent.imagesCreate("英短蓝白猫咪").get(0);
//        String imagesPath = "/Volumes/data/file_storage/de0ae295-fcb4-4098-9e6b-265facf76a6a.png";
        String imagesPath = "/Volumes/data/file_storage/20230313/WechatIMG1414.png";

        for (int i = 0; i < 100; i++) {
            imagesPath = chatGPTComponent.imagesVariation(imagesPath).get(0);
        }

    }

    @Test
    public void test() {
    }


}