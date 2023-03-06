/*
 * file: Chunk.java
 * author: BlockHeads
 * class: CS 4450 - Computer Graphics
 *
 * assignment: Check Point 3
 * data last modified: 11/14/2020
 *
 * purpose: This class will create chunks of blocks
 * that have different textures
 */
package semesterproject;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public final class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    static final float persistanceMin = 0.03f;
    static final float persistanceMax = 0.06f;
    private final Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private Texture texture;
    private int StartX, StartY, StartZ;
    private final Random random;
    
    //method: render
    //purpose: This method is used to update the cube 
    //in view while the window is open.
    public void render(){
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3,GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE*24);
        glPopMatrix();
    }
    
    //method: rebuildMesh
    //purpose: This method uses the vertex 
    //information to rebuild the mesh
    public void rebuildMesh(float startX, float startY, float startZ){
        Random rT = new Random();
        Random rM = new Random();
        
        float persistance = 0;
        
        while (persistance < persistanceMin) {
            persistance = (persistanceMax)*random.nextFloat();
        }
        int seed = (int)(50 * random.nextFloat());
        SimplexNoise noise = new SimplexNoise(CHUNK_SIZE, persistance, seed);
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE * CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE* CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        
        float maxHeight = 0;
        float height;
        for (float x = 0; x < CHUNK_SIZE; x++) {
            for (float z = 0; z < CHUNK_SIZE; z++) {
                int i = (int)(startX+x*((CHUNK_SIZE - startX)/CHUNK_SIZE));
                int j = (int)(startY+z*((CHUNK_SIZE - startY)/CHUNK_SIZE));  
                int k = (int)(startZ+z*((CHUNK_SIZE-startZ)/CHUNK_SIZE));
                height = Math.abs((startY + (int) (100 * noise.getNoise(i, j, k))* CUBE_LENGTH));
                if(height > maxHeight)
                {
                    maxHeight = height;
                }
                persistance = 0;
                for (float y = 0; y <= height; y++) {
                    while (persistance < persistanceMin) {
                        persistance = (persistanceMax) * random.nextFloat();
                    }

                    
                    VertexPositionData.put(createCube((startX + x * CUBE_LENGTH),(y * CUBE_LENGTH + (float) (CHUNK_SIZE * -2.0)),(startZ + z * CUBE_LENGTH) + (float)(CHUNK_SIZE * 1.5)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                    
                    float bottom = (float) .00 * maxHeight;
                    
                    if(y == maxHeight){
                        int type = rT.nextInt(3);
                        
                        switch (type) {
                            case 0:
                                VertexTextureData.put(createTexCube(0, 0, new Block(Block.BlockType.BlockType_Grass)));
                                break;
                            case 1:
                                VertexTextureData.put(createTexCube(0, 0, new Block(Block.BlockType.BlockType_Sand)));
                                break;
                            case 2:
                                VertexTextureData.put(createTexCube(0, 0, new Block(Block.BlockType.BlockType_Water)));
                                break;
                            default:
                                break;
                        }
                    }
                    else if(y > bottom && y < maxHeight){
                        
                        int top = (int) x - 1;
                        int topType = Blocks[(int) x][(int) y - 1][(int) z].GetID();
                       // System.out.println("topType " + topType);
                        
                        if(topType == 0 || topType == 1 || topType == 2)
                        {
                            int type = rM.nextInt(2);
                        
                            if(type == 0)
                                VertexTextureData.put(createTexCube(0, 0, new Block(Block.BlockType.BlockType_Dirt)));
                            else if(type == 1)
                                VertexTextureData.put(createTexCube(0, 0, new Block(Block.BlockType.BlockType_Stone)));
                        }
                        else
                        {
                            int type = rT.nextInt(3);
                        
                            if(type == 0)
                                VertexTextureData.put(createTexCube(0, 0, new Block(Block.BlockType.BlockType_Grass)));
                            else if(type == 1)
                                VertexTextureData.put(createTexCube(0, 0, new Block(Block.BlockType.BlockType_Sand)));
                            else if(type == 2)
                                VertexTextureData.put(createTexCube(0, 0, new Block(Block.BlockType.BlockType_Water)));
                        }
                    }
                    else if(y <= bottom){
                    VertexTextureData.put(createTexCube(0, 0, new Block(Block.BlockType.BlockType_Bedrock)));
                    }
                    else
                    VertexTextureData.put(createTexCube(0, 0, new Block(Block.BlockType.BlockType_Default)));
                }
            }
        }
        

        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0); 
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    //method: createCube
    //purpose: This method is used to create the cube by offseting
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
        //Top quad
        x + offset, y + offset, z,
        x - offset, y + offset, z,
        x - offset, y + offset, z - CUBE_LENGTH,
        x + offset, y + offset, z - CUBE_LENGTH,
                
        //Bottom quad
        x + offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z,
        x + offset, y - offset, z,
                
        //Front quad
        x + offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
                
        //Back quad
        x + offset, y - offset, z,
        x - offset, y - offset, z,
        x - offset, y + offset, z,
        x + offset, y + offset, z,
                
        //Left quad
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z,
        x - offset, y - offset, z,
        x - offset, y - offset, z - CUBE_LENGTH,
                
        //Right quad
        x + offset, y + offset, z,
        x + offset, y + offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z
        };
    }

    //method: getCubeColor
    //purpose: This method is usedto give the cube color 
    private float[] getCubeColor(Block block) {
        return new float[] { 1, 1, 1 };
    }
    
    //method: createCubeVertexCol
    //purpose: This method is used to create the vertex columns
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    
    //method: createTexCube
    //purpose: This method is used to create 
    //cubes with textures
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f/16)/1024f;
        
        int blockID = block.GetID();
        
        switch(blockID){
            case 0: 
                return new float[] {
                    // Top QUAD(DOWN=+Y)
                    x + offset*2, y + offset*9,
                    x + offset*3, y + offset*9,
                    x + offset*3, y + offset*10,
                    x + offset*2, y + offset*10,
                    // Bottom QUAD
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // FRONT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // BACK QUAD
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    // LEFT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // RIGHT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1 };
                
            case 1:   
                return new float[]{
                   // Top QUAD(DOWN=+Y)
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // Bottom QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // FRONT QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // BACK QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // LEFT QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // RIGHT QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                };
            case 2:
                return new float[]{
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13,
                    // Bottom QUAD
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13,
                    // FRONT QUAD
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13,
                    // BACK QUAD
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13,
                    // LEFT QUAD
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13,
                    // RIGHT QUAD
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13,
                };
                
            case 3:
                return new float[]{
                    // Top QUAD(DOWN=+Y)
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    // Bottom QUAD
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    // FRONT QUAD
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    // BACK QUAD
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    // LEFT QUAD
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    // RIGHT QUAD
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                };
            
            case 4:
                return new float[]{
                    // Top QUAD(DOWN=+Y)
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1,
                    // Bottom QUAD
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1,
                    // FRONT QUAD
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1,
                    // BACK QUAD
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1,
                    // LEFT QUAD
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1,
                    // RIGHT QUAD
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1,
                };
            
            case 5:
                return new float[]{
                    // Top QUAD(DOWN=+Y)
                    x + offset*5, y + offset*2,
                    x + offset*6, y + offset*2,
                    x + offset*6, y + offset*3,
                    x + offset*5, y + offset*3,
                    // Bottom QUAD
                    x + offset*5, y + offset*2,
                    x + offset*6, y + offset*2,
                    x + offset*6, y + offset*3,
                    x + offset*5, y + offset*3,
                    // FRONT QUAD
                    x + offset*5, y + offset*2,
                    x + offset*6, y + offset*2,
                    x + offset*6, y + offset*3,
                    x + offset*5, y + offset*3,
                    // BACK QUAD
                    x + offset*5, y + offset*2,
                    x + offset*6, y + offset*2,
                    x + offset*6, y + offset*3,
                    x + offset*5, y + offset*3,
                    // LEFT QUAD
                    x + offset*5, y + offset*2,
                    x + offset*6, y + offset*2,
                    x + offset*6, y + offset*3,
                    x + offset*5, y + offset*3,
                    // RIGHT QUAD
                    x + offset*5, y + offset*2,
                    x + offset*6, y + offset*2,
                    x + offset*6, y + offset*3,
                    x + offset*5, y + offset*3,
                };
            default:
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*4, y + offset*11,
                    x + offset*5, y + offset*11,
                    x + offset*5, y + offset*12,
                    x + offset*4, y + offset*12,
                    // TOP QUAD
                    x + offset*4, y + offset*11,
                    x + offset*5, y + offset*11,
                    x + offset*5, y + offset*12,
                    x + offset*4, y + offset*12,
                    // FRONT QUAD
                    x + offset*4, y + offset*11,
                    x + offset*5, y + offset*11,
                    x + offset*5, y + offset*12,
                    x + offset*4, y + offset*12,
                    // BACK QUAD
                    x + offset*4, y + offset*11,
                    x + offset*5, y + offset*11,
                    x + offset*5, y + offset*12,
                    x + offset*4, y + offset*12,
                    // LEFT QUAD
                    x + offset*4, y + offset*11,
                    x + offset*5, y + offset*11,
                    x + offset*5, y + offset*12,
                    x + offset*4, y + offset*12,
                    // RIGHT QUAD
                    x + offset*4, y + offset*11,
                    x + offset*5, y + offset*11,
                    x + offset*5, y + offset*12,
                    x + offset*4, y + offset*12,
                };
               
    }   
    }
    //method: Chunk
    //purpose: This method is the constructor 
    public Chunk(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture ("PNG",
            ResourceLoader.getResourceAsStream("terrain.png"));
        }catch (Exception e){
            System.out.print("ER-ROAR!");
        }
        
        random = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];

        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    float location = random.nextFloat();
                    
                    if(location >= 0.8f && location <= 1.0f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    }
                    else if(location >= 0.2f && location < 0.8f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }
                    else if(location >= 0.8f && location <= 1.0f)
                    {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    }
                    else if(location >= 0.8f && location <= 1.0f)
                    {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                    }
                    else if(location >= 0.0f && location < 0.2f)
                    {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    
                    }
                    else if(location >= 0.2f && location < 0.8f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    }
                    else
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Default);
                }
            }
        }
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
}
