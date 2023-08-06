package com.testcode.javatest;

import lombok.Getter;

@Getter
public class Study {

	private StudyStatus studyStatus = StudyStatus.DRAFT;

	private int limit;

	public Study(int limit) {
		if (limit < 0) {
			throw new IllegalArgumentException("limit 는 0보다 커야 한다.");
		}
		this.limit = limit;
	}

	public StudyStatus getStudyStatus() {
		return this.studyStatus;
	}

}
