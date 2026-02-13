package com.example.praktika1;

public class Section {
    public String id, name, coach, schedule;
    public int maxSpots, currentSpots;

    public Section() {}

    public Section(String id, String name, String coach, String schedule, int maxSpots) {
        this.id = id;
        this.name = name;
        this.coach = coach;
        this.schedule = schedule;
        this.maxSpots = maxSpots;
        this.currentSpots = 0;
    }
}