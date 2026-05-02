package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itheima.mp.domain.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class WrapperTest{
    @Autowired
    private UserMapper userMapper;
//    查询出名字中带o的，存款大于等于10oo元的人（id，username，info，balance）
//    select id,username,info,balance from user where username='%o%' and balance >= l000;
    @Test
    public void testQueryWrapper1(){
        // 创建条件构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //查询的列
        queryWrapper.select("id","username","info","balance");
        //查询条件 ：名字带o的
        queryWrapper.like("username","o");
        //查询条件 ：存款大于等于1000元
        queryWrapper.ge("balance",1000);

        //查询
        List<User> userList = userMapper.selectList(queryWrapper);
        //输出
        for (User user : userList) {
            System.out.println(user);
        }
    }


    //更新用户名为jack的用户的余额为2000
    @Test
    public void testQueryWrapperUpdate(){
        User user =new User();
        user.setBalance(2000);

        QueryWrapper<User> querywrapper = new QueryWrapper<>();
        querywrapper.eq("username","jack");
        userMapper.update(user,querywrapper);
    }


    //更新id为1，2，4的用户余额扣两百   update user set balance = balance - 200 where id in (1,2,4);
    @Test
    public void testQueryWrapperUpdate2(){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        //自定义更新语句 设置的是set语句 balance = balance - 200
        updateWrapper.setSql("balance=balance-200");
        //1，2，4的用户
        updateWrapper.in("id",1,2,4);
        userMapper.update(null,updateWrapper);
    }




    @Test
    public void testLambdaQueryWrapper1(){
        // 创建条件构造器
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询的列
        lambdaQueryWrapper.select(User::getId,User::getUsername,User::getInfo,User::getBalance);
        //查询条件 ：名字带o的
        lambdaQueryWrapper.like(User::getBalance,"o");
        //查询条件 ：存款大于等于1000元
        lambdaQueryWrapper.ge(User::getBalance,1000);
        //查询
        List<User> userList = userMapper.selectList(lambdaQueryWrapper);
        //输出
        for (User user : userList) {
            System.out.println(user);
        }
    }


    //自定义sql 更新id为1，2，4的用户余额扣两百
    @Test
    public void testCustomSqlSeqment(){
        //构造条件
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(User::getId,1L,2L,4L);

        //调用mapper方法实现功能
        userMapper.updateBalanceByWrapper(200,lambdaQueryWrapper);

    }

}




