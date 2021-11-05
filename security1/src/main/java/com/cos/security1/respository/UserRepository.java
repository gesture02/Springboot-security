package com.cos.security1.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

//JpaRepository 를 상속하면 자동 컴포넌트 스캔됨.
public interface UserRepository extends JpaRepository<User, Integer>{
	// JPA Naming 전략
	User findByUsername(String username);
}
