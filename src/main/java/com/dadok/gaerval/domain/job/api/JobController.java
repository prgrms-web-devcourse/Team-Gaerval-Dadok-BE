package com.dadok.gaerval.domain.job.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.job.dto.response.JobResponses;
import com.dadok.gaerval.domain.job.service.JobService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/jobs")
@RestController
@RequiredArgsConstructor
public class JobController {

	private final JobService jobService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public JobResponses findAll() {
		return jobService.findAllWithAsc();
	}

}
