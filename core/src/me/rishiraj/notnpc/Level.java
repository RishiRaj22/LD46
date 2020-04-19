package me.rishiraj.notnpc;

import me.rishiraj.notnpc.entity.Person;

import java.util.List;

public class Level {
    private List<Person> initialPersons;

    public Level(List<Person> initialPersons) {
        this.initialPersons = initialPersons;
    }

    public List<Person> getInitialPersons() {
        return initialPersons;
    }
}
