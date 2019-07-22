package com.pinyougou.content.service.impl;
import java.util.List;

import com.pinyougou.entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.pinyougou.content.service.ContentService;


import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);

		//清楚缓存
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//查询修改前的分类id
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		//清楚id
		redisTemplate.boundHashOps("content").delete(categoryId);
		contentMapper.updateByPrimaryKey(content);

		//如果分类id发生了修改  清除修改后分类的id
		if(categoryId.longValue() != content.getCategoryId().longValue()){
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//查询处分类广告id
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			//清除缓存
			redisTemplate.boundHashOps("content").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}



	@Autowired
	private RedisTemplate redisTemplate;

	//根据广告分类id查询广告列表
	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {

		//x现重缓存中获取
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
		if(contentList==null){
			//重数据库中读取数据放在缓存中
			TbContentExample tbContentExample = new TbContentExample();
			Criteria criteria = tbContentExample.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);

			//需要开启状态
			criteria.andStatusEqualTo("1");
			//需要排序
			tbContentExample.setOrderByClause("sort_order");
			//获取广告列表
			contentList = contentMapper.selectByExample(tbContentExample);
			//将获取到的广告列表存在了缓存中
			redisTemplate.boundHashOps("content").put(categoryId,contentList);
		}else {
			System.out.println("重缓存中获取");
		}
		return contentList;
	}
}
