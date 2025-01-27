package org.example.expert.domain.user.repository;

import java.util.List;
import java.util.Optional;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByNickname(String nickname);


    @Query(value = "SELECT * FROM users WHERE MATCH(nickname) AGAINST(:nickname IN NATURAL LANGUAGE MODE)", nativeQuery = true)
    List<User> fullTextSearch(@Param("nickname") String nickname);


}
