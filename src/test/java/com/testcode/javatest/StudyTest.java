package com.testcode.javatest;

import static org.junit.jupiter.api.Assertions.*;

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
	    Study study = new Study();
		assertNotNull(study);
		System.out.println("create");
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