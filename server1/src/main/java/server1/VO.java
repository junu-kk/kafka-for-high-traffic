package server1;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class VO {
    private String timestamp;
    private String msg;

    public VO(String timestamp, String msg) {
        this.timestamp = timestamp;
        this.msg = msg;
    }
}
