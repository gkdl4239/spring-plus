package org.example.expert.domain.user.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ElasticSearchService elasticSearchService;

    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(),
            user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(),
            user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
            !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
            !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    public List<UserResponse> getUsersByName(String nickname) {
        List<User> users = userRepository.findByNickname(nickname);

        return users.stream().map(user -> new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname()))
            .toList();
    }

    public List<UserResponse> fullTextSearch(String nickname) {
        List<User> users = userRepository.fullTextSearch(nickname);

        return users.stream().map(user -> new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname()))
            .toList();
    }

    public List<Map<String, Object>> searchByNicknameElastic(String nickname) throws IOException {
        return elasticSearchService.searchByNickname(nickname);
    }
}
