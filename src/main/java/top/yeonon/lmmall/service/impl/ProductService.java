package top.yeonon.lmmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import top.yeonon.lmmall.vo.ProductListVo;

import java.util.ArrayList;
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
}
