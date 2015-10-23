package sketchwars.map;

import java.awt.Color;
import java.awt.image.BufferedImage;
import sketchwars.graphics.GraphicsObject;
import sketchwars.game.GameObject;
import sketchwars.graphics.Texture;
import sketchwars.physics.BitMask;
import sketchwars.physics.BitMaskFactory;
import sketchwars.physics.BoundingBox;
import sketchwars.physics.PixelCollider;

/**
 *
 * @author Najash Najimudeen <najash.najm@gmail.com>
 */
public abstract class AbstractMap implements GraphicsObject, GameObject {
    private final PixelCollider mapCollider;
    private Texture background;
    private final Texture foreground;
    private final BufferedImage foregroundImage;

    public AbstractMap(PixelCollider mapCollider, Texture background, Texture foreground, BufferedImage foregroundImage) {
        this.mapCollider = mapCollider;
        this.background = background;
        this.foreground = foreground;
        this.foregroundImage = foregroundImage;
    }

    @Override
    public void update(double elapsedMillis) 
    {}

    @Override
    public boolean hasExpired()
    {
        return false;
    }

    public PixelCollider getMapCollider() {
        return mapCollider;
    }

    public Texture getForeground() {
        return foreground;
    }

    public BufferedImage getForegroundImage() {
        return foregroundImage;
    }
    
    public void setBackground(Texture background) {
        this.background = background;
    }
    
    @Override
    public void render() {
        background.draw(null, 0, 0, 2, 2);
        foreground.draw(null, 0, 0, 2, 2);
    }

    public void dispose() {
        background.dispose();
        foreground.dispose();
    }

    public boolean updateTexture(BufferedImage subImage, boolean erase, float xStart, float yStart) {
        int sWidth = foregroundImage.getWidth();
        int sHeight = foregroundImage.getHeight();
                
        int subWidth = subImage.getWidth();
        int subHeight = subImage.getHeight();

        int widthFG = foregroundImage.getWidth();
        int heightFG = foregroundImage.getHeight();
        int xImage = (int)((widthFG/2.0)*(xStart + 1.0))  - 6;
        int yImage = (int)((heightFG/2.0)*(2.0 - (yStart + 1.0))) - 2;
            
        if ((xImage + subWidth) < sWidth && (yImage + subHeight) < sHeight) {
            for (int i = 0; i < subWidth; i++) {
                for (int j = 0; j < subHeight; j++) {
                    int xSet = xImage + i;
                    int ySet = yImage + j;
                    if (xSet < sWidth && ySet < sHeight) {
                        int color = subImage.getRGB(i, j);
                        int alpha = color >> 24;

                        if (alpha != 0) { 
                            if (erase) {
                                foregroundImage.setRGB(xSet, ySet, Color.TRANSLUCENT);
                            } else {
                                foregroundImage.setRGB(xSet, ySet, color);
                            }
                        }
                    }
                }
            }
            BufferedImage replacedRegion = foregroundImage.getSubimage(xImage, yImage, subImage.getWidth(), subImage.getHeight());
            return foreground.setSubTexture(replacedRegion, xImage, yImage, replacedRegion.getWidth(), replacedRegion.getHeight());
        }
        return false;
    }

    public void updateInPhysics(BufferedImage subImage, boolean erase, float xStart, float yStart, float width, float height) {
        BitMask mapBitmask = mapCollider.getPixels();
        
        int xPhysics = (int) ((xStart - 0.018) * 1024.0);
        int yPhysics = (int) ((yStart - 0.025) * 1024.0);
        
        int widthPhysics = (int)(width * 1024.0);
        int heightPhysics = (int)(height * 1024.0);
        
        BoundingBox bb = new BoundingBox(yPhysics, xPhysics, yPhysics + heightPhysics, xPhysics + widthPhysics);
        
        if (erase) {
            BitMaskFactory.updateFromImageAlpha(subImage, mapBitmask, bb, false);
        } else {
            BitMaskFactory.updateFromImageAlpha(subImage, mapBitmask, bb, true);
        }
    }
}
