package avalon.tool;

import avalon.api.util.Message;
import avalon.util.DelayMessage;

import java.util.concurrent.DelayQueue;

/**
 * Created by Eldath Ray on 2017/3/22.
 *
 * @author Eldath Ray
 */
public class DelayResponse extends Thread {
    private static DelayResponse instance = null;
    private DelayQueue<DelayMessage> messages = new DelayQueue<>();

    public static DelayResponse getInstance() {
        if (instance == null) instance = new DelayResponse();
        return instance;
    }

    public boolean delay(DelayMessage message) {
        return messages.offer(message);
    }

    @Override
    public void run() {
        DelayMessage delayMessage;
        Message message;
        while ((delayMessage = messages.poll()) != null) {
            message = delayMessage.getMessage();
            message.response(delayMessage.getDoWhat().get());
        }
    }
}