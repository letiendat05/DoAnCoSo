package com.example.food_store.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.food_store.domain.Order;
import com.example.food_store.domain.OrderDetail;
import com.example.food_store.domain.Product;
import com.example.food_store.domain.Role;
import com.example.food_store.domain.User;
import com.example.food_store.repository.OrderDetailRepository;
import com.example.food_store.repository.OrderRepository;
import com.example.food_store.repository.ProductRepository;
import com.example.food_store.repository.RoleRepository;
import com.example.food_store.repository.UserRepository;

@Component
@org.springframework.core.annotation.Order(3)
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ProductRepository productRepository,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           OrderRepository orderRepository,
                           OrderDetailRepository orderDetailRepository,
                           PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedProducts();
        seedOrders();
    }

    private void seedUsers() {
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("Quyền người dùng thông thường");
            userRole = roleRepository.save(userRole);
        }

        if (!userRepository.existsByEmail("user@gmail.com")) {
            User normalUser = new User();
            normalUser.setEmail("user@gmail.com");
            normalUser.setPassword(passwordEncoder.encode("123456"));
            normalUser.setFullName("Nguyễn Văn User");
            normalUser.setRole(userRole);
            normalUser.setProvider("LOCAL");
            normalUser.setAddress("Hà Nội");
            normalUser.setAvatar("");
            normalUser.setPhone("0987654321");
            userRepository.save(normalUser);
            System.out.println("Imported demo user: user@gmail.com / 123456");
        }

        User adminUser = userRepository.findByEmail("letiendat@vti.com");
        if (adminUser != null) {
            adminUser.setFullName("Lê Tiến Đạt");
            adminUser.setPhone("0343866075");
            adminUser.setAddress("Hà Nội");
            userRepository.save(adminUser);
        }
    }
    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        List<Product> products = new ArrayList<>();

        products.add(createProduct(
            "Bánh mỳ nguyên cám",
            35000,
            "1747496175426-Bánh mỳ.jpeg",
            "Bánh mì đen nguyên cám giàu chất xơ, hỗ trợ giảm cân hiệu quả.",
            "Bánh mì nguyên cám được sản xuất từ 100% bột lúa mạch đen tự nhiên, không đường tinh luyện, giàu chất xơ và vitamin nhóm B. Phù hợp cho bữa sáng Eat Clean, người tập gym và chế độ giảm mỡ.",
            50,
            "Hà Nội",
            "ổ",
            "giam-can",
            "thuc-pham-chua-tinh-bot",
            "nguoi-an-kieng"
        ));

        products.add(createProduct(
            "Bột yến mạch Úc",
            85000,
            "1747496280880-Bột yến mạch.jpeg",
            "Yến mạch cán dẹt nguyên chất 100%, bữa sáng lành mạnh và tiện lợi.",
            "Bột yến mạch nhập khẩu trực tiếp từ Úc, chứa hàm lượng Beta-Glucan dồi dào giúp giảm cholesterol, ổn định đường huyết, kiểm soát cân nặng và duy trì năng lượng bền bỉ.",
            40,
            "Nhập khẩu Úc",
            "hộp 500g",
            "giam-can",
            "thuc-pham-chua-tinh-bot",
            "nguoi-an-kieng"
        ));

        products.add(createProduct(
            "Cá hồi Nauy tươi phi lê",
            295000,
            "1747496368238-Cá hồi.jpeg",
            "Cá hồi Nauy tươi phi lê giàu đạm chất lượng cao và Omega-3 tinh khiết.",
            "Thịt cá hồi mềm thơm ngậy đặc trưng, chứa hàm lượng Omega-3 và protein vượt trội, tốt cho tim mạch, phát triển cơ bắp và làm đẹp da. Dễ dàng áp chảo hoặc làm sashimi.",
            30,
            "Nauy",
            "kg",
            "tang-co",
            "thuc-pham-giau-protein",
            "tap-the-hinh"
        ));

        products.add(createProduct(
            "Bắp cải xanh Đà Lạt",
            25000,
            "1747496471293-Cải bắp.png",
            "Bắp cải tươi giòn ngọt tự nhiên, đạt tiêu chuẩn VietGAP.",
            "Bắp cải Đà Lạt cuộn chặt, lá dày xanh mướt, giàu vitamin C và chất chống oxy hóa, hỗ trợ tiêu hóa và giải độc gan hiệu quả. Thích hợp luộc, xào hoặc làm salad giòn ngon.",
            80,
            "Đà Lạt",
            "kg",
            "giam-can",
            "rau",
            "tat-ca"
        ));

        products.add(createProduct(
            "Cải bó xôi hữu cơ",
            35000,
            "1747496680924-Cái bó xôi.jpeg",
            "Rau bina giàu sắt, axit folic và chất chống oxy hóa cao cấp.",
            "Rau bina (cải bó xôi) canh tác theo hướng hữu cơ tại Đà Lạt, chứa dồi dào canxi, sắt, axit folic cần thiết cho phụ nữ mang thai, trẻ nhỏ và người muốn bổ máu, tăng sức đề kháng.",
            45,
            "Đà Lạt",
            "kg",
            "giam-can",
            "rau",
            "phu-nu-co-thai"
        ));

        products.add(createProduct(
            "Cải xoong tươi sạch",
            30000,
            "1747496803058-Cải xoong.jpeg",
            "Cải xoong mát lành, bổ máu và thanh lọc cơ thể tối ưu.",
            "Cải xoong tươi giàu vitamin A, C, canxi và i-ốt, có tác dụng chống oxy hóa mạnh mẽ, thanh nhiệt, hỗ trợ tim mạch và cải thiện sức khỏe làn da.",
            60,
            "Lâm Đồng",
            "kg",
            "giam-can",
            "rau",
            "tat-ca"
        ));

        products.add(createProduct(
            "Cam sành mọng nước",
            45000,
            "1747496933030-Cam.png",
            "Cam sành miền Tây vỏ mỏng, tép vàng mọng nước ngọt thanh.",
            "Cam sành tươi hái tận vườn tại miền Tây Nam Bộ, cung cấp lượng vitamin C dồi dào giúp tăng cường miễn dịch tự nhiên, tái tạo năng lượng và thanh mát tức thì.",
            70,
            "Tiền Giang",
            "kg",
            "tang-can",
            "trai-cay",
            "tat-ca"
        ));

        products.add(createProduct(
            "Cháo bột gạo dinh dưỡng",
            35000,
            "1747496999896-Cháo bột gạo.jpeg",
            "Bột gạo thơm mịn dễ tiêu hóa, chuyên biệt cho bé tập ăn dặm.",
            "Cháo bột gạo organic xay mịn từ hạt gạo ST25 tuyển chọn, bổ sung vi chất dinh dưỡng cần thiết, dễ nấu, tiêu hóa êm dịu, giúp bé tăng cân khỏe mạnh và ngon miệng.",
            40,
            "Sóc Trăng",
            "hộp",
            "tang-can",
            "thuc-pham-chua-tinh-bot",
            "be-duoi-1-tuoi"
        ));

        products.add(createProduct(
            "Chuối tiêu VietGAP",
            28000,
            "1747497140138-Chuối.webp",
            "Chuối tiêu chín tự nhiên, giàu kali và nạp năng lượng nhanh cho gymer.",
            "Chuối tiêu tươi chuẩn VietGAP giàu carbohydrate phức hợp, kali và magiê, chống chuột rút cơ bắp, hỗ trợ phục hồi sức lực nhanh chóng trước và sau khi vận động thể thao.",
            65,
            "Đồng Nai",
            "nải",
            "tang-co",
            "trai-cay",
            "tap-the-hinh"
        ));

        products.add(createProduct(
            "Dầu ô liu nguyên chất Extra Virgin",
            165000,
            "1747497194990-Dầu oliu.jpeg",
            "Dầu ô liu ép nguội nguyên chất Tây Ban Nha, tốt cho tim mạch và Eat Clean.",
            "Dầu ô liu siêu nguyên chất chiết xuất từ mẻ ô liu đầu tiên bằng phương pháp ép lạnh cơ học, giàu chất béo không bão hòa đơn và polyphenol chống lão hóa.",
            35,
            "Tây Ban Nha",
            "chai 500ml",
            "giam-can",
            "thuc-uong",
            "nguoi-an-kieng"
        ));

        products.add(createProduct(
            "Dâu tây giống Nhật Đà Lạt",
            140000,
            "1747497322365-Dâu tây.jpeg",
            "Dâu tây đỏ mọng thơm ngọt, giàu vitamin C và chất chống lão hóa.",
            "Dâu tây giống Nhật Bản trồng theo tiêu chuẩn thủy canh tại Đà Lạt, trái to mọng nước, hương thơm dịu nhẹ ngọt ngào, cung cấp vitamin C và anthocyanin dồi dào.",
            25,
            "Đà Lạt",
            "hộp 500g",
            "giam-can",
            "trai-cay",
            "dan-van-phong"
        ));

        products.add(createProduct(
            "Gạo lứt huyết rồng hữu cơ",
            55000,
            "1747497376759-Gạo lứt.jpeg",
            "Gạo lứt dẻo thơm, chỉ số GI thấp hỗ trợ kiểm soát đường huyết và cân nặng.",
            "Gạo lứt đỏ huyết rồng hữu cơ giữ trọn lớp cám gạo giàu vitamin nhóm B, khoáng chất vi lượng và chất xơ hòa tan. Món ăn tinh bột vàng cho chế độ giảm cân và người tiểu đường.",
            90,
            "Điện Biên",
            "túi 1kg",
            "giam-can",
            "thuc-pham-chua-tinh-bot",
            "nguoi-an-kieng"
        ));

        products.add(createProduct(
            "Hạnh nhân rang mộc tự nhiên",
            155000,
            "1747497422589-Hạnh Nhân.jpeg",
            "Hạnh nhân nhập khẩu Mỹ rang mộc giòn thơm, bổ sung protein và chất béo tốt.",
            "Hạnh nhân nhập khẩu Mỹ không ướp muối, giàu protein thực vật, vitamin E, canxi và chất béo không bão hòa tốt cho não bộ, cơ bắp và tim mạch.",
            50,
            "Mỹ",
            "hũ 500g",
            "tang-co",
            "thuc-pham-giau-protein",
            "tap-the-hinh"
        ));

        products.add(createProduct(
            "Hạt chia organic cao cấp",
            110000,
            "1747497616934-Hạt.jpg",
            "Hạt chia đen Nam Mỹ siêu thực phẩm giàu Omega-3 và chất xơ.",
            "Hạt chia hữu cơ cao cấp giúp tạo cảm giác no lâu, hỗ trợ hệ tiêu hóa hoạt động trơn tru, bổ sung khoáng chất và Omega-3 thực vật quý giá cho cơ thể.",
            45,
            "Peru",
            "hũ 250g",
            "giam-can",
            "thuc-pham-giau-protein",
            "dan-van-phong"
        ));

        products.add(createProduct(
            "Khoai lang mật Đà Lạt",
            38000,
            "1747497663680-Khoai lang.jpeg",
            "Khoai lang mật dẻo quánh thơm nức, tinh bột chuyển hóa chậm lý tưởng.",
            "Khoai lang mật tươi ngon từ vùng đất đỏ bazan Đà Lạt, khi nướng hoặc hấp mật chảy sánh mịn ngọt tự nhiên. Nguồn tinh bột hấp thu chậm giúp kiểm soát mỡ thừa tối ưu.",
            100,
            "Đà Lạt",
            "kg",
            "giam-can",
            "cu",
            "nguoi-an-kieng"
        ));

        products.add(createProduct(
            "Kiwi xanh New Zealand",
            125000,
            "1747497719855-Kiwi.jpeg",
            "Kiwi xanh nhập khẩu chua ngọt sảng khoái, hàm lượng vitamin C vượt bậc.",
            "Kiwi xanh New Zealand tươi giòn, chứa enzyme actinidin hỗ trợ tiêu hóa thức ăn giàu protein cùng lượng vitamin C cao gấp đôi cam, giúp da căng mịn và tiêu hao mỡ thừa.",
            30,
            "New Zealand",
            "kg",
            "giam-can",
            "trai-cay",
            "tat-ca"
        ));

        products.add(createProduct(
            "Nước ép cần tây nguyên chất Detox",
            42000,
            "1747497775219-Nước ép cần tây.jpeg",
            "Nước ép cần tây tươi 100% thanh lọc mỡ thừa và sáng da.",
            "Nước ép cần tây tươi hữu cơ nguyên chất, không pha đường, giàu chất chống oxy hóa và khoáng chất kiềm tính, giúp đào thải độc tố và đánh tan mỡ bụng hiệu quả.",
            30,
            "Food Store Organic",
            "chai 350ml",
            "giam-can",
            "thuc-uong",
            "dan-van-phong"
        ));

        products.add(createProduct(
            "Phô mai tươi tự nhiên",
            85000,
            "1747497829523-phomat.png",
            "Phô mai giàu canxi và đạm dễ hấp thu, hỗ trợ phát triển chiều cao vượt trội.",
            "Phô mai tươi làm từ sữa bò thanh trùng nguyên chất, mềm béo tự nhiên, bổ sung canxi và vitamin D tối đa cho bé trong giai đoạn phát triển thể chất và chiều cao.",
            40,
            "Pháp",
            "hộp",
            "tang-chieu-cao",
            "thuc-pham-giau-protein",
            "be-duoi-1-tuoi"
        ));

        products.add(createProduct(
            "Bơ sáp 034 Tây Nguyên",
            65000,
            "1747497909082-Quả bơ.jpeg",
            "Bơ sáp dẻo vàng ươm, nguồn chất béo đơn không bão hòa tốt cho sức khỏe.",
            "Bơ sáp 034 Đắk Lắk dẻo bùi, thịt dày hạt lép, giàu axit oleic giúp giảm cholesterol xấu, bảo vệ tim mạch và duy trì vóc dáng thon gọn cho người ăn Eat Clean.",
            60,
            "Đắk Lắk",
            "kg",
            "giam-can",
            "trai-cay",
            "tap-the-hinh"
        ));

        products.add(createProduct(
            "Rau củ nghiền dinh dưỡng cho bé",
            40000,
            "1747497974884-Rau củ nghiền.jpeg",
            "Hỗn hợp củ quả hữu cơ nghiền mịn, bổ sung vitamin tự nhiên cho bé.",
            "Rau củ nghiền kết hợp từ cà rốt, bí đỏ và khoai lang hữu cơ nghiền mịn theo công thức dinh dưỡng nhi khoa, giúp hệ tiêu hóa bé làm quen với chất xơ và kích thích vị giác.",
            35,
            "Đà Lạt",
            "hộp 200g",
            "tang-chieu-cao",
            "cu",
            "be-duoi-1-tuoi"
        ));

        products.add(createProduct(
            "Salad rau củ tổng hợp tươi giòn",
            48000,
            "1747498028458-Salad rau xanh.jpeg",
            "Khay salad rau tươi chuẩn sạch ăn liền kèm sốt mè rang thanh vị.",
            "Khay salad tươi giòn gồm xà lách thủy canh, cà chua bi, dưa chuột và bắp ngọt kèm sốt mè rang Nhật Bản tiện lợi, thích hợp cho bữa trưa văn phòng thanh nhẹ.",
            40,
            "Đà Lạt",
            "hộp",
            "giam-can",
            "rau",
            "dan-van-phong"
        ));

        products.add(createProduct(
            "Sữa chua Hy Lạp không đường",
            42000,
            "1747498085334-Sữa chua.jpeg",
            "Sữa chua Hy Lạp đậm đặc gấp đôi protein, tăng cường lợi khuẩn đường ruột.",
            "Sữa chua Hy Lạp lên men tự nhiên theo quy trình lọc váng sữa truyền thống, kết cấu đặc sánh mịn, hàm lượng protein cao gấp đôi sữa chua thường, lý tưởng cho gymer.",
            50,
            "Ba Vì",
            "lốc 4 hộp",
            "giam-can",
            "thuc-uong",
            "tap-the-hinh"
        ));

        products.add(createProduct(
            "Sữa công thức tăng trưởng",
            380000,
            "1747498155953-Sữa công thức.jpeg",
            "Sữa bột công thức bổ sung DHA, canxi và kháng thể IgG cho bé.",
            "Sữa công thức dinh dưỡng cao cấp bổ sung DHA, ARA, Canxi và kẽm giúp bé phát triển trí não toàn diện, tăng chiều cao vượt trội và củng cố hệ miễn dịch khỏe mạnh.",
            20,
            "Nhật Bản",
            "lon 900g",
            "tang-chieu-cao",
            "thuc-uong",
            "be-duoi-1-tuoi"
        ));

        products.add(createProduct(
            "Sữa tươi thanh trùng ít đường",
            34000,
            "1747498221958-Sữa ít đường.jpeg",
            "Sữa tươi thanh trùng 100% sữa bò tươi nguyên chất từ trang trại xanh.",
            "Sữa tươi thanh trùng công nghệ xử lý nhiệt nhẹ giữ nguyên vẹn các vi chất tự nhiên, vitamin A, D và canxi sinh học, ít đường nhẹ nhàng, phù hợp cho mọi lứa tuổi.",
            60,
            "Mộc Châu",
            "hộp 1L",
            "tang-chieu-cao",
            "thuc-uong",
            "tat-ca"
        ));

        products.add(createProduct(
            "Súp lơ xanh hữu cơ",
            38000,
            "1747498293032-Súp lơ.jpeg",
            "Bông cải xanh Đà Lạt giòn ngon, vua các loại rau hỗ trợ đốt mỡ.",
            "Súp lơ xanh canh tác hữu cơ chứa sulforaphane chống oxy hóa mạnh mẽ, giàu chất xơ và vitamin C giúp tăng cường trao đổi chất, hỗ trợ giảm béo bụng và săn chắc cơ.",
            55,
            "Đà Lạt",
            "kg",
            "giam-can",
            "rau",
            "nguoi-an-kieng"
        ));

        products.add(createProduct(
            "Thịt bò thăn nạc Úc",
            260000,
            "1747498354121-Thịt bò nạc.jpeg",
            "Thịt bò thăn nạc nhập khẩu Úc mềm ngọt, giàu protein và sắt sinh học.",
            "Thịt thăn bò Úc chuẩn mát cao cấp, lượng mỡ cực thấp, dồi dào protein và kẽm, sắt sinh học giúp tái tạo sợi cơ nhanh chóng sau các buổi tập luyện cường độ cao.",
            35,
            "Úc",
            "kg",
            "tang-co",
            "thuc-pham-giau-protein",
            "tap-the-hinh"
        ));

        products.add(createProduct(
            "Thịt thăn heo sạch MeatDeli",
            135000,
            "1747498422701-Thịt heo tươi ngon.png",
            "Thịt thăn heo chuẩn thịt mát châu Âu, thớ thịt mềm ngọt thơm lành.",
            "Thịt thăn heo tươi ngon chuẩn công nghệ thịt mát châu Âu, kiểm định nghiêm ngặt không chất tăng trọng, thớ thịt mềm mịn, cung cấp nguồn đạm dồi dào cho bữa cơm gia đình.",
            45,
            "MeatDeli",
            "kg",
            "tang-co",
            "thuc-pham-giau-protein",
            "tat-ca"
        ));

        products.add(createProduct(
            "Trà xanh hữu cơ không đường",
            25000,
            "1747498629424-Trà xanh không đường.jpeg",
            "Trà xanh Thái Nguyên ủ lạnh, dồi dào EGCG chống oxy hóa và tỉnh táo.",
            "Nước trà xanh nguyên chất hái từ búp trà non Tân Cương Thái Nguyên, không đường, chứa hoạt chất EGCG thúc đẩy đốt cháy calo và đem lại tinh thần sảng khoái tập trung.",
            50,
            "Thái Nguyên",
            "chai 450ml",
            "giam-can",
            "thuc-uong",
            "dan-van-phong"
        ));

        products.add(createProduct(
            "Nho đen không hạt Ninh Thuận",
            88000,
            "1747498723303-Trái cây nho.jpeg",
            "Nho đen giòn ngọt không hạt, giàu resveratrol tốt cho tim mạch.",
            "Nho đen Ninh Thuận quả mọng giòn ngọt, vỏ mỏng không hạt, chứa hàm lượng resveratrol cao giúp trẻ hóa tế bào, hỗ trợ tuần hoàn máu và nạp năng lượng tự nhiên.",
            40,
            "Ninh Thuận",
            "kg",
            "tang-can",
            "trai-cay",
            "tat-ca"
        ));

        products.add(createProduct(
            "Trứng gà ta thả vườn sạch",
            42000,
            "1747498795092-Trứng.jpeg",
            "Trứng gà ta lòng đỏ đậm đà, nguồn protein hoàn chỉnh giá trị sinh học cao.",
            "Trứng gà ta nuôi thả tự nhiên trong vườn đồi, lòng đỏ to đậm thơm bùi, cung cấp đầy đủ các axit amin thiết yếu, choline tốt cho não bộ và cơ bắp người tập luyện.",
            70,
            "Hà Nam",
            "vỉ 10 quả",
            "tang-co",
            "thuc-pham-giau-protein",
            "tap-the-hinh"
        ));

        products.add(createProduct(
            "Ức gà phi lê tươi CP",
            78000,
            "1747498873816-Ức gà.jpeg",
            "Ức gà tươi phi lê không da không xương, thực phẩm vàng cho dân tập gym.",
            "Ức gà phi lê tươi sạch đạt chuẩn an toàn vệ sinh thực phẩm quốc tế, hàm lượng đạm cao vượt trội và tỷ lệ chất béo gần như bằng 0, thực phẩm không thể thiếu cho thực đơn tăng cơ giảm mỡ.",
            60,
            "CP Việt Nam",
            "kg",
            "tang-co",
            "thuc-pham-giau-protein",
            "tap-the-hinh"
        ));

        products.add(createProduct(
            "Việt quất tươi Mỹ nhập khẩu",
            115000,
            "1747581397003-Việt quất.jpeg",
            "Việt quất tươi nhập khẩu mọng quả, siêu thực phẩm tăng cường trí nhớ.",
            "Việt quất tươi nhập khẩu trực tiếp từ Mỹ, quả mọng phủ lớp phấn tự nhiên, vị chua ngọt thanh mát, đứng đầu về nồng độ chất chống oxy hóa, hỗ trợ bảo vệ thị lực và cải thiện vóc dáng.",
            30,
            "Mỹ",
            "hộp 125g",
            "giam-can",
            "trai-cay",
            "tat-ca"
        ));

        productRepository.saveAll(products);
        System.out.println("Imported " + products.size() + " sample products successfully!");
    }

    private Product createProduct(String name,
                                  double price,
                                  String image,
                                  String shortDesc,
                                  String detailDesc,
                                  long quantity,
                                  String source,
                                  String unit,
                                  String target,
                                  String type,
                                  String customerTarget) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setImage(image);
        p.setShortDesc(shortDesc);
        p.setDetailDesc(detailDesc);
        p.setQuantity(quantity);
        p.setSource(source);
        p.setUnit(unit);
        p.setTarget(target);
        p.setType(type);
        p.setCustomerTarget(customerTarget);
        return p;
    }

    private void seedOrders() {
        if (orderRepository.count() > 0) {
            return;
        }

        User user = userRepository.findByEmail("user@gmail.com");
        List<Product> products = productRepository.findAll();
        if (user == null || products.isEmpty()) {
            return;
        }

        // Order 1: Hoàn thành, COD
        createOrder(user, "Nguyễn Văn User", "Số 15 Cầu Giấy, Hà Nội", "0987654321",
                "Hoàn thành", "COD", "Thanh toán thành công", "UNKNOWN",
                List.of(new OrderItem(products.get(0), 2), new OrderItem(products.get(1), 1)));

        // Order 2: Hoàn thành, VNPAY
        if (products.size() > 5) {
            createOrder(user, "Trần Mai Anh", "Times City, 458 Minh Khai, Hà Nội", "0912345678",
                    "Hoàn thành", "VNPAY", "Thanh toán thành công", "VNPAY20261019001",
                    List.of(new OrderItem(products.get(2), 2), new OrderItem(products.get(3), 1)));
        }

        // Order 3: Đang vận chuyển, COD
        if (products.size() > 7) {
            createOrder(user, "Lê Hoàng Long", "Keangnam Landmark, Nam Từ Liêm, Hà Nội", "0977889900",
                    "Đang vận chuyển", "COD", "Thanh toán khi nhận hàng", "UNKNOWN",
                    List.of(new OrderItem(products.get(4), 1), new OrderItem(products.get(5), 2)));
        }

        // Order 4: Đang vận chuyển, VNPAY
        if (products.size() > 10) {
            createOrder(user, "Nguyễn Văn User", "Số 15 Cầu Giấy, Hà Nội", "0987654321",
                    "Đang vận chuyển", "VNPAY", "Thanh toán thành công", "VNPAY20261020002",
                    List.of(new OrderItem(products.get(6), 3), new OrderItem(products.get(0), 1)));
        }

        // Order 5: Chưa xử lý, COD
        if (products.size() > 12) {
            createOrder(user, "Phạm Thu Hương", "68 Đội Cấn, Ba Đình, Hà Nội", "0905123456",
                    "Chưa xử lý", "COD", "Chưa thanh toán", "UNKNOWN",
                    List.of(new OrderItem(products.get(7), 2)));
        }

        System.out.println("Imported demo orders successfully!");
    }

    private static class OrderItem {
        final Product product;
        final long quantity;
        OrderItem(Product product, long quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    private void createOrder(User user, String receiverName, String receiverAddress, String receiverPhone,
                             String status, String paymentMethod, String paymentStatus, String paymentRef,
                             List<OrderItem> items) {
        Order order = new Order();
        order.setUser(user);
        order.setReceiverName(receiverName);
        order.setReceiverAddress(receiverAddress);
        order.setReceiverPhone(receiverPhone);
        order.setStatus(status);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(paymentStatus);
        order.setPaymentRef(paymentRef);

        double total = 0;
        for (OrderItem item : items) {
            total += item.product.getPrice() * item.quantity;
        }
        order.setTotalPrice(total);
        order = orderRepository.save(order);

        for (OrderItem item : items) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.product);
            detail.setPrice(item.product.getPrice());
            detail.setQuantity(item.quantity);
            orderDetailRepository.save(detail);
        }
    }
}
