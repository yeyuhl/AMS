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
package me.zhengjie.track.service;

import me.zhengjie.track.domain.FaceRecognition;
import me.zhengjie.track.domain.vo.FaceRecognitionQueryCriteria;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import me.zhengjie.utils.PageResult;

/**
 * @author yeyuhl
 * @description 服务接口
 * @date 2024-04-06
 **/
public interface FaceRecognitionService extends IService<FaceRecognition> {

    /**
     * 查询数据分页
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return PageResult
     */
    PageResult<FaceRecognition> queryAll(FaceRecognitionQueryCriteria criteria, Page<Object> page);

    /**
     * 查询所有数据不分页
     *
     * @param criteria 条件参数
     * @return List<FaceRecognitionDto>
     */
    List<FaceRecognition> queryAll(FaceRecognitionQueryCriteria criteria);

    /**
     * 创建
     *
     * @param resources /
     */
    void create(FaceRecognition resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(FaceRecognition resources);

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
    void download(List<FaceRecognition> all, HttpServletResponse response) throws IOException;

    /**
     * 将识别结果推送到考勤打卡表
     *
     * @param resources 识别结果
     */
    void pushToAttendance(FaceRecognition resources);
}