package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceUserImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public void deductBalanceById(Long id, int amount) {
        //1 判断用户存在
        User user = this.getById(id);
        if (user == null || user.getStatus()== UserStatus.FROZEN){
            throw new RuntimeException("用户有问题");
        }
        //2 获取用户余额是否充足,当前用户余额是否大于等于要扣的金额
        if (user.getBalance() < amount){
            throw new RuntimeException("余额不足");
        }
        //3 扣减金额
        //获取扣减之后的余额
        int remainBalance = user.getBalance() - amount;
//        userMapper.deductBalanceById(amount,id);
//        update user set balance = balance - ?[,status=2] where id = ?
        this.lambdaUpdate()
                .set(User::getBalance,remainBalance)//设置余额
                //当余额为0的适合用户的状态修改为2
                .set(remainBalance==0,User::getStatus,2)
                .eq(User::getId,id)
                .update();
    }

    @Override
    public UserVO queryUserAndAddressById(Long id) {
        //查询用户信息
        User user = getById(id);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

        //更具用户id查询其对应的用户地址列表，然后转换为vo列表 设置回user Vo里面
        List<Address> addressList = Db.lambdaQuery(Address.class)
                .eq(Address::getUserId,id)
                .list();
        List<AddressVO> addressVOS = BeanUtil.copyToList(addressList, AddressVO.class);
        userVO.setAddresses(addressVOS);

        return userVO;
    }

    @Override
    public List<UserVO> queryUserAndAddressByIds(List<Long> ids) {
        //根据id集合查询用户信息
        List<User> userList = listByIds(ids);
        List<UserVO> userVOSList = BeanUtil.copyToList(userList, UserVO.class);

        //根据用户id集合查询这些用户对于的地址列表  ---->如果一个用户有两个地址的话会有两条记录在这个集合中
        List<Address>addressList=Db.lambdaQuery(Address.class)
                .in(Address::getUserId,ids)
                .list();

        //希望可以将上面的列表转换为：map<用户id,用户地址列表>就可以循环用户列表；

        List<AddressVO> addressVOS = BeanUtil.copyToList(addressList, AddressVO.class);
        Map<Long, List<AddressVO>> userAddressMap = addressVOS.stream().collect(Collectors.groupingBy(AddressVO::getUserId));

        // 然后根据用户id从map中获取用户地址列表
        for (UserVO userVO : userVOSList) {
            List<AddressVO> addressVOList = userAddressMap.get(userVO.getId());
            userVO.setAddresses(addressVOList);
        }
        return userVOSList;
    }
}
