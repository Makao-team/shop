package kr.co.shop.makao.repository;

import kr.co.shop.makao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
            SELECT
            CASE WHEN EXISTS (SELECT 1 FROM user u WHERE u.email = :email) THEN true ELSE false END AS emailExists,
            CASE WHEN EXISTS (SELECT 1 FROM user u WHERE u.phoneNumber = :phoneNumber) THEN true ELSE false END AS phoneNumberExists
            """)
    ExistsEmailAndPhoneNumber existsEmailAndPhoneNumber(@Param("email") String email, @Param("phoneNumber") String phoneNumber);

    Optional<User> findByEmail(String email);
}

