package sketchwars.character.weapon;

import sketchwars.character.projectiles.*;
import sketchwars.graphics.Texture;

/**
 *
 * @author Najash Najimudeen <najash.najm@gmail.com>
 */
public class GrenadeWeapon extends AbstractWeapon {   
    private double explosionRadius;
    
    public GrenadeWeapon(Texture texture, ProjectileFactory projectileFactory) {
        this(texture, 1, projectileFactory);
    }
    
    public GrenadeWeapon(Texture texture, double scale, ProjectileFactory projectileFactory) {
        super(texture, scale, projectileFactory);
        init();
    }
    
    private void init() {
        explosionRadius = 1;
        setRateOfFire(0.5f);
    }

    public double getExplosionRadius() {
        return explosionRadius;
    }

    public void setExplosionRadius(double explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    @Override
    protected BasicProjectile createProjectile(long vPosition, long vVelocity) {
        return projectileFactory.createGrenade(vPosition, vVelocity, scale);
    }

    @Override
    protected double getProjectileSpeed(float power) {
        return power * 100.0f;
    }

}
