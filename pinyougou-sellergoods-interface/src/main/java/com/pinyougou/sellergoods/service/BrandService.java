package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    //查询所有
    public List<TbBrand> findAll();
    //分页查询
    PageResult findPage(Integer pageNum,Integer pageSize);
    //新增
    public void add(TbBrand tbBrand);


    //修改
    public void update(TbBrand tbBrand);
    //查一个
    public TbBrand findOne(Long id);




    //删除
    public void delete(Long [] ids);

    //分页条件查询
    PageResult findPage(TbBrand tbBrand, Integer pageNum,Integer pageSize);

    //下拉框
    public List<Map> selectOptionList();

}
