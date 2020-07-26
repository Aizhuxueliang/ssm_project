package com.example.mapper;

import java.util.List;

import com.example.pojo.User;


public interface UserMapper {

    /**
     * 分页查询 User
     * @param startRows 起始页
     * @return List<User>
     */
    List<User> queryUserPage(Integer startRows);

    /**
     * 查询 User 个数
     * @return 返回码
     */
    Integer getRowCount();

    /**
     * 添加 User
     * @param user
     * @return 返回码
     */
    Integer createUser(User user);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}