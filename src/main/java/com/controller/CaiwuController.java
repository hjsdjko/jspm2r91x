package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.CaiwuEntity;
import com.entity.view.CaiwuView;

import com.service.CaiwuService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;
import java.io.IOException;

/**
 * 财务
 * 后端接口
 * @author 
 * @email 
 * @date 2022-03-31 19:53:19
 */
@RestController
@RequestMapping("/caiwu")
public class CaiwuController {
    @Autowired
    private CaiwuService caiwuService;



    
	@Autowired
	private TokenService tokenService;
	
	/**
	 * 登录
	 */
	@IgnoreAuth
	@RequestMapping(value = "/login")
	public R login(String username, String password, String captcha, HttpServletRequest request) {
		CaiwuEntity user = caiwuService.selectOne(new EntityWrapper<CaiwuEntity>().eq("caiwuzhanghao", username));
		if(user==null || !user.getMima().equals(password)) {
			return R.error("账号或密码不正确");
		}
		String token = tokenService.generateToken(user.getId(), username,"caiwu",  "财务" );
		return R.ok().put("token", token);
	}
	
	/**
     * 注册
     */
	@IgnoreAuth
    @RequestMapping("/register")
    public R register(@RequestBody CaiwuEntity caiwu){
    	//ValidatorUtils.validateEntity(caiwu);
    	CaiwuEntity user = caiwuService.selectOne(new EntityWrapper<CaiwuEntity>().eq("caiwuzhanghao", caiwu.getCaiwuzhanghao()));
		if(user!=null) {
			return R.error("注册用户已存在");
		}
		Long uId = new Date().getTime();
		caiwu.setId(uId);
        caiwuService.insert(caiwu);
        return R.ok();
    }

	
	/**
	 * 退出
	 */
	@RequestMapping("/logout")
	public R logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return R.ok("退出成功");
	}
	
	/**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request){
    	Long id = (Long)request.getSession().getAttribute("userId");
        CaiwuEntity user = caiwuService.selectById(id);
        return R.ok().put("data", user);
    }
    
    /**
     * 密码重置
     */
    @IgnoreAuth
	@RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request){
    	CaiwuEntity user = caiwuService.selectOne(new EntityWrapper<CaiwuEntity>().eq("caiwuzhanghao", username));
    	if(user==null) {
    		return R.error("账号不存在");
    	}
        user.setMima("123456");
        caiwuService.updateById(user);
        return R.ok("密码已重置为：123456");
    }


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,CaiwuEntity caiwu, 
		HttpServletRequest request){

        EntityWrapper<CaiwuEntity> ew = new EntityWrapper<CaiwuEntity>();
    	PageUtils page = caiwuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, caiwu), params), params));
		request.setAttribute("data", page);
        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,CaiwuEntity caiwu, 
		HttpServletRequest request){
        EntityWrapper<CaiwuEntity> ew = new EntityWrapper<CaiwuEntity>();
    	PageUtils page = caiwuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, caiwu), params), params));
		request.setAttribute("data", page);
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( CaiwuEntity caiwu){
       	EntityWrapper<CaiwuEntity> ew = new EntityWrapper<CaiwuEntity>();
      	ew.allEq(MPUtil.allEQMapPre( caiwu, "caiwu")); 
        return R.ok().put("data", caiwuService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(CaiwuEntity caiwu){
        EntityWrapper< CaiwuEntity> ew = new EntityWrapper< CaiwuEntity>();
 		ew.allEq(MPUtil.allEQMapPre( caiwu, "caiwu")); 
		CaiwuView caiwuView =  caiwuService.selectView(ew);
		return R.ok("查询财务成功").put("data", caiwuView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        CaiwuEntity caiwu = caiwuService.selectById(id);
        return R.ok().put("data", caiwu);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        CaiwuEntity caiwu = caiwuService.selectById(id);
        return R.ok().put("data", caiwu);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CaiwuEntity caiwu, HttpServletRequest request){
    	caiwu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(caiwu);
    	CaiwuEntity user = caiwuService.selectOne(new EntityWrapper<CaiwuEntity>().eq("caiwuzhanghao", caiwu.getCaiwuzhanghao()));
		if(user!=null) {
			return R.error("用户已存在");
		}

		caiwu.setId(new Date().getTime());
        caiwuService.insert(caiwu);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody CaiwuEntity caiwu, HttpServletRequest request){
    	caiwu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(caiwu);
    	CaiwuEntity user = caiwuService.selectOne(new EntityWrapper<CaiwuEntity>().eq("caiwuzhanghao", caiwu.getCaiwuzhanghao()));
		if(user!=null) {
			return R.error("用户已存在");
		}

		caiwu.setId(new Date().getTime());
        caiwuService.insert(caiwu);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CaiwuEntity caiwu, HttpServletRequest request){
        //ValidatorUtils.validateEntity(caiwu);
        caiwuService.updateById(caiwu);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        caiwuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 提醒接口
     */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		map.put("column", columnName);
		map.put("type", type);
		
		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date()); 
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}
		
		Wrapper<CaiwuEntity> wrapper = new EntityWrapper<CaiwuEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = caiwuService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	
	





}
