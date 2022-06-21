package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;
    @Autowired
    BaseAttrValueService baseAttrValueService;
    /**
     * 根据id获取平台属性
     * @param c1Id
     * @param c2Id
     * @param c3Id
     * @return
     */
    @GetMapping("/attrInfoList/{c1id}/{c2id}/{c3id}")
    public Result attrInfoList(@PathVariable("c1id") Long c1Id,
                               @PathVariable("c2id") Long c2Id,
                               @PathVariable("c3id") Long c3Id){
        List<BaseAttrInfo> list = baseAttrInfoService.getArrInfoList(c1Id,c2Id,c3Id);

        return Result.ok(list);
    }

    /**
     * 保存属性/修改属性
     * @return
     */
    @Transactional
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        Long id = baseAttrInfo.getId();
        if (id==null){
            baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        }else {
            baseAttrInfoService.updateAttrInfo(baseAttrInfo);
        }
        return Result.ok();
    }

    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId){
        List<BaseAttrValue> list = baseAttrValueService.getAttrValueList(attrId);
        return Result.ok(list);
    }
}
