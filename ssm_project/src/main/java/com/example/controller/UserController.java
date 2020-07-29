package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.example.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.service.UserService;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/queryUserPage")
    @ResponseBody
    public List<User> queryUserPage(Integer page) {
        int pageNow = page == null ? 1 : page;
        int pageSize = 5;
        int startRows = pageSize*(pageNow-1);
        return userService.queryUserPage(startRows);
    }

    @RequestMapping("/selectUserPage")
    @ResponseBody
    public List<User> selectUserPage(String userName, String userSex, Integer page) {
        int pageNow = page == null ? 1 : page;
        int pageSize = 5;
        int startRows = pageSize*(pageNow-1);
        return userService.selectUserPage(userName, userSex, startRows);
    }

    @RequestMapping("/getRowCount")
    @ResponseBody
    public Integer getRowCount() {
        return userService.getRowCount();
    }

    @RequestMapping("/createUser")
    @ResponseBody
    public Integer createUser(User user) {
        Random random = new Random();
        Integer number = random.nextInt(9000) + 1000;
        user.setUserId(System.currentTimeMillis() + String.valueOf(number));
        return userService.createUser(user);
    }

    @RequestMapping("/deleteUserById")
    @ResponseBody
    public Integer deleteUserById(String userId) {
        return userService.deleteUserById(userId);
    }

    @RequestMapping("/updateUserById")
    @ResponseBody
    public Integer updateUserById(User user) {
        return userService.updateUserById(user);
    }


}