package com.whitemagic2014.service;


import com.whitemagic2014.constant.ConstantRepeater;
import com.whitemagic2014.util.RandomUtil;
import com.whitemagic2014.util.RegexUtil;
import com.whitemagic2014.util.StringUtil;


import net.mamoe.mirai.message.GroupMessageEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by MikuLink on 2020/8/31 15:40
 * for the Reisen
 * 专门处理关键词触发的相关业务
 */
@Service
public class KeyWordService {
    //保存群最后一条消息，用于复读
    private static Map<Long, String[]> LAST_MSG_MAP = new HashMap<>();

    /**
     * 群消息关键词匹配
     *
     * @param event 群消息监听事件
     */
    public void keyWordMatchGroup(GroupMessageEvent event) throws IOException {
        //每次只会触发一个回复
        //群复读
        boolean groupRep = groupRepeater(event);
        if (groupRep) {
            return;
        }
    }

    /**
     * 群复读
     *
     * @param event 群消息监控
     * @return bol值 表示有没有进行群复读
     */
    private boolean groupRepeater(GroupMessageEvent event) {
        //接收到的群消息
        String groupMsg = event.getMessage().contentToString();
        if ("[图片]".equalsIgnoreCase(groupMsg)) {
            return false;
        }
        Long groupId = event.getGroup().getId();

        //第一次消息初始化
        if (!LAST_MSG_MAP.containsKey(groupId)) {
            LAST_MSG_MAP.put(groupId, new String[2]);
        }

        String[] msgs = LAST_MSG_MAP.get(groupId);
        //群复读，三个相同的消息，复读一次，并重置计数
        if ((StringUtil.isEmpty(msgs[0]) || StringUtil.isEmpty(msgs[1]))
                || !(msgs[0].equals(msgs[1]) && msgs[0].equals(groupMsg))) {
            //刷新消息列表
            msgs[1] = msgs[0];
            msgs[0] = groupMsg;
            LAST_MSG_MAP.put(groupId, msgs);
            return false;
        }

        //概率复读
        if (!RandomUtil.rollBoolean(20)) {
            return false;
        }

        //概率打断复读，100%对复读打断复读的语句做出反应
        if (RandomUtil.rollBoolean(-80)) {
            //打断复读
            groupMsg = RandomUtil.rollStrFromList(ConstantRepeater.REPEATER_KILLER_LIST);
        }

        //正常复读
        event.getSubject().sendMessage(groupMsg);
        //复读一次后，重置复读计数
        LAST_MSG_MAP.put(groupId, new String[2]);
        return true;
    }

}
