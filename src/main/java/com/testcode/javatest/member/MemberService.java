package com.testcode.javatest.member;

import com.testcode.javatest.domain.Member;
import com.testcode.javatest.domain.Study;
import java.util.Optional;

public interface MemberService {

	Optional<Member> findById(Long memberId);

	void validate(Long memberId);

	void notify(Study newstudy);

	void notify(Member member);
}
