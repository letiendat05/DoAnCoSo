<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <div id="layoutSidenav_nav">
            <nav class="sb-sidenav accordion sb-sidenav-dark" id="sidenavAccordion">
                <div class="sb-sidenav-menu">
                    <div class="nav">
                        <div class="sb-sidenav-menu-heading">Chức năng quản trị</div>
                        <a class="nav-link" href="/admin">
                            <div class="sb-nav-link-icon"><i class="fas fa-chart-line text-primary"></i></div>
                            Báo cáo & Thống kê
                        </a>

                        <a class="nav-link" href="/admin/user">
                            <div class="sb-nav-link-icon"><i class="fas fa-users text-info"></i></div>
                            Người dùng
                        </a>

                        <a class="nav-link" href="/admin/product">
                            <div class="sb-nav-link-icon"><i class="fas fa-boxes-stacked text-success"></i></div>
                            Sản phẩm
                        </a>

                        <a class="nav-link" href="/admin/order">
                            <div class="sb-nav-link-icon"><i class="fas fa-receipt text-warning"></i></div>
                            Đơn hàng
                        </a>
                    </div>
                </div>
                <div class="sb-sidenav-footer">
                    <div class="small">Đăng nhập bởi:</div>
                    <div class="fw-bold text-white">Lê Tiến Đạt</div>
                </div>
            </nav>
        </div>
