package com.hmall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.exception.BadRequestException;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CollUtils;
import com.hmall.common.utils.UserContext;
import com.hmall.domain.dto.CartFormDTO;
import com.hmall.domain.dto.ItemDTO;
import com.hmall.domain.po.Cart;
import com.hmall.domain.po.Item;
import com.hmall.domain.vo.CartVO;
import com.hmall.mapper.CartMapper;
import com.hmall.service.ICartService;
import com.hmall.service.IItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    private final IItemService itemService;

    @Override
    public void addItem2Cart(CartFormDTO cartFormDTO) {
        //TODO 如果商品已经添加过则购买数量添加即可；如果未添加过则新增一条购物车记录。一个用户最多放置购物车商品为10

        Long userId = UserContext.getUser();
        //商品检查是否已经存在购物车中如果在则数量加一
        if (checkItemExists(cartFormDTO.getItemId(),userId)){
            //update cart set num = num + 1 where item_id = ? and user_id = ?
            lambdaUpdate()
                    .setSql("num = num + 1")
                    .eq(Cart::getItemId, cartFormDTO.getItemId())
                    .eq(Cart::getUserId, userId)
                    .update();
        }else {
            //如果商品不存在则新增一条购物车记录
            Cart cart = BeanUtils.copyBean(cartFormDTO, Cart.class);
            cart.setUserId(userId);
            cart.setNum(1);
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            save(cart);

        }
    }

    //根据商品id用户id查询是否有购物车记录
    private boolean checkItemExists(Long itemId, Long userId) {
        //select  count(*) from cart where item_id = ? and user_id = ?
        Long count = lambdaQuery().eq(Cart::getItemId, itemId)
                .eq(Cart::getUserId, userId)
                .count();
        return count > 0;
    }

    @Override
    public List<CartVO> queryMyCarts() {
       //TODO 查询当前登录用户的购物车列表；需要将Cart转换为CartVO；且CartVO中需要包含商品的最新价格、状态、库存等信息。
        //1.查询当前登录用户的购物车列表
        List<Cart> cartList = lambdaQuery().eq(Cart::getUserId, UserContext.getUser()).list();
        if (CollUtils.isNotEmpty(cartList)){

            List<CartVO> cartVOList = BeanUtils.copyList(cartList, CartVO.class);

            //2.查询商品最新价格、状态、库存等信息
//            select * from item where id in (？，？，？)
//            1.1将上述购物车中所有商品id收集到一个集合中
            Set<Long> idsSet = cartVOList.stream().map(CartVO::getItemId).collect(Collectors.toSet());
            //1.2根据商品id查询商品最新价格、状态、库存等信息
            List<Item> itemList = itemService.lambdaQuery().in(Item::getId, idsSet).list();
            //1.3将商品列表 list  ---->map<商品id，商品>
            Map<Long, Item> itemMap = itemList.stream().collect(Collectors.toMap(Item::getId, Function.identity()));
            //1.4将每个购物车中的商品信息从map中获取并更新

            for (CartVO cartVO : cartVOList) {
                Item item = itemMap.get(cartVO.getItemId());
                cartVO.setNewPrice(item.getPrice());
                cartVO.setStatus(item.getStatus());
                cartVO.setStock(item.getStock());
            }
            //返回
            return cartVOList;
        }
        //返回
        return CollUtils.emptyList();
    }

    @Override
    public void removeByItemIds(Collection<Long> itemIds) {
        // 1.构建删除条件，userId和itemId
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>();
        queryWrapper.lambda()
                .eq(Cart::getUserId, UserContext.getUser())
                .in(Cart::getItemId, itemIds);
        // 2.删除
        remove(queryWrapper);
    }
}
