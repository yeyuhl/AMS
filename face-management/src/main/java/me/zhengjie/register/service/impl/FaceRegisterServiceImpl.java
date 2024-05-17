package me.zhengjie.register.service.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.zhengjie.register.domain.FaceRegister;
import me.zhengjie.register.domain.vo.FaceRegisterQueryCriteria;
import me.zhengjie.register.mapper.FaceRegisterMapper;
import me.zhengjie.register.service.FaceRegisterService;
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
import java.util.*;

/**
 * @author yeyuhl
 * @description 服务实现
 * @date 2024-03-27
 **/
@Service
@RequiredArgsConstructor
public class FaceRegisterServiceImpl extends ServiceImpl<FaceRegisterMapper, FaceRegister> implements FaceRegisterService {

    private final FaceRegisterMapper faceRegisterMapper;

    private static final String ROOT_PATH = System.getProperty("user.dir") + File.separator + "files";

    @Override
    public PageResult<FaceRegister> queryAll(FaceRegisterQueryCriteria criteria, Page<Object> page) {
        return PageUtil.toPage(faceRegisterMapper.findAll(criteria, page));
    }

    @Override
    public List<FaceRegister> queryAll(FaceRegisterQueryCriteria criteria) {
        return faceRegisterMapper.findAll(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(FaceRegister resources) {
        save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(FaceRegister resources) {
        FaceRegister faceRegister = getById(resources.getPersonId());
        faceRegister.copy(resources);
        saveOrUpdate(faceRegister);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<String> ids) {
        removeBatchByIds(ids);
        for (String id : ids) {
            deleteImage(id);
        }
    }


    @Override
    public void download(List<FaceRegister> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (FaceRegister faceRegister : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名称", faceRegister.getName());
            map.put("所属公司", faceRegister.getCompany());
            map.put("人脸图片(存储地址)", faceRegister.getImage());
            map.put("黑名单(0为白名单，1为黑名单)", faceRegister.getType());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public ResponseEntity<String> uploadImage(MultipartFile file, String fileName) throws IOException {
        File saveFile = new File(ROOT_PATH + File.separator + fileName);
        file.transferTo(saveFile);
        String url = "http://localhost:8000/api/faceRegister/downloadImages/" + fileName;
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> uploadImages(MultipartFile file) throws IOException {
        File saveFile = new File(ROOT_PATH + File.separator + file.getOriginalFilename());
        file.transferTo(saveFile);
        String url = "http://localhost:8000/api/faceRegister/downloadImages/" + file.getOriginalFilename();
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @Override
    public String uploadCSV(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String path = ROOT_PATH + File.separator + "csv" + File.separator + fileName;
        File saveFile = new File(path);
        file.transferTo(saveFile);
        return path;
    }

    @Override
    public boolean encodeImages(String fileName) {
        HashMap<String, Object> map = new HashMap<>();
        String mainFileName = FileUtil.getFileNameNoEx(fileName);
        map.put("path", ROOT_PATH + File.separator + fileName);
        map.put("imageName", mainFileName);
        try {
            String response = HttpUtil.post("http://127.0.0.1:5000/encode", map);
            return JSONUtil.parseObj(response).getInt("code") == 200;
        } catch (IORuntimeException e) {
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> updateImageUrl(String fileName, String path) {
        String personId = FileUtil.getFileNameNoEx(fileName);
        FaceRegister faceRegister = faceRegisterMapper.selectById(personId);
        if (faceRegister != null) {
            boolean flag = encodeImages(fileName);
            if (!flag) {
                // 删除本地图片
                FileUtil.del(ROOT_PATH + File.separator + fileName);
                return new ResponseEntity<>("更新失败，请重传或更换图片", HttpStatus.NOT_FOUND);
            }
            UpdateWrapper<FaceRegister> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("person_id", personId);
            updateWrapper.set("image", path);
            faceRegisterMapper.update(faceRegister, updateWrapper);
            return new ResponseEntity<>("更新成功", HttpStatus.OK);
        } else {
            // 删除本地图片
            FileUtil.del(ROOT_PATH + File.separator + fileName);
            return new ResponseEntity<>("更新失败，请重传或更换图片", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<String> parsingCSV(String path) {
        CsvReader reader = CsvUtil.getReader();
        List<FaceRegister> rows = reader.read(ResourceUtil.getUtf8Reader(path), FaceRegister.class);
        for (FaceRegister row : rows) {
            faceRegisterMapper.insert(row);
        }
        FileUtil.del(path);
        return new ResponseEntity<>("上传并解析成功", HttpStatus.OK);
    }

    @Override
    public void deleteImage(String fileName) {
        // 删除本地图片
        File[] images = FileUtil.ls(ROOT_PATH);
        for (File image : images) {
            if (image.getName().contains(".")) {
                String[] split = image.getName().split("\\.");
                if (fileName.equals(split[0])) {
                    image.delete();
                }
            }
        }
    }
}