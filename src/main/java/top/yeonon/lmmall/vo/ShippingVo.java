package top.yeonon.lmmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author yeonon
 * @date 2018/4/9 0009 20:49
 **/
@Getter
@Setter
public class ShippingVo implements Serializable {

    private String receiverName;

    private String receiverPhone;

    private String receiverMobile;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private String receiverZip;
}
