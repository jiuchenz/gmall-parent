package com.atguigu.gmall.product.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.CategoryVo;
import com.atguigu.gmall.product.biz.CategoryBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rpc/inner/product")
public class CategoryRpcController {

    @Autowired
    CategoryBizService categoryBizService;

    @GetMapping("/category/all")
    public Result<List<CategoryVo>> getCategorys(){

        List<CategoryVo> list = categoryBizService.getCategorys();
        return Result.ok(list);
    }
}
