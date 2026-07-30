# Project MD04 - Microservices Architecture

## Task 3: Báo Cáo Phân Tích Kiến Trúc Microservices

### 1. Tại sao API Gateway không nên gọi trực tiếp IP/Port cố định của Microservice?
Theo em hiểu, trong hệ thống Microservices, các dịch vụ không đứng yên một chỗ mà có thể thay đổi IP hoặc Port liên tục (ví dụ khi server khởi động lại, hoặc khi dùng Docker/Kubernetes thì các instance bị tắt đi mở lại sẽ nhận IP mới).

Nếu API Gateway gán cứng IP/Port (ví dụ `http://192.168.1.10:8081`):
- Khi dịch vụ đổi IP hoặc đổi Port, Gateway sẽ lập tức bị lỗi kết nối không gọi được nữa, lúc nào cũng phải sửa cấu hình rồi restart lại Gateway rất phiền.
- Khi cần chạy nhiều máy chủ (Scale out) để gánh tải, Gateway sẽ không biết phân phối lượt truy cập cho các máy mới thế nào.

Do đó, Gateway nên gọi qua tên dịch vụ (ví dụ `lb://ORDER-SERVICE`) kết hợp với Eureka Server để tự động lấy danh sách IP/Port đang sống.

---

### 2. Làm thế nào để Scale Order Service khi lượng request tăng đột biến mà không cần sửa cấu hình Gateway?
Khi lượng khách hàng đặt hàng tăng vọt (ví dụ ngày săn sale), em sẽ xử lý như sau:

- **Bật thêm nhiều instance của Order Service**: Cho chạy thêm các bản sao của Order Service trên các port khác nhau (như 8083, 8084, 8085...).
- **Tự động đăng ký với Eureka**: Ngay khi các instance mới này bật lên, chúng sẽ tự báo cho Eureka Server biết: *"Tôi là ORDER-SERVICE, tôi đang chạy ở IP:Port này nè"*.
- **Gateway tự điều hướng tải**: API Gateway đã cài sẵn `lb://ORDER-SERVICE`. Gateway chỉ cần hỏi Eureka xem Order Service đang có những máy nào chạy, rồi tự chia đều request (Round-Robin) cho các máy đó. 

=> Kết quả là hệ thống gánh tải tốt hơn nhiều mà **không cần sửa một dòng code hay restart lại API Gateway**.

- **Về phía Kafka**: Các instance Order Service mới sẽ tham gia vào cùng nhóm `order-group` để cùng nhau chia việc đọc thông báo từ Kafka, giúp tăng tốc độ xử lý phản hồi từ Inventory Service.

---

### 3. So sánh giữa OpenFeign (Đồng bộ) và Kafka (Bất đồng bộ) trong bài toán Đặt hàng

Em rút ra sự khác biệt giữa 2 cách này như sau:

- **Về cơ chế làm việc**:
  - **OpenFeign (Đồng bộ - Synchronous)**: Giống như gọi điện thoại trực tiếp. Khi Order Service gọi Inventory Service để trừ kho, nó phải đứng chờ Inventory Service làm xong và trả lời thì mới báo cho khách hàng được.
  - **Kafka (Bất đồng bộ - Asynchronous)**: Giống như gửi tin nhắn. Order Service chỉ cần đăng đơn hàng lên Kafka (topic) rồi báo ngay cho khách hàng là *"Đã nhận đơn, trạng thái PENDING"*. Inventory Service rảnh lúc nào thì vào Kafka đọc tin nhắn và trừ kho lúc đó.

- **Về độ phụ thuộc (Coupling)**:
  - **OpenFeign**: Làm cho Order Service phụ thuộc chặt chẽ vào Inventory Service (Gắn kết chặt - Tight Coupling).
  - **Kafka**: Giúp 2 dịch vụ độc lập hoàn toàn với nhau (Gắn kết lỏng - Loose Coupling), Order Service không cần quan tâm khi nào Inventory Service xử lý xong.

- **Về khả năng chịu lỗi (Resilience)**:
  - **OpenFeign**: Nếu Inventory Service bị ngơ hoặc sập, toàn bộ luồng đặt hàng sẽ bị tắc nghẽn, sập theo hoặc báo lỗi cho khách hàng.
  - **Kafka**: Nếu Inventory Service sập, tin nhắn vẫn nằm an toàn trên Kafka Topic. Khi nào Inventory Service chạy lại thì nó đọc tiếp và xử lý bình thường, đơn hàng không bị mất.

- **Về tốc độ và trải nghiệm khách hàng (Latency & UX)**:
  - **OpenFeign**: Khách hàng phải chờ lâu hơn vì phản hồi chậm (tổng thời gian = Order Service + Inventory Service).
  - **Kafka**: Trả lời khách hàng cực nhanh (vài mili-giây), việc xử lý kho diễn ra ngầm ở phía sau.

- **Khi nào nên dùng cách nào?**:
  - **OpenFeign**: Thích hợp cho các thao tác đọc dữ liệu cần kết quả ngay lập tức (xem chi tiết sản phẩm, tìm kiếm, lấy số dư tài khoản).
  - **Kafka**: Thích hợp cho các giao dịch phức tạp, ghi dữ liệu (Write-heavy) như luồng đặt hàng, thanh toán, gửi email thông báo.
