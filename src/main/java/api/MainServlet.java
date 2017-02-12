package api;

import extend.Recorder;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tool.APIRateLimit;
import tool.APISurvivePool;
import tool.Response;
import tool.VariablePool;
import util.GroupMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eldath on 2017/1/28 0028.
 *
 * @author Eldath
 */
public class MainServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MainServlet.class);
    private static Map<String, GroupMessageAPI> apiList = new LinkedHashMap<>();
    public static final long[] followGroup = {617118724};
    static final long[] followPeople = {951394653, 360736041, 1464443139, 704639565};

    // CUSTOM 指令最小间隔，几秒才能发出一次指令（单位：毫秒），注意同步修改下文注释处。
    private static APIRateLimit cooling = new APIRateLimit(4000L);

    public MainServlet() {
        // CUSTOM 注意：此处configure的顺序决定优先级。
        // 开发者非常不建议修改此处内容，容易造成奇怪的问题。
        MainServlet.configure("avalon apimanager ", APIManager.getInstance());
        MainServlet.configure("avalon blacklist ", Blacklist.getInstance());
        MainServlet.configure("avalon help", Help.getInstance());
        MainServlet.configure("avalon version", Version.getInstance());
        MainServlet.configure(Mo.keywords, Mo.getInstance());
        MainServlet.configure("avalon echo ", Echo.getInstance());
        MainServlet.configure(XiaoIce.keywords, XiaoIce.getInstance());
    }


    static GroupMessageAPI getAPIByKeyword(String keyword) {
        if (!apiList.containsKey(keyword)) return null;
        return apiList.get(keyword);
    }

    private static void configure(String keyWord, GroupMessageAPI api) {
        apiList.put(keyWord.toLowerCase(), api);
        APISurvivePool.getInstance().addAPI(api);
    }

    private static void configure(List<String> keywords, GroupMessageAPI API) {
        for (String thisKeyWord : keywords)
            configure(thisKeyWord, API);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject object = (JSONObject) new JSONTokener(req.getInputStream()).nextValue();
        if (object.isNull("post_type") || object.isNull("type")) return;
        if (!("receive_message".equals(object.getString("post_type"))) &&
                !("group_message".equals(object.getString("type"))))
            return;
        String group = object.get("group").toString();
        long timeLong = object.getLong("time");
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeLong), ZoneId.of("Asia/Shanghai"));
        int Id = object.getInt("id");
        long groupUid = object.getLong("group_uid");
        long senderUid = object.getLong("sender_uid");
        long receiverUid = object.getLong("receiver_uid");
        String receiver = object.get("receiver").toString();
        String sender = object.get("sender").toString();
        String content = object.get("content").toString();
        String lowerContent = content.toLowerCase();

        //recodeMessage(senderUid, sender, time, content, groupUid, group);
        for (long thisFollowGroup : followGroup)
            if (groupUid == thisFollowGroup) {
                for (Map.Entry<String, GroupMessageAPI> stringAPIEntry : apiList.entrySet()) {
                    String key = stringAPIEntry.getKey();
                    GroupMessageAPI value = stringAPIEntry.getValue();
                    if (doCheck(key, value, lowerContent, groupUid, sender, timeLong))
                        value.doPost(new GroupMessage(Id, time, senderUid, sender, receiverUid,
                                receiver, groupUid, group, content));
                    else return;
                }
            }
    }

    private void recodeMessage(long senderUid,
                               String sender,
                               long timeLong,
                               String content,
                               String groupUid,
                               String groupName) {
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeLong * 1000), ZoneId.of("Asia/Shanghai"));
        Recorder.getInstance().recode("[" +
                time.toString() + "]\tGroupMessage: \"" + content +
                "\"\n\t\tsaid by " + sender + " : " + senderUid + " in " +
                groupName + " : " +
                groupUid);
    }

    private boolean doCheck(String key, GroupMessageAPI value, String lowerContent, long groupUid, String sender, long time) {
        if (lowerContent.contains(key)) {
            if (!cooling.trySet(time)) {
                if (!VariablePool.Limit_Noticed) {
                    // CUSTOM 若修改了指令最小间隔，请同步修改此处。
                    Response.responseGroup(groupUid, "@" + sender +
                            " 对不起，您的指令超频。4s内仅能有一次指令输入，未到4s内的输入将被忽略。" +
                            "注意：此消息仅会显示一次。");
                    //
                    VariablePool.Limit_Noticed = true;
                }
                return false;
            }
            if (!APISurvivePool.getInstance().isSurvive(value)) {
                if (!APISurvivePool.getInstance().isNoticed(value)) {
                    Response.responseGroup(groupUid, "@" + sender +
                            " 对不起，您调用的方法目前已被停止；注意：此消息仅会显示一次。");
                    APISurvivePool.getInstance().setNoticed(value);
                }
                return false;
            } else {
                try {
                    if (!lowerContent.contains(" ") || !lowerContent.equals(new String(lowerContent
                            .getBytes("GB2312"), "GB2312"))) {
                        Response.responseGroup(groupUid, "@" + sender + " 您的指示编码好像不对劲啊(╯︵╰,)");
                        return false;
                    }
                } catch (UnsupportedEncodingException ignore) {
                }
                return true;
            }
        }
        return false;
    }
}
