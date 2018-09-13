package top.yeonon.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.common.ServerResponse;
import top.yeonon.entity.Category;
import top.yeonon.interceptor.authenticationAnnotation.Manager;
import top.yeonon.service.ICategoryService;

import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/5 0005 13:47
 **/
@RestController
@RequestMapping("manage/categories")
public class CategoryManagerController {

    @Autowired
    private ICategoryService categoryService;


    /**
     *添加品类
     * @return
     */
    @PostMapping
    @Manager
    public ServerResponse addCategory(@RequestParam(value = "parentId", defaultValue = "0") Integer parentId,
                                      String categoryName) {
        return categoryService.addCategory(parentId, categoryName);
    }

    /**
     * 更新品类名称
     */
    @PutMapping("/{id}")
    @Manager
    public ServerResponse updateCategory(@PathVariable("id") Integer categoryId,
                                         String categoryName) {

        return categoryService.updateCategory(categoryId, categoryName);
    }

    /**
     * 获取平级（单层）的品类信息
     */
    @GetMapping("parallel/{parentId}")
    @Manager
    public ServerResponse<List<Category>> getParallelChildrenCategory(@PathVariable("parentId") Integer parentId) {
        return categoryService.getParallelChildrenCategory(parentId);
    }

    /**
     * 递归获取所有子层的品类信息
     * 如果存在下面这样的层级关系
     * 0 -> 10000 > 100010
     * 如果 categoryId == 0，那么会获取和10000同一层（以0为父）的品类，且会继续怼
     * 和10000做相同的操作，即获取100010同级且父亲是10000的所有品类
     *
     * 如果 categoryId == 10000,则仅仅获取100010层级中父亲为10000的品类以及下面的品类，和10000
     * 同层的不受影响
     */
    @GetMapping("deep/{categoryId}")
    @Manager
    public ServerResponse<List<Integer>> getDeepChildrenCategory(@PathVariable("categoryId") Integer categoryId) {
        return categoryService.getDeepChildrenCategory(categoryId);
    }


}
