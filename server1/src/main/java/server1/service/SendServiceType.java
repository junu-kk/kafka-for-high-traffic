package server1.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SendServiceType {
    API("Api"),
    KAFKA("Kafka");

    private final String type;
}
