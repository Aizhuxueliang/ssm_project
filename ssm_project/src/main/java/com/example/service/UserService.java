package com.example.service;

import java.util.List;

import com.example.pojo.User;

public interface UserService {

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

    /**
     * 根据 userId 删除用户
     * @return 返回码
     */
    Integer deleteUserById(String userId);

    void insert(User user);

    User selectByPrimaryKey(Integer id);

    void updateByPrimaryKeySelective(User user);

    void deleteByPrimaryKey(Integer id);
}