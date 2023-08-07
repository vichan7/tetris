import java.awt.Color;
/**
* class BLock encapsulates a Block abstraction which can be placed into a Gridworld style grid
* You are expected to comment this class according to the style guide.
* @author 
*/
public class Block
{
    private MyBoundedGrid<Block> grid;
    private Location location;
    private Color color;
	/**
    * constructs a blue block, because blue is the greatest color ever!
    */
    public Block()
    {
        color = Color.BLUE;
        grid = null;
        location = null;
    }
	/**
	* 
    */
    public Color getColor()
    {
        return color;
    }
	/**
	* 
	*/
    public void setColor(Color newColor)
    {
        color = newColor;
    }
    
	/**
    * 
    */
    public MyBoundedGrid<Block> getGrid()
    {
        return grid;
    }
    
	/**
	* 
	*/
    public Location getLocation()
    {
        return location;
    }
    
	/**
	* 
	*/
    public void removeSelfFromGrid()
    {
        grid.remove(getLocation());
        location = null;
        grid = null;
    }
    
	/**
	* 
	*/
    public void putSelfInGrid(MyBoundedGrid<Block> gr, Location loc)
    {
        if(gr.get(loc) != null)
        {
            Block block = gr.remove(loc);
            block.removeSelfFromGrid();
        }
        gr.put(loc,this);
        location = loc;
        grid = gr;
    }

    /**
	*
	*/
    public void moveTo(Location newLocation)
    {
        MyBoundedGrid<Block> gr = getGrid();
        removeSelfFromGrid();
        putSelfInGrid(gr, newLocation);
    }

    /**
	* returns a string with the location and color of this block
	*/
    public String toString()
    {
        return "Block[location=" + location + ",color=" + color + "]";
    }
}