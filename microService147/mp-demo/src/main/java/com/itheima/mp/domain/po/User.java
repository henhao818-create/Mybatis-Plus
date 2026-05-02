package com.itheima.mp.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonValue;
import com.itheima.mp.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "user",autoResultMap = true)
public class User {

    /**
     * 用户id
     */
    /**
     * IdType.AUTO 根据表中主键的字段目前自增到最大值进行自增
     * IdType.INPUT 手动输入：一般来说自己指定
     * IdType.ASSIGN_ID 雪花算法；生成数值字符串
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 注册手机号
     */
    private String phone;

    /**
     * 详细信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private UserInfo info;

    /**
     * 使用状态（1正常 2冻结）
     */
    private UserStatus status;

    /**
     * 账户余额
     */
    private Integer balance;

    /**
     * 创建时间 表示数据库中的表中字段不存在的字段，那么在实体类中声明的字段就会变成数据库表中不存在的字段，那么在数据库中就会生成一个字段，字段名为数据库表中不存在的字段名，字段值为null
     */
    @TableField(value = "create_time",exist = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
