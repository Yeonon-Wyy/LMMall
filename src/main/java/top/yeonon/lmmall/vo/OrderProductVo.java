package top.yeonon.lmmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/10 0010 12:23
 **/
@Getter
@Setter
public class OrderProductVo implements Serializable {
    private List<OrderItemVo> orderItemVoList;
    private BigDecimal totalPrice;
    private String imageHost;
}
