package com.testcode.javatest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudyTest {

	@Test
	@DisplayName("스터디 만들기")
	public void create() throws Exception {
	    Study study = new Study(10);
		assertNotNull(study);

		// 테스트는 위에서 부터 실행됨, 중간에 실패하면 거기서 멈춤 다음거는 진행하지 않음
		// 내부의 테스트를 모두 실행함
		assertAll(
				() -> assertEquals(StudyStatus.DRAFT, study.getStudyStatus(), "스터디를 처음 만들면 상태값이 DRAFT 여야 한다."),
				// 메세지 출력을 실패했을때만 하도록 성능최적화를 하고싶다면 아래와 같이 람다식으로 해야한다.
				() -> assertEquals(StudyStatus.DRAFT, study.getStudyStatus(), () -> "스터디를 처음 만들면 상태값이 DRAFT 여야 한다."),
				// 실패하면 메세지 출력할수있게 하기 가능
				() -> assertTrue(study.getLimit() > 0, "스터디 최대 참석인원은 0보다 커야한다.")
		);

		System.out.println("create");
	}

	@Test
	@DisplayName("에러 터트리기")
	void error() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class, () -> new Study(-10));
		// 오류 메세지 확인하기
		String message = exception.getMessage();
		assertEquals("limit 는 0보다 커야 한다.", message);
	}

	@Test
	@DisplayName("시간내에 테스트가 완료되는가") // threadLocal 를 사용하는 코드가 {}안에 있다면 예상치못한 결과가 나올수있음 주의 ex 트랜잭션
	void timeOut() {
		// assertTimeoutPreemptively 설정한 시간 초과하면 거기서 멈춰버림
//		assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
//			new Study(10);
//			Thread.sleep(300);
//			// 시간을 기다리기 때문에 그 시간만큼 테스트가 지연될 수 있다.
//		});
		assertTimeout(Duration.ofMillis(500), () -> {
			new Study(10);
			Thread.sleep(300);
			// 시간을 기다리기 때문에 그 시간만큼 테스트가 지연될 수 있다.
		});
	}

	@Test
	@Disabled // test 에서 제외시킴
	void create2() {
		System.out.println("create2");
	}

	@BeforeAll // 맨처음 테스트하기 전에 실행
	static void beforeAll() {
		System.out.println("before all");
	}

	@AfterAll // 맨뒤 테스트하고나서 실행
	static void afterAll() {
		System.out.println("after all");
	}

	@BeforeEach // 각각 테스트 실행하기 전에 실행
	void beforeEach() {
		System.out.println("before each");
	}

	@AfterEach // 각각 테스트 실행하고나서 실행
	void afterEach() {
		System.out.println("after each");
	}



}