package me.rishiraj.notnpc.entity;

public class Person extends GameCharacter {

    private boolean infected;
    public Person(float x, float y, float speedX, float speedY, float angle, boolean infected) {
        super();
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.width = 32;
        this.height = 32;
        this.infected = infected;
        this.angle = angle;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
