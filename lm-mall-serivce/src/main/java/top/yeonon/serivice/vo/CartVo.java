package top.yeonon.serivice.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/7 0007 18:43
 **/
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartVo implements Serializable {
    private List<CartProductVo> cartProductVoList;
    private BigDecimal cartTotalPrice;
    private boolean allChecked;             //是否都勾选
    private String imageHost;
}
