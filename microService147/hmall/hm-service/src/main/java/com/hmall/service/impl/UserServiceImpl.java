package com.hmall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.exception.BadRequestException;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.exception.ForbiddenException;
import com.hmall.common.utils.UserContext;
import com.hmall.config.JwtProperties;
import com.hmall.domain.dto.LoginFormDTO;
import com.hmall.domain.po.User;
import com.hmall.domain.vo.UserLoginVO;
import com.hmall.enums.UserStatus;
import com.hmall.mapper.UserMapper;
import com.hmall.service.IUserService;
import com.hmall.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        //TODO 校验用户名和密码; 用户名、密码不对登录失败；状态为冻结不能登录；验证成功后生成token
        //校验用户名是否存在
        User user = lambdaQuery().eq(User::getUsername, loginDTO.getUsername()).one();
        if (user == null){
            throw new ForbiddenException("用户不存在");
        }
//        用户的状态是否被冻结，冻结的不能登录
        if (user.getStatus()==UserStatus.FROZEN){
            throw new ForbiddenException("用户状态异常");
        }
//        校验用户的密码是否正确
        if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())){
            throw new BadRequestException("用户密码错误");
        }
//        都正确的清况下；根据用户id生成 token令牌
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        // 返回userLoginVo并设置其对应的信息
        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setUsername(user.getUsername());
        userLoginVO.setUserId(user.getId());
        userLoginVO.setBalance(user.getBalance());
        userLoginVO.setToken(token);

        return userLoginVO;
    }

    @Override
    public void deductMoney(String pw, Integer totalFee) {
        log.info("开始扣款");
        // 1.校验密码
        User user = getById(UserContext.getUser());
        if(user == null || !passwordEncoder.matches(pw, user.getPassword())){
            // 密码错误
            throw new BizIllegalException("用户密码错误");
        }

        // 2.尝试扣款
        try {
            baseMapper.updateMoney(UserContext.getUser(), totalFee);
        } catch (Exception e) {
            throw new RuntimeException("扣款失败，可能是余额不足！", e);
        }
        log.info("扣款成功");
    }
}
