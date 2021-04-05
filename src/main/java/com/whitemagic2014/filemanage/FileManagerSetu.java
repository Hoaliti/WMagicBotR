package com.whitemagic2014.filemanage;


import com.whitemagic2014.constant.ConstantFile;
import com.whitemagic2014.constant.ConstantPixiv;
import com.whitemagic2014.util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * create by MikuLink on 2020/12/13 15:44
 * for the Reisen
 * <p>
 * p站色图的文件专用管理器
 * 如果太多的话，考虑用逗号分隔来存储
 */
public class FileManagerSetu {

    /**
     * 加载文件内容
     *
     * @throws IOException 读写异常
     */
    public static void loadFile() throws IOException {
        File setuFile = FileUtil.fileCheck(ConstantFile.PIXIV_SETU_FILE_PATH);

        //初始化集合
        ConstantPixiv.List_SETU_PID = new ArrayList<>();

        //创建读取器
        BufferedReader reader = new BufferedReader(new FileReader(setuFile));
        //逐行读取文件
        String setuPid = null;
        while ((setuPid = reader.readLine()) != null) {
            //过滤掉空行
            if (setuPid.length() <= 0) continue;
            ConstantPixiv.List_SETU_PID.add(setuPid);
        }
        //关闭读取器
        reader.close();
        //刷新最大元素数目
        ConstantPixiv.SETU_PID_LIST_MAX_SIZE = ConstantPixiv.List_SETU_PID.size();
    }

    /**
     * 对文件写入内容
     *
     * @throws IOException 读写异常
     */
    public static List<String> writeFile(List<String> pids) throws IOException {

        //覆写原本配置
        //先读取出所有pid，判重后直接覆写文件
        //如果超出2W，真要考虑接DB了
        List<String> tempSetuPid = new ArrayList<>();
        File setuFile = FileUtil.fileCheck(ConstantFile.PIXIV_SETU_FILE_PATH);
        //创建读取器
        BufferedReader reader = new BufferedReader(new FileReader(setuFile));
        //逐行读取文件
        String setuPid = null;
        while ((setuPid = reader.readLine()) != null) {
            //过滤掉空行
            if (setuPid.length() <= 0) continue;
            tempSetuPid.add(setuPid);
        }
        //关闭读取器
        reader.close();

        //过滤重复
        List<String> tempNewPid = new ArrayList<>();
        for (String pidStr : pids) {
            if (tempSetuPid.contains(pidStr)) {
                continue;
            }
            tempSetuPid.add(pidStr);
            tempNewPid.add(pidStr);
        }
        //创建写入流
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ConstantFile.PIXIV_SETU_FILE_PATH, false)));
        for (String pidStr : tempSetuPid) {
            out.write("\r\n" + pidStr);
        }
        //关闭写入流
        out.close();

        //返回删选后的pid列表
        return tempNewPid;
    }
}
