package top.yeonon.lmmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.Category;
import top.yeonon.lmmall.entity.Product;
import top.yeonon.lmmall.repository.CategoryRepository;
import top.yeonon.lmmall.repository.ProductElasticRespository;
import top.yeonon.lmmall.repository.ProductRepository;
import top.yeonon.lmmall.service.ICategoryService;
import top.yeonon.lmmall.service.IProductService;
import top.yeonon.lmmall.vo.ProductDetailsVo;
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

    @Autowired
    private ProductElasticRespository productElasticRespository;

    @Override
    @SuppressWarnings("unchecked")
    public ServerResponse<Page<ProductListVo>> getProducts(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy) {
        if (categoryId == null && StringUtils.isBlank(keyword)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误，keyword或categoryId不能为空");
        }

        List<Integer> categoryIdList = Lists.newArrayList();
        //获取品类的id以及子id
        if (categoryId != null) {
            Category category = categoryRepository.selectByPrimaryKey(categoryId);
            if (category == null) {
                List<ProductListVo> productListVos = Lists.newArrayList();
                Page<ProductListVo> page = new AggregatedPageImpl<>(productListVos);
                return ServerResponse.createBySuccess(page);
            }
            categoryIdList = categoryService.getDeepChildrenCategory(category.getId()).getData();
        }


        /**
         * 下面开始构造查询语句，逻辑应该是这样的：
         * 1. 如果categoryId不为空，则应构造出来should，即或语法来实现查询，对应json格式是:
         *    "should" : 100006,
         *    "should" : 100007
         * 2. 如果有关键字就对关键字搜索，逻辑也是使用must和should结合
         * 3. 如果有排序要求，就根据排序的规则排序
         * 故，最后对应SQL应该是这样的.(大致逻辑):
         *  SELECT product FROM lmmall_product WHERE status=1 AND categoryId = 100006 OR categoryId = 100007 OR ....
         *  (如果有的话)AND name LIKE keyword or subtitle LIKE keyword
         *
         *  即在status等于1前提下，查找满足categoryId集合的商品，如果有keyword，那么keyword就作为AND条件来加强约束
         *
         *
         *
         *
         */

        QueryBuilder queryCategoryId = QueryBuilders.boolQuery();
        for (Integer cid : categoryIdList) {
            ((BoolQueryBuilder) queryCategoryId).should(QueryBuilders.matchQuery("categoryId", cid.toString()));
        }

        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(new QueryStringQueryBuilder("1").field("status"))
                .must(queryCategoryId);


        //如果keyword为空，就不采用搜索
        if (StringUtils.isNotEmpty(keyword)) {
            ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("name", keyword))
                    .should(QueryBuilders.matchQuery("subtitle", keyword)));
        }



        SearchQuery searchQuery = new NativeSearchQuery(queryBuilder);
        //spring elasticsearch 中分页pageNum从0开始，故前端传过来的数据应-1
        searchQuery.setPageable(PageRequest.of(pageNum - 1, pageSize));


        //排序
        if (StringUtils.isNotBlank(orderBy)) {
            if (ServerConst.ProductOrderBy.ORDER_BY_PRICE.contains(orderBy)) {
                String[] orderByArray = StringUtils.split(orderBy, "_");
                if (orderByArray.length == 2) {
                    if ("desc".equals(orderByArray[1]))
                        searchQuery.addSort(Sort.by(Sort.Order.desc(orderByArray[0])));
                    else
                        searchQuery.addSort(Sort.by(Sort.Order.asc(orderByArray[0])));
                }
            }
        }


        //获取满足条件的商品
        //这里需要注意，categoryIdList不可能是null，Mybatis返回的集合类型不可能是null，如果没有数据会返回空的集合
        //List<Product> productList = productRepository.selectProductsByNameAndProductIds(keyword, categoryIdList);

        Page<ProductListVo> productListVos = productElasticRespository.search(searchQuery)
                .map(this::assembleProductListVo);


        return ServerResponse.createBySuccess(productListVos);
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
    @CachePut(value = "ProductCache", key = "#root.caches[0].name + ':' + #product.id",
            condition = "#product.id != null")
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
            Product newProduct = productRepository.selectByPrimaryKey(product.getId());
            ProductDetailsVo productDetailsVo = assembleProductDetailsVo(newProduct);
            return ServerResponse.createBySuccess(productDetailsVo);
        }
        //如果上面没有返回，就表示product 的id没有传进来，这时候就是增加商品
        int rowCount = productRepository.insert(product);
        if (rowCount < 0) {
            return ServerResponse.createByErrorMessage("添加商品失败");
        }
        return ServerResponse.createBySuccessMessage("添加商品成功");
    }


    /**
     * 搜索商品
     *
     * @param productName 商品名称
     * @param productId   商品ID
     * @param pageNum     分页页码，从1开始
     * @param pageSize    每页返回数字
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public ServerResponse<Page<ProductListVo>> search(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        //如果不传入ID，这个参数就是null，elasticSearch不允许为null,故设为-1
        if (productId == null)
            productId = -1;
        //List<Product> productList = productRepository.searchProductsByNameAndId(productName, productId);

        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", productName))
                        .should(QueryBuilders.matchQuery("subtitle", productName))
                        .should(QueryBuilders.matchQuery("id", productId)));

        //spring data elastic 是从0号开始分页的，故需要减1
        SearchQuery searchQuery =
                new NativeSearchQuery(queryBuilder).addSort(Sort.by(Sort.Order.asc("id"))).setPageable(PageRequest.of(pageNum - 1, pageSize));


        Page<ProductListVo> products = productElasticRespository.search(searchQuery).map(this::assembleProductListVo);

        return ServerResponse.createBySuccess(products);
    }


    @Override
    @Cacheable(value = "ProductCache")
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
    @CachePut(value = "ProductCache", key = "#root.caches[0].name + ':' + #productId")
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

        Product newProduct = productRepository.selectByPrimaryKey(product.getId());
        ProductDetailsVo productDetailsVo = assembleProductDetailsVo(newProduct);
        return ServerResponse.createBySuccess(productDetailsVo);
    }

    /**
     * 装配productList VO 对象
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
        productDetailsVo.setDetails(product.getDetail());

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
