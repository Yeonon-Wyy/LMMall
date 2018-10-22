package top.yeonon.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import top.yeonon.common.ServerResponse;
import top.yeonon.serivice.IProductService;
import top.yeonon.serivice.vo.ProductDetailsVo;
import top.yeonon.serivice.vo.ProductListVo;

/**
 * @Author yeonon
 * @date 2018/4/5 0005 15:35
 **/
@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private IProductService productService;

    /**
     * 前台获取商品列表（未上架的不会显示）
     */
    @GetMapping
    public ServerResponse<Page<ProductListVo>> getProductList(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                              @RequestParam(value = "keyword", required = false) String keyword,
                                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                              @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {

        return productService.getProducts(keyword, categoryId, pageNum, pageSize, orderBy);
    }

    @GetMapping("{productId}")
    public ServerResponse<ProductDetailsVo> getProductDetails(@PathVariable("productId") Integer productId) {
        return productService.getProductDetails(productId);
    }
}
