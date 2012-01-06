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

    static int sizex=8192;
    static int sizey=8192;
    
    static int bailout=100000;
    static int minIterations=3;
    static int numToRun=2000;
    
    static boolean threadsRunning;
    static Renderer renderer;
    static RenderThread[] threads;
    static Thread guiThread;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        setLwjglPath();
        
        renderer = new PNGRenderer();
        float[] bgcolor= {0f,0f,0f,0f};
        float[] fgcolor= {1f,1f,1f,1f};
        
        renderer.init(bgcolor, fgcolor, sizex, sizey);
        
        threads = new RenderThread[4];
        
        
        int i=0;
        while(true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {}
            i++;
            
            if(threadsRunning) {
                renderer.updateInfo("buddha-"+Buddha.bailout+"-"+Buddha.minIterations+"-"+renderer.getNumDataRecvd()/1000000+"M    running       ");
                System.out.println("generated "+renderer.getNumDataRecvd()+" exposures");
                if(i%150==0) // all 5 minutes
                    renderer.render();
            }
        }    
    }
    
    static void stopThreads() {
        if(threadsRunning) {
            threadsRunning=false;
            guiThread=Thread.currentThread();
            renderer.updateInfo("buddha-"+Buddha.bailout+"-"+Buddha.minIterations+"-"+renderer.getNumDataRecvd()/1000000+"M    stopping       ");
            for(int i=0;i<threads.length;i++) {
                threads[i].run=false;
                threads[i].interrupt();
            }
            for(int i=0;i<threads.length;i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException ex) {}
            }
        }
        renderer.updateInfo("buddha-"+Buddha.bailout+"-"+Buddha.minIterations+"-"+renderer.getNumDataRecvd()/1000000+"M    stopped        ");
    }
    
    static void restartThreads() {
        stopThreads();
        for(int i=0;i<4;i++) {
            threads[i]=new RenderThread();
            threads[i].setPriority(Thread.MIN_PRIORITY);
            threads[i].start();
        }
        threadsRunning=true;
    }

    private static void setLwjglPath() {
        String fs=System.getProperty("file.separator","/");
        //System.setProperty("java.library.path","lib"+fs+"native"+fs+"windows"+fs);
        System.getProperties().list(System.out);
    }
    
    static class RenderThread extends Thread {

        boolean run = true;

        @Override
        public void run() {
            Fractal f = new Buddhabrot();
            f.init(sizex, sizey, renderer);
            while (run) {
                f.generateData(minIterations, numToRun, bailout);
            }
            try {
                    Buddha.guiThread.join();
                } catch (InterruptedException ex) {
            }
        }
    }
}
