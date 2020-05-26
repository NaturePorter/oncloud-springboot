package cn.oncloud.controller;

import cn.oncloud.aspect.WebLog;
import cn.oncloud.dto.base.ResultBean;
import cn.oncloud.dto.base.ResultConst;
import cn.oncloud.exception.BussinessException;
import cn.oncloud.pojo.Document;
import cn.oncloud.pojo.User;
import cn.oncloud.service.DocumentService;
import cn.oncloud.util.DownloadFileUtil;
import cn.oncloud.util.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

/**
 * @author 余弘洋
 */
@RestController
@RequestMapping("/api")
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    DocumentService documentService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @PostMapping("/docupload")
    @WebLog("上传文档接口")
    public ResponseEntity docUpload(@RequestParam("file") MultipartFile file, String docname, String doclabel, String docdescribe,
                                    @RequestHeader("token") String token) {
        String strJson = stringRedisTemplate.opsForValue().get(token);
        User user = JSON.parseObject(strJson, User.class);
        //创建时间
        Date date = new Date();
        //创建Document实体类用于保存
        Document document = new Document();
        //判断该文件是否为空
        if (file.isEmpty()) {
            LOGGER.info("上传失败，请选择文件！");
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), ResultConst.FILE_NOT_EMPTY);
        }
        //获取原始的名字  original:最初的，起始的  方法是得到原来的文件名在客户机的文件系统名称
        //能获得上传文件的名称
        String oldName = file.getOriginalFilename();
        LOGGER.info("-----------文件原始的名字【" + oldName + "】-----------");
        //截取后缀名
        String prefix = oldName.substring(oldName.lastIndexOf(".") + 1);
        //生成新的文件名
        String newName = UUID.randomUUID() + "." + prefix;
        //获取项目的存放路径
        String docPath = FileUtil.systemPath() + "\\";
        File dest = new File(docPath + newName);
        LOGGER.info("-----------文件要保存后的新名字【" + newName + "】-----------");
        LOGGER.info("-----------上传文件保存的路径【" + docPath + "】-----------");
        LOGGER.info("-----------输出文件夹绝对路径 -- 这里的绝对路径是相当于当前项目的路径而不是“容器”路径【" + dest.getAbsolutePath() + "】-----------");
        InetAddress addr = null;
        try {
            //获取本机IP
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOGGER.error("程序异常, 详细信息:{}", e.getLocalizedMessage(), e);
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), ResultConst.UPLOAD_FAIL);
        }
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            //假如文件不存在即重新创建新的文件已防止异常发生
            dest.getParentFile().mkdirs();
        }
        try {
            document.setDocname(docname);
            document.setRealname(newName);
            document.setPerfix(prefix);
            document.setDocpath(docPath);
            document.setDocsize(file.getSize() + 0L);
            document.setDoclabel(doclabel);
            document.setDocdescribe(docdescribe);
            document.setDownloadfrequency(0L);
            document.setUploadtime(date);
            document.setUid(user.getId());
            document.setIsshared("0");
            document.setIsdelete("0");
            document.setIsaudit("0");
            documentService.save(document);
            file.transferTo(dest);
            return new ResponseEntity<>(ResultBean.ok("文件上传成功！"), HttpStatus.OK);
        } catch (IOException e) {
            LOGGER.error("程序异常, 详细信息:{}", e.getLocalizedMessage(), e);
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), ResultConst.UPLOAD_FAIL);
        }
    }

    @GetMapping("/doclist")
    @WebLog("文档列表接口")
    public ResponseEntity docList(@RequestParam(name = "query", required = false) String docName,
                                  @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                  @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                  @RequestHeader("token") String token) {
        String strJson = stringRedisTemplate.opsForValue().get(token);
        User user = JSON.parseObject(strJson, User.class);
        IPage<Document> page = documentService.selectByDocname(new Page<>(pageNum, pageSize), docName, user.getId());
        JSONObject resp = new JSONObject().fluentPut("total", page.getTotal());
        resp.put("docs", page.getRecords());
        return new ResponseEntity<>(ResultBean.ok(ResultConst.GET_DOCUMENTS_LIST_SUCC, resp), HttpStatus.OK);
    }

    @GetMapping("/halldoclist")
    @WebLog("大厅文档列表接口")
    public ResponseEntity halldoclist(@RequestParam(name = "query", required = false) String docName,
                                      @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                      @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        IPage<Document> page = documentService.hallselectByDocname(new Page<>(pageNum, pageSize), docName);
        JSONObject resp = new JSONObject().fluentPut("total", page.getTotal());
        resp.put("docs", page.getRecords());
        return new ResponseEntity<>(ResultBean.ok(ResultConst.GET_DOCUMENTS_LIST_SUCC, resp), HttpStatus.OK);
    }

    @PutMapping("/doclist/{id}/shared/{shared}")
    @WebLog("修改文档共享状态接口")
    public ResponseEntity updateState(@PathVariable Integer id, @PathVariable Integer shared) {
        documentService.updateDocSharedById(id, shared);
        return new ResponseEntity<>(ResultBean.ok(ResultConst.UPD_DOCUMENTS_SHARED_SUCC), HttpStatus.OK);
    }


    @RequestMapping("/download")
    @WebLog("下载文档接口")
    public void downloadFile(String id, HttpServletRequest request, HttpServletResponse response) {
        System.out.println(id);
        LambdaQueryWrapper<Document> queryWrapper = Wrappers.<Document>lambdaQuery().eq(Document::getId, id);
        Document document = documentService.getOne(queryWrapper);
        String realname = document.getRealname();
        //拼接下载名称
        String docName = document.getDocname() + "." + document.getPerfix();
        //拼接下载文件路径
        File file = new File(DownloadFileUtil.systemPath() + "\\" + realname);
        Long downloadfrequency = document.getDownloadfrequency();
        downloadfrequency = DownloadFileUtil.downloadFile(docName, file, response, request, downloadfrequency);
        document.setDownloadfrequency(downloadfrequency);
        documentService.saveOrUpdate(document);
    }

    @DeleteMapping("/deleteDocById/{id}")
    @WebLog("根据文档ID删除文档接口")
    public ResponseEntity deleteDocById(@PathVariable Integer id) {
        LambdaQueryWrapper<Document> queryWrapper = Wrappers.<Document>lambdaQuery().eq(Document::getId, id);
        Document document = documentService.getOne(queryWrapper);
        document.setIsdelete("1");
        documentService.updateById(document);
        return new ResponseEntity<>(ResultBean.ok(ResultConst.DEL_DOCUMENT_SUCC), HttpStatus.OK);
    }

    @GetMapping("/auditdoclist")
    @WebLog("审核文档列表接口")
    @RequiresPermissions("oc:doc:audit")
    public ResponseEntity auditlist(@RequestParam(name = "query", required = false) String docName,
                                      @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                      @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        IPage<Document> page = documentService.auditselectByDocname(new Page<>(pageNum, pageSize), docName);
        JSONObject resp = new JSONObject().fluentPut("total", page.getTotal());
        resp.put("docs", page.getRecords());
        return new ResponseEntity<>(ResultBean.ok(ResultConst.GET_DOCUMENTS_LIST_SUCC, resp), HttpStatus.OK);
    }

    @PutMapping("/auditdoclist/{id}/audit/{audit}")
    @WebLog("审核文档接口")
    public ResponseEntity updateAudit(@PathVariable Integer id, @PathVariable Integer audit) {
        documentService.updateDocAuditById(id, audit);
        return new ResponseEntity<>(ResultBean.ok(ResultConst.UPD_DOCUMENTS_AUDIT_SUCC), HttpStatus.OK);
    }

}
