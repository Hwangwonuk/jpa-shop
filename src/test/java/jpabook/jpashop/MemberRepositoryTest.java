package jpabook.jpashop;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
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
// Junit4의 RunWith(SpringRunner.class)와 동일한 역할을한다.
@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  /**
   * Entity Manager 를 통한 모든 데이터 변경은 항상 트랜잭션 안에서 이루어져야 한다.
   * @Transactional 어노테이션이 테스트 케이스에 있으면 테스트 실행 후 롤백이 수행된다.
   * @Rollback(value = false) 속성으로 롤백을 하지않고 테스트가 가능하다.
   */
  @Test
  @Transactional
  @Rollback(value = false)
  public void testMember() throws Exception {
     // given
    Member member = new Member();
    member.setUserName("memberA");

     // when
    Long saveId = memberRepository.save(member);
    Member findMember = memberRepository.find(saveId);

    // then
    Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
    Assertions.assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
    // 저장한것과 조회한것은 같은가? TRUE
    // 같은 트랜잭션 안에서 저장하고 조회하면 같은 영속성 컨텍스트가 똑같다.
    // 같은 영속성 컨텍스트 안에서는 아이디값이 같으면 같은 엔티티로 식별한다. (1차캐시!)
    Assertions.assertThat(findMember).isEqualTo(member);
    System.out.println("findMember == member: " + (findMember == member));
  }
}