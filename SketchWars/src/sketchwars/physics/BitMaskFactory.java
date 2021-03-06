package sketchwars.physics;

import static sketchwars.physics.BitMask.*;
import static sketchwars.physics.Vectors.*;

import java.awt.image.BufferedImage;
import static sketchwars.SketchWars.PHYSICS_HEIGHT;
import static sketchwars.SketchWars.PHYSICS_LEFT;
import static sketchwars.SketchWars.PHYSICS_TOP;
import static sketchwars.SketchWars.PHYSICS_WIDTH;

/**
 * Static factory methods for creating BitMasks
 * @author brian <bthomas50@my.bcit.ca>
 */
public class BitMaskFactory
{
    private static long ALL_ONES = 0xFFFFFFFFFFFFFFFFl;

    private BitMaskFactory()
    {}

    public static BitMask createEmpty()
    {
        return new BitMask();
    }

    public static BitMask createEmpty(BoundingBox bounds)
    {
        return new BitMask(bounds);
    }

    public static BitMask createFromImageAlpha(BufferedImage im, BoundingBox bounds)
    {
        BitMask ret = new BitMask(bounds);
        float width = (float)bounds.getWidth();
        float height = (float)bounds.getHeight();
        float widthRatio = (float) im.getWidth() / width;
        float heightRatio = (float) im.getHeight() / height;
        for(int i = bounds.getTop(); i <= bounds.getBottom(); i++)
        {
            float relativeI = (float)(i - bounds.getTop());
            for(int j = bounds.getLeft(); j <= bounds.getRight(); j++)
            {
                float relativeJ = (float)(j - bounds.getLeft());
                int imageI = im.getHeight() - 1 - (int) ((float)relativeI * heightRatio);
                int imageJ = (int) ((float)relativeJ * widthRatio);
                int alpha = im.getRGB(imageJ, imageI) >> 24;
                if(alpha != 0)
                {
                    ret.setBit(i, j);
                }
            }
        }
        ret.trim();
        return ret;
    }
    
    public static void updateFromImageAlpha(BufferedImage im, BitMask bitmask, BoundingBox bounds, boolean set) {
        float width = (float)bounds.getWidth();
        float height = (float)bounds.getHeight();
        float widthRatio = (float) im.getWidth() / width;
        float heightRatio = (float) im.getHeight() / height;
        
        BoundingBox physicsBounds =  new BoundingBox(PHYSICS_TOP, PHYSICS_LEFT, 
                PHYSICS_TOP + PHYSICS_WIDTH, PHYSICS_LEFT + PHYSICS_WIDTH);
        BoundingBox intersection = physicsBounds.intersection(bounds);
        
        //no part of the bitmap will be affected.
        if(intersection == BoundingBox.EMPTY) {
            return;
        }
        
        for(int i = intersection.getTop(); i <= intersection.getBottom(); i++)
        {
            float relativeI = (float)(i - intersection.getTop());
            for(int j = intersection.getLeft(); j <= intersection.getRight(); j++)
            {
                float relativeJ = (float)(j - intersection.getLeft());
                int imageI = im.getHeight() - 1 - (int) ((float)relativeI * heightRatio);
                int imageJ = (int) ((float)relativeJ * widthRatio);
                int alpha = im.getRGB(imageJ, imageI) >> 24;
                if(alpha != 0)
                {
                    if (set) {
                        bitmask.setBit(i, j);
                    } else {
                        bitmask.unsetBit(i, j);
                    }
                }
            }
        }
    }

	public static BitMask createLine(final long vPt1, final long vPt2)
	{
		long vDirection = subtract(vPt2, vPt1);
		double numSamples = Math.ceil(length(vDirection));
		long vNormalizedDirection = normalize(vDirection);
		BitMask ret = new BitMask(new BoundingBox(vPt1, vPt2));
		//System.out.println(ret.getBounds());
		for(double extent = 0; extent <= numSamples; extent += 1.0)
		{
			long vPtInLine = add(vPt1, scalarMultiply(vNormalizedDirection, extent));
			int i = iyComp(vPtInLine);
			int j = ixComp(vPtInLine);
			ret.setBit(i, j);
		}
		return ret;
	}
	
	public static BitMask createLine(long vOrigin, long vDirection, double length)
	{
		return createLine(vOrigin, add(vOrigin, scaleToLength(vDirection, length)));
	}
	
    public static BitMask createCircle(double radius)
    {
        double r2 = radius * radius;
        BoundingBox bounds = new BoundingBox(0, 0, (int) Math.ceil(2 * radius), (int) Math.ceil(2 * radius));
        long[][] data = new long[bounds.getHeight()][getNumberOfLongs(bounds.getWidth())];
        for(int i = bounds.getTop(); i <= bounds.getBottom(); i++)
        {
            double y = (double) i - radius;
            double y2 = y * y;
            //x^2 + y^2 = r^2
            //x = +-sqrt(r^2 - y^2)
            if(y2 > r2)
                continue;
            double xPos = Math.sqrt(r2 - y2);
            double xNeg = -xPos;
            int jStart = (int) Math.floor(xNeg + radius);
            int jEnd = (int) Math.ceil(xPos + radius);
            setRowOfBits(data[i], jStart, jEnd);
        }
        return new BitMask(data);
    }
    
    public static BitMask createRectangle(int width, int height)
    {
        return createRectangle(new BoundingBox(0, 0, height - 1, width - 1));
    }

    public static BitMask createRectangle(BoundingBox bounds)
    {
        long vTopLeft = bounds.getTopLeftVector();
        bounds = bounds.getTranslatedBox(Vectors.reverse(vTopLeft));

        long[][] data = new long[bounds.getHeight()][getNumberOfLongs(bounds.getWidth())];
        for(int i = bounds.getTop(); i <= bounds.getBottom(); i++)
        {
            setRowOfBits(data[i], bounds.getLeft(), bounds.getRight());
        }
        return new BitMask(data, vTopLeft);
    }

    private static void setRowOfBits(long[] dest, int start, int end)
    {
        int firstFullLong = setFirstLongIfIrregular(dest, start, end);
        if(firstFullLong > end)
        {
            return;
        }
        int lastFullLong = setLastLongIfIrregular(dest, end);
        for(int j = firstFullLong; j <= lastFullLong; j += BITS_PER_LONG)
        {
            int jLongIdx = j / BITS_PER_LONG;
            dest[jLongIdx] = ALL_ONES;
        }
    }
    //returns the aligned bit index after what was written.
    private static int setFirstLongIfIrregular(long[] dest, int start, int end)
    {
        int startOffset = start % BITS_PER_LONG;
        if(startOffset != 0)
        {
            int startLongIdx = start / BITS_PER_LONG;
            int endLongIdx = end / BITS_PER_LONG;
            long bits;
            if(startLongIdx == endLongIdx)
            {
                bits = getBits(end - start + 1, start);
            }
            else
            {
                bits = getBitsOnRight(startOffset);
            }
            dest[startLongIdx] = bits;
            return (startLongIdx + 1) * BITS_PER_LONG;
        }
        else
        {
            return start;
        }
    }
    //returns the aligned bit index before what was written.
    private static int setLastLongIfIrregular(long[] dest, int end)
    {
        int endOffset = 1 + (end % BITS_PER_LONG);
        if(endOffset != BITS_PER_LONG)
        {
            int endLongIdx = end / BITS_PER_LONG;
            long bits = getBitsOnLeft(endOffset);
            dest[endLongIdx] = bits;
            return (endLongIdx - 1) * BITS_PER_LONG;
        }
        else
        {
            return end;
        }
    }
    private static long getBits(long numBits, long firstBitOffset)
    {
        return getBitsOnLeft(numBits) >>> firstBitOffset;
    }

    private static long getBitsOnRight(long numBits)
    {
        return ~(ALL_ONES << numBits);
    }

    private static long getBitsOnLeft(long numBits)
    {
        return ~(ALL_ONES >>> numBits);
    }
}