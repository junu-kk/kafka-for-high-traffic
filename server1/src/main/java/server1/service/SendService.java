package server1.service;

public interface SendService {
    SendServiceType getType();

    void sendMsg(String msg);
}
