package com.testcode.javatest.study;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.testcode.javatest.domain.Member;
import com.testcode.javatest.domain.Study;
import com.testcode.javatest.domain.StudyStatus;
import com.testcode.javatest.member.MemberService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest // 빈등록한걸 사용하겠다.
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
//@Transactional 이거 있으면 롤백해서 db에 저장안됨
class StudyServiceTest2 {

	@Mock
	MemberService memberService;

	@Autowired
	StudyRepository studyRepository;



	@Test
	void createNewStudy() {

		// Given
		StudyService studyService = new StudyService(memberService, studyRepository);
		assertNotNull(studyService);

		Member member = new Member();
		member.setId(1L);
		member.setEmail("keesun@email.com");

		Study study = new Study(10, "-테스트");

		given(memberService.findById(1L)).willReturn(Optional.of(member));

		// When
		studyService.createNewStudy(1L, study);

		// Then
		assertEquals(1L, study.getOwnerId());
		then(memberService).should(times(1)).notify(study);
		then(memberService).shouldHaveNoMoreInteractions();
	}

	@DisplayName("다른 사용자가 볼 수 있도록 스터디를 공개한다.")
	@Test
	void openStudy() {
		// Given
		StudyService studyService = new StudyService(memberService, studyRepository);
		Study study = new Study(10, "더 자바");
		assertNull(study.getOpenedDateTime());

		// When
		studyService.openStudy(study);

		// Then
		assertEquals(StudyStatus.OPENED, study.getStatus());
		assertNotNull(study.getOpenedDateTime());
		then(memberService).should().notify(study);
	}

}
