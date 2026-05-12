package com.org.devPulse.repository;

import com.org.devPulse.entity.LastSeenRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastSeenReleaseRepo extends JpaRepository<LastSeenRelease, String> {
}
