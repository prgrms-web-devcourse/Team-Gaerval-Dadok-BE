package com.dadok.gaerval.domain.user.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.service.BookshelfService;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.service.JobService;
import com.dadok.gaerval.domain.user.dto.request.UserChangeProfileRequest;
import com.dadok.gaerval.domain.user.dto.request.UserJobChangeRequest;
import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserJobChangeResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponses;
import com.dadok.gaerval.domain.user.entity.Authority;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.domain.user.exception.DuplicateNicknameException;
import com.dadok.gaerval.domain.user.repository.AuthorityRepository;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultUserService implements UserService {

	private final UserRepository userRepository;

	private final AuthorityRepository authorityRepository;

	private final JobService jobService;

	private final BookshelfService bookshelfService;

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
		bookshelfService.createBookshelf(user);
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

	@Transactional(readOnly = true)
	@Override
	public UserProfileResponse getUserProfile(Long userId) {
		UserProfileResponse userProfile = userRepository.findUserProfile(userId);
		if (Objects.isNull(userProfile)) {
			throw new ResourceNotfoundException(User.class);
		}
		return userProfile;
	}

	@Transactional(readOnly = true)
	@Override
	public UserDetailResponse getUserDetail(Long userId) {
		UserDetailResponse userDetail = userRepository.findUserDetail(userId);

		if (Objects.isNull(userDetail)) {
			throw new ResourceNotfoundException(User.class);
		}
		return userDetail;
	}

	@Transactional
	@Override
	public UserJobChangeResponse changeJob(Long userId, UserJobChangeRequest request) {
		User user = userRepository.getReferenceById(userId);
		Job job = jobService.getBy(request.jobGroup(), request.jobName());

		user.changeJob(job);
		bookshelfService.updateJobIdByUserId(user, job.getId());

		return new UserJobChangeResponse(user.getId(),
			new UserDetailResponse.JobDetailResponse(job.getJobGroup(), job.getJobName(), job.getSortOrder()));
	}

	@Transactional
	@Override
	public UserDetailResponse changeProfile(Long userId, UserChangeProfileRequest request) {
		User user = userRepository.getReferenceById(userId);
		Nickname nickname = new Nickname(request.nickname());

		if (!user.isSameNickname(nickname)) {
			validateExistsNickname(nickname);
			try {
				user.changeNickname(nickname);
			} catch (DataIntegrityViolationException e) {
				log.warn("닉네임 중복 예외. {} - {} ", e.getClass().getName(), e.getMessage());
				throw new DuplicateNicknameException(e);
			}
			Bookshelf bookshelf = bookshelfService.getByUserId(user.getId());
			bookshelf.changeName(user.getNickname());
		}

		UserJobChangeRequest jobRequest = request.job();
		Job job = jobService.getBy(jobRequest.jobGroup(), jobRequest.jobName());

		if (!user.isSameJob(job)) {
			user.changeJob(job);
		}

		bookshelfService.updateJobIdByUserId(user, job.getId());

		return new UserDetailResponse(user.getId(), user.getName(), user.getNickname().nickname(),
			user.getOauthNickname(), user.getEmail(), user.getProfileImage(), user.getGender(),
			user.getAuthProvider(),
			new UserDetailResponse.JobDetailResponse(job.getJobGroup(), job.getJobName(), job.getSortOrder()));
	}

	@Transactional(readOnly = true)
	@Override
	public boolean existsNickname(Nickname nickname) {
		return userRepository.existsByNickname(nickname);
	}

	@Transactional
	@Override
	public void changeNickname(Long userId, Nickname nickname) {
		validateExistsNickname(nickname);
		User user = userRepository.getReferenceById(userId);
		try {
			user.changeNickname(nickname);
		} catch (DataIntegrityViolationException e) {
			log.warn("닉네임 중복 예외. {} - {} ", e.getClass().getName(), e.getMessage());
			throw new DuplicateNicknameException(e);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public UserProfileResponses searchAllByNickname(Nickname nickname, int pageSize) {
		return userRepository.findAllByNickname(nickname,
			PageRequest.ofSize(pageSize == 0 ? 10 : pageSize));
	}

	@Transactional(readOnly = true)
	public void validateExistsNickname(Nickname nickname) {

		if (userRepository.existsByNickname(nickname)) {
			throw new DuplicateNicknameException();
		}

	}

}
