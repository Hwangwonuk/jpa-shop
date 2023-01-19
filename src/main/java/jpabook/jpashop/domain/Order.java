/*
 * Created by Wonuk Hwang on 2023/01/17
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by infra Team <wonuk_hwang@bigin.io>, 2023/01/17
 */
package jpabook.jpashop.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

/**
 * create on 2023/01/17. create by IntelliJ IDEA.
 *
 * <p> </p>
 * <p> {@link } and {@link } </p> *
 *
 * @author wonukHwang
 * @version 1.0
 * @see
 * @since (ex : 5 + 5)
 */
@Entity
@Getter
@Setter
// 직접 생성하지 않고 생성 메서드를 사용해야 생성되도록 제약하기 위함.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// ORDER는 테이블명으로 사용할 수 없다.
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue
  @Column(name = "order_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
  @JoinColumn(name = "member_id")
  private Member member;

  // 컬렉션의 경우 global 설정 yml 말고 @Batchsize 로 개별적으로 적용이 가능하다.
  @BatchSize(size = 1000)
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItem> orderItems = new ArrayList<>();

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  // 연관관계의 주인 쪽에 JoinColumn
  @JoinColumn(name = "delivery_id")
  private Delivery delivery;

  private LocalDateTime orderDate;

  // EnumType.STRING으로 항상 사용하여야한다.
  @Enumerated(EnumType.STRING)
  private OrderStatus status; // 주문 상태 ORDER , CANCEL

  // == 연관 관계 메서드 ==
  // 양방향일때 편의 메서드를 구현하여 사용한다.
  public void setMember(Member member) {
    this.member = member;
    member.getOrders().add(this);
  }

  public void addOrderItem(OrderItem orderItem) {
    orderItems.add(orderItem);
    orderItem.setOrder(this);
  }

  public void setDelivery(Delivery delivery) {
    this.delivery = delivery;
    delivery.setOrder(this);
  }

  // == 생성 메서드 ==
  public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
    Order order = new Order();
    order.setMember(member);
    order.setDelivery(delivery);
    for (OrderItem orderItem : orderItems) {
      order.addOrderItem(orderItem);
    }
    order.setStatus(OrderStatus.ORDER);
    order.setOrderDate(LocalDateTime.now());
    return order;
  }

  // == 비지니스 로직 ==

  /**
   * 주문 취소
   */
  public void cancel() {
    if (delivery.getStatus() == DeliveryStatus.COMP) {
      throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
    }

    this.setStatus(OrderStatus.CANCEL);
    for (OrderItem orderItem : this.orderItems) {
      orderItem.cancel(); // 재고를 원상복구 시킨다.
    }
  }

  // == 조회 로직 ==
  /**
   * 전체 주문 가격 조회
   * 주문수량 * 주문 금액을 총 금액으로 계산하여 반환한다.
   */
  public int getTotalPrice() {
    return orderItems.stream()
        .mapToInt(OrderItem::getTotalPrice)
        .sum();
  }
}
