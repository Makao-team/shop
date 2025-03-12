package kr.co.shop.makao.repository;

import kr.co.shop.makao.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
            SELECT p.id as id, p.name as name, p.description as description, p.price as price, p.stock as stock, p.createdAt as createdAt, 
                   (SELECT name FROM user WHERE id = p.merchantId) as merchantName 
            FROM product p
            WHERE p.isArchived = false AND
                    (COALESCE(:filter, '') = '' OR
                      (:filter IN ('name', 'description', 'merchant')) AND (
                          (:filter = 'name' AND p.name LIKE CONCAT('%', COALESCE(:keyword, ''), '%')) OR
                          (:filter = 'description' AND p.description LIKE CONCAT('%', COALESCE(:keyword, ''), '%')) OR
                          (:filter = 'merchant' AND EXISTS (
                              SELECT 1 FROM user u WHERE u.id = p.merchantId AND u.name LIKE CONCAT('%', COALESCE(:keyword, ''), '%')
                          ))
                      ))
            """)
    Slice<Product.View> findAllView(@Param("filter") String filter, @Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT p.id as id, p.name as name, p.description as description, p.price as price, p.stock as stock, p.createdAt as createdAt, 
                   (SELECT name FROM user WHERE id = p.merchantId) as merchantName 
            FROM product p
            WHERE p.id = :id
            """)
    Optional<Product.View> findOneView(Long id);
}
