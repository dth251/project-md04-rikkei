package re.edu.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import re.edu.orderservice.client.InventoryClient;
import re.edu.orderservice.dto.OrderRequest;
import re.edu.orderservice.dto.OrderResponse;
import re.edu.orderservice.dto.ProductResponse;
import re.edu.orderservice.entity.Order;
import re.edu.orderservice.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public OrderResponse createOrder(OrderRequest orderRequest) {
        ProductResponse product = inventoryClient.getProductById(orderRequest.getProductId());

        if (product == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + orderRequest.getProductId());
        }

        if (product.getQuantity() < orderRequest.getQuantity()) {
            throw new RuntimeException("Số lượng tồn kho không đủ. Hiện có: " + product.getQuantity() + ", Yêu cầu: " + orderRequest.getQuantity());
        }

        BigDecimal totalPrice = orderRequest.getTotalPrice() != null
                ? orderRequest.getTotalPrice()
                : product.getPrice().multiply(BigDecimal.valueOf(orderRequest.getQuantity()));

        Order order = Order.builder()
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .status("COMPLETED")
                .totalPrice(totalPrice)
                .build();

        Order savedOrder = orderRepository.save(order);
        logger.info("Đã tạo đơn hàng thành công với ID: {} và Trạng thái: COMPLETED", savedOrder.getId());

        return mapToResponse(savedOrder);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
        return mapToResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .build();
    }
}
