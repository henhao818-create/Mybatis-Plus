package com.hmall.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.common.domain.PageDTO;
import com.hmall.domain.dto.ItemDTO;
import com.hmall.domain.po.Item;
import com.hmall.domain.query.ItemPageQuery;
import com.hmall.service.IItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "搜索相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final IItemService itemService;

    @ApiOperation("搜索商品")
    @GetMapping("/list")
    public PageDTO<ItemDTO> search(ItemPageQuery query) {
        // 1. 构建查询条件
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<Item>()
                .like(StrUtil.isNotBlank(query.getKey()), Item::getName, query.getKey())
                .eq(StrUtil.isNotBlank(query.getBrand()), Item::getBrand, query.getBrand())
                .eq(StrUtil.isNotBlank(query.getCategory()), Item::getCategory, query.getCategory())
                .eq(StrUtil.isNotBlank(query.getBrand()), Item::getBrand, query.getBrand())
                .ge(query.getMinPrice() != null, Item::getPrice, query.getMinPrice())
                .le(query.getMaxPrice() != null && query.getMaxPrice() > 0, Item::getPrice, query.getMaxPrice())
                .eq(Item::getStatus, 1); // 只查上架商品

        // 2. 处理排序（带安全校验 + 默认值）
        String sortBy = query.getSortBy();
        Boolean isAsc = query.getIsAsc() != null ? query.getIsAsc() : false; // 默认降序

        if (StrUtil.isNotBlank(sortBy)) {
            switch (sortBy) {
                case "price":
                    wrapper.orderBy(true, isAsc, Item::getPrice);
                    break;
                case "sold":
                    wrapper.orderBy(true, isAsc, Item::getSold);
                    break;
                default:
                    // 非法排序字段 → 走默认排序
                    wrapper.orderByDesc(Item::getUpdateTime);
            }
        } else {
            // 无排序 → 默认按更新时间倒序
            wrapper.orderByDesc(Item::getUpdateTime);
        }

        // 3. 分页查询
        Page<Item> page = itemService.page(new Page<>(query.getPageNo(), query.getPageSize()), wrapper);

        // 4. 转换并返回
        return PageDTO.of(page, ItemDTO.class);
    }
}