package com.testcode.javatest.study;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.testcode.javatest.member.MemberService;
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
	void createStudyService() { // Mock 객체를 파라미터로 넣어도 됨
		// 1. 방법 Mockito static import 하기, Mock 객체 만들기 이거도 줄일수 있음
//		MemberService memberService = mock(MemberService.class);
//		StudyRepository studyRepository = mock(StudyRepository.class);

		StudyService studyService = new StudyService(memberService, studyRepository);

		assertNotNull(studyService);
	}

}