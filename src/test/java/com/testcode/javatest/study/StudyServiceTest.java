package com.testcode.javatest.study;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.testcode.javatest.domain.Member;
import com.testcode.javatest.domain.Study;
import com.testcode.javatest.member.MemberService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
// 2. 방법 어노테이션들로 mock 객체 만들기
@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

	// Mock 어노테이션 사용해서 줄여주기
	@Mock MemberService memberService;
	@Mock StudyRepository studyRepository;

	@Test
	void createNewStudy() { // Mock 객체를 파라미터로 넣어도 됨
		// 1. 방법 Mockito static import 하기, Mock 객체 만들기 이거도 줄일수 있음
//		MemberService memberService = mock(MemberService.class);
//		StudyRepository studyRepository = mock(StudyRepository.class);

		StudyService studyService = new StudyService(memberService, studyRepository);

		assertNotNull(studyService);

		// 기본으로 출력되는 타입은 뭘까?
		Optional<Member> id = memberService.findById(1L); // optional 은 기본값이 Optional.empty 다
		System.out.println("id = " + id);

		memberService.validate(2L); // void 는 아무런 출력이 없다

		// mockito 를 이용해서 when 어떤 동작을 했을때 then 어떤 결과 나오도록 조작해준다.
		Member member = new Member();
		member.setId(1L);
		member.setEmail("ddd@ddd.com");
		when(memberService.findById(1L)).thenReturn(Optional.of(member));
		//when(memberService.findById(any())).thenReturn(Optional.of(member)); // 뭐가 들어가든 결과 호출하도록 설정 any()

		// mockito 로 조작한게 실제로 나오는지 확인
		Optional<Member> findMember = memberService.findById(1L);
		assertEquals("ddd@ddd.com", findMember.get().getEmail());

		Study study = new Study(10, "java");
		studyService.createNewStudy(1L, study);

		// when 은 리턴 타입이 있는것들을 사용함, 오류 던지기도 가능
		when(memberService.findById(6L)).thenThrow(new RuntimeException());
		doThrow(new IllegalArgumentException()).when(memberService).validate(5L);

		assertThrows(RuntimeException.class, () -> {
			memberService.findById(6L);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			memberService.validate(5L);
		});
		memberService.validate(7L);
	}

	@Test
	@DisplayName("when 조건으로 결과를 여러번 조작하기")
	void multiProcess() {
		StudyService studyService = new StudyService(memberService, studyRepository);

		Member member = new Member();
		member.setId(1L);
		member.setEmail("ddd@ddd.com");

		when(memberService.findById(any()))
				.thenReturn(Optional.of(member))
				.thenThrow(new RuntimeException())
				.thenReturn(Optional.empty());

		assertEquals("ddd@ddd.com", memberService.findById(1L).get().getEmail());
		assertThrows(RuntimeException.class, () -> {
			memberService.findById(2L);
		});
		assertEquals(Optional.empty(), memberService.findById(3L));
	}

}