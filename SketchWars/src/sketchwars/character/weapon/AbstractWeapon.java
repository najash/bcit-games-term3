package sketchwars.character.weapon;

import sketchwars.game.GameObject;
import sketchwars.character.projectiles.*;
import sketchwars.graphics.GraphicsObject;
import sketchwars.graphics.Texture;
import sketchwars.character.SketchCharacter;
import sketchwars.physics.Vectors;

/**
 *
 * @author Najash Najimudeen <najash.najm@gmail.com>
 */

public abstract class AbstractWeapon implements GameObject, GraphicsObject {
    public static final int INFINITE_AMMO = -1;
    
    private float rateOfFire; //per second
    private double lastTimeFired;
    private double elapsed;
            
    protected double posX;
    protected double posY;
    protected double scale;
    protected Texture texture;
    
    protected int ammo;
    protected ProjectileFactory projectileFactory;

    public AbstractWeapon(Texture texture, double scale, ProjectileFactory projectileFactory) {
        this.texture = texture;
        this.scale = scale;
        this.projectileFactory = projectileFactory;
        
        rateOfFire = 1;
        ammo = INFINITE_AMMO;
        elapsed = Integer.MAX_VALUE;
    }
    
    
    
    @Override
    public void render() {
        if (texture != null) {
            texture.drawNormalized(posX, posY, scale);
        }
    }
    
    @Override
    public void update(double elapsed) {
        this.elapsed += elapsed;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosition(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void tryToFire(SketchCharacter owner, float power, long direction) {
        double timeFired = elapsed;
        double timeSinceLastFired = timeFired - lastTimeFired;
        float rateOfFireInMilli = 1000/rateOfFire;
                            
        if (timeSinceLastFired > rateOfFireInMilli) {
            fire(owner, power, direction);
            lastTimeFired = timeFired;
        }
    }

    private void fire(SketchCharacter owner, float power, long direction) {
        long normalDir = Vectors.normalize(direction);
        long vVelocity = Vectors.scalarMultiply(getProjectileSpeed(power), normalDir);
        long vPosition = Vectors.add(owner.getCollider().getPosition(), Vectors.scaleToLength(normalDir, 100.0));
        System.out.println("velocity: " + Vectors.toString(vVelocity));
        BasicProjectile projectile = createProjectile(owner, vPosition, vVelocity);

        projectile.setPower(power);
        projectile.setDirection(direction);
    }

    protected abstract BasicProjectile createProjectile(SketchCharacter owner, long vPosition, long vVelocity);

    protected abstract double getProjectileSpeed(float power);

    public float getRateOfFire() {
        return rateOfFire;
    }

    /**
     * per second
     * @param rateOfFire 
     */
    public void setRateOfFire(float rateOfFire) {
        this.rateOfFire = rateOfFire;
    }

    public int getAmmo()
    {
        return ammo;
    }
    public void setAmmo(int ammo)
    {
        this.ammo = ammo;
    }
    public void increaseAmmo(int num)
    {
        ammo += num;
    }
    public void decreaseAmmo(int num)
    {
        if(ammo != INFINITE_AMMO)
        {
            ammo -= num;
            ammo = Math.max(ammo, 0);
        }
    }
}
