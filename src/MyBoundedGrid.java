import java.util.*;

public class MyBoundedGrid<E>
{
    private Object[][] locations;

    public MyBoundedGrid(int rows, int cols)
    {
        locations = new Object[rows][cols];
    }

    public int getNumRows()
    {
        return locations.length;
    }

    public int getNumCols()
    {
        return locations[0].length;
    }

    public boolean isValid(Location loc)
    {
        return loc.getRow() >= 0 && loc.getRow() < getNumRows()
                && loc.getCol() >= 0 && loc.getCol() < getNumCols();
    }

    public E put(Location loc, E obj)
    {
        Object temp = null;
        if(isValid(loc) && locations[loc.getRow()][loc.getCol()] != null)
        {
            temp = locations[loc.getRow()][loc.getCol()];
        }
        if(isValid(loc))
        {
            locations[loc.getRow()][loc.getCol()] = obj;
        }
        return (E) temp;
    }

    public E remove(Location loc)
    {
        Object temp = null;
        if(isValid(loc) && locations[loc.getRow()][loc.getCol()] != null)
        {
            temp = locations[loc.getRow()][loc.getCol()];
            locations[loc.getRow()][loc.getCol()] = null;
        }
        return (E) temp;
    }

    public E get(Location loc)
    {
        if(isValid(loc))
        {
            return (E) locations[loc.getRow()][loc.getCol()];
        }
        return null;
    }

    public ArrayList<Location> getOccupiedLocations()
    {
        ArrayList<Location> occupied = new ArrayList<Location>();
        for(int i = 0; i < locations.length; i++)
        {
            for(int j = 0; j < locations[0].length; j++)
            {
                if(locations[i][j] != null)
                {
                    occupied.add(new Location(i, j));
                }
            }
        }
        return occupied;
    }

}
