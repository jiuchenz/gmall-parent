package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueService baseAttrValueService;

    @Override
    public List<BaseAttrInfo> getArrInfoList(Long c1Id, Long c2Id, Long c3Id) {

        List<BaseAttrInfo> list = baseAttrInfoMapper.getArrInfoList(c1Id,c2Id,c3Id);
        return list;
    }

    /**
     * 新增属性值
     * @param baseAttrInfo
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //属性名保存
        baseAttrInfoMapper.insert(baseAttrInfo);
        //属性值保存
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(baseAttrInfo.getId());
        }
        baseAttrValueService.saveBatch(attrValueList);
    }

    @Override
    public void updateAttrInfo(BaseAttrInfo baseAttrInfo) {
        //修改属性名
        baseAttrInfoMapper.updateById(baseAttrInfo);
        //修改属性值
        List<Long> ids = new ArrayList<>();
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            //判断是否存在
            if (baseAttrValue.getId()==null){
                //没id说明新增
                //回填属性id
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueService.save(baseAttrValue);
            }
            if (baseAttrValue.getId()!=null){
                //说明存在，修改
                baseAttrValueService.updateById(baseAttrValue);
                //将id存到id集合总
                ids.add(baseAttrValue.getId());
            }
        }
        //删除属性值
        //59 60 61
        //60 61
        if (ids.size() == 0){
            //没有任何id,全部删除
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper();
            wrapper.eq("attr_id",baseAttrInfo.getId());
            baseAttrValueService.remove(wrapper);
        }else {
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id",baseAttrInfo.getId());
            wrapper.notIn("id",ids);//取出所有不在集合总的id，删除id
            baseAttrValueService.remove(wrapper);
        }

    }
}




