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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest // 빈등록한걸 사용하겠다.
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Testcontainers
@Slf4j
@ContextConfiguration(initializers = StudyServiceTest2.ContainerPropertyInitializer.class)
//@Transactional 이거 있으면 롤백해서 db에 저장안됨
class StudyServiceTest2 {

//	Logger LOGGER = LoggerFactory.getLogger(StudyServiceTest2.class); // 어노테이션으로 대체

	@Mock
	MemberService memberService;

	@Autowired
	StudyRepository studyRepository;

//	@Autowired
//	Environment environment;

	@Value("${container.port}") int port;

	// test 마다 컨테이너를 만듬 그래서 하나로 공유해서 사용하려고 static 을 붙이자
	// 주소는 테스트 yml 설정해주고 환경변수에 넣어줌
	// 느린게 단점
//	@Container
//	static MySQLContainer mySQLContainer = new MySQLContainer("mysql") // 여기 이미지 파일 넣어야할듯
//			.withDatabaseName("studyTest"); // 이름 설정가능

	// 모듈에 없는 db 라도 이렇게 도커이미지만 설정해주면 사용가능하다 , 포트나 이름도 설정가능
	@Container
	static GenericContainer mySQLContainer2 = new GenericContainer("mysql") // 여기 이미지 파일 넣어야할듯
			.withExposedPorts(3306)
			.withEnv("likelion-db", "studyTest") // 이름 설정가능
			.waitingFor(Wait.forListeningPort());


	@BeforeAll
	static void beforeAll() {
		Slf4jLogConsumer slf4jLogConsumer = new Slf4jLogConsumer(log);
		mySQLContainer2.followOutput(slf4jLogConsumer);
	}

	@BeforeEach
	void beforeEach() {
		System.out.println("===============");
		System.out.println(mySQLContainer2.getMappedPort(3306)); // host 랑 어떤 포트로 연결되어있는가 확인
		//System.out.println(mySQLContainer2.getLogs());
//		System.out.println(environment.getProperty("container.port"));
		System.out.println(port);
		studyRepository.deleteAll();
		// 컨테이너를 테스트마다 실행하면 시간이 너무너무 많이 걸림
		// 그래서 static 으로 하나만 띄우는거고
		// 거기 값들을 테스트마다 지워주기 위해서 이거 만듬
	}

	// BeforeAll, AfterAll 이것들 어노테이션으로 줄일수 있음
//	@BeforeAll // 테스트를 실행하기 전에 컨테이너를 띄우고
//	static void beforeAll() {
//		mySQLContainer.start();
//		System.out.println("mysql url = "+ mySQLContainer.getJdbcUrl());
//	}
//
//	@AfterAll // 테스트가 끝나면 컨테이너를 스톱해준다.
//	static void afterAll() {
//		mySQLContainer.stop();
//	}

	@Test
	void createNewStudy() {

		System.out.println("mysql url = "+ mySQLContainer2.getExposedPorts());
		System.out.println("mysql url = "+ mySQLContainer2.getDockerImageName());
		System.out.println("mysql url = "+ mySQLContainer2.getContainerName());
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

	static class ContainerPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext context) {
			TestPropertyValues.of("container.port=" + mySQLContainer2.getMappedPort(3306))
					.applyTo(context.getEnvironment());
		}
	}
}
