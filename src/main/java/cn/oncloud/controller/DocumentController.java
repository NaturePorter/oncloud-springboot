package cn.oncloud.controller;

import cn.oncloud.aspect.WebLog;
import cn.oncloud.common.utils.Constant;
import cn.oncloud.common.utils.DownloadFileUtil;
import cn.oncloud.dto.base.ResultBean;
import cn.oncloud.dto.base.ResultConst;
import cn.oncloud.entity.Document;
import cn.oncloud.entity.User;
import cn.oncloud.exception.BussinessException;
import cn.oncloud.service.DocumentService;
import cn.oncloud.service.TokenService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author 余弘洋
 */
@RestController
@RequestMapping("/api")
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    /**
     * 只允许常规的字母数字后缀
     */
    private static final Pattern EXT_PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,10}$");

    private final DocumentService documentService;
    private final TokenService tokenService;

    @Value("${oncloud.upload-dir}")
    private String uploadDir;

    public DocumentController(DocumentService documentService, TokenService tokenService) {
        this.documentService = documentService;
        this.tokenService = tokenService;
    }

    @PostMapping("/docupload")
    @WebLog("上传文档接口")
    public ResponseEntity<ResultBean> docUpload(@RequestParam("file") MultipartFile file,
                                                @RequestParam String docname,
                                                @RequestParam(required = false) String doclabel,
                                                @RequestParam(required = false) String docdescribe,
                                                HttpServletRequest request) throws IOException {
        User user = currentUser(request);
        if (file == null || file.isEmpty()) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), ResultConst.FILE_NOT_EMPTY);
        }
        if (StringUtils.isBlank(docname)) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "文档名称不能为空");
        }

        String oldName = StringUtils.defaultString(file.getOriginalFilename());
        String prefix = StringUtils.substringAfterLast(oldName, ".").toLowerCase();
        if (!EXT_PATTERN.matcher(prefix).matches()) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "不支持的文件类型");
        }

        Path storage = storagePath();
        Files.createDirectories(storage);
        String newName = UUID.randomUUID() + "." + prefix;
        Path dest = storage.resolve(newName).normalize();
        if (!dest.startsWith(storage)) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "非法的文件名");
        }

        Document document = new Document();
        document.setDocname(docname.trim());
        document.setRealname(newName);
        document.setPerfix(prefix);
        document.setDocpath(storage.toString() + File.separator);
        document.setDocsize(file.getSize());
        document.setDoclabel(doclabel);
        document.setDocdescribe(docdescribe);
        document.setDownloadfrequency(0L);
        document.setUploadtime(new Date());
        document.setUid(user.getId());
        document.setIsshared("0");
        document.setIsdelete("0");
        document.setIsaudit("0");
        documentService.save(document);

        try {
            file.transferTo(dest.toFile());
        } catch (IOException e) {
            // 文件写入失败时回滚数据库记录，避免出现脏数据
            documentService.removeById(document.getId());
            LOGGER.error("文件保存失败: {}", e.getMessage(), e);
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), ResultConst.UPLOAD_FAIL);
        }
        return ResponseEntity.ok(ResultBean.ok("文件上传成功！"));
    }

    @GetMapping("/doclist")
    @WebLog("文档列表接口")
    public ResponseEntity<ResultBean> docList(@RequestParam(name = "query", required = false) String docName,
                                              @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                              @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                              HttpServletRequest request) {
        User user = currentUser(request);
        IPage<Document> page = documentService.selectByDocname(new Page<>(pageNum, pageSize), docName, user.getId());
        return ResponseEntity.ok(ResultBean.ok(ResultConst.GET_DOCUMENTS_LIST_SUCC, toResponse(page)));
    }

    @GetMapping("/halldoclist")
    @WebLog("大厅文档列表接口")
    public ResponseEntity<ResultBean> halldoclist(@RequestParam(name = "query", required = false) String docName,
                                                  @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        IPage<Document> page = documentService.hallselectByDocname(new Page<>(pageNum, pageSize), docName);
        return ResponseEntity.ok(ResultBean.ok(ResultConst.GET_DOCUMENTS_LIST_SUCC, toResponse(page)));
    }

    @PutMapping("/doclist/{id}/shared/{shared}")
    @WebLog("修改文档共享状态接口")
    public ResponseEntity<ResultBean> updateShared(@PathVariable Long id, @PathVariable Integer shared,
                                                   HttpServletRequest request) {
        User user = currentUser(request);
        Document document = getDocument(id);
        if (!tokenService.canManage(user, document.getUid())) {
            throw new BussinessException(HttpStatus.FORBIDDEN.value(), "无权操作该文档");
        }
        documentService.updateDocSharedById(id, resolveFlag(shared, "共享状态"));
        return ResponseEntity.ok(ResultBean.ok(ResultConst.UPD_DOCUMENTS_SHARED_SUCC));
    }

    @GetMapping("/download")
    @WebLog("下载文档接口")
    public void downloadFile(@RequestParam Long id, HttpServletRequest request, HttpServletResponse response) {
        User user = currentUser(request);
        Document document = getDocument(id);
        boolean shared = "1".equals(document.getIsshared()) && "1".equals(document.getIsaudit());
        if (!shared && !tokenService.canManage(user, document.getUid())) {
            throw new BussinessException(HttpStatus.FORBIDDEN.value(), "无权下载该文档");
        }

        Path storage = storagePath();
        File file = storage.resolve(document.getRealname()).normalize().toFile();
        if (!file.toPath().normalize().startsWith(storage)) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "非法的文件路径");
        }
        String downloadName = document.getDocname() + "." + document.getPerfix();
        DownloadFileUtil.downloadFile(downloadName, file, response, document.getDownloadfrequency());
        documentService.increaseDownloadFrequency(id);
    }

    @DeleteMapping("/deleteDocById/{id}")
    @WebLog("根据文档ID删除文档接口")
    public ResponseEntity<ResultBean> deleteDocById(@PathVariable Long id, HttpServletRequest request) {
        User user = currentUser(request);
        Document document = getDocument(id);
        if (!tokenService.canManage(user, document.getUid())) {
            throw new BussinessException(HttpStatus.FORBIDDEN.value(), "无权操作该文档");
        }
        Document update = new Document();
        update.setId(id);
        update.setIsdelete("1");
        documentService.updateById(update);
        return ResponseEntity.ok(ResultBean.ok(ResultConst.DEL_DOCUMENT_SUCC));
    }

    @GetMapping("/auditdoclist")
    @WebLog("审核文档列表接口")
    @RequiresRoles(value = {Constant.ROLE_SUPER_ADMIN, Constant.ROLE_ADMIN}, logical = Logical.OR)
    public ResponseEntity<ResultBean> auditlist(@RequestParam(name = "query", required = false) String docName,
                                                @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        IPage<Document> page = documentService.auditselectByDocname(new Page<>(pageNum, pageSize), docName);
        return ResponseEntity.ok(ResultBean.ok(ResultConst.GET_DOCUMENTS_LIST_SUCC, toResponse(page)));
    }

    @PutMapping("/auditdoclist/{id}/audit/{audit}")
    @WebLog("审核文档接口")
    @RequiresRoles(value = {Constant.ROLE_SUPER_ADMIN, Constant.ROLE_ADMIN}, logical = Logical.OR)
    public ResponseEntity<ResultBean> updateAudit(@PathVariable Long id, @PathVariable Integer audit) {
        getDocument(id);
        documentService.updateDocAuditById(id, resolveFlag(audit, "审核状态"));
        return ResponseEntity.ok(ResultBean.ok(ResultConst.UPD_DOCUMENTS_AUDIT_SUCC));
    }

    private JSONObject toResponse(IPage<Document> page) {
        JSONObject resp = new JSONObject().fluentPut("total", page.getTotal());
        resp.put("docs", page.getRecords());
        return resp;
    }

    private Document getDocument(Long id) {
        Document document = documentService.getById(id);
        if (document == null || "1".equals(document.getIsdelete())) {
            throw new BussinessException(HttpStatus.NOT_FOUND.value(), "文档不存在");
        }
        return document;
    }

    private Integer resolveFlag(Integer value, String name) {
        if (value == null || (value != 0 && value != 1)) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), name + "不合法");
        }
        return value;
    }

    private User currentUser(HttpServletRequest request) {
        return tokenService.getUser(resolveToken(request));
    }

    /**
     * 兼容 header 与 query 参数两种传 token 的方式
     */
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token)) {
            token = request.getParameter("token");
        }
        return token;
    }

    private Path storagePath() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }
}
