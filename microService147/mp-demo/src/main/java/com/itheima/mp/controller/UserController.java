package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("用户接口管理")
@RestController
@RequestMapping("/users")
//@RequiredArgsConstructor 构造函数注入  等同于   @Autowired private IUserService userService
@RequiredArgsConstructor
public class UserController {


//    @Autowired
//    private IUserService userService

    private final IUserService userService;

    @ApiOperation("新增用户")
    @PostMapping
    //@RequestBody 接收来自前端的json数据
    public void saveUser(@RequestBody UserFormDTO userFormDTO){
        //转化为user
        User user = BeanUtil.copyProperties(userFormDTO, User.class);
        userService.save(user);
    }

    @ApiOperation("删除用户")
    @DeleteMapping ("/{id}")
    public void deleteUser(@PathVariable("id")Long id){
        userService.removeById(id);
    }

    @ApiOperation("根据id查询用户")
    @GetMapping("/{id}")
    public UserVO queryUserById(@PathVariable("id")Long id){
/*        User user = userService.getById(id);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);*/
        return userService.queryUserAndAddressById(id);
    }

    @ApiOperation("根据id批量查询用户")
    @GetMapping
    public List<UserVO> queryByIds(@RequestParam("ids") List<Long> ids){
    /*    List<User> userList = userService.listByIds(ids);
        return BeanUtil.copyToList(userList, UserVO.class);*/
        return userService.queryUserAndAddressByIds(ids);
    }


    /**
     * 根据id扣减余额
     * @param id 用户id
     * @param amount 扣减的金额
     */
    @ApiOperation("根据id扣减余额")
    @PutMapping("/{id}/deduction/{amount}")
    public void updateBalance(@PathVariable("id")Long id,@PathVariable("amount")int amount){
            userService.deductBalanceById(id,amount);
    }


    @ApiOperation("根据条件查询用户列表")
    @PostMapping("/list")
    private List<UserVO> queryList(@RequestBody UserQuery userQuery){
        String username= userQuery.getName();
        Integer status = userQuery.getStatus();
        Integer maxBalance = userQuery.getMaxBalance();
        Integer minBalance = userQuery.getMinBalance();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
/*        if(StrUtil.isNotBlank(username)){
            queryWrapper.lambda().like(User::getUsername,username);
        }*/

  /*       queryWrapper.lambda()
               //成立的条件字段查询的关键字:如果第一个查询的值为true才会设置当前这个字段的where中
                .like(StrUtil.isNotBlank(username),User::getUsername,username)
                .eq(status!=null,User::getStatus,status)
                .ge(minBalance !=null,User::getBalance,minBalance)
                .le(maxBalance !=null,User::getBalance,maxBalance);

        List<User> userList = userService.list(queryWrapper);*/


        List<User> userList = userService.lambdaQuery()
                .like(StrUtil.isNotBlank(username),User::getUsername,username)
                .eq(status!=null,User::getStatus,status)
                .ge(minBalance !=null,User::getBalance,minBalance)
                .le(maxBalance !=null,User::getBalance,maxBalance)
                .list();//最终进行查询

        return BeanUtil.copyToList(userList, UserVO.class);

    }
}
