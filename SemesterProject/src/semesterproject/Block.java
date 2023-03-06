/*
 * file: Block.java
 * author: BlockHeads
 * class: CS 4450 - Computer Graphics
 *
 * assignment: Check Point 2
 * data last modified: 10/12/2020
 *
 * purpose: This class will create the 
 * different types of blocks.
 */
package semesterproject;

public class Block {
    
    private Boolean isActive;
    private BlockType Type;
    private float x, y, z;
    
    public enum BlockType{
        
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Default(6);

        static BlockType GetID() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private int blockID;
        
        //method: BlockType
        //purpose: This method sets the block type
        BlockType(int i){
            blockID = i;
        }
        
        //method: getID
        //purpose: This method returns the block type     
        public int getID(){
            return blockID;
        }
        
        //method: setID
        //purpose: This method sets the block type       
        public void setID(int i){
            blockID = i;
        }
    }
        
    //method: Block
    //purpose: This method is the constructor       
    public Block(BlockType type){
        Type = type;
    }
    
    //method: setCoords
    //purpose: This method sets the x, y, z coordinates   
    public void setCoords(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //method: isActive
    //purpose: This method returns whether the block type is active     
    public boolean isActive(){
        return isActive;
    }
   
    //method: SetActive
    //purpose: This method is sets the active block type   
    public void SetActive(boolean active){
        isActive = active;
    }

    //method: GetID
    //purpose: This method gets the block type 
    public int GetID(){
        return Type.getID();
    }
}
