package sketchwars.character.projectiles;

import sketchwars.graphics.Texture;
import sketchwars.character.SketchCharacter;

import sketchwars.graphics.Texture;
import sketchwars.physics.*;
/**
 *
 * @author Najash Najimudeen <najash.najm@gmail.com>
 */
public class RangedProjectile extends AbstractProjectile {
    public static final double RANGE = 100;
    private static final int DAMAGE = 35;
    private static final float DRAW_SIZE = 10.0f / 1024.0f;

    public RangedProjectile(Texture texture, SketchCharacter owner) {
        super(texture, owner, DAMAGE);
    }

    @Override
    public void render() {
        if (texture != null) {
            BoundingBox bounds = coll.getBounds();
            long vCenter = bounds.getCenterVector();
            texture.draw(null, (float)Vectors.xComp(vCenter) / 1024.0f , (float)Vectors.yComp(vCenter) / 1024.0f, DRAW_SIZE, DRAW_SIZE);
        }
    }

    //do damage to the character we hit
    @Override
    protected void handleCollisionWithCharacter(SketchCharacter ch) {
        if(ch != owner) {
            ch.takeDamage(damage);
            System.out.println(ch + " is hit for " + damage + " damage.");
            expired = true;
        }
    }

    //expire on any collision except for with owner
    @Override
    protected void handleCollision(Collider c) {
        if(c != owner.getCollider()) {
            expired = true;
        }
    }
}
