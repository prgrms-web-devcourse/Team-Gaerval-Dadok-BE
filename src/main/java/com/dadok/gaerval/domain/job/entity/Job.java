package com.dadok.gaerval.domain.job.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "jobs")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Job extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private JobGroup jobGroup;

	@Enumerated(EnumType.STRING)
	private JobGroup.JobName jobName;

	@Column(columnDefinition = "SMALLINT UNSIGNED NOT NULL")
	private Integer sortOrder;

	protected Job(JobGroup jobGroup, JobGroup.JobName jobName, Integer sortOrder) {
		this.jobGroup = jobGroup;
		this.jobName = jobName;
		this.sortOrder = sortOrder;
	}

	public static Job create(JobGroup jobGroup, JobGroup.JobName jobName, Integer sortOrder) {
		return new Job(jobGroup, jobName, sortOrder);
	}

	@JacocoExcludeGenerated
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Job job = (Job)o;
		return Objects.equals(id, job.id) && jobGroup == job.jobGroup && jobName == job.jobName
			&& Objects.equals(sortOrder, job.sortOrder);
	}

	@JacocoExcludeGenerated
	@Override
	public int hashCode() {
		return Objects.hash(id, jobGroup, jobName, sortOrder);
	}
}
