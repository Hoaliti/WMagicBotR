package com.whitemagic2014.service;

import com.alibaba.fastjson.JSONObject;
import com.whitemagic2014.constant.ConstantPixiv;
import com.whitemagic2014.filemanage.FileManagerSetu;
import com.whitemagic2014.pojo.ReString;
import com.whitemagic2014.pojo.setu.PixivImageInfo;
import com.whitemagic2014.util.NumberUtil;
import com.whitemagic2014.util.RandomUtil;
import com.whitemagic2014.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * create by MikuLink on 2020/12/31 15:18
 * for the Reisen
 * 来点色图
 */
@Service
public class SetuService {
    private static final Logger logger = LoggerFactory.getLogger(SetuService.class);

    //pixiv色图操作间隔
    @Value("${setu.split.time:60000}")
    public Long SETU_PID_SPLIT_TIME = 1000L * 60;

    @Autowired
    private PixivService pixivService;
    @Autowired
    private RabbitBotService rabbitBotService;

    /**
     * 来张色图
     */
    public PixivImageInfo getSetu() throws IOException {
        //获取一个pid
        Long setu_pid = NumberUtil.toLong(randOneSetuPid());

        //走pixiv图片id获取流程
        return pixivService.getPixivImgInfoById(setu_pid);
    }


    /**
     * 检查色图操作间隔
     *
     * @param userId   企鹅号
     * @param userNick 昵称
     * @return 是否可以操作，和提示语什么的
     */
    public ReString setuTimeCheck(Long userId, String userNick) {
        ReString reString = new ReString(true);
        //操作间隔判断
        String timeCheck = rabbitBotService.commandTimeSplitCheck(ConstantPixiv.SETU_PID_SPLIT_MAP, userId, userNick,
                SETU_PID_SPLIT_TIME, RandomUtil.rollStrFromList(ConstantPixiv.SETU_SPLIT_ERROR_LIST));
        if (StringUtil.isNotEmpty(timeCheck)) {
            reString = new ReString(false, timeCheck);
        }
        return reString;
    }

    /**
     * 从列表里选取一张色图pid
     *
     * @return 色图pid
     */
    public String randOneSetuPid() throws IOException {
        //集合为空时，重新加载一次色图文件
        if (ConstantPixiv.List_SETU_PID.size() == 0) {
            FileManagerSetu.loadFile();
        }
        //随机色图
        String setuPid = RandomUtil.rollStrFromList(ConstantPixiv.List_SETU_PID);
        //删除这个色图，实现伪随机
        ConstantPixiv.List_SETU_PID.remove(setuPid);
        //元素少于1/6的时候，重新加载
        if (ConstantPixiv.List_SETU_PID.size() < ConstantPixiv.SETU_PID_LIST_MAX_SIZE / 6) {
            FileManagerSetu.loadFile();
        }
        return setuPid;
    }

    /**
     * 添加一些色图
     *
     * @param pidList 色图pid列表
     */
    public void addSetu(List<String> pidList) {
        //写入文件，顺便判重
        try {
            pidList = FileManagerSetu.writeFile(pidList);
        } catch (Exception ex) {
            //异常直接舍弃所有，允许这类损失
            logger.error("SetuService addSetu error,pidList:{}", JSONObject.toJSONString(pidList), ex);
        }

        //加入当前内存列表里
        ConstantPixiv.List_SETU_PID.addAll(pidList);
        //刷新最大元素数目
        ConstantPixiv.SETU_PID_LIST_MAX_SIZE += pidList.size();
    }

}
