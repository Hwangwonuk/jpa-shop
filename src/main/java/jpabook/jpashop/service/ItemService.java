/*
 * Created by Wonuk Hwang on 2023/01/17
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by infra Team <wonuk_hwang@bigin.io>, 2023/01/17
 */
package jpabook.jpashop.service;

import java.util.List;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

  private final ItemRepository itemRepository;

  public void saveItem(Item item) {
    itemRepository.save(item);
  }

  /**
   * 영속성 컨텍스트에서 엔티티를 다시 조회한 후에 데이터를 수정하는 방법
   * 트랜잭션 안에서 엔티티를 다시 조회, 변경할 값 선택 트랜잭션 커밋 시점에
   * 변경 감지(Dirty Checking)이 동작해서 데이터베이스에 UPDATE SQL 실행
   */
  @Transactional
  public void updateItem(Long itemId, String name, int price, int stockQuantity) {
    // id를 기반으로 실제 영속상태의 엔티티를 조회한다.
    Item item = itemRepository.findOne(itemId);

    // 변경 감지
    item.setName(name);
    item.setPrice(price);
    item.setStockQuantity(stockQuantity);

    // save를 호출할 필요 없다. Transactional에 의해서 flush한다.
  }

  /**
   * 가급적 머지를 사용하지말고 변경감지 더티체킹을 사용하자!!!!!
   *
   * 병합 사용
   * 병합은 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용하는 기능이다
   *
   * 병합 동작 방식
   * 1. merge() 를 실행한다.
   * 2. 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티를 조회한다.
   * 2-1. 만약 1차 캐시에 엔티티가 없으면 데이터베이스에서 엔티티를 조회하고, 1차 캐시에 저장한다.
   * 3. 조회한 영속 엔티티( mergeMember )에 member 엔티티의 값을 채워 넣는다. (member 엔티티의 모든
   * 값을 mergeMember에 밀어 넣는다. 이때 mergeMember의 “회원1”이라는 이름이 “회원명변경”으로 바뀐다.)
   * 4. 영속 상태인 mergeMember를 반환한다
   *
   * 병합시 동작 방식을 간단히 정리
   * 1. 준영속 엔티티의 식별자 값으로 영속 엔티티를 조회한다.
   * 2. 영속 엔티티의 값을 준영속 엔티티의 값으로 모두 교체한다.(병합한다.)
   * 3. 트랜잭션 커밋 시점에 변경 감지 기능이 동작해서 데이터베이스에 UPDATE SQL이 실행
   *
   * 주의: 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만,
   * 병합을 사용하면 모든 속성이 변경된다. 병합시 값이 없으면 null 로 업데이트 할 위험도 있다. (병합은 모든 필드를 교체한다.)
   */
//  @Transactional
//  void update(Item itemParam) { //itemParam: 파리미터로 넘어온 준영속 상태의 엔티티
//    Item mergeItem = em.merge(itemParam);
//  }

  public List<Item> findItems() {
    return itemRepository.findAll();
  }

  public Item findOne(Long itemId) {
    return itemRepository.findOne(itemId);
  }


}
