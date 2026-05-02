package com.itheima.mp.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DbTest {

    //根据id查询用户:
    @Test
    public void testGetById(){
        User user = Db.getById(1L, User.class);
        System.out.println(user);
    }
    //查询名字中包含0且余额大于1000的用户
    @Test
    public void testQueryByNameAndBalance(){

        List<User> userList = Db.lambdaQuery(User.class)
                .like(User::getUsername,"o")
                .ge(User::getBalance,1000)
                .list();
        for (User user : userList) {
            System.out.println(user);
        }
    }
    //更新用户名为Rose的余额为2000
    @Test
    public void testUpdate() {
        boolean success = Db.lambdaUpdate(User.class)
                .eq(User::getUsername, "Rose")
                .set(User::getBalance, 2000)
                .update();

        System.out.println("更新结果: " + success);
    }
}
