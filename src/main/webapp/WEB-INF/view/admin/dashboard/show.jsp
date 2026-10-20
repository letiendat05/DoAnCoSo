<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <meta name="description" content="Bảng điều khiển và báo cáo thống kê" />
    <meta name="author" content="Lê Tiến Đạt" />
    <title>Báo Cáo & Thống Kê - Quản Trị Hệ Thống</title>
    <link href="https://cdn.jsdelivr.net/npm/simple-datatables@7.1.2/dist/style.min.css" rel="stylesheet" />
    <link href="/css/styles.css" rel="stylesheet" />
    <script src="https://use.fontawesome.com/releases/v6.3.0/js/all.js" crossorigin="anonymous"></script>
    <style>
        body {
            background-color: #f4f6f9;
        }

        .stat-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
            transition: all 0.25s ease-in-out;
            overflow: hidden;
        }

        .stat-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        }

        .stat-icon {
            font-size: 2.4rem;
            opacity: 0.35;
        }

        .stat-number {
            font-size: 1.85rem;
            font-weight: 700;
            line-height: 1.2;
        }

        .stat-label {
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            opacity: 0.9;
        }

        .chart-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
        }

        .chart-card .card-header {
            background-color: #ffffff;
            border-bottom: 1px solid #edf2f7;
            font-weight: 600;
            padding: 1rem 1.25rem;
        }

        .breadcrumb {
            background-color: #ffffff;
            border-radius: 8px;
            padding: 12px 16px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
        }

        .table-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
            overflow: hidden;
        }

        .table-card .card-header {
            background-color: #ffffff;
            border-bottom: 1px solid #edf2f7;
            font-weight: 600;
            padding: 1rem 1.25rem;
        }

        .footer-link {
            text-decoration: none;
            color: rgba(255, 255, 255, 0.9);
            font-size: 0.85rem;
        }

        .footer-link:hover {
            color: #ffffff;
            text-decoration: underline;
        }
    </style>
</head>

<body class="sb-nav-fixed">
    <jsp:include page="../layout/header.jsp" />

    <div id="layoutSidenav">
        <jsp:include page="../layout/sidebar.jsp" />

        <div id="layoutSidenav_content">
            <main>
                <div class="container-fluid px-4 pb-5">
                    <div class="d-flex justify-content-between align-items-center mt-4 mb-3">
                        <div>
                            <h2 class="mb-0 fw-bold text-dark">
                                <i class="fas fa-chart-line text-primary me-2"></i>Báo Cáo Thống Kê
                            </h2>
                            <p class="text-muted small mb-0 mt-1">Tổng quan doanh thu, đơn hàng, sản phẩm và khách hàng hệ thống Healthy Food</p>
                        </div>
                        <div>
                            <a href="/admin/order" class="btn btn-outline-primary btn-sm me-2">
                                <i class="fas fa-receipt me-1"></i> Quản lý Đơn Hàng
                            </a>
                            <a href="/admin/product" class="btn btn-primary btn-sm">
                                <i class="fas fa-plus me-1"></i> Quản lý Sản Phẩm
                            </a>
                        </div>
                    </div>

                    <ol class="breadcrumb mb-4">
                        <li class="breadcrumb-item"><a href="/admin" class="text-decoration-none">Hệ thống</a></li>
                        <li class="breadcrumb-item active">Báo cáo & Thống kê</li>
                    </ol>

                    <!-- Row 4 Metric Cards -->
                    <div class="row g-3 mb-4">
                        <!-- Card 1: Users -->
                        <div class="col-xl-3 col-md-6">
                            <div class="card stat-card bg-primary text-white h-100">
                                <div class="card-body d-flex justify-content-between align-items-center">
                                    <div>
                                        <div class="stat-label">Khách hàng</div>
                                        <div class="stat-number my-1">${countUser}</div>
                                        <small class="text-white-50"><i class="fas fa-arrow-up me-1"></i>Tài khoản hoạt động</small>
                                    </div>
                                    <div class="stat-icon">
                                        <i class="fas fa-users"></i>
                                    </div>
                                </div>
                                <div class="card-footer bg-transparent border-0 pt-0 d-flex align-items-center justify-content-between">
                                    <a class="footer-link" href="/admin/user">Xem danh sách người dùng</a>
                                    <i class="fas fa-arrow-right text-white-50 small"></i>
                                </div>
                            </div>
                        </div>

                        <!-- Card 2: Products -->
                        <div class="col-xl-3 col-md-6">
                            <div class="card stat-card bg-success text-white h-100">
                                <div class="card-body d-flex justify-content-between align-items-center">
                                    <div>
                                        <div class="stat-label">Sản phẩm đang bán</div>
                                        <div class="stat-number my-1">${countProduct}</div>
                                        <small class="text-white-50"><i class="fas fa-check-circle me-1"></i>32 danh mục Eat Clean</small>
                                    </div>
                                    <div class="stat-icon">
                                        <i class="fas fa-box-open"></i>
                                    </div>
                                </div>
                                <div class="card-footer bg-transparent border-0 pt-0 d-flex align-items-center justify-content-between">
                                    <a class="footer-link" href="/admin/product">Xem kho sản phẩm</a>
                                    <i class="fas fa-arrow-right text-white-50 small"></i>
                                </div>
                            </div>
                        </div>

                        <!-- Card 3: Orders -->
                        <div class="col-xl-3 col-md-6">
                            <div class="card stat-card bg-warning text-white h-100">
                                <div class="card-body d-flex justify-content-between align-items-center">
                                    <div>
                                        <div class="stat-label">Tổng số đơn hàng</div>
                                        <div class="stat-number my-1">${countOrder}</div>
                                        <small class="text-white-50"><i class="fas fa-shipping-fast me-1"></i>COD & VNPAY</small>
                                    </div>
                                    <div class="stat-icon">
                                        <i class="fas fa-shopping-bag"></i>
                                    </div>
                                </div>
                                <div class="card-footer bg-transparent border-0 pt-0 d-flex align-items-center justify-content-between">
                                    <a class="footer-link" href="/admin/order">Xem chi tiết đơn hàng</a>
                                    <i class="fas fa-arrow-right text-white-50 small"></i>
                                </div>
                            </div>
                        </div>

                        <!-- Card 4: Revenue -->
                        <div class="col-xl-3 col-md-6">
                            <div class="card stat-card bg-danger text-white h-100">
                                <div class="card-body d-flex justify-content-between align-items-center">
                                    <div>
                                        <div class="stat-label">Tổng doanh thu</div>
                                        <div class="stat-number my-1">
                                            <fmt:formatNumber type="number" value="${totalRevenue}" /> đ
                                        </div>
                                        <small class="text-white-50"><i class="fas fa-chart-pie me-1"></i>Tăng trưởng tích cực</small>
                                    </div>
                                    <div class="stat-icon">
                                        <i class="fas fa-coins"></i>
                                    </div>
                                </div>
                                <div class="card-footer bg-transparent border-0 pt-0 d-flex align-items-center justify-content-between">
                                    <a class="footer-link" href="/admin/order">Báo cáo doanh số</a>
                                    <i class="fas fa-arrow-right text-white-50 small"></i>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Row 2 Charts -->
                    <div class="row g-4 mb-4">
                        <div class="col-xl-6">
                            <div class="card chart-card h-100">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <span>
                                        <i class="fas fa-chart-area text-primary me-2"></i>
                                        Xu hướng đơn hàng & lượt giao dịch theo ngày
                                    </span>
                                    <span class="badge bg-primary-subtle text-primary border">10 ngày gần nhất</span>
                                </div>
                                <div class="card-body">
                                    <canvas id="myAreaChart" width="100%" height="45"></canvas>
                                </div>
                            </div>
                        </div>
                        <div class="col-xl-6">
                            <div class="card chart-card h-100">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <span>
                                        <i class="fas fa-chart-bar text-success me-2"></i>
                                        Doanh thu các tháng gần đây (VNĐ)
                                    </span>
                                    <span class="badge bg-success-subtle text-success border">Năm 2026</span>
                                </div>
                                <div class="card-body">
                                    <canvas id="myBarChart" width="100%" height="45"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Recent Orders Table -->
                    <div class="card table-card mb-4">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <span class="fw-bold">
                                <i class="fas fa-table text-warning me-2"></i>
                                Danh Sách Đơn Hàng Mới Nhất
                            </span>
                            <a href="/admin/order" class="btn btn-sm btn-outline-secondary">
                                Xem tất cả (${countOrder}) <i class="fas fa-arrow-right ms-1"></i>
                            </a>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th class="ps-3" style="width: 80px;">Mã ĐH</th>
                                            <th>Khách hàng nhận</th>
                                            <th>Điện thoại</th>
                                            <th>Địa chỉ nhận hàng</th>
                                            <th>Phương thức</th>
                                            <th>Trạng thái</th>
                                            <th class="text-end">Tổng tiền</th>
                                            <th class="text-center pe-3" style="width: 120px;">Hành động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${not empty recentOrders}">
                                                <c:forEach var="order" items="${recentOrders}">
                                                    <tr>
                                                        <td class="ps-3 fw-bold text-primary">#${order.id}</td>
                                                        <td>
                                                            <div class="fw-semibold">${order.receiverName}</div>
                                                            <small class="text-muted"><c:out value="${order.user.email}" default="Khách vãng lai" /></small>
                                                        </td>
                                                        <td>${order.receiverPhone}</td>
                                                        <td class="text-truncate" style="max-width: 250px;" title="${order.receiverAddress}">
                                                            ${order.receiverAddress}
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${order.paymentMethod == 'VNPAY'}">
                                                                    <span class="badge bg-danger">VNPAY</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge bg-secondary">COD</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${order.status == 'Hoàn thành'}">
                                                                    <span class="badge bg-success"><i class="fas fa-check me-1"></i>Hoàn thành</span>
                                                                </c:when>
                                                                <c:when test="${order.status == 'Đang vận chuyển'}">
                                                                    <span class="badge bg-info text-dark"><i class="fas fa-truck me-1"></i>Đang giao</span>
                                                                </c:when>
                                                                <c:when test="${order.status == 'Chưa xử lý'}">
                                                                    <span class="badge bg-warning text-dark"><i class="fas fa-clock me-1"></i>Chờ xử lý</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge bg-secondary">${order.status}</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td class="text-end fw-bold text-danger">
                                                            <fmt:formatNumber type="number" value="${order.totalPrice}" /> đ
                                                        </td>
                                                        <td class="text-center pe-3">
                                                            <a href="/admin/order/${order.id}" class="btn btn-sm btn-outline-primary" title="Xem chi tiết">
                                                                <i class="fas fa-eye"></i>
                                                            </a>
                                                            <a href="/admin/order/update/${order.id}" class="btn btn-sm btn-outline-warning ms-1" title="Cập nhật">
                                                                <i class="fas fa-edit"></i>
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <tr>
                                                    <td colspan="8" class="text-center py-4 text-muted">
                                                        <i class="fas fa-box-open fa-2x mb-2 d-block text-secondary"></i>
                                                        Chưa có đơn hàng nào được ghi nhận.
                                                    </td>
                                                </tr>
                                            </c:otherwise>
                                        </c:choose>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                </div>
            </main>
            <jsp:include page="../layout/footer.jsp" />
        </div>
    </div>

    <jsp:include page="../../client/layout/chat-bot.jsp" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
    <script src="/js/scripts.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.js" crossorigin="anonymous"></script>
    
    <script>
        // Set new default font family and font color to mimic Bootstrap's default styling
        Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
        Chart.defaults.global.defaultFontColor = '#292b2c';

        // 1. Area Chart: Đơn hàng theo ngày
        var ctxArea = document.getElementById("myAreaChart");
        if (ctxArea) {
            new Chart(ctxArea, {
                type: 'line',
                data: {
                    labels: ["11/10", "12/10", "13/10", "14/10", "15/10", "16/10", "17/10", "18/10", "19/10", "20/10"],
                    datasets: [{
                        label: "Số đơn hàng",
                        lineTension: 0.3,
                        backgroundColor: "rgba(2,117,216,0.15)",
                        borderColor: "rgba(2,117,216,1)",
                        pointRadius: 4,
                        pointBackgroundColor: "rgba(2,117,216,1)",
                        pointBorderColor: "rgba(255,255,255,0.8)",
                        pointHoverRadius: 6,
                        pointHoverBackgroundColor: "rgba(2,117,216,1)",
                        pointHitRadius: 50,
                        pointBorderWidth: 2,
                        data: [2, 3, 5, 4, 6, 8, 7, 9, 12, 15],
                    }],
                },
                options: {
                    scales: {
                        xAxes: [{
                            gridLines: { display: false },
                            ticks: { maxTicksLimit: 7 }
                        }],
                        yAxes: [{
                            ticks: { min: 0, stepSize: 3, maxTicksLimit: 6 },
                            gridLines: { color: "rgba(0, 0, 0, .075)" }
                        }],
                    },
                    legend: { display: false }
                }
            });
        }

        // 2. Bar Chart: Doanh thu theo tháng
        var ctxBar = document.getElementById("myBarChart");
        if (ctxBar) {
            new Chart(ctxBar, {
                type: 'bar',
                data: {
                    labels: ["Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10"],
                    datasets: [{
                        label: "Doanh thu (VNĐ)",
                        backgroundColor: "rgba(40, 167, 69, 0.85)",
                        borderColor: "rgba(40, 167, 69, 1)",
                        hoverBackgroundColor: "rgba(40, 167, 69, 1)",
                        data: [14200000, 18500000, 22600000, 26800000, 31400000, 38900000],
                    }],
                },
                options: {
                    scales: {
                        xAxes: [{
                            gridLines: { display: false },
                            ticks: { maxTicksLimit: 6 }
                        }],
                        yAxes: [{
                            ticks: {
                                min: 0,
                                callback: function(value) {
                                    return (value / 1000000) + " tr";
                                }
                            },
                            gridLines: { color: "rgba(0, 0, 0, .075)" }
                        }],
                    },
                    tooltips: {
                        callbacks: {
                            label: function(tooltipItem, data) {
                                return "Doanh thu: " + Number(tooltipItem.yLabel).toLocaleString('vi-VN') + " đ";
                            }
                        }
                    },
                    legend: { display: false }
                }
            });
        }
    </script>
</body>

</html>
