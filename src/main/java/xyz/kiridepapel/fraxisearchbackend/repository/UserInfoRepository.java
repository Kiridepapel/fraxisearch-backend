package xyz.kiridepapel.fraxisearchbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import xyz.kiridepapel.fraxisearchbackend.entity.UserInfoEntity;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long>{
  public Optional<UserInfoEntity> findByDni(String dni);

  @Query("SELECT u FROM UserInfoEntity u WHERE u.names = ?1 AND u.fatherLastName = ?2 AND u.motherLastName = ?3")
  public Optional<UserInfoEntity> findByFullName(String names, String fatherLastName, String motherLastName);
}
