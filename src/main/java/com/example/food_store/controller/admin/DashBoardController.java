package com.example.food_store.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.food_store.controller.BaseController;
import com.example.food_store.domain.Order;
import com.example.food_store.repository.OrderRepository;
import com.example.food_store.service.impl.ProductService;
import com.example.food_store.service.impl.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DashBoardController extends BaseController {
    private final UserService userService;
    private final ProductService productService;
    private final OrderRepository orderRepository;

    @GetMapping("/admin")
    public String getDashBoard(Model model) {
        log.info("Request to /admin");
        long countUser = this.userService.countUser();
        long countOrder = this.userService.countOrder();
        long countProduct = this.productService.countProduct();

        List<Order> allOrders = this.orderRepository.findAll();
        double totalRevenue = allOrders.stream().mapToDouble(Order::getTotalPrice).sum();
        List<Order> recentOrders = allOrders.stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                .limit(6)
                .toList();

        model.addAttribute("countUser", countUser);
        model.addAttribute("countOrder", countOrder);
        model.addAttribute("countProduct", countProduct);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("recentOrders", recentOrders);
        return "admin/dashboard/show";
    }

}
