package com.pinyougou.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

public class UserDetailsServiceImpl implements UserDetailsService {

    /*认证类调用服务方法*/
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /*认证类*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //System.out.println("woaini=============================");
        //构建角色列表
        ArrayList<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        TbSeller seller = sellerService.findOne(username);
        if(seller!=null){
            if(seller.getStatus().equals("1")){
                //返回的是用户对象
                return new User(username,seller.getPassword(),grantedAuths);
            }else{
               return null;
            }

        }else {
            return null;
        }
    }
}
