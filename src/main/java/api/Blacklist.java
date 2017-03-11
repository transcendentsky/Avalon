package api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tool.Response;
import util.GroupMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Eldath on 2017/1/30 0030.
 *
 * @author Eldath
 */
public class Blacklist extends GroupMessageAPI {
    private static Logger logger = LoggerFactory.getLogger(Blacklist.class);
    private static List<Long> allowList = new ArrayList<>();
    private static Blacklist instance = null;

    public static Blacklist getInstance() {
        if (instance == null) instance = new Blacklist();
        return instance;
    }

    static {
        // CUSTOM 以下为允许操作黑名单的QQ号
        allowList.add(1464443139L);
    }

    @Override
    public void doPost(GroupMessage message) {
        long sender_uid = message.getSenderUid();
        long group_uid = message.getGroupUid();
        String content = message.getContent();
        String sender = message.getSenderNickName();
        String[] split;
        String action;
        long toBan;
        if (!content.contains(" ")) {
            Response.responseGroup(group_uid, "@" + sender + " 您的指示格式不对辣！（｀Δ´）！");
            return;
        }
        split = content.split(" ");
        if (split.length < 3) {
            Response.responseGroup(group_uid, "@" + sender + " 您的指示格式不对辣！（｀Δ´）！");
            return;
        }
        action = split[2];
        toBan = Long.parseLong(split[3]);
        for (long thisAllowUid : allowList)
            if (thisAllowUid == sender_uid) {
                if ("add".equals(action)) {
                    Response.responseGroup(group_uid, "@" + sender + " 帐号" + toBan + "现已被 阿瓦隆回答我 功能屏蔽。");
                    logger.info("Account " + toBan + " is baned by " + sender_uid + " : " + sender + ".");
                    XiaoIce.blackList.put(toBan, 4);
                    return;
                } else if ("remove".equals(action)) {
                    if (!XiaoIce.blackList.containsKey(sender_uid)) {
                        Response.responseGroup(group_uid, "@" + sender + " 好像帐号" + toBan + "没有被屏蔽过呢-。-");
                        return;
                    }
                    Response.responseGroup(group_uid, "@" + sender + " 帐号" + toBan + "的屏蔽已被解除(^.^)");
                    logger.info("Account " + toBan + " is allowed again by " + sender_uid + " : " + sender + ".");
                    XiaoIce.blackList.put(toBan, 0);
                    return;
                } else {
                    Response.responseGroup(group_uid, "@" + sender + " 您的指示格式不对辣！（｀Δ´）！");
                    return;
                }
            }
        Response.responseGroup(group_uid, "@" + sender + " 您没有权限辣！（｀Δ´）！");
    }

    @Override
    public String getHelpMessage() {
        return "avalon blacklist (add/remove)：将指定的QQ号 添加至黑名单/从黑名单移除";
    }

    @Override
    public Pattern getKeyWordRegex() {
        return Pattern.compile("avalon blacklist add |avalon blacklist remove ");
    }
}