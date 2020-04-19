package me.rishiraj.notnpc;

public class Popup {
    private String text;
    private float timeLeft;
    private float x,y;
    public Popup(String text, int x, int y) {
        timeLeft = 1.5f;
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public void update(float delta) throws PopupExpiredException {
        timeLeft -= delta;
        if(timeLeft < 0) throw new PopupExpiredException();
    }

    public String getText() {
        return text;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public class PopupExpiredException extends Exception {

    }
}
