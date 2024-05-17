package me.zhengjie.register.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import me.zhengjie.register.domain.FaceRegister;
import me.zhengjie.register.domain.vo.FaceRegisterQueryCriteria;
import me.zhengjie.utils.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author yeyuhl
 * @description 服务接口
 * @date 2024-03-27
 **/
public interface FaceRegisterService extends IService<FaceRegister> {

    /**
     * 查询数据分页
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return PageResult
     */
    PageResult<FaceRegister> queryAll(FaceRegisterQueryCriteria criteria, Page<Object> page);

    /**
     * 查询所有数据不分页
     *
     * @param criteria 条件参数
     * @return List<FaceRegisterDto>
     */
    List<FaceRegister> queryAll(FaceRegisterQueryCriteria criteria);

    /**
     * 创建
     *
     * @param resources /
     */
    void create(FaceRegister resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(FaceRegister resources);

    /**
     * 多选删除
     *
     * @param ids /
     */
    void deleteAll(List<String> ids);

    /**
     * 导出数据
     *
     * @param all      待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<FaceRegister> all, HttpServletResponse response) throws IOException;

    /**
     * 上传图片
     *
     * @param file     图片文件
     * @param fileName 文件名
     * @return 图片路径 + 文件名(不包含后缀)
     */
    ResponseEntity<String> uploadImage(MultipartFile file, String fileName) throws IOException;

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片路径 + 文件名(不包含后缀)
     */
    ResponseEntity<String> uploadImages(MultipartFile file) throws IOException;

    /**
     * 上传csv
     *
     * @param file csv文件
     * @return 文件路径
     * @throws IOException
     */
    String uploadCSV(MultipartFile file) throws IOException;

    /**
     * 编码图片
     *
     * @param imageName 图片名
     * @return /
     * @throws IOException
     */
    boolean encodeImages(String imageName) throws IOException;

    /**
     * 更新图片url
     */
    ResponseEntity<String> updateImageUrl(String personId, String imageUrl);


    /**
     * 解析csv
     *
     * @param path 文件路径
     * @return /
     */
    ResponseEntity<String> parsingCSV(String path);

    /**
     * 删除图片
     *
     * @param fileName 文件名
     * @return /
     */
    void deleteImage(String fileName);

}