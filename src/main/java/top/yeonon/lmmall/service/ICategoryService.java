package top.yeonon.lmmall.service;

import top.yeonon.lmmall.common.ServerResponse;

/**
 * @Author yeonon
 * @date 2018/4/5 0005 14:02
 **/
public interface ICategoryService {

    ServerResponse addCategory(Integer parentId, String categoryName);

    ServerResponse updateCategory(Integer categoryId, String categoryName);

    ServerResponse getParallelChildrenCategory(Integer parentId);

    ServerResponse getDeepChildrenCategory(Integer categoryId);
}
