package top.yeonon.lmmall.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yeonon.lmmall.common.ServerResponse;

import top.yeonon.lmmall.service.IProductService;

/**
 * @Author yeonon
 * @date 2018/4/5 0005 15:35
 **/
@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping
    public ServerResponse<PageInfo> getProductList(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                   @RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                   @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {

        return productService.getProducts(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
