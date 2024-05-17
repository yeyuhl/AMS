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
package me.zhengjie.track.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import java.sql.Timestamp;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
* @description /
* @author yeyuhl
* @date 2024-04-06
**/
@Data
@TableName("face_recognition")
public class FaceRecognition implements Serializable {

    @TableId(value = "recognition_id", type = IdType.AUTO)
    @ApiModelProperty(value = "识别结果ID")
    private Integer recognitionId;

    @ApiModelProperty(value = "用户身份码")
    private String personId;

    @NotNull
    @ApiModelProperty(value = "识别时间")
    private Timestamp time;

    @NotBlank
    @ApiModelProperty(value = "识别位置")
    private String location;

    public void copy(FaceRecognition source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
