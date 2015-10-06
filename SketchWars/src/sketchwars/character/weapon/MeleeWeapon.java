package sketchwars.character.weapon;

import sketchwars.character.projectiles.*;
import sketchwars.graphics.Texture;

/**
 *
 * @author Najash Najimudeen <najash.najm@gmail.com>
 */
public class MeleeWeapon extends AbstractWeapon {
    public MeleeWeapon(Texture texture, ProjectileFactory projectileFactory) {
        this(texture, 1, projectileFactory);
    }
    
    public MeleeWeapon(Texture texture, double scale, ProjectileFactory projectileFactory) {
        super(texture, scale, projectileFactory);
        setRateOfFire(0.5f);
    }
    
    @Override
    public void render() {
        if (texture != null) {
            texture.drawNormalized(posX, posY, scale);
        }
    }

    @Override
    public BasicProjectile createProjectile(long vPosition, long vVelocity) {
        return projectileFactory.createMelee(vPosition, vVelocity, scale);
    }

    @Override
    protected double getProjectileSpeed(float power) {
        return 10.0f;
    }

}
