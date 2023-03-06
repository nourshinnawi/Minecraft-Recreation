/*
 * file: SemesterProject.java
 * author: BlockHeads
 * class: CS 4450 - Computer Graphics
 *
 * assignment: Check Point 3
 * data last modified: 10/30/2020
 *
 * purpose: This program allows a user to navigate around
 * a colored cube in first person using the key bindings w,a,s,d  
 * or the arrow keys and use space bar to move up and left shift
 * to move down.
 */
package semesterproject;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.GLU;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class SemesterProject {
    
    private final FPCameraController fp = new FPCameraController(0f,0f,0f);
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;

    
    //method: createWindow
    //purpose: this method is used to create the window for
    //the cube to be displayed and viewed
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640 && 
                    d[i].getHeight() == 480 && 
                    d[i].getBitsPerPixel() == 32){
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode); 
        Display.setTitle("Semester Project");
        Display.create();
    }
    
    //method: initGL
    //purpose: this method is used to set the display for 
    //the window
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)
                displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT,
                GL_NICEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState (GL_TEXTURE_COORD_ARRAY);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        
//        initLightArrays();
//        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
//        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
//        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
//        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
//        glEnable(GL_LIGHTING);//enables our lighting
//        glEnable(GL_LIGHT0);//enables light0
    }
    
    private void initLightArrays() {
    lightPosition = BufferUtils.createFloatBuffer(4);
    lightPosition.put(0.8f).put(0.8f).put(0.8f).put(1.0f).flip();
    whiteLight = BufferUtils.createFloatBuffer(4);
    whiteLight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
}

    //method: start
    //purpose: this method is used to run the program  
    //in the main method
    public void start() {
        try {
            createWindow();
            initGL();
            fp.gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //method: render
    //purpose: this method is used to update the cube 
    //in view while the window is open.
    private void render() {
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            try {
               
                Display.update();
                Display.sync(60);
            } catch (Exception e) {
            }
        }
        Display.destroy();
    }
    
    //method: main
    //purpose: this method is used to run the program
    //for viewing the cube
    public static void main(String[] args) {
        SemesterProject sp = new SemesterProject();
        sp.start();
    }
}
