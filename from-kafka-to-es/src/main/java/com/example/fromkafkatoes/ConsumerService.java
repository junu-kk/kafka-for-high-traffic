package com.example.fromkafkatoes;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerService {

    @KafkaListener(topics = "brandon-test", groupId = "kafka-test", concurrency = "4")
    @Transactional
    public void consumeMessage(String msg, Consumer<String, String> consumer){
        Gson gson = new Gson();
        UserEventVO map = gson.fromJson(msg, UserEventVO.class);

        // 로그로 저장.
        System.out.println(map);


        try {
            testRepository.save(test);
        } catch (Exception e) {
            System.out.print("중복");
        }

        consumer.commitAsync();

    }
}
