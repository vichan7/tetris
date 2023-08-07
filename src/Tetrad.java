import java.awt.Color;
import java.util.concurrent.Semaphore;

public class Tetrad
{
    private Block[] blocks;
    private MyBoundedGrid<Block> grid;
    private Semaphore lock;

    /**
     * Constructor for Tetrad objects.
     *
     * @param gr      a reference to the grid the blocks will be added to
     */
    public Tetrad(MyBoundedGrid<Block> gr)
    {
        blocks = new Block[4];
        grid = gr;
        lock = new Semaphore(1, true);

        int mid = grid.getNumCols()/2;
        Color[] colors = new Color[]{Color.RED, Color.GRAY, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.BLUE, Color.GREEN};
        Location[][] locs = new Location[][]{
            {new Location(1,mid), new Location(0,mid), new Location(2,mid), new Location(3,mid)},//I
            {new Location(0,mid), new Location(0,mid+1), new Location(0,mid-1), new Location(1,mid)},//T
            {new Location(0,mid), new Location(0,mid+1), new Location(1,mid), new Location(1,mid+1)},//O
            {new Location(1,mid), new Location(0,mid), new Location(2,mid), new Location(2,mid+1)},//L
            {new Location(1,mid), new Location(0,mid), new Location(2,mid), new Location(2,mid-1)},//J
            {new Location(0,mid), new Location(0,mid+1),new Location(1,mid-1), new Location(1,mid)},//S
            {new Location(0,mid), new Location(0,mid-1), new Location(1, mid), new Location(1,mid+1)}};//Z

        int random = (int) (Math.random() * 7);
        for(int i = 0; i < blocks.length; i++)
        {
            blocks[i] = new Block();
            blocks[i].setColor(colors[random]);
        }
        addToLocations(grid, locs[random]);
    }
    /**
     * Adds these blocks to the given grid at the given locations.
     *
     * @param grid       a reference to the grid the blocks will be added to
     * @param locs      a reference to an array of length 4 containing the locations the
     *                  blocks will be added at
     * @precondition The blocks are not in any grid
     * @postcondition The locations of blocks match locs, and blocks have been
     * put in the grid.
     */
    private void addToLocations(MyBoundedGrid<Block> grid, Location[] locs)
    {
        for(int i = 0; i < locs.length; i++)
        {
            blocks[i].putSelfInGrid(grid,locs[i]);
        }
    }

    /**
     * Removes these blocks from the grid.
     *
     * @precondition Blocks are in the grid.
     * @postcondition Blocks have been removed from the grid.
     * @return Returns an array containing the old locations of the blocks.
     */
    private Location[] removeBlocks()
    {
        Location[] removed = new Location[blocks.length];
        for(int i = 0; i < blocks.length; i++)
        {
            removed[i] = blocks[i].getLocation();
            blocks[i].removeSelfFromGrid();
        }
        return removed;
    }

    /**
     * Determines whether each of the locations in locs is empty and valid.
     *
     * @param grid      a reference to the grid whose locations will be tested
     * @param locs      a reference to the array containing the locations to be tested
     * @return  Returns true if all of the given locations are empty and valid.
     */
    private boolean areEmpty(MyBoundedGrid<Block> grid, Location[] locs)
    {
        for(Location l : locs)
        {
            if(grid.get(l) != null || !grid.isValid(l))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Moves this tetrad deltaRow rows down and deltaCol columns to the right
     * if those position are valid and empty.
     *
     * @param deltaRow      the number of rows the tetrad will be moved down
     * @param deltaCol      the number of columns will be moved to the right
     * @postcondition This tetrad was moved deltaRow rows down and deltaCol columns
     * to the right if those positions are valid and empty.
     * @return Returns true if this tetrad was successfully moved; false otherwise.
     */
    public boolean translate(int deltaRow, int deltaCol)
    {
        try
        {
            lock.acquire();
            Location[] oldLocs = removeBlocks();
            Location[] newLocs = new Location[oldLocs.length];
            for(int i = 0; i < newLocs.length; i++)
            {
                newLocs[i] = new Location(oldLocs[i].getRow()+deltaRow, oldLocs[i].getCol()+deltaCol);
            }
            if(areEmpty(grid, newLocs))
            {
                addToLocations(grid, newLocs);
                return true;
            }
            else
            {
                addToLocations(grid, oldLocs);
                return false;
            }

        }
        catch (InterruptedException e)
        {
            // did not modify the tetrad
            return false;
        }
        finally
        {
            lock.release();
        }
    }

    /**
     * Rotates this tetrad clockwise by 90 degrees about its center,
     * if the necessary positions are empty.
     *
     * @postcondition If the necessary positions are empty, this tetradd
     * was rotated clockwise 90 degrees about its center.
     * @return Returns true if this tetrad was successfully rotated; false otherwise.
     */
    public boolean rotate()
    {
        try
        {
            lock.acquire();
            Location[] oldLocs = removeBlocks();
            Location[] newLocs = new Location[oldLocs.length];
            int row0 = oldLocs[0].getRow();
            int col0 = oldLocs[0].getCol();
            for(int i = 0; i < newLocs.length; i++)
            {
                newLocs[i] = new Location(row0-col0+oldLocs[i].getCol(),row0+col0-oldLocs[i].getRow());
            }
            if(areEmpty(grid, newLocs))
            {
                addToLocations(grid, newLocs);
                return true;
            }
            else
            {
                addToLocations(grid, oldLocs);
                return false;
            }
        }
        catch (InterruptedException e)
        {
            // did not modify the tetrad
            return false;
        }
        finally
        {
            lock.release();
        }
    }
}
