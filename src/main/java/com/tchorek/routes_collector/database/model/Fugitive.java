package com.tchorek.routes_collector.database.model;

import com.tchorek.routes_collector.utils.Timer;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "fugitives")
public class Fugitive {

    @Id
    @Column(name = "user_id", nullable = false)
    private String phoneNumber;

    @Column(name = "latitude")
    private Float latitude;

    @Column(name = "longitude")
    private Float longitude;

    @Column(name = "escape_date", nullable = false)
    private long date;

    @Column(name = "is_reported")
    private boolean isReported;

    @Override
    public String toString() {
        return "Fugitive:" + phoneNumber + ", lat:" + latitude + ", lng:" + longitude+ " ,escape date: "+ Timer.getFullDate(date) +" "+ isReported +"\n";
    }
}
