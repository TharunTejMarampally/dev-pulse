package com.org.devPulse.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LastSeenRelease {
    @Id
    private String feedName;  // SpringBoot, SpringFramework, OpenJDK

    private String lastReleaseId;  // unique identifier for the last seen release, e.g., tag name or version number
}
