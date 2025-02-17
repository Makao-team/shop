package kr.co.shop.makao.repository;

import jakarta.validation.constraints.Email;
import kr.co.shop.makao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(@Email String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
