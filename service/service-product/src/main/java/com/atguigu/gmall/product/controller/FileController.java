package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/admin/product")
@RestController
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestPart("file") MultipartFile file) throws Exception {
        String url = fileService.fileUpload(file);
        return Result.ok(url);
    }


}
