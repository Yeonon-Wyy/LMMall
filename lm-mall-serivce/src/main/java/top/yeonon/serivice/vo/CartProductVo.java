package top.yeonon.serivice.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author yeonon
 * @date 2018/4/7 0007 18:42
 **/
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartProductVo implements Serializable {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal ProductPrice;
    private Integer productStatus;
    private BigDecimal ProductTotalPrice;
    private Integer productStock;
    private Integer productChecked;

    private String limitQuantity;       //限制数量的返回结果
}
