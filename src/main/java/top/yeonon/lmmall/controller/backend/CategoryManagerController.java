package top.yeonon.lmmall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.Category;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.service.ICategoryService;
import top.yeonon.lmmall.service.IUserService;

import javax.servlet.http.HttpSession;
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

    @Autowired
    private IUserService userService;

    /**
     *添加品类
     * @return
     */
    @PostMapping
    public ServerResponse addCategory(HttpSession session, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId,
                                      String categoryName) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录管理员账号");
        }
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("没有权限");
        }
        return categoryService.addCategory(parentId, categoryName);
    }

    /**
     * 更新品类名称
     */
    @PutMapping("/{id}")
    public ServerResponse updateCategory(HttpSession session, @PathVariable("id") Integer categoryId,
                                         String categoryName) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录管理员账号");
        }
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("没有权限");
        }

        return categoryService.updateCategory(categoryId, categoryName);
    }

    /**
     * 获取平级（单层）的品类信息
     */
    @GetMapping("parallel/{parentId}")
    public ServerResponse<List<Category>> getParallelChildrenCategory(HttpSession session
                                                                      ,@PathVariable("parentId") Integer parentId) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录管理员账号");
        }
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("没有权限");
        }
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
    public ServerResponse<List<Integer>> getDeepChildrenCategory(HttpSession session, @PathVariable("categoryId") Integer categoryId) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录管理员账号");
        }
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("没有权限");
        }
        return categoryService.getDeepChildrenCategory(categoryId);
    }


}
