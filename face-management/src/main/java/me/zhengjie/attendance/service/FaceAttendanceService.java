package me.zhengjie.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import me.zhengjie.attendance.domain.FaceAttendance;
import me.zhengjie.attendance.domain.vo.FaceAttendanceQueryCriteria;
import me.zhengjie.utils.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author yeyuhl
 * @description 服务接口
 * @date 2024-04-05
 **/
public interface FaceAttendanceService extends IService<FaceAttendance> {

    /**
     * 查询数据分页
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return PageResult
     */
    PageResult<FaceAttendance> queryAll(FaceAttendanceQueryCriteria criteria, Page<Object> page);

    /**
     * 查询所有数据不分页
     *
     * @param criteria 条件参数
     * @return List<FaceAttendanceDto>
     */
    List<FaceAttendance> queryAll(FaceAttendanceQueryCriteria criteria);

    /**
     * 创建
     *
     * @param resources /
     */
    void create(FaceAttendance resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(FaceAttendance resources);

    /**
     * 多选删除
     *
     * @param ids /
     */
    void deleteAll(List<Integer> ids);

    /**
     * 导出数据
     *
     * @param all      待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<FaceAttendance> all, HttpServletResponse response) throws IOException;


    /**
     * 上传图片
     *
     * @param file 上传的图片
     * @return ResponseEntity<String>
     */
    ResponseEntity<String> uploadImages(MultipartFile file) throws IOException;


    /**
     * 调用识别API
     *
     * @param imageName 图片名称
     * @return String
     */
    String recognize(String imageName);

    /**
     * 根据识别结果更新考勤信息
     *
     * @param result 识别结果
     * @return ResponseEntity<String>
     */
    ResponseEntity<String> updateAttendance(String result);
}