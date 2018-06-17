package top.yeonon.lmmall.service;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Page;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.Product;
import top.yeonon.lmmall.vo.ProductDetailsVo;
import top.yeonon.lmmall.vo.ProductListVo;

/**
 * @Author yeonon
 * @date 2018/4/5 0005 16:02
 **/
public interface IProductService {

    ServerResponse<Page<ProductListVo>> getProducts(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy);

    ServerResponse<ProductDetailsVo> getProductDetails(Integer productId);

    //backend
    ServerResponse<PageInfo> getManageProducts(Integer pageNum, Integer pageSize);

    ServerResponse addOrUpdateProduct(Product product);

    ServerResponse<Page<ProductListVo>> search(String productName, Integer productId, Integer pageNum, Integer pageSize);

    ServerResponse<ProductDetailsVo> getManageProductDetails(Integer productId);

    ServerResponse updateProductStatus(Integer productId, Integer status);

}
