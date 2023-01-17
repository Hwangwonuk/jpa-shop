/*
 * Created by Wonuk Hwang on 2023/01/17
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by infra Team <wonuk_hwang@bigin.io>, 2023/01/17
 */
package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

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
@Repository
public class MemberRepository {

  @PersistenceContext
  private EntityManager em;

  public Long save(Member member) {
    em.persist(member);
    return member.getId();
  }

  public Member find(Long id) {
    return em.find(Member.class, id);
  }
}
