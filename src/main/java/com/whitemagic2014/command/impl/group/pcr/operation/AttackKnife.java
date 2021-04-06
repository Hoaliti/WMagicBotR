package com.whitemagic2014.command.impl.group.pcr.operation;

import com.whitemagic2014.annotate.Command;
import com.whitemagic2014.command.impl.group.pcr.PcrNoAuthCommand;
import com.whitemagic2014.pojo.CommandProperties;
import com.whitemagic2014.vo.PrivateModel;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;

/**
 * @Description: 出刀
 * @author: magic chen
 * @date: 2020/8/23 19:17
 **/
@Command
public class AttackKnife extends PcrNoAuthCommand {

    String txt = "报刀";

    @Override
    public CommandProperties properties() {
        return new CommandProperties("报刀");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) {

        try {
            PrivateModel<String> result;
            result = pcrBotService.baoKnife(subject.getId(), sender.getId());
            return simpleMsg(sender, result);
        } catch (Exception e) {
            e.printStackTrace();
            return new At(sender).plus("指令错误." + txt);
        }

    }


}
