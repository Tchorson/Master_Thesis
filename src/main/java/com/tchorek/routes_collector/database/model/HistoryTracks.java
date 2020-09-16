package com.tchorek.routes_collector.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "user_routes_history")
public class HistoryTracks extends BaseTrack {
}
