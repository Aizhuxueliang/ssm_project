package com.example.dao;

import com.example.entity.User;

import java.util.List;

public interface UserDao {
    /**
     * 插入 User
     * @param user user
     * @return 返回码
     */
    public String createUser(User user);

    /**
     * 根据 cardNo 查询用户 cardNo
     * @param cardNo 证件号码
     * @return 返回码
     */
    public String queryUserByCardNo(String cardNo);

    /**
     * 分页查询全部 User
     * @param startRows 起始页
     * @return List<User>
     */
    public List<User> queryUserPage(Integer startRows);

    /**
     * 根据 userName 模糊查询 User
     * @param userName 用户名
     * @param startRows 起始页
     * @return List<User>
     */
    public List<User> retrieveUserByName(String userName, Integer startRows);

    /**
     * 更新 User
     * @param cardNo 证件号码
     * @return 返回码
     */
    public String updateUserByCardNo(String cardNo);

    /**
     * 删除 User
     * @param cardNo 证件号码
     * @return 返回码
     */
    public String deleteUserByCardNo(String cardNo);

    /**
     * 查询用户个数
     * @return 用户个数
     */
    public Integer getRowCount();

}
