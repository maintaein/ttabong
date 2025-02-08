package com.ttabong.repository.user;

import com.ttabong.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    //이메일을 찾되, 삭제되지 않은 이메일이어야 한다.

    //로그인 할 때 사용
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    //이메일 중복확인이나 계정찾기처럼 간단히 조회할 때 사용
    boolean existsByEmailAndIsDeletedFalse(String email);


}
