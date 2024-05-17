/*
 *  Copyright 2019-2023 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.track.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.track.domain.FaceRecognition;
import me.zhengjie.track.service.FaceRecognitionService;
import me.zhengjie.track.domain.vo.FaceRecognitionQueryCriteria;
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

/**
 * @author yeyuhl
 * @date 2024-04-06
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "轨迹查询管理")
@RequestMapping("/api/faceRecognition")
public class FaceRecognitionController {

    private final FaceRecognitionService faceRecognitionService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('faceRecognition:list')")
    public void exportFaceRecognition(HttpServletResponse response, FaceRecognitionQueryCriteria criteria) throws IOException {
        faceRecognitionService.download(faceRecognitionService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询轨迹查询")
    @ApiOperation("查询轨迹查询")
    @PreAuthorize("@el.check('faceRecognition:list')")
    public ResponseEntity<PageResult<FaceRecognition>> queryFaceRecognition(FaceRecognitionQueryCriteria criteria, Page<Object> page) {
        return new ResponseEntity<>(faceRecognitionService.queryAll(criteria, page), HttpStatus.OK);
    }

    @PostMapping
    @Log("新增轨迹查询")
    @ApiOperation("新增轨迹查询")
    @PreAuthorize("@el.check('faceRecognition:add')")
    public ResponseEntity<Object> createFaceRecognition(@Validated @RequestBody FaceRecognition resources) {
        faceRecognitionService.create(resources);
        if (resources.getPersonId() != null) {
            faceRecognitionService.pushToAttendance(resources);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改轨迹查询")
    @ApiOperation("修改轨迹查询")
    @PreAuthorize("@el.check('faceRecognition:edit')")
    public ResponseEntity<Object> updateFaceRecognition(@Validated @RequestBody FaceRecognition resources) {
        faceRecognitionService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除轨迹查询")
    @ApiOperation("删除轨迹查询")
    @PreAuthorize("@el.check('faceRecognition:del')")
    public ResponseEntity<Object> deleteFaceRecognition(@RequestBody List<Integer> ids) {
        faceRecognitionService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}