package com.dadok.gaerval.domain.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.service.JobService;
import com.dadok.gaerval.domain.user.dto.request.UserJobRegisterRequest;
import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserJobRegisterResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;
import com.dadok.gaerval.domain.user.entity.Authority;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.domain.user.repository.AuthorityRepository;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DefaultUserService implements UserService {

	private final UserRepository userRepository;

	private final AuthorityRepository authorityRepository;

	private final JobService jobService;

	@Transactional(readOnly = true)
	@Override
	public Optional<User> findByEmailWithAuthorities(String email) {
		return userRepository.findTopByEmailWithAuthorities(email);
	}

	@Transactional
	@Override
	public User register(OAuth2Attribute attribute) {
		Authority authority = authorityRepository.findById(Role.USER)
			.orElse(authorityRepository.save(Authority.create(Role.USER)));

		User user = User.createByOAuth(attribute, UserAuthority.create(authority));
		return userRepository.save(user);
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<User> findById(Long userId) {
		return userRepository.findById(userId);
	}

	@Transactional(readOnly = true)
	@Override
	public User getById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotfoundException(User.class));
	}

	@Override
	public UserProfileResponse getUserProfile(Long userId) {
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public UserDetailResponse getUserDetail(Long userId) {
		UserDetailResponse userDetail = userRepository.findUserDetail(userId);

		if (userDetail == null) {
			throw new ResourceNotfoundException(User.class);
		}
		return userDetail;
	}

	@Transactional
	@Override
	public UserJobRegisterResponse registerJob(Long userId, UserJobRegisterRequest request) {
		User user = userRepository.getReferenceById(userId);
		Job job = jobService.getBy(request.jobGroup(), request.jobName());

		user.changeJob(job);

		return new UserJobRegisterResponse(user.getId(), new UserDetailResponse.JobDetailResponse(job.getJobGroup(), job.getJobName(), job.getSortOrder()));
	}

}
