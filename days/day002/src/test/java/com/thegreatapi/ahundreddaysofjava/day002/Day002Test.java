package com.thegreatapi.ahundreddaysofjava.day002;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class Day002Test {

    @Test
    void formatDate() {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        LocalDateTime currentDateTime = LocalDateTime.of(2020, 1, 31, 20, 35, 45);
        ZonedDateTime zonedTime = Instant.now().atZone(ZoneId.systemDefault());
        String instantTime = zonedTime.format(FORMATTER);
        System.out.println("instantTime "+ instantTime);
        assertThat(currentDateTime.format(FORMATTER))
                .isEqualTo("31/01/2020 20:35:45");
    }
}
