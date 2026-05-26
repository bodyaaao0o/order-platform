//package com.orderplatform.inventory_service;
//
//import com.orderplatform.inventory_service.event.InventoryReserveRequestEvent;
//import com.orderplatform.inventory_service.kafka.InventoryReserveRequestConsumer;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/test")
//public class TestController {
//
//    private final InventoryReserveRequestConsumer consumer;
//
//    @PostMapping("/duplicate")
//    public void testDuplicateEvent() {
//
//        InventoryReserveRequestEvent event =
//                new InventoryReserveRequestEvent(
//
//                        UUID.fromString(
//                                "11111111-1111-1111-1111-111111111111"
//                        ),
//
//                        1L,
//
//                        "MAC-1",
//
//                        2
//                );
//
//        consumer.consume(event);
//
//        consumer.consume(event);
//    }
//}
