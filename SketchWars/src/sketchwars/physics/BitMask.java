package sketchwars.physics;

public class BitMask
{
    private static final int BITS_PER_LONG = 64;
    private static final long[] LONG_MASKS = new long[BITS_PER_LONG];

    static
    {
        for(int i = 0; i < BITS_PER_LONG; i++)
        {
            LONG_MASKS[BITS_PER_LONG - 1 - i] = 1l << (long)i;
        }
    }
	//pixel data in local space
    private long[][] data;
	//in local coordinate space (disregards the offset vector)
    private BoundingBox bounds;
	//translation from local space to world space
    private long vOffset;

	BitMask(final long[][] data, long vPosition)
	{
		if(data == null)
        {
            this.data = new long[1][];
        }
        else 
        {
            this.data = data.clone();
        }
        for(int i = 0; i < this.data.length; i++)
        {
            if(this.data[i] != null)
            {
                this.data[i] = this.data[i].clone();
            }
        }
        ensureRectangular();
		updateBounds();
        vOffset = vPosition;
	}
	
    BitMask(final long[][] data)
    {
        this(data, Vectors.create(0,0));
    }

    public int getWidth()
    {
        return bounds.getWidth();
    }

    public int getHeight()
    {
        return bounds.getHeight();
    }

    public int getArea()
    {
        return getWidth() * getHeight();
    }
	
	public long getPosition()
	{
		return vOffset;
	}
	
	public void setPosition(long vPos)
	{
		vOffset = vPos;
	}
	
	private void updateBounds()
	{
        bounds = new BoundingBox(0, 0, this.data.length - 1, this.data[0].length * BITS_PER_LONG - 1);
	}
	
    //returns a vector
    public long getCenterOfMass()
    {
        double xCOM = 0.0;
        double yCOM = 0.0;
        int numPoints = 0;
        for(int i = bounds.getTop(); i <= bounds.getBottom(); i++)
        {
            for(int j = bounds.getLeft(); j <= bounds.getRight(); j++)
            {
                final int jLongIdx = j / BITS_PER_LONG;
                final int jBitIdx = j % BITS_PER_LONG;
                if((data[i][jLongIdx] & LONG_MASKS[jBitIdx]) != 0l)
                {
                    yCOM += i;
                    xCOM += j;
                    numPoints++;
                }
            }
        }
        xCOM /= numPoints;
        yCOM /= numPoints;
        return Vectors.add(Vectors.create(xCOM, yCOM), vOffset);
    }

    public BoundingBox getBounds()
    {
		if(bounds == null)
		{
			return null;	
		}
        return bounds.getTranslatedBox(vOffset);
    }

    public boolean isEmpty()
    {
        return getArea() == 0;
    }

    public BitMask and(BitMask other)
    {
        BoundingBox intersection = getBounds().intersection(other.getBounds());
        if(intersection == null)
        {
            return new BitMask(null);
        }
		long vResultOffset = intersection.getTopLeftVector();
		long thisToResult = Vectors.subtract(vResultOffset, vOffset);
		long otherToResult = Vectors.subtract(vResultOffset, other.vOffset);
		//shift intersection s.t. topLeft = (0,0)
		intersection = intersection.getTranslatedBox(Vectors.reverse(vResultOffset));
        long[][] resultData = new long[intersection.getHeight()][(int)Math.ceil((double)intersection.getWidth() / (double)BITS_PER_LONG)];
		for(int resultI = intersection.getTop(); resultI <= intersection.getBottom(); resultI++)
        {
            final int localI = resultI + Vectors.iyComp(thisToResult);
			final int otherI = resultI + Vectors.iyComp(otherToResult);
            for(int resultJ = intersection.getLeft(); resultJ <= intersection.getRight(); resultJ += BITS_PER_LONG)
            {
                final int localJ = resultJ + Vectors.ixComp(thisToResult);
				final int otherJ = resultJ + Vectors.ixComp(otherToResult);
				final int resultJLongIdx = resultJ / BITS_PER_LONG;
                resultData[resultI][resultJLongIdx] = getSubmaskElement(localI, localJ) & other.getSubmaskElement(otherI, otherJ);
            }
        }
        return new BitMask(resultData, vResultOffset);
    }

    //get 64 bits from row row, starting from bit start.
    protected long getSubmaskElement(int row, int start)
    {
        final int startIdx = start / BITS_PER_LONG;
        final long bitIdx = start % BITS_PER_LONG;
        if(bitIdx <= -BITS_PER_LONG)
        {
            return 0l;
        }
        else if(bitIdx < 0)
        {
            return data[row][0] >>> (-bitIdx);
        }
        else if(bitIdx == 0)
        {
            if(isInBounds(startIdx))
                return data[row][startIdx];
            else
                return 0l;
        }
        else
        {
            long ret = 0l;
            if(isInBounds(startIdx))
            {
                long retLeft = data[row][startIdx] << bitIdx;
                ret |= retLeft;
            }
            if(isInBounds(startIdx+1))
            {
                long retRight = data[row][startIdx + 1] >>> ((long)BITS_PER_LONG - bitIdx);
                ret |= retRight;
            }
            return ret;
        }
    }

    private boolean isInBounds(int j)
    {
        return j >= 0 && j < data[0].length;
    }
    //minimize the bounds of the mask by removing empty rows and columns on the edges.
    public void trim()
    {
        int top = findFirstNonemptyRow(data);
        if(top == -1)
        {
            data = null;
            bounds = null;
            return;
        }
        int bottom = findLastNonemptyRow(data);
        int left = findFirstNonemptyColumn(data);
        int right = findLastNonemptyColumn(data);
        bounds = new BoundingBox(top, left, bottom, right);
    }


    private int findFirstNonemptyRow(long[][] bits)
    {
        for(int i = 0; i < bits.length; i++)
        {
            if(!rowIsEmpty(bits[i]))
            {
                return i;
            }
        }
        return -1;
    }

    private int findLastNonemptyRow(long[][] bits)
    {
        for(int i = bits.length - 1; i >= 0; i--)
        {
            if(!rowIsEmpty(bits[i]))
            {
                return i;
            }
        }
        return -1;
    }

    private boolean rowIsEmpty(long[] row)
    {
        for(int i = 0; i < row.length; i++)
        {
            if(row[i] != 0l)
            {
                return false;
            }
        }
        return true;
    }

    private int findFirstNonemptyColumn(long[][] bits)
    {
        int min = -1;
        for(int i = 0; i < bits.length; i++)
        {
            int test = findFirstInRow(bits[i]);
            if(min == -1)
            {
                min = test;
            }
            else if(test != -1)
            {
                min = Math.min(test, min);
            }
        }
        return min;
    }

    private int findFirstInRow(long[] row)
    {
        for(int i = 0; i < row.length; i++)
        {
            int numLeadingZeros = (int) Long.numberOfLeadingZeros(row[i]);
            if(numLeadingZeros != BITS_PER_LONG)
            {
                return (i * BITS_PER_LONG) + numLeadingZeros;
            }
        }
        return -1;
    }

    private int findLastNonemptyColumn(long[][] bits)
    {
        int max = -1;
        for(int i = 0; i < bits.length; i++)
        {
            int test = findLastInRow(bits[i]);
            if(max == -1)
            {
                max = test;
            }
            else if(test != -1)
            {
                max = Math.max(test, max);
            }
        }
        return max;
    }

    private int findLastInRow(long[] row)
    {
        for(int i = row.length - 1; i >= 0; i--)
        {
            int numTrailingZeros = (int) Long.numberOfTrailingZeros(row[i]);
            if(numTrailingZeros != BITS_PER_LONG)
            {
                return (i * BITS_PER_LONG) + (BITS_PER_LONG - 1 - numTrailingZeros);
            }
        }
        return -1;
    }

    private void ensureRectangular()
    {
        int maxWidth = getMaxWidth();
        padToWidth(maxWidth);
    }

    private int getMaxWidth()
    {
        int maxWidth = 0;
        for(int i = 0; i < data.length; i++)
        {
            if(data[i] != null)
            {
                maxWidth = Math.max(maxWidth, data[i].length);
            }
        }
        return maxWidth;
    }

    private void padToWidth(int width)
    {
        for(int i = 0; i < data.length; i++)
        {
            if(data[i] == null)
            {
                data[i] = new long[width];
            }
            else if(data[i].length < width)
            {
                long[] newRow = new long[width];
                System.arraycopy(data[i], 0, newRow, 0, data[i].length);
            }
        }
    }
}