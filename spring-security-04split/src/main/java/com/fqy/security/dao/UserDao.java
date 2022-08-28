package com.fqy.security.dao;

import com.fqy.security.entity.Role;
import com.fqy.security.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {


    //根据用户名返回用户方法
    User loadUserByUsername(String username);

    //根据用户  id  查询角色信息方法
    List<Role> getRolesByUid(Integer id);
}
