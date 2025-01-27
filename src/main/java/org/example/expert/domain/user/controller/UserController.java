package org.example.expert.domain.user.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(
        @AuthenticationPrincipal AuthUser authUser,
        @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @GetMapping("/users/select")
    public ResponseEntity<List<UserResponse>> getUsersByName(@RequestParam String nickname) {
        List<UserResponse> users = userService.getUsersByName(nickname);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/fulltext")
    public ResponseEntity<List<UserResponse>> getByFullText(@RequestParam String nickname) {
        List<UserResponse> users = userService.fullTextSearch(nickname);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/elasticsearch")
    public ResponseEntity<?> getByElasticsearch(@RequestParam String nickname) throws IOException {
        List<Map<String, Object>> users = userService.searchByNicknameElastic(nickname);
        return ResponseEntity.ok(users);
    }
}
