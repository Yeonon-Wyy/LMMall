package top.yeonon.serivice;


import top.yeonon.common.ServerResponse;
import top.yeonon.entity.Category;

import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/5 0005 14:02
 **/
public interface ICategoryService {


    //backend
    ServerResponse addCategory(Integer parentId, String categoryName);

    ServerResponse updateCategory(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getParallelChildrenCategory(Integer parentId);

    ServerResponse<List<Integer>> getDeepChildrenCategory(Integer categoryId);
}
