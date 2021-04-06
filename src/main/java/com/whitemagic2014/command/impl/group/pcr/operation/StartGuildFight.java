package com.whitemagic2014.command.impl.group.pcr.operation;

import com.whitemagic2014.annotate.Command;
import com.whitemagic2014.command.impl.group.pcr.PcrAdminCommand;
import com.whitemagic2014.pojo.CommandProperties;
import com.whitemagic2014.vo.PrivateModel;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;

/**
 * @Description: 初始化公会战数据
 * @author: magic chen
 * @date: 2020/8/23 18:31
 **/
@Command
public class StartGuildFight extends PcrAdminCommand {

    @Override
    public CommandProperties properties() {
        return new CommandProperties("日替", "开始公会战");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) {
        PrivateModel<String> result = pcrBotService.startGuildFight(subject.getId());
        return simpleMsg(sender, result);
    }


}
