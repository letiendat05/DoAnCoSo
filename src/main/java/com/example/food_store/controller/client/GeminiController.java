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

        // 1. Trich xuat cau hoi nguoi dung
        String userText = "";
        try {
            JsonNode root = objectMapper.readTree(requestBody);
            userText = root.path("contents").path(0).path("parts").path(0).path("text").asText();
        } catch (Exception ignored) {
            userText = requestBody;
        }

        // 2. Lay danh muc san pham tu Database
        List<Product> products = null;
        StringBuilder catalog = new StringBuilder();
        try {
            products = this.productRepository.findAll();
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

        // Neu chua cau hinh Gemini API key thuc su, tra ve goi y thong minh tu kho hang
        boolean isPlaceholderKey = apiUrl == null || apiUrl.isBlank()
                || apiUrl.contains("demo-key")
                || apiUrl.contains("YOUR_API_KEY")
                || apiUrl.contains("YOUR_GEMINI_API_KEY");

        if (isPlaceholderKey) {
            return ResponseEntity.ok()
                    .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                    .body(generateSmartCatalogSuggestion(userText, products));
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
                            .body(generateSmartCatalogSuggestion(userText, products));
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
                    log.warn("Lỗi từ máy chủ Gemini (HTTP " + statusCode + "): " + responseStr.toString());
                    return ResponseEntity.ok()
                            .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                            .body(generateSmartCatalogSuggestion(userText, products));
                }
            } catch (Exception e) {
                log.error("Error communicating with Gemini API", e);
            }
        }

        return ResponseEntity.ok()
                .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                .body(generateSmartCatalogSuggestion(userText, products));
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

    private String generateSmartCatalogSuggestion(String query, List<Product> products) {
        String lower = query != null ? query.toLowerCase() : "";

        if (lower.contains("giam can") || lower.contains("giảm cân") || lower.contains("eat clean") || lower.contains("healthy")) {
            return "🥗 **GỢI Ý THỰC ĐƠN GIẢM CÂN LÀNH MẠNH (EAT CLEAN)** 🥑\n\n"
                    + "Để giảm mỡ hiệu quả và duy trì năng lượng bền bỉ, Food Store gợi ý bạn thực đơn từ các nguyên liệu tươi sạch đang có sẵn tại cửa hàng:\n\n"
                    + "🌅 **Bữa sáng (Năng lượng nhẹ, no lâu):**\n"
                    + "• **Bánh mỳ nguyên cám** (35.000 đ/ổ) hoặc **Bột yến mạch Úc** (85.000 đ/hộp)\n"
                    + "• Kết hợp cùng **Sữa chua Hy Lạp** (42.000 đ/lốc) và **Dâu tây Đà Lạt** (140.000 đ/hộp)\n"
                    + "💡 *Lợi ích:* Tinh bột chuyển hóa chậm, giàu đạm và chất xơ giúp no lâu, tránh thèm ngọt.\n\n"
                    + "☀️ **Bữa trưa (Cân bằng Đạm nạc & Rau xanh):**\n"
                    + "• **Ức gà phi lê tươi CP** (78.000 đ/kg) áp chảo hoặc luộc\n"
                    + "• Ăn kèm **Gạo lứt huyết rồng** (55.000 đ/túi 1kg) và **Súp lơ xanh hữu cơ** (38.000 đ/kg)\n"
                    + "💡 *Lợi ích:* Đốt mỡ thừa tự nhiên, giữ cơ săn chắc và hỗ trợ tiêu hóa êm dịu.\n\n"
                    + "☕ **Bữa phụ chiều (Detox & Giữ dáng):**\n"
                    + "• 1 ly **Nước ép cần tây nguyên chất Detox** (42.000 đ/chai) hoặc 1 nắm **Hạt chia organic** (110.000 đ/hũ).\n\n"
                    + "🌙 **Bữa tối (Thanh đạm, nhẹ bụng):**\n"
                    + "• **Salad rau củ tổng hợp tươi giòn** (48.000 đ/hộp) trộn **Dầu ô liu Extra Virgin** (165.000 đ/chai)\n"
                    + "• Thêm 1/2 quả **Bơ sáp 034 Tây Nguyên** (65.000 đ/kg)\n\n"
                    + "👉 *Tất cả sản phẩm trên đều có sẵn tại Food Store. Bạn hãy bấm chọn để thêm ngay vào giỏ hàng nhé!*";
        }

        if (lower.contains("tang co") || lower.contains("tăng cơ") || lower.contains("gymer") || lower.contains("the hinh") || lower.contains("thể hình") || lower.contains("protein")) {
            return "💪 **THỰC ĐƠN TĂNG CƠ GIÀU PROTEIN CHO GYMER** 🏋️‍♂️\n\n"
                    + "Food Store tư vấn bạn danh sách thực phẩm vàng giàu đạm sinh học cao giúp phát triển sợi cơ tối đa:\n\n"
                    + "🥩 **Nguồn Protein cao cấp tại quán:**\n"
                    + "• **Cá hồi Nauy tươi phi lê** (295.000 đ/kg): Giàu Omega-3 và đạm tinh khiết, kháng viêm cơ bắp.\n"
                    + "• **Thịt bò thăn nạc Úc** (260.000 đ/kg): Dồi dào Creatine tự nhiên và sắt sinh học, tăng sức mạnh buổi tập.\n"
                    + "• **Ức gà phi lê tươi CP** (78.000 đ/kg): Nguồn protein kinh điển, hàm lượng chất béo cực thấp.\n"
                    + "• **Trứng gà ta sạch** (42.000 đ/vỉ 10 quả): Nguồn amino acid hoàn hảo cho cơ bắp.\n\n"
                    + "🍠 **Nguồn Tinh bột nạp năng lượng tập luyện:**\n"
                    + "• **Khoai lang mật Đà Lạt** (38.000 đ/kg) hoặc **Gạo lứt đỏ** (55.000 đ/túi 1kg).\n\n"
                    + "🍌 **Bữa phụ trước và sau tập (Pre / Post Workout):**\n"
                    + "• 1-2 quả **Chuối tiêu VietGAP** (28.000 đ/nải) bổ sung Kali ngừa chuột rút.\n"
                    + "• 1 hộp **Sữa chua Hy Lạp** (42.000 đ) + **Hạnh nhân rang mộc** (155.000 đ/hũ 500g).\n\n"
                    + "👉 *Mời bạn ghé danh mục Sản phẩm để đặt mua các nguyên liệu tăng cơ tươi ngon hôm nay!*";
        }

        if (lower.contains("trai cay") || lower.contains("trái cây") || lower.contains("hoa qua") || lower.contains("hoa quả")) {
            return "🍎 **DANH SÁCH TRÁI CÂY TƯƠI SẠCH HÔM NAY TẠI FOOD STORE** 🍇\n\n"
                    + "Cửa hàng hiện có các loại trái cây tươi ngon chuẩn VietGAP và nhập khẩu tuyển chọn:\n"
                    + "• **Dâu tây giống Nhật Đà Lạt**: 140.000 đ/hộp 500g (Đỏ mọng, thơm ngọt tự nhiên)\n"
                    + "• **Việt quất tươi Mỹ nhập khẩu**: 115.000 đ/hộp 125g (Siêu thực phẩm chống lão hóa)\n"
                    + "• **Kiwi xanh New Zealand**: 125.000 đ/kg (Chua ngọt thanh mát, giàu enzyme tiêu hóa)\n"
                    + "• **Bơ sáp 034 Tây Nguyên**: 65.000 đ/kg (Thịt dẻo vàng ươm, béo bùi)\n"
                    + "• **Cam sành mọng nước**: 45.000 đ/kg (Vỏ mỏng tép vàng, giàu vitamin C)\n"
                    + "• **Nho đen không hạt Ninh Thuận**: 88.000 đ/kg (Giòn ngọt, giàu Resveratrol)\n"
                    + "• **Chuối tiêu VietGAP**: 28.000 đ/nải (Chín tự nhiên, thơm dẻo)\n\n"
                    + "👉 *Bạn có thể bấm vào sản phẩm bất kỳ để xem chi tiết và thêm vào giỏ hàng!*";
        }

        if (lower.contains("an dam") || lower.contains("ăn dặm") || lower.contains("be") || lower.contains("bé") || lower.contains("tre em")) {
            return "👶 **THỰC PHẨM ĂN DẶM & DINH DƯỠNG CHO BÉ TẠI FOOD STORE** 🍼\n\n"
                    + "Dành riêng cho các mẹ bỉm sữa chăm sóc sức khỏe và thể chất bé yêu:\n"
                    + "• **Cháo bột gạo dinh dưỡng** (35.000 đ/hộp): Xay từ gạo ST25 organic, êm bụng, dễ hấp thu.\n"
                    + "• **Rau củ nghiền dinh dưỡng cho bé** (40.000 đ/hộp): Cà rốt, bí đỏ, khoai lang nghiền mịn chuẩn nhi khoa.\n"
                    + "• **Phô mai tươi tự nhiên** (85.000 đ/hộp): Bổ sung Canxi và Vitamin D3 phát triển chiều cao.\n"
                    + "• **Sữa công thức tăng trưởng** (380.000 đ/lon 900g): Bổ sung DHA, kẽm và kháng thể cho bé khỏe mạnh.\n"
                    + "• **Cải bó xôi hữu cơ** (35.000 đ/kg): Nấu súp hoặc cháo bổ sung sắt và axit folic.\n\n"
                    + "👉 *Sản phẩm đạt kiểm định an toàn vệ sinh thực phẩm nghiêm ngặt, an tâm tuyệt đối cho bé yêu!*";
        }

        // Mặc định hoặc câu hỏi tổng hợp: "Hôm nay ăn gì?", "Gợi ý món"
        return "🍲 **GỢI Ý THỰC ĐƠN DINH DƯỠNG TRỌN VẸN HÔM NAY TẠI FOOD STORE** 🍱\n\n"
                + "Xin chào bạn! Food Store xin gợi ý thực đơn 3 bữa cân bằng dưỡng chất từ các nguyên liệu sạch của quán:\n\n"
                + "☀️ **Bữa sáng khởi đầu ngày mới:**\n"
                + "• **Bánh mỳ nguyên cám** (35.000 đ/ổ) kẹp **Trứng gà ta** (42.000 đ/vỉ) và xà lách tươi.\n"
                + "• Uống kèm 1 ly **Sữa tươi thanh trùng ít đường** (34.000 đ/hộp 1L).\n\n"
                + "🍛 **Bữa trưa đậm đà, đủ chất:**\n"
                + "• **Thịt thăn heo sạch MeatDeli** (135.000 đ/kg) kho hoặc **Thịt bò thăn nạc Úc** (260.000 đ/kg) xào cần tỏi.\n"
                + "• Canh **Bắp cải xanh Đà Lạt** (25.000 đ/kg) hoặc **Cải xoong tươi sạch** (30.000 đ/kg).\n"
                + "• Cơm nóng từ **Gạo lứt huyết rồng** (55.000 đ/túi).\n\n"
                + "🌙 **Bữa tối nhẹ nhàng, ấm cúng:**\n"
                + "• **Cá hồi Nauy áp chảo** (295.000 đ/kg) hoặc **Ức gà sốt chua ngọt** (78.000 đ/kg).\n"
                + "• Đĩa **Salad rau củ tổng hợp tươi giòn** (48.000 đ/hộp).\n"
                + "• Tráng miệng bằng **Cam sành mọng nước** (45.000 đ/kg) hoặc **Nho đen không hạt** (88.000 đ/kg).\n\n"
                + "👉 *Bạn muốn tư vấn thêm về thực đơn nào (Giảm cân, Tăng cơ, Ăn dặm...), hãy bấm nút gợi ý bên dưới nhé!*";
    }
}
