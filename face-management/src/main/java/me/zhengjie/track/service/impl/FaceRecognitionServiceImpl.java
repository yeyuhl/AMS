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
package me.zhengjie.track.service.impl;

import me.zhengjie.attendance.domain.FaceAttendance;
import me.zhengjie.attendance.mapper.FaceAttendanceMapper;
import me.zhengjie.register.mapper.FaceRegisterMapper;
import me.zhengjie.track.domain.FaceRecognition;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.zhengjie.track.service.FaceRecognitionService;
import me.zhengjie.track.domain.vo.FaceRecognitionQueryCriteria;
import me.zhengjie.track.mapper.FaceRecognitionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import me.zhengjie.utils.PageUtil;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import me.zhengjie.utils.PageResult;

/**
 * @author yeyuhl
 * @description 服务实现
 * @date 2024-04-06
 **/
@Service
@RequiredArgsConstructor
public class FaceRecognitionServiceImpl extends ServiceImpl<FaceRecognitionMapper, FaceRecognition> implements FaceRecognitionService {

    private final FaceRecognitionMapper faceRecognitionMapper;

    private final FaceAttendanceMapper faceAttendanceMapper;

    private final FaceRegisterMapper faceRegisterMapper;

    @Override
    public PageResult<FaceRecognition> queryAll(FaceRecognitionQueryCriteria criteria, Page<Object> page) {
        return PageUtil.toPage(faceRecognitionMapper.findAll(criteria, page));
    }

    @Override
    public List<FaceRecognition> queryAll(FaceRecognitionQueryCriteria criteria) {
        return faceRecognitionMapper.findAll(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(FaceRecognition resources) {
        save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(FaceRecognition resources) {
        FaceRecognition faceRecognition = getById(resources.getRecognitionId());
        faceRecognition.copy(resources);
        saveOrUpdate(faceRecognition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Integer> ids) {
        removeBatchByIds(ids);
    }

    @Override
    public void download(List<FaceRecognition> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (FaceRecognition faceRecognition : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户身份码", faceRecognition.getPersonId());
            map.put("识别时间", faceRecognition.getTime());
            map.put("识别位置", faceRecognition.getLocation());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void pushToAttendance(FaceRecognition resources) {
        FaceAttendance faceAttendance = new FaceAttendance();
        if(faceRegisterMapper.selectById(resources.getPersonId()).getType()==1){
            return;
        }
        faceAttendance.setPersonId(resources.getPersonId());
        faceAttendance.setTime(resources.getTime());
        faceAttendance.setLocation(resources.getLocation());
        faceAttendance.setName(faceRegisterMapper.selectById(resources.getPersonId()).getName());
        faceAttendanceMapper.insert(faceAttendance);
    }
}