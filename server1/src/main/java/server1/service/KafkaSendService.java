package server1.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;
import server1.Controller;
import server1.VO;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class KafkaSendService implements SendService {
    private final Logger logger = LoggerFactory.getLogger(Controller.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaSendService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendMsg(String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        Date now = new Date();
        VO vo = new VO(sdf.format(now), msg);

        kafkaTemplate.send("test-topic", vo.toString()).addCallback(
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
