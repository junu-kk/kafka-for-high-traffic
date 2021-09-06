package com.example.fromwebtokafka;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Controller {
    private final Logger logger = LoggerFactory.getLogger(Controller.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public Controller(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/api/select")
    public void selectColor(
            @RequestHeader("user-agent") String userAgentName,
            @RequestParam(value = "color") String colorName,
            @RequestParam(value = "user") String userName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        Date now = new Date();
        Gson gson = new Gson();
        UserEventVO userEventVO = new UserEventVO(sdf.format(now), userAgentName, colorName, userName);
        String jsonColorLog = gson.toJson(userEventVO);

        kafkaTemplate.send("brandon-test", jsonColorLog).addCallback(
                new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        logger.error(ex.getMessage(), ex);
                    }

                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        logger.info(result.toString());
                    }
                }
        );
    }
}
