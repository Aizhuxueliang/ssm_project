package com.example.dao;

import com.example.entity.User;

import java.util.List;

public interface UserDao {
    /**
     * 插入 User
     * @param user user
     * @return 返回码
     */
    public Integer createUser(User user);

    /**
     * 根据 userName 查询 User
     * @param userName 用户名
     * @param startRows 起始页
     * @return List<User>
     */
    public List<User> retrieveUser(String userName, Integer startRows);

    /**
     * 更新 User
     * @param userName 用户名
     * @return 返回码
     */
    public Integer updateUser(String userName);

    /**
     * 删除 User
     * @param userName 用户名
     * @return 返回码
     */
    public Integer deleteUser(String userName);

    /**
     * 查询全部 User
     * @param startRows 起始页
     * @return List<User>
     */
    public List<User> queryPage(Integer startRows);

    /**
     * 查询用户个数
     * @return 用户个数
     */
    public Integer getRowCount();

}
