package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.springframework.transaction.annotation.Transactional;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//获取规格名称
		TbSpecification tbspecification= specification.getSpecification();
		specificationMapper.insert(tbspecification);

		//获取规格选项集合
		List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptionList();
		for (TbSpecificationOption option : specificationOptions) {
			//设置规格id
			option.setSpecId(tbspecification.getId());
			specificationOptionMapper.insert(option);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){

		//修改
		/*
		1.当我根据id查询一个规格名称的时候  先保存原有的修改规格
		2.直接先删除规格列表  再去添加
		*/

		specificationMapper.updateByPrimaryKey(specification.getSpecification());

		//删除原有的规格列表
		//根据id为条件删除
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(specification.getSpecification().getId());
		specificationOptionMapper.deleteByExample(example);

		//循环插入规格选项
		List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptionList();
		for (TbSpecificationOption specificationOption : specificationOptions) {
			specificationOption.setSpecId(specification.getSpecification().getId());
			//根据id去添加
			specificationOptionMapper.insert(specificationOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//根据id查出
		//首先查出规格
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);

		//然后在查出规格选项集合
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(id);//根据id查询
		List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);


		//构建组合实体类返回
		Specification specification = new Specification();
		specification.setSpecification(tbSpecification);
		specification.setSpecificationOptionList(tbSpecificationOptions);
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//先删除规格名称
		for (Long id : ids) {
			specificationMapper.deleteByPrimaryKey(id);

			//zai 其次删除规格列表
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andIdEqualTo(id);

			specificationOptionMapper.deleteByExample(example);

		}
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/*下拉框*/
	@Override
	public List<Map> selectOptionList() {
		return specificationMapper.selectOptionList();
	}
}
