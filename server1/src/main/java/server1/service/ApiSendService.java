package server1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import server1.VO;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class ApiSendService implements SendService {

    @Override
    public SendServiceType getType() {
        return SendServiceType.API;
    }

    @Override
    public void sendMsg(String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        Date now = new Date();
        VO vo = new VO(sdf.format(now), msg);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange("http://localhost:8082?msg=" + vo.toString(), HttpMethod.POST, new HttpEntity<>(null, null), String.class);
        restTemplate.exchange("http://localhost:8083?msg=" + vo.toString(), HttpMethod.POST, new HttpEntity<>(null, null), String.class);
    }
}
