package com.whitemagic2014.command.impl.group.funny;


import com.whitemagic2014.annotate.Command;
import com.whitemagic2014.command.impl.group.NoAuthCommand;
import com.whitemagic2014.pojo.CommandProperties;
import com.whitemagic2014.service.RabbitBotService;
import com.whitemagic2014.util.RandomUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;


/**
 * @author MikuLink
 * @date 2021/03/13 14:25
 * for the Reisen
 * <p>
 * 抽签，自己做的图片
 */
@Command
public class DrawLotsCommand extends NoAuthCommand {
    private static final Logger logger = LoggerFactory.getLogger(DrawLotsCommand.class);
    //签的图片存放路径
    public static final String IMAGE_DRAWLOTS_SAVE_PATH = "data/images/drawlots";
    //标记签的总数
    private static final int DRAWLOTS_MAX_COUNT = 6;

    @Autowired
    private RabbitBotService rabbitBotService;

    @Override
    public CommandProperties properties() {
        return new CommandProperties("DrawLots", "抽签");
    }

    @Override
    public Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) {
        String userNick = sender.getNick();
        MessageChain result = MessageUtils.newChain();
        try {
            //基于签的最大数量抽取一个数字作为索引
            int randNum = RandomUtil.roll(DRAWLOTS_MAX_COUNT - 1);
            //图片处理
            String imageName = randNum + ".png";
            Image miraiImage = rabbitBotService.uploadMiraiImage(IMAGE_DRAWLOTS_SAVE_PATH + File.separator + imageName);
            //拼接请求人信息
            At member = new At(sender);
            result = result.plus(member);
            //拼接图片
            MessageChain imageMsg = rabbitBotService.parseMsgChainByImg(miraiImage);
            result = result.plus(imageMsg);
            return result;
        } catch (Exception ex) {
            logger.error("DrawLotsCommand 抽签功能异常", ex);
            return new PlainText("抽签功能出错啦");
        }
    }
}
