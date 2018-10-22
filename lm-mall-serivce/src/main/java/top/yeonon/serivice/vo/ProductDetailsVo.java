package top.yeonon.serivice.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author yeonon
 * @date 2018/4/6 0006 15:14
 **/
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDetailsVo implements Serializable {

    private Integer productId;
    private Integer categoryId;
    private String name;
    private String subTitle;
    private String mainImage;
    private String subImage;
    private String details;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    private Integer parentCategoryId;
    private String imageHost;

}
