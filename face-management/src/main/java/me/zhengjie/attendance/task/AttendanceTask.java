package me.zhengjie.attendance.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.utils.FileUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;


/**
 * @author yeyuhl
 * @description 定时任务
 * @date 2024-04-08
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class AttendanceTask {
    private static final String ROOT_PATH = System.getProperty("user.dir") + File.separator + "attendance";

    /**
     * 定时删除考勤图片
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Async
    public void deleteAttendanceImg() {
        File[] images = FileUtil.ls(ROOT_PATH);
        for (File image : images) {
            image.delete();
        }
    }
}
