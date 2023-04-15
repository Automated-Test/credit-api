package com.eliasnogueira.credit.repository;

import com.eliasnogueira.credit.entity.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<TestUser, Long> {

    @Transactional
    @Modifying
    @Query("update TestUser u set u.nickname = ?1 where u.id = ?2")
    int modifyUserNickName(String nickName, Long userId);
}
