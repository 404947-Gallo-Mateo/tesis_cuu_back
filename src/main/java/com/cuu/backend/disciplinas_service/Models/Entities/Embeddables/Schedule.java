package com.cuu.backend.disciplinas_service.Models.Entities.Embeddables;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    private LocalTime startHour;

    private LocalTime endHour;
}
