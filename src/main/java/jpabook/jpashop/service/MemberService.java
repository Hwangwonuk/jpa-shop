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
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
// JPA로 데이터를 변경하는것은 모두 Transactional 이 필요하다. 읽기인 경우 readOnly = true 옵션을 사용한다.
// public으로 생성된 메소드는 모두 클래스 단위에 선언된 Transactional을 따라간다.
// Javax와 spring이 제공하는 Transactional중 스프링을 사용하자. 스프링에 종속적으로 개발하기 때문에 활용범위가 높다.
@Transactional(readOnly = true)
// @RequiredArgsConstructor 이 있는 필드만 가지고 생성자를 만들어 주는 어노테이션
@RequiredArgsConstructor
public class MemberService {

//  @Autowired 필드 주입, setter주입, 생성자 주입 -> 생성자 주입을 사용하자.
  // final을 사용하면 컴파일 단계에서 에러가 발생한다.
  private final MemberRepository memberRepository;

//  @Autowired 생성자가 하나만 있을 때는 생략이 가능하다.
//  public MemberService(MemberRepository memberRepository) {
//    this.memberRepository = memberRepository;
//  }

  // 회원 가입
  // Transactional의 default는 readOnly = false 이다.
  @Transactional
  public Long join(Member member) {
    validateDuplicateMember(member);
    memberRepository.save(member);
    return member.getId();
  }

  /**
   * 실제 비지니스 로직에서는 동시에 같은 아이디를 생성할 수 있기때문에 DB에 유니크 설정을 해두는것이 좋다.
   */
  // 중복 회원 검증 EXCEPTION
  private void validateDuplicateMember(Member member) {
    List<Member> findMembers = memberRepository.findByName(member.getName());
    if (!findMembers.isEmpty()) {
      throw new IllegalStateException("이미 존재하는 회원입니다.");
    }
  }

  // 회원 전체 조회
  public List<Member> findMembers() {
    return memberRepository.findAll();
  }

  // 회원 단건 조회
  public Member findOne(Long memberId) {
    return memberRepository.findOne(memberId);
  }
}
