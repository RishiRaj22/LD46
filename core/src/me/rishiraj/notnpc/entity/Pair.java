package me.rishiraj.notnpc.entity;

public class Pair<T> {
    private T first;
    private T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public T getSecond() {
        return second;
    }

    public void setSecond(T second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair pair = (Pair) obj;
            return (pair.first.equals(this.first) && pair.second.equals(this.second));
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getFirst() + "," + this.getSecond();
    }
}
