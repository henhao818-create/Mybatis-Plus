package com.itheima.mp.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private IUserService userService;

    //批量一次插入1000条 数据：总共十万

    @Test
    public void testBatch(){
        //记录开始时间
        long start = System.currentTimeMillis();
        List<User> list = new ArrayList<>(1000);
        for (int i = 0; i < 100000; i++) {
            list.add(bulidUser(i));
            if (i%1000 == 0){
                userService.saveBatch(list);
                //每次保存完以后清空集合
                list.clear();
            }
        }
        //记录结束时间
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start));

    }

    @Test
    public void testOneByOne() {
        //记录开始时间
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            userService.save(bulidUser (i));
        }
        //记录结束时间
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start));

    }

    private User bulidUser(int i) {
        User user = new User();
//        user.setId(5L);
        user.setUsername("user"+i);
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
//        user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }

    //测试分页
    @Test
    public void testPage(){
        //创建一个分页对象
        Page page = new Page<>(2, 2);
        //分页查询 select  * from user where 1=1 limit 2,2
        //limit 起始的索引号=，页大小
        Page userpage = userService.page(page);
        //输出分页信息
        System.out.println("总页数"+userpage.getPages());
        System.out.println("总记录数"+userpage.getTotal());
        for (Object user : userpage.getRecords()) {
            System.out.println(user);

        }
    }
}
