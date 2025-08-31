package com.example.foodndeliv.repository;

import com.example.foodndeliv.entity.Rider;
import com.example.foodndeliv.types.RiderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource(
        path = "riders",
        collectionResourceRel = "riders",
        itemResourceRel = "rider"
)
public interface RiderRepository extends JpaRepository<Rider, Long> {

    @RestResource(path = "by-status", rel = "by-status")
    Page<Rider> findByStatus(@Param("status") RiderStatus status, Pageable pageable);

    Optional<Rider> findByPhone(@Param("phone") String phone);

    @Override @RestResource(exported = false)
    void deleteById(Long id);

    @Override @RestResource(exported = false)
    void delete(Rider entity);

    @Override @RestResource(exported = false)
    void deleteAll();
}
