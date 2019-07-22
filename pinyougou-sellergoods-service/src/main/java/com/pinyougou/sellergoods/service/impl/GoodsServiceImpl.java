package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.transaction.annotation.Transactional;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service(timeout = 10000)
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper tbGoodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper sellerMapper;

	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加   商品录入
	 */
	@Override
	public void add(Goods goods) {

		//插入封装中 goods  和 gooddesc 中的东西

		goods.getGoods().setAuditStatus("0"); //设置为申请的状态
		goodsMapper.insert(goods.getGoods());

		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());  //给gooddesc设置id
		tbGoodsDescMapper.insert(goods.getGoodsDesc());  //插入商品扩展数据

		saveItemList(goods); //插入sku数据
	}

	private void setItemValus(Goods goods,TbItem item) {

		item.setGoodsId(goods.getGoods().getId());//商品SPU编号
		item.setSellerId(goods.getGoods().getSellerId());//商家编号
		item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//修改日期

		//品牌名称
		TbBrand brand= brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());

		//分类名称
		TbItemCat itemCat= itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());

		//商家名称
		TbSeller seller= sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());

		//图片地址（取spu的第一个图片）
		List<Map> imageList= JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class) ;
		if(imageList.size()>0){
			item.setImage ( (String)imageList.get(0).get("url"));
		}
	}

	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//更新基本数据
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//更新扩展数据
		tbGoodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//更新sku数据   但是前提的先删除原有的sku
		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(tbItemExample);

		saveItemList(goods);  //从新插入sku数据

	}


	//修饰一个公共类
	private void saveItemList(Goods goods){

		//如果开启规格
		if ("1".equals(goods.getGoods().getIsEnableSpec())) {

			//商品录入 中 规格选项框勾选时 的保存
			//遍历itemList表  goods.getitemList相当于前端页面的表格
			for (TbItem item : goods.getItemList()) {
				//添加标题
				String title = goods.getGoods().getGoodsName();  // spu名称
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);

				setItemValus(goods, item);
				itemMapper.insert(item);
			}
		} else {

			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());
			//商品KPU+规格描述串作为SKU名称
			item.setPrice(goods.getGoods().getPrice());
			//价格
			item.setStatus("1");
			//状态
			item.setIsDefault("1");
			//是否默认
			item.setNum(99999);
			//库存数量
			item.setSpec("{}");
			setItemValus(goods, item);
			itemMapper.insert(item);
		}


	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){

		//商品点击修改时 首先看到的页面
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);

		TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);

		//读取sku列表
		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
		goods.setItemList(tbItems);

		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//商品删除   不可以在根源上直接删除   需进行物理删除
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);//查询出要删除的对象
			tbGoods.setIsDelete("1");  //表示逻辑删除
			//zai再去更新
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}


		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		criteria.andIsDeleteIsNull();//非删除状态   x相当于值查询为空的

		if(goods!=null){
				if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
					//商家这里查询必须的精准查询
					criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}

		}

		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}


	//修改商品审核状态

	@Override
	public void updateStatus(Long[] ids, String status) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}


	}
}
