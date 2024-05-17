package me.zhengjie.attendance.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.attendance.domain.FaceAttendance;
import me.zhengjie.attendance.service.FaceAttendanceService;
import me.zhengjie.attendance.domain.vo.FaceAttendanceQueryCriteria;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.zhengjie.utils.PageResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author yeyuhl
 * @date 2024-04-05
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "考勤管理")
@RequestMapping("/api/faceAttendance")
public class FaceAttendanceController {

    private final FaceAttendanceService faceAttendanceService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('faceAttendance:list')")
    public void exportFaceAttendance(HttpServletResponse response, FaceAttendanceQueryCriteria criteria) throws IOException {
        faceAttendanceService.download(faceAttendanceService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询考勤")
    @ApiOperation("查询考勤")
    @PreAuthorize("@el.check('faceAttendance:list')")
    public ResponseEntity<PageResult<FaceAttendance>> queryFaceAttendance(FaceAttendanceQueryCriteria criteria, Page<Object> page) {
        return new ResponseEntity<>(faceAttendanceService.queryAll(criteria, page), HttpStatus.OK);
    }

    @PostMapping
    @Log("新增考勤")
    @ApiOperation("新增考勤")
    @PreAuthorize("@el.check('faceAttendance:add')")
    public ResponseEntity<Object> createFaceAttendance(@Validated @RequestBody FaceAttendance resources) {
        faceAttendanceService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改考勤")
    @ApiOperation("修改考勤")
    @PreAuthorize("@el.check('faceAttendance:edit')")
    public ResponseEntity<Object> updateFaceAttendance(@Validated @RequestBody FaceAttendance resources) {
        faceAttendanceService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除考勤")
    @ApiOperation("删除考勤")
    @PreAuthorize("@el.check('faceAttendance:del')")
    public ResponseEntity<Object> deleteFaceAttendance(@RequestBody List<Integer> ids) {
        faceAttendanceService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/uploadImages")
    @Log("上传图片并识别")
    @ApiOperation("上传图片并识别")
    public ResponseEntity<String> uploadAndRecognize(MultipartFile file) throws IOException {
        ResponseEntity<String> upload = faceAttendanceService.uploadImages(file);
        if (upload.getStatusCode() != HttpStatus.OK) {
            return upload;
        }
        return faceAttendanceService.updateAttendance(faceAttendanceService.recognize(file.getOriginalFilename()));
    }
}