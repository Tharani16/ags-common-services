package com.asg.common.services.repository;

import com.asg.common.services.entity.GlobalParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalParametersRepository extends JpaRepository<GlobalParameters, Long> {

    @Query("SELECT g.parameterValue FROM GlobalParameters g WHERE g.parameterName = :parameterName AND (g.deleted IS NULL OR g.deleted = 'N')")
    Optional<String> findParameterValueByName(@Param("parameterName") String parameterName);
}
