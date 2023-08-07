import java.util.*;
import java.util.concurrent.Semaphore;

public class Tetris implements ArrowListener
{
    private MyBoundedGrid<Block> grid;
    private BlockDisplay display;
    private Tetrad tetrad;
    private int downCount;
    private Tetrad next;
    private Semaphore lock;

    /**
     * Constructor for Tetris objects.
     */
    public Tetris()
    {
        grid = new MyBoundedGrid<Block>(20, 10);
        tetrad = new Tetrad(grid);
        display = new BlockDisplay(grid);
        display.setTitle("Tetris");
        display.showBlocks();
        display.setArrowListener(this);
        downCount = 0;
        next = null;
        lock = new Semaphore(1, true);
    }

    /**
     * Creates a tetris object and plays tetris.
     *
     * @param args      unused in this method
     */
    public static void main(String[] args)
    {
        Tetris tetris = new Tetris();
        tetris.play();
    }

    /**
     * Rotates this tetrad clockwise by 90 degrees about its center
     * and updates the display.
     *
     * @postcondition This tetrad was rotated and display was updated.
     */
    public void upPressed()
    {
        tetrad.rotate();
        display.showBlocks();
    }

    /**
     * Moves this tetrad down one row and updates the display.
     *
     * @postcondition This tetrad was moved down one row and
     * display was updated.
     */
    public void downPressed()
    {
        if(tetrad.translate(1, 0))
        {
            incrementDownCount();
        }
        display.showBlocks();
    }

    /**
     * Moves this tetrad left one column and updates the display.
     *
     * @postcondition This tetrad was moved left one column and
     * display was updated.
     */
    public void leftPressed()
    {
        tetrad.translate(0, -1);
        display.showBlocks();
    }

    /**
     * Moves this tetrad right one column and updates the display.
     *
     * @postcondition This tetrad was moved right one column and
     * display was updated.
     */
    public void rightPressed()
    {
        tetrad.translate(0, 1);
        display.showBlocks();
    }

    /**
     * Moves this tetrad as far down as it can go and updates the display.
     *
     * @postcondition This tetrad was moved as far down as possible and
     * dispaly was updated.
     */
    public void spacePressed() {
        while(tetrad.translate(1, 0))
        {
            downPressed();
        }
    }

    /**
     * Repeatedly pauses for 1 second, moves the active tetrad down one row,
     * clears all completed rows, and redraws the display. If the active tetrad cannot be shifted down
     * further, creates a new active tetrad.
     *
     * @postcondition The active tetrad is moved down as far as possible,
     * all completed rows were cleared, and a new active tetrad was created.
     */
    public void play()
    {
        downCount = 0;
        while(true)
        {
            if(tetrad.translate(1, 0))
            {
                try
                {
                    //Pause for 1000 milliseconds.
                    Thread.sleep(1000);
                }
                catch(InterruptedException e)
                {
                    //ignore
                }
                downPressed();

            }
            else
            {
                clearCompletedRows();
                try
                {
                    lock.acquire();
                    if(next == null)
                    {
                        tetrad = new Tetrad(grid);
                        downCount = 0;
                    }
                    else
                    {
                        tetrad = next;
                        next = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.release();
                }
            }
        }
    }

    /**
     * Determines whether the given row is completely occupied.
     *
     * @param row       0 <= row < number of rows in this grid
     * @return Returns true if every cell in this row is occupied;
     * false otherwise;
     */
    private boolean isCompleted(int row)
    {
        for(int i = 0; i < grid.getNumCols(); i++)
        {
            Location temp = new Location(row, i);
            if(grid.get(temp) == null)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes all blocks from the given row.
     *
     * @precondition The given row is full of blocks.
     * @param row       0 <= row < number of rows in this grid
     * @postcondition Every block in the given row has been removed,
     * and every block above the given row has been moved down one row.
     */
    private void clearRow(int row)
    {

        try {
            lock.acquire();
            for(int i = 0; i < grid.getNumCols(); i++)
            {
                Location temp = new Location(row, i);
                grid.remove(temp);
            }
            ArrayList<Location> occupied = grid.getOccupiedLocations();
            for(Location loc : occupied)
            {
                if(loc.getRow() < row)
                {
                    Block b = grid.get(loc);
                    b.moveTo(new Location(loc.getRow()+1,loc.getCol()));
                }
            }
            downCount++;
        } catch (Exception e) {
        } finally {
            lock.release();
        }
    }

    /**
     * Clears all completed rows in the grid.
     *
     * @postcondition All completed rows have been cleared.
     */
    private void clearCompletedRows()
    {
        for(int i = 0; i < grid.getNumRows(); i++)
        {
            if(isCompleted(i))
            {
                clearRow(i);
            }
        }
    }

    private void incrementDownCount()
    {
        try
        {
            lock.acquire();
            if(next == null)
            {
                downCount++;
            }
            if(downCount == 4)
            {
                next = new Tetrad(grid);
                downCount = 0;
            }
        }
        catch (InterruptedException e)
        {
// did not modify the tetrad
        }
        finally
        {
            lock.release();
        }
    }
}
