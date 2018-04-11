package top.yeonon.lmmall.controller.backend;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Manager;
import top.yeonon.lmmall.service.IOrderService;
import top.yeonon.lmmall.service.IUserService;

import javax.servlet.http.HttpSession;

/**
 * @Author yeonon
 * @date 2018/4/10 0010 13:02
 **/
@RestController
@RequestMapping("manage/order")
public class OrderManagerController {

    @Autowired
    private IOrderService orderService;

    @GetMapping
    public ServerResponse getOrders(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return orderService.getManageList(pageNum, pageSize);
    }

    @GetMapping("{orderNo}")
    @Manager
    public ServerResponse getDetail(@PathVariable("orderNo") Long orderNo) {
        return orderService.getManageDetails(orderNo);
    }

    @GetMapping("search")
    @Manager
    public ServerResponse<PageInfo> search(Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return orderService.manageSearch(orderNo, pageNum, pageSize);
    }

    @PostMapping("{orderNo}/send")
    @Manager
    public ServerResponse sendOrder(@PathVariable("orderNo") Long orderNo) {

        return orderService.manageSendGoods(orderNo);
    }
}
