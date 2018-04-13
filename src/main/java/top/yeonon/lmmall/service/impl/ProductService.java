package top.yeonon.lmmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.Category;
import top.yeonon.lmmall.entity.Product;
import top.yeonon.lmmall.repository.CategoryRepository;
import top.yeonon.lmmall.repository.ProductRepository;
import top.yeonon.lmmall.service.ICategoryService;
import top.yeonon.lmmall.service.IProductService;
import top.yeonon.lmmall.vo.ProductDetailsVo;
import top.yeonon.lmmall.vo.ProductListVo;

import java.util.List;
import top.yeonon.lmmall.properties.CoreProperties;

/**
 * @Author yeonon
 * @date 2018/4/5 0005 16:03
 **/
@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private CoreProperties coreProperties;

    @Override
    @SuppressWarnings("unchecked")
    @Cacheable(value = "ProductCache", key = "#root.caches[0].name + ':' + #pageNum + ':products'")
    public ServerResponse<PageInfo> getProducts(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy) {
        if (categoryId == null && StringUtils.isBlank(keyword)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误，keyword和categoryId都不能为空");
        }

        List<Integer> categoryIdList = Lists.newArrayList();
        Category category = categoryRepository.selectByPrimaryKey(categoryId);
        if (category == null) {
            PageHelper.startPage(pageNum, pageSize);
            List<ProductListVo> productListVos = Lists.newArrayList();
            PageInfo pageInfo = new PageInfo(productListVos);
            return ServerResponse.createBySuccess(pageInfo);
        }
        //获取品类的id以及子id
        categoryIdList = (List<Integer>) categoryService.getDeepChildrenCategory(category.getId()).getData();

        //拼接查询的关键字
        keyword = new StringBuilder().append("%").append(keyword).append("%").toString();

        PageHelper.startPage(pageNum, pageSize);

        //排序
        if (StringUtils.isNotBlank(orderBy)) {
            if (ServerConst.ProductOrderBy.ORDER_BY_PRICE.contains(orderBy)) {
                String[] orderByArray = StringUtils.split(orderBy, "_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }

        //获取满足条件的商品
        List<Product> productList = productRepository.selectProductsByNameAndProductIds(keyword, categoryIdList);

        //转换成VO对象返回
        List<ProductListVo> productListVos = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVos.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    @Cacheable(value = "ProductCache")
    public ServerResponse<ProductDetailsVo> getProductDetails(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误（productId不能为空）");
        }

        Product product = productRepository.selectByPrimaryKey(productId);
        if (product == null || !product.getStatus().equals(ServerConst.ProductStatus.ON_SELL.getCode())) {
            return ServerResponse.createByErrorMessage("没有找到该商品或者该商品已下架");
        }
        ProductDetailsVo productDetailsVo = assembleProductDetailsVo(product);
        return ServerResponse.createBySuccess(productDetailsVo);
    }


    //backend


    @Override
    @SuppressWarnings("unchecked")
    public ServerResponse<PageInfo> getManageProducts(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productRepository.selectProducts();

        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorMessage("没有找到任何商品");
        }

        List<ProductListVo> productListVos = Lists.newArrayList();
        for (Product product : productList) {
            productListVos.add(assembleProductListVo(product));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }


    @Override
    public ServerResponse addOrUpdateProduct(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误!(product无效)");
        }

        if (product.getId() != null) {
            //不为null表示这是一次更新操作
            int rowCount = productRepository.updateByPrimaryKeySelective(product);
            if (rowCount < 0) {
                return ServerResponse.createByErrorMessage("更新商品失败");
            }
            return ServerResponse.createBySuccessMessage("更新商品成功");
        }
        //如果上面没有返回，就表示product 的id没有传进来，这时候就是增加商品
        int rowCount = productRepository.insert(product);
        if (rowCount < 0) {
            return ServerResponse.createByErrorMessage("添加商品失败");
        }
        return ServerResponse.createBySuccessMessage("添加商品成功!");
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServerResponse<PageInfo> search(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productRepository.searchProductsByNameAndId(productName, productId);

        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorMessage("没有找到符合条件的任何商品");
        }

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            productListVoList.add(assembleProductListVo(product));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailsVo> getManageProductDetails(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误（productId不能为空）");
        }

        Product product = productRepository.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("没有找到该商品");
        }
        ProductDetailsVo productDetailsVo = assembleProductDetailsVo(product);
        return ServerResponse.createBySuccess(productDetailsVo);
    }

    @Override
    public ServerResponse updateProductStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误（productId或者status不能为空）");
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount = productRepository.updateByPrimaryKeySelective(product);
        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("商品上下架失败");
        }
        return ServerResponse.createBySuccessMessage("商品上下架成功");
    }

    /**
     *     装配productList VO 对象
     */

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setStock(product.getStock());
        productListVo.setSubTitle(product.getSubtitle());
        productListVo.setImageHost(coreProperties.getFtp().getHostPrefix());
        return productListVo;
    }

    /**
     * 装配productDetails对象
     */
    private ProductDetailsVo assembleProductDetailsVo(Product product) {
        ProductDetailsVo productDetailsVo = new ProductDetailsVo();
        productDetailsVo.setCategoryId(product.getCategoryId());
        productDetailsVo.setProductId(product.getId());
        productDetailsVo.setName(product.getName());
        productDetailsVo.setMainImage(product.getMainImage());
        productDetailsVo.setSubImage(product.getSubImages());
        productDetailsVo.setSubTitle(product.getSubtitle());
        productDetailsVo.setPrice(product.getPrice());
        productDetailsVo.setStatus(product.getStatus());
        productDetailsVo.setStock(product.getStock());

        Category category = categoryRepository.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            productDetailsVo.setParentCategoryId(0);
        } else {
            productDetailsVo.setParentCategoryId(category.getParentId());
        }

        productDetailsVo.setCreateTime(product.getCreateTime());
        productDetailsVo.setUpdateTime(product.getUpdateTime());
        productDetailsVo.setImageHost(coreProperties.getFtp().getHostPrefix());
        return productDetailsVo;
    }
}
