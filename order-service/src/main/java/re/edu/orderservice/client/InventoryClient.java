package re.edu.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import re.edu.orderservice.dto.ProductResponse;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {

    @GetMapping("/api/inventory/{productId}")
    ProductResponse getProductById(@PathVariable("productId") Long productId);
}
