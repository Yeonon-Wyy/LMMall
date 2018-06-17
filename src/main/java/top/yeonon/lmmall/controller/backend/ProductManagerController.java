package top.yeonon.lmmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.Product;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Manager;
import top.yeonon.lmmall.properties.CoreProperties;
import top.yeonon.lmmall.service.IFileService;
import top.yeonon.lmmall.service.IProductService;
import top.yeonon.lmmall.service.IUserService;
import top.yeonon.lmmall.vo.ProductDetailsVo;
import top.yeonon.lmmall.vo.ProductListVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

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
    private IFileService fileService;

    @Autowired
    private CoreProperties coreProperties;

    /**
     * 获取所有商品，不需要排序和关键字（就是所有，就是那么暴力！）
     */
    @GetMapping
    @Manager
    public ServerResponse<PageInfo> getManageProducts(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return productService.getManageProducts(pageNum, pageSize);
    }

    /**
     * 添加商品
     */
    @PostMapping
    @Manager
    public ServerResponse addProduct(Product product) {
        return productService.addOrUpdateProduct(product);
    }

    /**
     * 更新商品信息
     */
    @PutMapping("{productId}")
    @Manager
    public ServerResponse updateProduct(@PathVariable("productId") Integer productId, Product product) {
        if (product != null) {
            product.setId(productId);
        }
        return productService.addOrUpdateProduct(product);
    }

    /**
     * 商品搜索
     */
    @GetMapping("search")
    @Manager
    public ServerResponse<Page<ProductListVo>> search(String productName, Integer productId,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return productService.search(productName, productId, pageNum, pageSize);
    }

    /**
     * 获取商品详情（未上架的也可以获取到）
     */
    @GetMapping("{productId}")
    @Manager
    public ServerResponse<ProductDetailsVo> getManageProductDetails(@PathVariable("productId") Integer productId) {

        return productService.getManageProductDetails(productId);
    }

    /**
     * 商品上下架
     */
    @PutMapping("{productId}/status")
    @Manager
    public ServerResponse updateProductStatus(@PathVariable("productId") Integer productId, Integer status) {
        return productService.updateProductStatus(productId, status);
    }

    /**
     * 上传图片
     */
    @PostMapping("upload")
    @Manager
    public ServerResponse<Map<String, String>> uploadImage(HttpServletRequest request,
                            @RequestParam(value = "upload_file", required = false)MultipartFile file) {

        //1.首先需要知道上传文件的路径，这个路径只是一个临时路径，可以直接存储到temp里
        String path = request.getServletContext().getRealPath("upload");
        //2. 需要知道目标文件名
        String targetFileName  = fileService.upload(file, path);
        //3. 构造URL
        String url = coreProperties.getFtp().getHostPrefix() + targetFileName;

        //4. 将url和uri返回给前端
        Map<String, String> urlMap = Maps.newHashMap();
        urlMap.put("uri", targetFileName);
        urlMap.put("url", url);
        return ServerResponse.createBySuccess("上传文件成功", urlMap);
    }
}
