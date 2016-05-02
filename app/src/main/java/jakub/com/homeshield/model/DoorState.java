package jakub.com.homeshield.model;

import java.security.MessageDigest;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jakub on 1/25/16.
 */
public class DoorState {
    private long id;
    public String msg;
    public int state;
    public Long timestamp;

    public DoorState(){

    }

    public DoorState(String msg, Integer state, Long timestamp) {
        this.msg = msg;
        this.state = state;
        this.timestamp = timestamp;
    }

    public long getId() { return id;  }

    public void setId(long id) { this.id = id; }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
