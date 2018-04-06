package top.yeonon.lmmall.controller.backend;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.Product;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.service.IProductService;
import top.yeonon.lmmall.service.IUserService;
import top.yeonon.lmmall.vo.ProductDetailsVo;

import javax.servlet.http.HttpSession;

/**
 * @Author yeonon
 * @date 2018/4/6 0006 15:40
 **/
@RestController
@RequestMapping("manage/products")
public class ProductManagerController {

    @Autowired
    private IProductService productService;

    @Autowired
    private IUserService userService;

    /**
     * 获取所有商品，不需要排序和关键字（就是所有，就是那么暴力！）
     */
    @GetMapping
    public ServerResponse<PageInfo> getManageProducts(HttpSession session,
                                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录管理员账号");
        }
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("没有权限");
        }
        return productService.getManageProducts(pageNum, pageSize);
    }

    /**
     * 添加商品
     */
    @PostMapping
    public ServerResponse addProduct(HttpSession session, Product product) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录管理员账号");
        }
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("没有权限");
        }
        return productService.addOrUpdateProduct(product);
    }

    /**
     * 更新商品信息
     */
    @PutMapping("{productId}")
    public ServerResponse updateProduct(HttpSession session, @PathVariable("productId") Integer productId, Product product) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录管理员账号");
        }
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("没有权限");
        }
        if (product != null) {
            product.setId(productId);
        }
        return productService.addOrUpdateProduct(product);
    }

    /**
     * 商品搜索
     */
    @GetMapping("search")
    public ServerResponse<PageInfo> search(HttpSession session,
                                 String productName, Integer productId,
                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录管理员账号");
        }
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("没有权限");
        }
        return productService.search(productName, productId, pageNum, pageSize);
    }

    /**
     * 获取商品详情（未上架的也可以获取到）
     */
    @GetMapping("{productId}")
    public ServerResponse<ProductDetailsVo> getManageProductDetails(HttpSession session, @PathVariable("productId") Integer productId) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录管理员账号");
        }
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("没有权限");
        }

        return productService.getManageProductDetails(productId);
    }

    /**
     * 商品上下架
     */
    @PutMapping("{productId}/status")
    public ServerResponse updateProductStatus(HttpSession session, @PathVariable("productId") Integer productId, Integer status) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录管理员账号");
        }
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("没有权限");
        }
        return productService.updateProductStatus(productId, status);
    }
}
