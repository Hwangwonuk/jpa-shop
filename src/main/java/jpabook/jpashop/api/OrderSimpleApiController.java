/*
 * Created by Wonuk Hwang on 2023/01/18
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by infra Team <wonuk_hwang@bigin.io>, 2023/01/18
 */
package jpabook.jpashop.api;

/**
 * create on 2023/01/18. create by IntelliJ IDEA.
 *
 * <p> </p>
 * <p> {@link } and {@link } </p> *
 *
 * @author wonukHwang
 * @version 1.0
 * @see
 * @since (ex : 5 + 5)
 */

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member - ManyToOne
 * Order -> Delivery - OneToOne
 *
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

  private final OrderRepository orderRepository;
  private final OrderSimpleQueryRepository orderSimpleQueryRepository; //의존관계 주입

  /**
   * V1. 엔티티 직접 노출
   * - Hibernate5Module 모듈 등록, LAZY=null 처리
   * - 양방향 관계 문제 발생 -> @JsonIgnore
   */
  @GetMapping("/api/v1/simple-orders")
  public List<Order> ordersV1() {
    /**
     * 검색 조건 없이 모든 Order 조회
     * 여기까지 all로 그냥 반환하면 무한루프에 빠져버린다. Order -> member -> orders -> member -> 무한반복 : 양방향 연관 관계 문제
     * 해결하려면 둘중 하나를 @JsonIgnore 해야한다.
     */
    List<Order> all = orderRepository.findAllByString(new OrderSearch());

    // 원하는 것만 추출하는법.
    for (Order order : all) {
      order.getMember().getName(); // Lazy 강제 초기화 -> 실제 Name을 가져와야 하기때문에.
      order.getDelivery().getAddress(); // Lazy 강제 초기화
    }
    return all;
  }

  /**
   * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
   * - 단점: 지연로딩으로 쿼리 N번 호출 ORDER, MEMBER, DELIVERY
   */
  @GetMapping("/api/v2/simple-orders")
  public List<SimpleOrderDto> ordersV2() {
    // ORDER 2개
    // N + 1 -> 1 + 회원 N + 배송 N
    List<Order> orders = orderRepository.findAll();
    List<SimpleOrderDto> result = orders.stream()
        .map(o -> new SimpleOrderDto(o))
        .collect(toList());

    return result;
  }

  /**
   * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
   * - fetch join으로 쿼리 1번 호출
   * 참고: fetch join에 대한 자세한 내용은 JPA 기본편 참고(정말 중요함)
   * 실무에서 대부분의 성능 문제는 fetch join을 사용하여 해결이 가능하다.
   *
   * 리포지토리 재사용성도 좋고 개발도 단순해진다.
   */
  @GetMapping("/api/v3/simple-orders")
  public List<SimpleOrderDto> ordersV3() {
    List<Order> orders = orderRepository.findAllWithMemberDelivery();
    List<SimpleOrderDto> result = orders.stream()
        .map(o -> new SimpleOrderDto(o))
        .collect(toList());
    return result;
  }

  /**
   * 처음부터 DTO로 조회.
   * 내가 원하는 컬럼만 select 가 가능하다.
   * v3는 모든 컬럼이 있기때문에 재사용성이 높지만
   * v4는 원하는 컬럼만 가져오기 때문에 재사용성이 낮다. 하지만 성능 최적화 면에서는 좋다.
   * 어플리케이션 네트웍 용량 최적화(요즘 성능이 좋기 때문에 생각보다 미비, 대부분의 성능은 join 문에서 문제가 발생하기 때문에 사실 성능에 큰 차이가 없다.)
   * 따지고보면 API SPEC이 그대로 Repository에 그대로 들어가기 때문에. 논리적으로 의존관계가 깨진다.
   *
   * 해결방법 - 쿼리 패키지를 별도로 분리하여 Repository를 사용한다.
   * Repository는 가급적 순수한 Entity를 조회하기 위해 사용한다. -> 재사용성이 높다.
   * OrderSimpleQueryRepository를 별도로 분리하면 유지 보수성이 좋아진다.
   */
  @GetMapping("/api/v4/simple-orders")
  public List<OrderSimpleQueryDto> ordersV4() {
    return orderSimpleQueryRepository.findOrderDtos();
  }


  @Data
  static class SimpleOrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간
    private OrderStatus orderStatus;
    private Address address;

    public SimpleOrderDto(Order order) {
      orderId = order.getId();
      name = order.getMember().getName(); // LAZY 초기화
      orderDate = order.getOrderDate();
      orderStatus = order.getStatus();
      address = order.getDelivery().getAddress(); // LAZY 초기화
    }
  }

}