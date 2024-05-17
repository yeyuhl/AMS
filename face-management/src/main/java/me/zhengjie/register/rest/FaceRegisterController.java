package me.zhengjie.register.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.register.domain.FaceRegister;
import me.zhengjie.register.domain.vo.FaceRegisterQueryCriteria;
import me.zhengjie.register.service.FaceRegisterService;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.PageResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author yeyuhl
 * @date 2024-03-27
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "人脸注册管理")
@RequestMapping("/api/faceRegister")
public class FaceRegisterController {

    private final FaceRegisterService faceRegisterService;

    private static final String ROOT_PATH = System.getProperty("user.dir") + File.separator + "files";

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('faceRegister:list')")
    public void exportFaceRegister(HttpServletResponse response, FaceRegisterQueryCriteria criteria) throws IOException {
        faceRegisterService.download(faceRegisterService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询人脸注册")
    @ApiOperation("查询人脸注册")
    @PreAuthorize("@el.check('faceRegister:list')")
    public ResponseEntity<PageResult<FaceRegister>> queryFaceRegister(FaceRegisterQueryCriteria criteria, Page<Object> page) {
        return new ResponseEntity<>(faceRegisterService.queryAll(criteria, page), HttpStatus.OK);
    }

    @PostMapping
    @Log("新增人脸注册")
    @ApiOperation("新增人脸注册")
    @PreAuthorize("@el.check('faceRegister:add')")
    public ResponseEntity<Object> createFaceRegister(@Validated @RequestBody FaceRegister resources) {
        faceRegisterService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改人脸注册")
    @ApiOperation("修改人脸注册")
    @PreAuthorize("@el.check('faceRegister:edit')")
    public ResponseEntity<Object> updateFaceRegister(@Validated @RequestBody FaceRegister resources) {
        faceRegisterService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除人脸注册")
    @ApiOperation("删除人脸注册")
    @PreAuthorize("@el.check('faceRegister:del')")
    public ResponseEntity<Object> deleteFaceRegister(@RequestBody List<String> ids) {
        faceRegisterService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/uploadImage")
    @Log("上传图片(单张)")
    @ApiOperation("上传图片(单张)")
    public ResponseEntity<String> uploadImageAndEncode(MultipartFile file, String fileName) throws IOException {
        return faceRegisterService.uploadImage(file, fileName);
    }

    @PostMapping("/uploadImages")
    @Log("上传图片(多张)")
    @ApiOperation("上传图片(多张)")
    public ResponseEntity<String> uploadImagesAndEncode(MultipartFile file) throws IOException {
        return faceRegisterService.uploadImages(file);
    }

    @PostMapping("/uploadCSV")
    @Log("上传CSV文件并解析")
    @ApiOperation("上传CSV文件并解析")
    public ResponseEntity<String> uploadCSV(MultipartFile file) throws IOException {
        String filePath = faceRegisterService.uploadCSV(file);
        return faceRegisterService.parsingCSV(filePath);
    }

    @GetMapping("/downloadImages/{fileName}")
    public void downloadImage(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        String filePath = ROOT_PATH + File.separator + fileName;
        if (!FileUtil.exist(filePath)) {
            return;
        }
        byte[] bytes = FileUtil.readBytes(filePath);
        ServletOutputStream os = response.getOutputStream();
        os.write(bytes);
        os.flush();
        os.close();
    }

    @PostMapping("/updateImageUrl")
    @Log("更新图片URL并编码")
    @ApiOperation("更新图片URL并编码")
    public ResponseEntity<String> updateImageUrl(String path,String fileName) {
        return faceRegisterService.updateImageUrl(fileName, path);
    }
}