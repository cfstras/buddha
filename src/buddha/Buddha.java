/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

/**
 *
 * @author claus
 */
public class Buddha {

    static int sizex=800;
    static int sizey=800;
    
    static int bailout=50;
    static int numToRun=2000;
    
    static Renderer renderer;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        setLwjglPath();
        
        
        renderer = new GLRenderer();
        float[] bgcolor= {0f,0f,0f,0f};
        float[] fgcolor= {1f,1f,1f,1f};
        
        renderer.init(bgcolor, fgcolor, sizex, sizey);
        
        
        for(int i=0;i<4;i++) {
            new Thread() {
                @Override public void run() {
                    Fractal f= new Buddhabrot();
                    f.init(sizex, sizey, renderer);
                    while(true) {
                        f.generateData(numToRun, bailout);
                    }
                }
            }.start();
        }
        
        while(true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {}
            
            System.out.println("gemerated "+renderer.getNumDataRecvd()+" exposures");
            renderer.render();
        }    
    }

    private static void setLwjglPath() {
        String fs=System.getProperty("file.separator","/");
        //System.setProperty("java.library.path","lib"+fs+"native"+fs+"windows"+fs);
        System.getProperties().list(System.out);
    }
}
