package com.whitemagic2014.service;


import com.whitemagic2014.bot.MagicBotR;
import com.whitemagic2014.util.CollectionUtil;
import com.whitemagic2014.util.NumberUtil;
import com.whitemagic2014.util.StringUtil;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * create by MikuLink on 2020/1/9 17:00
 * for the Reisen
 * 机器人服务
 */
@Service
public class RabbitBotService {
    @Value("${bot.master}")
    private String account_master;
    private List<Long> accountMasterList = new ArrayList<>();
    @Value("${bot.admin}")
    private String account_admin;
    private List<Long> accountAdminList = new ArrayList<>();

    //上传图片用 不清楚这样有什么影响，但每次代码里为了上传图片，必须取消息来源的Contact太说不过去了
    private Group group;

    /**
     * 检测指令操作间隔
     *
     * @param splitMap        指令对应的计时map
     * @param userId          qq号
     * @param userName        qq名称
     * @param commadSplitTime 间隔多久（毫秒）
     * @param splitMsg        检测未通过时的提示信息
     */
    public String commandTimeSplitCheck(Map<Long, Long> splitMap, Long userId, String userName, Long commadSplitTime, String splitMsg) {
        if (isMaster(userId)) {
            return null;
        }
        if (!splitMap.containsKey(userId)) {
            return null;
        }
        Long lastTime = splitMap.get(userId);
        if (null == lastTime) {
            return null;
        }
        Long nowTime = System.currentTimeMillis();
        Long splitTime = nowTime - lastTime;
        //判断是否允许操作
        if (splitTime <= commadSplitTime) {
            return String.format(splitMsg, userName, (commadSplitTime - splitTime) / 1000);
        }
        //其他情况允许操作
        return null;
    }

    /**
     * 是否最高权限
     *
     * @param userId qq号
     * @return 是否为最高权限
     */
    public boolean isMaster(Long userId) {
        if (null == userId || StringUtil.isEmpty(account_master)) {
            return false;
        }
        if (CollectionUtils.isEmpty(accountMasterList)) {
            accountStrCheck(account_master, accountMasterList);
        }
        return accountMasterList.contains(userId);
    }

    /**
     * 是否为管理员权限
     *
     * @param userId qq号
     * @return 是否为管理员权限
     */
    public boolean isRabbitAdmin(Long userId) {
        if (null == userId) {
            return false;
        }
        if (isMaster((userId))) {
            return true;
        }
        if (CollectionUtils.isEmpty(accountAdminList)) {
            accountStrCheck(account_admin, accountAdminList);
        }
        return accountAdminList.contains(userId);
    }

    private void accountStrCheck(String account_strs, List<Long> accountList) {
        if (StringUtil.isEmpty(account_strs)) {
            return;
        }
        String[] accounts = account_strs.split(",");
        for (String account : accounts) {
            if (StringUtil.isEmpty(account) || !NumberUtil.isNumberOnly(account)) {
                continue;
            }
            accountList.add(NumberUtil.toLong(account));
        }
    }


    /**
     * 上传图片，获取图片id
     * 重载，单条转化
     *
     * @param localImagesPath 本地图片地址
     * @return mirai图片id
     */
    public Image uploadMiraiImage(String localImagesPath) {
        if (null == group) {
            ContactList<Group> groupList = MagicBotR.getBot().getGroups();
            for (Group grouptemp : groupList) {
                group = grouptemp;
                break;
            }
        }
        //上传
        return group.uploadImage(new File(localImagesPath));
//        return ExternalResource.uploadAsImage(new File(localImagesPath), group);
    }

    /**
     * 上传图片，获取图片id
     *
     * @param localImagesPath 本地图片列表
     * @return mirai图片id列表
     */
    public List<Image> uploadMiraiImage(List<String> localImagesPath) {
        List<Image> miraiImgList = new ArrayList<>();
        //上传并获取每张图片的id
        if (CollectionUtil.isEmpty(localImagesPath)) {
            return miraiImgList;
        }
        for (String localImagePath : localImagesPath) {
            Image tempMiraiImg = uploadMiraiImage(localImagePath);
            miraiImgList.add(tempMiraiImg);
        }
        return miraiImgList;
    }

    /**
     * 单图拼接成消息连做的代码封装方法
     *
     * @param imgInfo mirai图片
     * @return 消息链
     */
    public MessageChain parseMsgChainByImg(Image imgInfo) {
        MessageChain messageChain = MessageUtils.newChain();
        messageChain = messageChain.plus("").plus(imgInfo).plus("\n");
        return messageChain;
    }

    /**
     * 针对多张图拼接成消息连做的代码封装方法
     *
     * @param imgList mirai图片集合
     * @return 消息链
     */
    public MessageChain parseMsgChainByImgs(List<Image> imgList) {
        MessageChain messageChain = MessageUtils.newChain();
        for (Image image : imgList) {
            messageChain = messageChain.plus("").plus(image).plus("\n");
        }
        return messageChain;
    }


    /**
     * 针对本地图片路径上传并拼接成消息连做的代码封装方法
     *
     * @param localImgPath 本地图片路径
     * @return 消息链
     */
    public MessageChain parseMsgChainByLocalImgs(String localImgPath) {
        return parseMsgChainByLocalImgs(Arrays.asList(localImgPath));
    }

    /**
     * 针对本地图片路径上传并拼接成消息连做的代码封装方法
     * 重载 批量处理
     *
     * @param localImgsPath 本地图片路径
     * @return 消息链
     */
    public MessageChain parseMsgChainByLocalImgs(List<String> localImgsPath) {
        MessageChain messageChain = MessageUtils.newChain();
        List<Image> imageList = uploadMiraiImage(localImgsPath);
        for (Image image : imageList) {
            messageChain.plus("").plus(image).plus("\n");
        }
        return messageChain;
    }

    /**
     * 给最高权限发送消息
     *
     * @param messageChain 消息链
     */
    public void sendMasterMessage(MessageChain messageChain) {
        if (CollectionUtils.isEmpty(accountMasterList)) {
            accountStrCheck(account_master, accountMasterList);
        }
        for (Long accountMaster : accountMasterList) {
            sendFriendMessage(accountMaster, messageChain);
        }
    }

    /**
     * 发送好友私聊消息
     *
     * @param qq           目标账号
     * @param messageChain 消息链
     */
    public void sendFriendMessage(Long qq, MessageChain messageChain) {
        Friend friend = MagicBotR.getBot().getFriend(qq);
        if (null == friend) {
            return;
        }
        friend.sendMessage(messageChain);
    }
}
