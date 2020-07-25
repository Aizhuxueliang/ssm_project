package com.example.controller;

import java.util.List;

import com.example.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @RequestMapping("/getRowCount")
    @ResponseBody
    public Integer getRowCount() {
        return userService.getRowCount();
    }

    @RequestMapping("add")
    public String add(User user) {

        userService.insert(user);
        return "redirect:list";


    }

    @RequestMapping("goToUpdate")
    public String goToUpdate(Integer id, Model model) {

        User user = userService.selectByPrimaryKey(id);
        model.addAttribute("stu", user);
        return "update";

    }

    @RequestMapping("update")
    public String update(User user) {
        userService.updateByPrimaryKeySelective(user);
        return "redirect:list";
    }

    @RequestMapping("del")
    public String del(Integer id){

        userService.deleteByPrimaryKey(id);
        return "redirect:list";
    }

}