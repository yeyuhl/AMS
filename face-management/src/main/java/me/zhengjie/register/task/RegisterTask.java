package me.zhengjie.register.task;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;


/**
 * @author yeyuhl
 * @description 定时任务
 * @date 2024-04-08
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class RegisterTask {
    private static final String ROOT_PATH = System.getProperty("user.dir") + File.separator + "files";

    /**
     * 由于有时会对人脸图片进行删除，所以需要定期重新编码
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Async
    public void recode() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("path", ROOT_PATH + File.separator);
        try {
            String response = HttpUtil.post("http://127.0.0.1:5000/recode", map);
            log.info("recode response: {}", response);
        } catch (IORuntimeException e) {
            log.error("连接失败，请稍后再试");
        }

    }
}
