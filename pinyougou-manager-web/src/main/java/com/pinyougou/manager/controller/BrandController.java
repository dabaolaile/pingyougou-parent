package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {


    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }


    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer size) {

        PageResult pageResult = brandService.findPage(page, size);
        return pageResult;
    }

    @RequestMapping("add")
    public Result add(@RequestBody TbBrand tbBrand) {

        try {
            brandService.add(tbBrand);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }
    }

    @RequestMapping("/findOne")
    public TbBrand findOne(Long id) {

        TbBrand one = brandService.findOne(id);
        return one;
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand) {

        try {
            brandService.update(tbBrand);
            return new Result(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long [] ids) {

        try {
            brandService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand tbBrand,Integer page, Integer size){

        PageResult page1 = brandService.findPage(tbBrand, page, size);
        return page1;
    }

    //下拉框
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }

}
