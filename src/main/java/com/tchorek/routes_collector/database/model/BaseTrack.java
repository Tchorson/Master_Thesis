package com.tchorek.routes_collector.database.model;


import com.tchorek.routes_collector.utils.Timer;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BaseTrack {

    @EmbeddedId
    UserPlaceTime id;

    public BaseTrack(String phoneNumber, String location, long date) {
        this.id = new UserPlaceTime(phoneNumber, location, date);
    }

    public String getPhoneNumber() {
        return id.getPhoneNumber();
    }

    public long getDate() {
        return id.getDate();
    }

    public String getLocation() {
        return id.getLocation();
    }

    public void setPhoneNumber(String phoneNumber) {
        id.setPhoneNumber(phoneNumber);
    }

    @Override
    public String toString() {
        return "Track" + id.getPhoneNumber() + " " + id.getLocation() + " " + Timer.getFullDate(id.getDate()) + "\n";
    }


}

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Embeddable
class UserPlaceTime implements Serializable {

    @Column(name = "user_id")
    private String phoneNumber;

    @Column(name = "device_id")
    private String location;

    @Column(name = "timestamp")
    private long date;
}
