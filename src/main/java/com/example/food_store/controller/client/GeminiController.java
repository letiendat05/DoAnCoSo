package com.example.food_store.controller.client;

import org.springframework.web.bind.annotation.*;
import com.example.food_store.controller.BaseController;
import com.example.food_store.domain.Product;
import com.example.food_store.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gemini-proxy")
public class GeminiController extends BaseController {

    @Value("${api-gemini}")
    private String apiUrl;

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<String> proxyToGemini(@RequestBody String requestBody) {
        log.info("Request to /gemini-proxy");
        if (apiUrl == null || apiUrl.isBlank() || apiUrl.contains("demo-key")) {
            return ResponseEntity.ok()
                    .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                    .body("Hệ thống chatbot chưa được cấu hình API Key thật (hiện đang dùng demo-key trong file .env). Vui lòng cấu hình API_GEMINI_URL với API Key từ Google AI Studio để trò chuyện với Gemini AI.");
        }

        // 1. Trich xuat cau hoi nguoi dung
        String userText = "";
        try {
            JsonNode root = objectMapper.readTree(requestBody);
            userText = root.path("contents").path(0).path("parts").path(0).path("text").asText();
        } catch (Exception ignored) {
            userText = requestBody;
        }

        // 2. Lay danh muc san pham tu Database
        StringBuilder catalog = new StringBuilder();
        try {
            List<Product> products = this.productRepository.findAll();
            if (products != null && !products.isEmpty()) {
                catalog.append("\n[DANH SÁCH MÓN ĂN VÀ SẢN PHẨM ĐANG BÁN TẠI CỬA HÀNG FOOD STORE]:\n");
                for (Product p : products) {
                    catalog.append("- ").append(p.getName())
                           .append(" (Giá: ").append(String.format("%,.0f", p.getPrice())).append(" đ/").append(p.getUnit() != null ? p.getUnit() : "phần").append(")")
                           .append(" [Mục tiêu: ").append(p.getTarget() != null ? p.getTarget() : "Mọi đối tượng").append("]")
                           .append(": ").append(p.getShortDesc() != null ? p.getShortDesc() : "").append("\n");
                }
            }
        } catch (Exception e) {
            log.warn("Không thể tải danh sách sản phẩm cho chatbot: ", e);
        }

        // 3. Xay dung prompt huong dan goi y mon thuc te
        String promptWithContext = "Bạn là Trợ lý ảo AI kiêm Chuyên gia tư vấn dinh dưỡng thông minh của cửa hàng Food Store.\n"
                + catalog.toString() + "\n"
                + "NHIỆM VỤ VÀ QUY TẮC TƯ VẤN QUAN TRỌNG:\n"
                + "1. GỢI Ý MÓN ĂN VÀ THỰC ĐƠN: Khi khách hàng hỏi gợi ý món ăn, thực đơn (như giảm cân, tăng cân, tăng cơ, healthy/eat clean, ăn dặm cho bé, bữa sáng/trưa/tối, món rau củ quả...), BẮT BUỘC ƯU TIÊN GỢI Ý CÁC MÓN ĂN VÀ NGUYÊN LIỆU CÓ TRONG DANH SÁCH TRÊN CỦA FOOD STORE.\n"
                + "2. THÔNG TIN CHI TIẾT TỪNG MÓN: Nêu rõ tên món, nguyên liệu có sẵn tại Food Store kèm GIÁ TIỀN và ĐƠN VỊ TÍNH, giá trị dinh dưỡng/lợi ích sức khỏe và mẹo chế biến hoặc gợi ý kết hợp bữa ăn hoàn chỉnh.\n"
                + "3. ĐỊNH DẠNG VÀ VĂN PHONG: Luôn trả lời bằng tiếng Việt CÓ DẤU ĐẦY ĐỦ, chuẩn ngữ pháp, giọng điệu niềm nở, nhiệt tình, lịch sự. Trình bày ngắt dòng rõ ràng, dùng gạch đầu dòng và biểu tượng icon (như salad, bơ, thịt, cá, hoa quả) để người đọc dễ theo dõi.\n"
                + "4. SẢN PHẨM CHƯA CÓ: Nếu khách hỏi món cửa hàng chưa có sẵn, hãy khéo léo gợi ý món tương tự hoặc các sản phẩm phù hợp nhất đang bán tại Food Store.\n\n"
                + "Câu hỏi của khách hàng: " + userText;

        String finalPayload;
        try {
            Map<String, Object> payloadMap = Collections.singletonMap("contents",
                    Collections.singletonList(
                            Collections.singletonMap("parts",
                                    Collections.singletonList(
                                            Collections.singletonMap("text", promptWithContext)
                                    )
                            )
                    )
            );
            finalPayload = objectMapper.writeValueAsString(payloadMap);
        } catch (Exception e) {
            finalPayload = requestBody;
        }

        // 4. Goi API Gemini voi fallback
        String[] targetUrls = new String[] {
            apiUrl,
            apiUrl.contains("gemini-3.5-flash-lite") ? apiUrl.replace("gemini-3.5-flash-lite", "gemini-flash-lite-latest") : apiUrl
        };

        for (String targetUrl : targetUrls) {
            try {
                URL url = new URL(targetUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(15000);
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(finalPayload.getBytes(StandardCharsets.UTF_8));
                }

                int statusCode = conn.getResponseCode();
                if ((statusCode == 503 || statusCode == 429) && !targetUrl.equals(targetUrls[targetUrls.length - 1])) {
                    log.warn("Gemini model at " + targetUrl + " returned " + statusCode + ", attempting fallback...");
                    continue;
                }

                InputStream is = (statusCode >= 200 && statusCode < 300) ? conn.getInputStream() : conn.getErrorStream();
                if (is == null) {
                    return ResponseEntity.ok()
                            .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                            .body("Không nhận được phản hồi từ Gemini API (HTTP " + statusCode + ").");
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder responseStr = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseStr.append(line);
                }
                reader.close();

                if (statusCode >= 200 && statusCode < 300) {
                    return ResponseEntity.ok()
                            .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                            .body(extractTextFromResponse(responseStr.toString()));
                } else if (statusCode == 503) {
                    return ResponseEntity.ok()
                            .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                            .body("Máy chủ Google AI hiện đang quá tải trong giây lát. Bạn vui lòng đợi 5-10 giây rồi thử gửi lại câu hỏi nhé!");
                } else {
                    return ResponseEntity.ok()
                            .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                            .body("Lỗi từ máy chủ Gemini (HTTP " + statusCode + "): " + responseStr.toString());
                }
            } catch (Exception e) {
                log.error("Error communicating with Gemini API", e);
            }
        }

        return ResponseEntity.ok()
                .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                .body("Hệ thống chatbot tạm thời gián đoạn do lưu lượng truy cập cao. Vui lòng thử lại sau vài giây.");
    }

    private String extractTextFromResponse(String jsonResponse) {
        try {
            ObjectMapper om = new ObjectMapper();
            JsonNode rootNode = om.readTree(jsonResponse);
            return rootNode.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            return "Lỗi xử lý phản hồi từ Gemini: " + e.getMessage();
        }
    }
}
