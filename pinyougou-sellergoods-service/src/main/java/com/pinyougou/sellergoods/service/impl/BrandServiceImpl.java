package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    //修改
    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    //查一个
    @Override
    public TbBrand findOne(Long id) {
        TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(id);
        return tbBrand;
    }

    //条件查询
    @Override
    public PageResult findPage(TbBrand tbBrand, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        //example查询需要的参数
        TbBrandExample example = new TbBrandExample();

        TbBrandExample.Criteria criteria = example.createCriteria();
        //判断两个条件是否满足
        if (tbBrand!=null){
            if(tbBrand.getName()!=null&&tbBrand.getName().length()>0){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if (tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
            }
        }

        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(example);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());

        return pageResult;
    }

    //删除复选
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }
    }

    //新增
    @Override
    public void add(TbBrand tbBrand) {
            tbBrandMapper.insert(tbBrand);

    }

    //分页
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);

        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    //查询全部
    @Override
    public List<TbBrand> findAll() {
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(null);

        return tbBrands;
    }

    @Override
    public List<Map> selectOptionList() {
        return tbBrandMapper.selectOptionList();
    }
}
