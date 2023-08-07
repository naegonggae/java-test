package com.testcode.javatest;

import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

public class FindSlowTestExtension implements BeforeTestExecutionCallback,
		AfterTestExecutionCallback {

//	private static final long THRESHOLD = 1000L; // 1초 이상걸리는 메서드 이름은 slowTest 어노테이션 붙이라고 해주기

	// 이거 사용하는 곳마다 THRESHOLD 값을 다르게 반영하고 싶다면 아래와 같이 구성
	// 이렇게 작성해도 테스트클래스 상단에 어노테이션으로 적용시키면 반영시킬 방법이 없음
	private long THRESHOLD;

	public FindSlowTestExtension(long THRESHOLD) {
		this.THRESHOLD = THRESHOLD;
	}

	@Override
	public void beforeTestExecution(ExtensionContext context) throws Exception {
		Store store = getStore(context);
		store.put("START_TIME", System.currentTimeMillis());

	}

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {
		Method requiredTestMethod = context.getRequiredTestMethod();
		SlowAnnotation annotation = requiredTestMethod.getAnnotation(SlowAnnotation.class);

		String testMethodName = context.getRequiredTestMethod().getName();
		Store store = getStore(context);
		long start_time = store.remove("START_TIME", long.class);
		long duration = System.currentTimeMillis() - start_time;

		if (duration > THRESHOLD && annotation == null) {
			System.out.printf("Please consider mark method [%s] with @SlowTest.\n", testMethodName);
		}
	}

	private static Store getStore(ExtensionContext context) {
		String testClassName = context.getRequiredTestClass().getName();
		String testMethodName = context.getRequiredTestMethod().getName();
		Store store = context.getStore(Namespace.create(testClassName, testMethodName));
		return store;
	}
}
