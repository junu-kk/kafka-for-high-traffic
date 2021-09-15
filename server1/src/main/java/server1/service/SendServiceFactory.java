package server1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SendServiceFactory {
    private final List<SendService> sendServiceList;

    public SendService getSendService(SendServiceType type) {
        return sendServiceList.stream()
                .filter(service -> service.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Type not found"));
    }
}
