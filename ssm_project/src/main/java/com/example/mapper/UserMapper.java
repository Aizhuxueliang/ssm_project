package com.example.mapper;

import java.util.List;

import com.example.pojo.User;


public interface UserMapper {

    /**
     * 分页查询User
     * @param startRows 起始页
     * @return List<User>
     */
    List<User> queryUserPage(Integer startRows);

    /**
     * 查询User个数
     * @return
     */
    Integer getRowCount();

    User getById(int id);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}