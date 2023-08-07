package com.testcode.javatest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.time.Duration;
import net.bytebuddy.asm.Advice.Argument;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
@TestInstance(Lifecycle.PER_CLASS) // 클래스마다 하나의 인스턴스를 생성한다.
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
	@DisplayName("특정환경에서 적용하는 테스트")
	@EnabledOnOs(OS.MAC)
	//@DisabledOnOs({OS.MAC, OS.WINDOWS}) // 전체실행했을때만 적용시켜줌 , 여러개도 선택가능
	@EnabledOnJre(JRE.JAVA_17)
	//@DisabledOnJre(JRE.JAVA_17) // 이거도 전체 실행했을때만 적용시킴
	//@EnabledIfEnvironmentVariable(named = "env", matches = "local") // Environment variable [env] does not exist
	void osTest1() {
		// 로컬인 경우에만 다음으로 넘어가 테스트를 실행할 수 있다.
		String env = System.getenv("local");
		System.out.println("env = " + env);
//		assumeTrue("LOCAL".equalsIgnoreCase(env));

		assumeTrue(System.getProperty("os.name").startsWith("Mac"));

		Study study = new Study(10);
		assertTrue(study.getLimit() > 0);
	}

	@Test
	@DisplayName("특정환경에서 적용하는 테스트2")
	void osTest2() {
		// 로컬인 경우에만 다음으로 넘어가 테스트를 실행할 수 있다.
		assumingThat(System.getProperty("os.name").startsWith("Window"), () -> {
			System.out.println("Window 사용중");
			Study study = new Study(10);
			assertTrue(study.getLimit() > 0);
		});
 		assumingThat(System.getProperty("os.name").startsWith("Mac"), () -> {
			System.out.println("Mac 사용중");
			Study study = new Study(10);
			assertTrue(study.getLimit() > 0);
		});
	}

	@Test
	@DisplayName("fast 테스트")
	@Tag("fast") // gradle 설정으로 안됨, 환경변수 설정으로만 됨
	void fast() {
		System.out.println("fast 테스트");
		Study study = new Study(10);
		assertTrue(study.getLimit() > 0);
	}

	@Test
	@DisplayName("slow 테스트")
	@Tag("slow") // gradle 설정으로 안됨, 환경변수 설정으로만 됨
	void slow() {
		System.out.println("slow 테스트");
		Study study = new Study(10);
		assertTrue(study.getLimit() > 0);
	}

	@FastAnnotation // tag 로 fast 가 설정되어있다.
	@DisplayName("fast 테스트")
	void fastAnnotation() {
		System.out.println("fast 테스트");
		Study study = new Study(10);
		assertTrue(study.getLimit() > 0);
	}

	@DisplayName("반복 테스트")
//	@RepeatedTest(10) //repetition 1 of 10
	@RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}") // 반복 테스트, 1/10
	void repeatedTest(RepetitionInfo repetitionInfo) {
		System.out.println("test"+" / "+repetitionInfo.getCurrentRepetition()+
				" / "+repetitionInfo.getTotalRepetitions());
	}

	@DisplayName("반복하기")
	@ParameterizedTest(name = "{index} {displayName} message={0}") // 1 반복하기 message=날씨가
	@ValueSource(strings = {"날씨가", "많이", "추워지고", "있네요"})
	@EmptySource // 빈칸을 소스로 갖는 테스트 추가
	@NullSource // null 소스로 갖는 테스트 추가
	@NullAndEmptySource // 위에 두개 합친거 //세개 다쓰면 중복 적용은 안된다.
	void parameterTest(String message) {
		System.out.println(message);
		// 글자 하나하나 들어올때마다 한개의 테스트로 친다.
		//before each
		//날씨가
		//after each
	}

	@DisplayName("반복하기2") // 숫자 파라미터 사용하기
	@ParameterizedTest(name = "{index} {displayName} message={0}") // 1 반복하기 message=날씨가
	@ValueSource(ints = {10, 20, 40})
	void parameterTest2(Integer integer) {
		System.out.println(integer);
	}

	@DisplayName("반복하기3") // 객체 파라미터 사용하기 , 하나의 파라미터만 받기
	@ParameterizedTest(name = "{index} {displayName} message={0}") // 1 반복하기 message=날씨가
	@ValueSource(ints = {10, 20, 40})
	void parameterTest3(@ConvertWith(StudyConverter.class) Study study) {
		System.out.println(study.getLimit());
	}
	static class StudyConverter extends SimpleArgumentConverter {
		@Override
		protected Object convert(Object source, Class<?> targetType)
				throws ArgumentConversionException {
			assertEquals(Study.class, targetType, "Can only convert to Study");
			return new Study(Integer.parseInt(source.toString()));
		}
	}

	@DisplayName("반복하기4") // 객체 파라미터 사용하기, 두개이상 파라미터 받기
	@ParameterizedTest(name = "{index} {displayName} message={0}") // 1 반복하기 message=날씨가
	@CsvSource({"10, 자바 스터디", "20, 스프링"})
	void parameterTest4(Integer limit, String name) {
		System.out.println(new Study(limit, name));
	}

	@DisplayName("반복하기5") // 객체 파라미터 사용하기, 두개이상 파라미터 받기
	@ParameterizedTest(name = "{index} {displayName} message={0}") // 1 반복하기 message=날씨가
	@CsvSource({"10, 자바 스터디", "20, 스프링"})
	void parameterTest5(ArgumentsAccessor argumentsAccessor) { //하나로 받고 싶다면 이렇게 하셈
		Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
		System.out.println(study);
	}

	@DisplayName("반복하기6") // 객체 파라미터 사용하기, 두개이상 파라미터 받기
	@ParameterizedTest(name = "{index} {displayName} message={0}") // 1 반복하기 message=날씨가
	@CsvSource({"10, 자바 스터디", "20, 스프링"})
	void parameterTest6(@AggregateWith(StudyAggregator.class) Study study) { //하나로 받고 싶다면 이렇게 하셈
		System.out.println(study);
	}
	static class StudyAggregator implements ArgumentsAggregator {
		@Override
		public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context)
				throws ArgumentsAggregationException {
			Study study = new Study(accessor.getInteger(0), accessor.getString(1));
			return study;
		}
	}

	int value = 1;
	@Test
	@DisplayName("value 값이 공유 되는가1")
	void value1() {
		System.out.println(value++); // 1
		System.out.println(this); // 메서드마다 다른 주소가 나온다. / 다른 인스턴스다. -> 테스트간 의존성을 없애기 위해서
	}
	@Test
	@DisplayName("value 값이 공유 되는가2")
	void value2() {
		System.out.println(value++); // 1
		System.out.println(this);
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