package me.zhengjie.attendance.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.zhengjie.attendance.domain.FaceAttendance;
import me.zhengjie.attendance.domain.vo.FaceAttendanceQueryCriteria;
import me.zhengjie.attendance.mapper.FaceAttendanceMapper;
import me.zhengjie.attendance.service.FaceAttendanceService;
import me.zhengjie.register.mapper.FaceRegisterMapper;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yeyuhl
 * @description 服务实现
 * @date 2024-04-05
 **/
@Service
@RequiredArgsConstructor
public class FaceAttendanceServiceImpl extends ServiceImpl<FaceAttendanceMapper, FaceAttendance> implements FaceAttendanceService {

    private final FaceAttendanceMapper faceAttendanceMapper;

    private final FaceRegisterMapper faceRegisterMapper;

    private static final String ROOT_PATH = System.getProperty("user.dir") + File.separator + "attendance";

    @Override
    public PageResult<FaceAttendance> queryAll(FaceAttendanceQueryCriteria criteria, Page<Object> page) {
        return PageUtil.toPage(faceAttendanceMapper.findAll(criteria, page));
    }

    @Override
    public List<FaceAttendance> queryAll(FaceAttendanceQueryCriteria criteria) {
        return faceAttendanceMapper.findAll(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(FaceAttendance resources) {
        save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(FaceAttendance resources) {
        FaceAttendance faceAttendance = getById(resources.getAttendanceId());
        faceAttendance.copy(resources);
        saveOrUpdate(faceAttendance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Integer> ids) {
        removeBatchByIds(ids);
    }

    @Override
    public void download(List<FaceAttendance> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (FaceAttendance faceAttendance : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("考勤时间", faceAttendance.getTime());
            map.put("用户身份码", faceAttendance.getPersonId());
            map.put("考勤位置", faceAttendance.getLocation());
            map.put("用户名称", faceAttendance.getName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public ResponseEntity<String> uploadImages(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (FileUtil.exist(ROOT_PATH + File.separator + originalFilename)) {
            return new ResponseEntity<>("文件已存在", HttpStatus.NOT_ACCEPTABLE);
        }
        File saveFile = new File(ROOT_PATH + File.separator + originalFilename);
        file.transferTo(saveFile);
        return new ResponseEntity<>("上传成功", HttpStatus.OK);
    }

    @Override
    public String recognize(String imageName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("imagePath", ROOT_PATH + File.separator + imageName);
        try {
            String response = HttpUtil.post("http://127.0.0.1:5000/recognize", map);
            return JSONUtil.parseObj(response).getStr("result");
        } catch (Exception e) {
            log.error(e.getMessage());
            return "EXPECTATION_FAILED";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> updateAttendance(String result) {
        if (result.equals("EXPECTATION_FAILED")) {
            return new ResponseEntity<>("识别失败", HttpStatus.EXPECTATION_FAILED);
        }
        String[] ids = result.split(",");
        String regex = "\"(.*?)\"";
        for (String id : ids) {
            // 创建Pattern对象
            Pattern pattern = Pattern.compile(regex);
            // 创建Matcher对象
            Matcher matcher = pattern.matcher(id);
            // 查找匹配的内容
            if (matcher.find()) {
                // 提取双引号中的内容
                String quotedString = matcher.group(1);
                // 需要先查询name是否存在，或者查询type是否为0
                if (faceRegisterMapper.selectById(quotedString) == null || faceRegisterMapper.selectById(quotedString).getType() == 1) {
                    continue;
                }
                FaceAttendance faceAttendance = new FaceAttendance();
                faceAttendance.setPersonId(quotedString);
                faceAttendance.setName(faceRegisterMapper.selectById(quotedString).getName());
                faceAttendance.setTime(new Timestamp(System.currentTimeMillis()));
                save(faceAttendance);
            }
        }
        return new ResponseEntity<>("识别成功", HttpStatus.OK);
    }
}