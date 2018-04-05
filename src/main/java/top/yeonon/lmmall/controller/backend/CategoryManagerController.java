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

    @GetMapping("parallel/{parentId}")
    public ServerResponse getParallelChildrenCategory(HttpSession session
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

    @GetMapping("deep/{categoryId}")
    public ServerResponse getDeepChildrenCategory(HttpSession session, @PathVariable("categoryId") Integer categoryId) {
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
