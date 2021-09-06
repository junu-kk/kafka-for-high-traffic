package com.example.fromkafkatoes;

import com.example.fromkafkatoes.entity.Test;
import com.example.fromkafkatoes.repository.TestRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final TestRepository testRepository;

    private static String sha256(String msg) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(msg.getBytes());

        return bytesToHex(md.digest());
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b: bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

@KafkaListener(topics = "brandon-test", groupId = "zicobar", concurrency = "4")
@Transactional
public void consumeMessage(String msg, Consumer<String, String> consumer) throws NoSuchAlgorithmException {
    Gson gson = new Gson();
    UserEventVO map = gson.fromJson(msg, UserEventVO.class);

    // elasticsearch에 key값과 함께 적재
    System.out.println(map);
    Test test = Test.builder()
            ._id(sha256(map.getColorName() + ":" + map.getUserName()))
            .timestamp(map.getTimestamp())
            .colorName(map.getColorName())
            .userAgent(map.getUserAgent())
            .userName(map.getUserName())
            .build();


    try {
        testRepository.save(test);
    } catch (Exception e) {
        System.out.print("중복");
    }

    consumer.commitAsync();

}
}
