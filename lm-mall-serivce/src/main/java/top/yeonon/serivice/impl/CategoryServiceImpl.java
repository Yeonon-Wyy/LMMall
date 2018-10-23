package top.yeonon.serivice.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeonon.common.ResponseCode;
import top.yeonon.common.ServerResponse;
import top.yeonon.entity.Category;
import top.yeonon.repository.CategoryRepository;
import top.yeonon.serivice.ICategoryService;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author yeonon
 * @date 2018/4/5 0005 14:03
 **/
@Service("categoryService")
public class CategoryServiceImpl implements ICategoryService {


    @Autowired
    private CategoryRepository categoryRepository;

    /**
     *增加品类
     */
    @Override
    public ServerResponse addCategory(Integer parentId, String categoryName) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误（品类名称和parentId不能为空）");
        }

        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        int rowCount = categoryRepository.insert(category);
        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("添加品类失败");
        }
        return ServerResponse.createBySuccessMessage("添加品类成功");
    }

    /**
     * 更新id对应的品类名称
     */
    @Override
    public ServerResponse updateCategory(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误（品类名称和categoryId不能为空）");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryRepository.updateByPrimaryKeySelective(category);
        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("更新品类名称失败");
        }
        return ServerResponse.createBySuccessMessage("更新品类名称成功");
    }

    @Override
    public ServerResponse<List<Category>> getParallelChildrenCategory(Integer parentId) {
        if (parentId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误!parentId不能为空");
        }

        List<Category> categories = categoryRepository.selectParallelChildrenCategoryByParentId(parentId);

        if (CollectionUtils.isEmpty(categories)) {
            return ServerResponse.createByErrorMessage("没有找到对应的品类");
        }
        return ServerResponse.createBySuccess(categories);
    }

    @Override
    public ServerResponse<List<Integer>> getDeepChildrenCategory(Integer categoryId) {
        if (categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误!categoryId不能为空");
        }

        Set<Category> categorySet = new HashSet<>();
        deepSearch(categorySet, categoryId);
        if (CollectionUtils.isEmpty(categorySet)) {
            return ServerResponse.createByErrorMessage("没有找到任何一个品类，请检查参数");
        }

        List<Integer> categoryIdList = new ArrayList<>();
        for (Category category : categorySet) {
            categoryIdList.add(category.getId());
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    private void deepSearch(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryRepository.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }

        List<Category> categories = categoryRepository.selectParallelChildrenCategoryByParentId(categoryId);
        //递归查找
        if (CollectionUtils.isNotEmpty(categories)) {
            for (Category categoryItem : categories) {
                deepSearch(categorySet, categoryItem.getId());
            }
        }
    }
}
