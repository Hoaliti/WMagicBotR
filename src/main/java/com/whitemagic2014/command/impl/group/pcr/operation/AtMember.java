package com.whitemagic2014.command.impl.group.pcr.operation;

import com.whitemagic2014.annotate.Command;
import com.whitemagic2014.command.impl.group.pcr.PcrNoAuthCommand;
import com.whitemagic2014.dic.PcrNoticeType;
import com.whitemagic2014.pojo.CommandProperties;
import com.whitemagic2014.vo.PrivateModel;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 添加成员
 * @author: magic chen
 * @date: 2020/8/23 18:36
 **/
@Command
public class AtMember extends PcrNoAuthCommand {

    String txt = "通知 [1-5]";

    @Override
    public CommandProperties properties() {
        return new CommandProperties("通知", "通知");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) {

        try {
            PrivateModel<Map<String, String>> result;
            Integer bossNum = Integer.parseInt(args.get(0));
            if (bossNum < 1 || bossNum > 5) {
                return new At(sender).plus("指令错误: " + txt);
            }
            result = pcrBotService.remindBoss(subject.getId(),bossNum);
            Map<String, List<Long>> ats = result.getAts();

            if (ats.containsKey(PcrNoticeType.order.name())) {
                Message order = makeAts(ats.get(PcrNoticeType.order.name()), subject)
                        .plus("\n" + result.getReturnObject().get(PcrNoticeType.order.name()));
                subject.sendMessage(order);
            }else{
                return new At(sender).plus("暂时没有人预约该boss");
            }
            return null;
        } catch (Exception e) {
            return new At(sender).plus("指令错误: " + txt);
        }

    }


}
