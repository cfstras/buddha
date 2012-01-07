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

    static int sizex=1024;
    static int sizey=1024;
    
    static int maxIterations=200;
    static int minIterations=5;
    static int numToRun=1024*1024*512;
    
    static int numThreads=8;
    
    static float color_r = 1f, color_g = 1f, color_b = 1f, alpha = 1f;
    
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
        float[] fgcolor= {color_r,color_g,color_b,alpha};
        
        renderer.init(bgcolor, fgcolor, sizex, sizey);
        
        threads = new RenderThread[numThreads];
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {}
        
        renderer.updateInfo("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+renderer.getNumDataRecvd()/1000000+"M    not started     ");
        
        int i=0;
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {}
            i++;
            
            if(threadsRunning) {
                renderer.updateInfo("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+renderer.getNumDataRecvd()/1000000+"M    running       ");
                System.out.println("generated "+renderer.getNumDataRecvd()+" exposures");
                if(i%600==0) // every 10 minutes
                    renderer.render();
            }
        }    
    }
    
    static void stopThreads() {
        if(threadsRunning) {
            threadsRunning=false;
            guiThread=Thread.currentThread();
            renderer.updateInfo("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+renderer.getNumDataRecvd()/1000000+"M    stopping       ");
            for(int i=0;i<threads.length;i++) {
                threads[i].run=false;
                threads[i].interrupt();
            }
            for(int i=0;i<threads.length;i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException ex) {}
            }
            System.gc();
        }
        renderer.updateInfo("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+renderer.getNumDataRecvd()/1000000+"M    stopped        ");
    }
    
    static void restartThreads() {
        stopThreads();
        for(int i=0;i<numThreads;i++) {
            threads[i]=new RenderThread();
            threads[i].setPriority(Thread.MIN_PRIORITY);
            threads[i].setName("GeneratorThread-"+i);
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
        Fractal f;
        @Override
        public void run() {
            f = new Buddhabrot();
            f.init(sizex, sizey,minIterations,maxIterations, renderer);
            while (run) {
                f.generateData(numToRun/maxIterations);
                //System.out.println("Renderthread finished "+numToRun/maxIterations+" iterations");
            }
            try {
                    Buddha.guiThread.join(1000);
                } catch (InterruptedException ex) {
            }
        }
    }
}
