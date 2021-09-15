package server1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server1.service.SendService;
import server1.service.SendServiceFactory;
import server1.service.SendServiceType;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class Controller {
    private final SendServiceFactory factory;
    private final SendService sendService;

    public Controller(SendServiceFactory factory) {
        this.factory = factory;
        // this.sendService = factory.getSendService(SendServiceType.API);
        this.sendService = factory.getSendService(SendServiceType.KAFKA);
    }

    @PostMapping("/")
    public void sendDateAndString(@RequestParam String msg) {
        sendService.sendMsg(msg);
    }
}
