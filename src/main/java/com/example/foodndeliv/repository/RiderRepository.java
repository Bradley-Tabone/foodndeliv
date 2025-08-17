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

    // Exposed as: /api/riders/search/by-status?status=ACTIVE
    @RestResource(path = "by-status", rel = "by-status")
    Page<Rider> findByStatus(@Param("status") RiderStatus status, Pageable pageable);

    // Handy lookup (appears under /api/riders/search/findByPhone?phone=...)
    Optional<Rider> findByPhone(@Param("phone") String phone);

    /* --- Preserve history: disable DELETE over REST --- */
    @Override @RestResource(exported = false)
    void deleteById(Long id);

    @Override @RestResource(exported = false)
    void delete(Rider entity);

    @Override @RestResource(exported = false)
    void deleteAll();

    // (Optional) also block these if your Spring Data version exposes them over REST
    // @Override @RestResource(exported = false) void deleteAll(Iterable<? extends Rider> entities);
    // @Override @RestResource(exported = false) void deleteAllInBatch();
}
