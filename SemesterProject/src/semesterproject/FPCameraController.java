/*
 * file: FPCameraController.java
 * author: BlockHeads
 * class: CS 4450 - Computer Graphics
 *
 * assignment: Check Point 3
 * data last modified: 10/30/2020
 *
 * purpose: This class will hold have the camera
 * controlls and the information for the cube
 */
package semesterproject;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;

public class FPCameraController {
    
    //3D vectors to store camera's position
    private Vector3f position = null;
    private Vector3f lPosition = null;
    private final int xMin = -59;
    private final int xMax = 0;
    private final int zMin = -91;
    private final int zMax = -34;
    
    //Rotation around the y-axis
    private float yaw = 0.0f;
    //Rotation around the x-axis
    private float pitch = 0.0f;

    private Vector3Float me;
    
    //method: FPCameraController
    //purpose: This method instantiates position Vector3f to the x y z parameters
    public FPCameraController(float x, float y, float z){
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x,y,z);
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
    }
    
    //method: yaw
    //purpose: This method increments the camera's current yaw rotation
    public void yaw(float amt){
        yaw += amt;
    }
    
    //method: pitch
    //purpose: This method increments the pitch by the amount paramater
    public void pitch(float amt){
        pitch -= amt;
    }
    
    //method: walkForward
    //purpose: This method moves the camera forward relative to its current rotation
    public void walkForward(float distance, FPCameraController c){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        if((position.x > xMin && position.x < xMax))
        {
            position.x -= xOffset;
        }
        else
        {
            c.yaw(200);
            position.x += xOffset;
        }
        if((position.z > zMin && position.z < zMax))
        {
            position.z += zOffset;
        }
        else
        {
            c.yaw(200);
            position.z -= zOffset;
        }
            
//System.out.println(position.z);
    }
    
    //method: walkBackward
    //purpose: This method moves the camera backward relative to its current rotation
    public void walkBackward(float distance, FPCameraController c){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        if((position.x > xMin && position.x < xMax))
        {
            position.x += xOffset;
        }
        else
        {
            c.yaw(-200);
            position.z -= xOffset;
        }
        if((position.z > zMin && position.z < zMax))
        {
            position.z -= zOffset;
        }
        else
        {
            c.yaw(200);
            position.z += zOffset;
        }
//System.out.println(position.z);
    }
    
    //method: strafeLeft
    //purpose: This method strafes the camera left relative to its current rotation
    public void strafeLeft(float distance, FPCameraController c){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90));
        if((position.x > xMin && position.x < xMax))
        {
            position.x -= xOffset;
        }
        else
        {
            c.yaw(200);
            position.z += xOffset;
        }
        if((position.z > zMin && position.z < zMax))
        {
            position.z += zOffset;
        }
        else
        {
            c.yaw(200);
            position.z -= zOffset;
        }
        
//System.out.println(position.z);
    }
    
    //method: strafeRight
    //purpose: This method strafes the camera right relative to its current rotation
    public void strafeRight(float distance, FPCameraController c){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90));
        if((position.x > xMin && position.x < xMax))
        {
            position.x -= xOffset;
        }
        else
        {
            c.yaw(200);
            position.z += xOffset;
        }
        if((position.z > zMin && position.z < zMax))
        {
            position.z += zOffset;
        }
        else
        {
            c.yaw(200);
            position.z -= zOffset;
        }
        
//System.out.println(position.z);
    }
    
    //method: moveUp
    //purpose: This method moves the camera up relative to its current rotation
    public void moveUp(float distance){
        position.y -= distance;
        
    }
    
    //method: moveDown
    //purpose: This method moves the camera down relative to its current rotation
    public void moveDown(float distance){
        position.y += distance;
    }
    
    //method: lookThrough
    //purpose: This method translates and rotate the matrix 
    //so that it looks through the camera
    public void lookThrough(){
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y, position.z);
    }
    
    
    //method: gameLoop
    //purpose: This method will control the camera
    public void gameLoop(){
        FPCameraController camera = new FPCameraController(-30, 10, -60);
        
        Chunk chunk = new Chunk(0, 0, -10);
        
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f;
        float lastTime = 0.0f;
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = .90f;
        Mouse.setGrabbed(true);
        
        while (!Display.isCloseRequested() &&
                !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            time = Sys.getTime();
            lastTime = time;

            dx = Mouse.getDX();
            
            camera.yaw(dx * mouseSensitivity);
            
            dy = Mouse.getDY();
            camera.pitch(dy * mouseSensitivity);
            
            if ((Keyboard.isKeyDown(Keyboard.KEY_UP)) || (Keyboard.isKeyDown(Keyboard.KEY_W))){
                camera.walkForward(movementSpeed, camera);
                
            }
            if ((Keyboard.isKeyDown(Keyboard.KEY_DOWN)) || (Keyboard.isKeyDown(Keyboard.KEY_S))){
                camera.walkBackward(movementSpeed, camera);
                
            }
            if ((Keyboard.isKeyDown(Keyboard.KEY_LEFT)) || (Keyboard.isKeyDown(Keyboard.KEY_A))){
                camera.strafeLeft(movementSpeed, camera);
                

            }
            if ((Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) || (Keyboard.isKeyDown(Keyboard.KEY_D))){
                camera.strafeRight(movementSpeed, camera);
                
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                camera.moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                camera.moveDown(movementSpeed);
            } 
            
            
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            chunk.render();
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }
    
    //method: render
    //purpose: This method is used to update the cube 
    //in view while the window is open.    
    private void render(){
        try{
            glBegin(GL_QUADS);
            glColor3f(0.0f,0.0f,1.0f); 
            glVertex3f( 1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);  
            
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f( 1.0f,-1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f( 1.0f,-1.0f,-1.0f);
            
            glColor3f(0.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);
            glVertex3f( 1.0f,-1.0f, 1.0f);
            
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f( 1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f(1.0f, 1.0f,-1.0f);
            
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(-1.0f, 1.0f,1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);

            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f(1.0f, 1.0f,-1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(1.0f,-1.0f, 1.0f);
            glVertex3f(1.0f,-1.0f,-1.0f);
            
            glEnd();
            
            glBegin(GL_LINE_LOOP);
            glColor3f(0.0f,0.0f,0.0f);
            glVertex3f( 1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);

            glEnd();
            
            glBegin(GL_LINE_LOOP);

            glColor3f(0.0f,0.0f,0.0f);
            glVertex3f( 1.0f,-1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f( 1.0f,-1.0f,-1.0f);
            glEnd();
            
            glBegin(GL_LINE_LOOP);

            glColor3f(0.0f,0.0f,0.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);
            glVertex3f( 1.0f,-1.0f, 1.0f);
            glEnd();
            
            glBegin(GL_LINE_LOOP);
            
            glColor3f(0.0f,0.0f,0.0f);
            glVertex3f( 1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f( 1.0f, 1.0f,-1.0f);
            glEnd();

            glBegin(GL_LINE_LOOP);

            glColor3f(0.0f,0.0f,0.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);
            glEnd();
            
            glBegin(GL_LINE_LOOP);

            glColor3f(0.0f,0.0f,0.0f);
            glVertex3f( 1.0f, 1.0f,-1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f,-1.0f, 1.0f);
            glVertex3f( 1.0f,-1.0f,-1.0f);
            glEnd();
            
        } catch(Exception e){
        }
    }
}

