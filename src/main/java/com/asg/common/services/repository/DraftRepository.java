package com.asg.common.services.repository;

import com.asg.common.services.entity.Draft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DraftRepository extends JpaRepository<Draft, Long> {

    Optional<Draft> findByDocIdAndCompanyPoidAndUserPoid(String docId, Long companyId, Long userId);

    long deleteByDocIdAndCompanyPoidAndUserPoid(String docId, Long companyId, Long userId);

    long deleteByUserPoid(Long userPoid);

}
