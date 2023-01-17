package jpabook.jpashop.service;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
/*
 * Created by Wonuk Hwang on 2023/01/17
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by infra Team <wonuk_hwang@bigin.io>, 2023/01/17
 */

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

@SpringBootTest
// JUnit5에서는 @SpringBootTest에 @RunWith(SpringRunner.class)가 포함되어있고 [Spring과 함께 실행한다는 어노테이션], public을 명시하지 않아도 됩니다
//@ExtendWith(SpringExtension.class)
// testcode에 Transactional이 들어가면 자동으로 rollback이 된다.
@Transactional
class MemberServiceTest {

  @Autowired
  MemberService memberService;

  @Autowired
  MemberRepository memberRepository;

  @Test
//  @Rollback(false) 롤백을 하지않는 경우 사용
  void 회원가입() throws Exception {
    // given
    Member member = new Member();
    member.setName("kim");

    // when
    Long savedId = memberService.join(member);

    // then
    assertEquals(member, memberRepository.findOne(savedId));
  }

  // Junit4에서는 @Test(expected = IllegalStateException.class) 를 사용한다
  @Test
  void 중복회원예외() throws Exception {
    // given
    Member member1 = new Member();
    member1.setName("kim");

    Member member2 = new Member();
    member2.setName("kim");

    // when
    memberService.join(member1);
    try {
      memberService.join(member2); // 예외가 발생해야 한다!
    } catch (IllegalStateException e) {
      return;
    }

    // then
//    fail("예외가 발생해야 한다."); Junit4 방식
    IllegalStateException thrown = assertThrows(IllegalStateException.class,
        () -> memberService.join(member2));
    assertEquals("이미 존재하는 회원입니다.", thrown.getMessage());

  }

}