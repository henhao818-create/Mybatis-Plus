package com.hmall.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "新增购物车商品表单实体")
public class CartFormDTO {
    @ApiModelProperty("商品id")
    @NotNull(message = "商品id不能为空")
    private Long itemId;
    @ApiModelProperty("商品标题")
    @NotBlank(message = "商品标题不能为空")
    private String name;
    @ApiModelProperty("商品动态属性键值集")
    private String spec;
    @ApiModelProperty("价格,单位：分")
    @NotNull(message = "价格不能为空")
    @Min(value = 1, message = "价格必须大于0")
    private Integer price;
    @ApiModelProperty("商品图片")
    private String image;
}
