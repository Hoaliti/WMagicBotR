package com.whitemagic2014.command.impl.group.funny;

import com.whitemagic2014.annotate.Command;
import com.whitemagic2014.command.EverywhereCommand;
import com.whitemagic2014.command.impl.group.NoAuthCommand;
import com.whitemagic2014.constant.ConstantPixiv;
import com.whitemagic2014.pojo.CommandProperties;
import com.whitemagic2014.pojo.ReString;
import com.whitemagic2014.pojo.setu.PixivImageInfo;
import com.whitemagic2014.service.PixivService;
import com.whitemagic2014.service.SetuService;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * @author MikuLink
 * @date 2020/12/13 15:50
 * for the Reisen
 * <p>
 * 来点色图
 */
@Command
public class SetuCommand extends NoAuthCommand {
    private static final Logger logger = LoggerFactory.getLogger(SetuCommand.class);

    @Autowired
    private SetuService setuService;

    @Autowired
    private PixivService pixivService;

    @Override
    public CommandProperties properties() {
        return new CommandProperties("laidiansetu", "setu", "色图", "来点色图", "来份色图", "来张色图");
    }

    @Override
    public Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) {


        Long userId = sender.getId();
        String userNick = sender.getNick();

        //检查操作间隔
        ReString reString = setuService.setuTimeCheck(userId, userNick);
        if (!reString.isSuccess()) {
            return new PlainText(reString.getMessage());
        }

        //刷新操作间隔
        ConstantPixiv.SETU_PID_SPLIT_MAP.put(sender.getId(), System.currentTimeMillis());

        try {
            //来张色图
            PixivImageInfo pixivImageInfo = setuService.getSetu();
            pixivImageInfo.setSender(sender);
            pixivImageInfo.setSubject(subject);
            return pixivService.parsePixivImgInfoByApiInfo(pixivImageInfo);
        } catch (FileNotFoundException fileNotFoundEx) {
            logger.warn(ConstantPixiv.PIXIV_IMAGE_DELETE + fileNotFoundEx.toString());
            return new PlainText(ConstantPixiv.PIXIV_IMAGE_DELETE);
        } catch (SocketTimeoutException stockTimeoutEx) {
            logger.warn(ConstantPixiv.PIXIV_IMAGE_TIMEOUT + stockTimeoutEx.toString(), stockTimeoutEx);
            return new PlainText(ConstantPixiv.PIXIV_IMAGE_TIMEOUT);
        } catch (Exception ex) {
            logger.error(ConstantPixiv.PIXIV_ID_GET_ERROR_GROUP_MESSAGE + ex.toString(), ex);
            return new PlainText(ConstantPixiv.PIXIV_ID_GET_ERROR_GROUP_MESSAGE);
        }
    }
}
