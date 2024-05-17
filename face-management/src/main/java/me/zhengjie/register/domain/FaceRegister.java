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
package me.zhengjie.register.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import javax.validation.constraints.NotNull;

/**
* @description /
* @author yeyuhl
* @date 2024-03-27
**/
@Data
@TableName("face_register")
public class FaceRegister implements Serializable {

    @ApiModelProperty(value = "用户名称")
    private String name;

    @TableId(value = "person_id")
    @ApiModelProperty(value = "身份码")
    private String personId;

    @ApiModelProperty(value = "所属公司")
    private String company;

    @ApiModelProperty(value = "人脸图片(存储地址)")
    private String image;

    @NotNull
    @ApiModelProperty(value = "黑名单(0为白名单，1为黑名单)")
    private Integer type;

    public void copy(FaceRegister source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
