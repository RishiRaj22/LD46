package me.rishiraj.notnpc;

public interface LineEndListener {
    public void lineEnded(Line line);
    public void lineEndedByCollision(Line line);
    public void lineEndedPrematurely(Line line);
}
