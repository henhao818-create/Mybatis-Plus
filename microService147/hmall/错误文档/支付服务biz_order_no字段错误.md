# 支付服务 biz_order_no 字段错误

## 错误信息

```
java.sql.SQLException: Field 'biz_order_no' doesn't have a default value
```

## 错误时间

2026-05-25 17:25:10

## 错误位置

- 文件：`pay-service/src/main/java/com/hmall/pay/service/impl/PayOrderServiceImpl.java`
- 方法：`checkIdempotent()` 第87行
- 调用链：`PayController.applyPayOrder()` -> `PayOrderServiceImpl.applyPayOrder()` -> `checkIdempotent()`

## 错误原因

### 直接原因

插入`pay_order`表时，SQL语句中没有包含`biz_order_no`字段：

```sql
INSERT INTO pay_order (id, pay_order_no, status, pay_over_time) VALUES (?, ?, ?, ?)
```

数据库表`pay_order`的`biz_order_no`字段设置了`NOT NULL`约束且没有默认值，导致插入失败。

### 根本原因

`PayApplyDTO`类使用了Lombok的`@Builder`注解，但**缺少**`@NoArgsConstructor`和`@AllArgsConstructor`注解：

```java
// 错误的写法
@Data
@Builder
public class PayApplyDTO {
    private Long bizOrderNo;
    // ...
}
```

**问题分析：**
1. `@Builder`注解会生成一个全参构造函数和Builder模式代码
2. 但是Spring MVC在反序列化JSON请求体时，需要无参构造函数来创建对象实例
3. 缺少无参构造函数导致Spring MVC无法正确创建`PayApplyDTO`对象
4. 最终导致`bizOrderNo`字段为null
5. `BeanUtils.toBean()`转换时，null值不会被设置到`PayOrder`对象中
6. MyBatis-Plus插入时，null字段不会出现在SQL语句中

## 解决方案

在`PayApplyDTO.java`中添加`@NoArgsConstructor`和`@AllArgsConstructor`注解：

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "支付下单表单实体")
public class PayApplyDTO {
    // ...
}
```

## 相关代码

### PayApplyDTO.java（修复后）

```java
package com.hmall.pay.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "支付下单表单实体")
public class PayApplyDTO {
    @ApiModelProperty("业务订单id不能为空")
    @NotNull(message = "业务订单id不能为空")
    private Long bizOrderNo;

    @ApiModelProperty("支付金额必须为正数")
    @Min(value = 1, message = "支付金额必须为正数")
    private Integer amount;

    @ApiModelProperty("支付渠道编码不能为空")
    @NotNull(message = "支付渠道编码不能为空")
    private String payChannelCode;

    @ApiModelProperty("支付方式不能为空")
    @NotNull(message = "支付方式不能为空")
    private Integer payType;

    @ApiModelProperty("订单中的商品信息不能为空")
    @NotNull(message = "订单中的商品信息不能为空")
    private String orderInfo;
}
```

### PayOrderServiceImpl.java（相关方法）

```java
private PayOrder buildPayOrder(PayApplyDTO payApplyDTO) {
    // 1.数据转换
    PayOrder payOrder = BeanUtils.toBean(payApplyDTO, PayOrder.class);
    // 2.初始化数据
    payOrder.setPayOverTime(LocalDateTime.now().plusMinutes(120L));
    payOrder.setStatus(PayStatus.WAIT_BUYER_PAY.getValue());
    payOrder.setBizUserId(UserContext.getUser());
    return payOrder;
}
```

## 预防措施

1. **使用@Builder时必须配合@NoArgsConstructor和@AllArgsConstructor**
   - 当类需要被Spring MVC、Jackson等框架反序列化时，必须有无参构造函数
   - 推荐写法：`@Data @Builder @NoArgsConstructor @AllArgsConstructor`

2. **检查数据库表字段约束**
   - 确保NOT NULL字段都有对应的值
   - 或者在数据库中设置合理的默认值

3. **添加参数校验**
   - 在Controller层使用`@Valid`注解验证请求参数
   - 确保必填字段不为null

## 验证方法

修复后重新启动服务，调用支付下单接口，检查：
1. 日志中SQL语句应包含`biz_order_no`字段
2. 数据库`pay_order`表中应正确插入记录
3. 不再出现`Field 'biz_order_no' doesn't have a default value`错误

## 相关文档

- [Lombok @Builder注解官方文档](https://projectlombok.org/features/Builder)
- [Spring MVC JSON反序列化机制](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestbody.html)
