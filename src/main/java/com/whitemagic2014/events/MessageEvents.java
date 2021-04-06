package com.whitemagic2014.events;

import com.whitemagic2014.service.KeyWordService;
import com.whitemagic2014.util.MagicMaps;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 非指令消息的消息事件
 * @author: magic chen
 * @date: 2020/9/8 11:40
 **/
@Component
public class MessageEvents extends SimpleListenerHost {

    private static final Logger logger = LoggerFactory.getLogger(MessageEvents.class);

    @Autowired
    private KeyWordService keyWordService;


    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        logger.error("RecallEvent Error:{}", exception.getMessage());
    }

    @NotNull
    @EventHandler
    public ListeningStatus onMessage(@NotNull GroupMessageEvent event) throws Exception {
        // 捕获cd 当捕获一次后,进入cd
        //keyWordService.keyWordMatchGroup(event);

        return ListeningStatus.LISTENING;
    }

}
