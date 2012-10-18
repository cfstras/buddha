/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

import java.awt.Color;

/**
 *
 * @author claus
 */
public class Buddha {

    static int updateInterval = 300;
    
    static int sizex=1024;
    static int sizey=1024;
    
    static int maxIterations=200;
    static int minIterations=5;
    static int numToRun=1024*1024*512;
    
    static int maxThreads=8;
    static int numThreads;
    
    static Color fgColor;
    static Color bgColor;
    
    static boolean threadsRunning;
    static Renderer renderer;
    static GenerateThread[] threads;
    static PreviewThread prevThread;
    //static Thread guiThread;
    static GUI gui;
    static boolean previewing;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        setLwjglPath();
        renderer = new PNGRenderer();
        bgColor= new Color(0,0,0,0);
        fgColor= new Color(255,255,255,255);
        
        renderer.init(sizex, sizey);
        renderer.setColor(fgColor, bgColor);
        
        numThreads = Math.min(Runtime.getRuntime().availableProcessors(),maxThreads);
        threads = new GenerateThread[numThreads];
        //renderer.updateInfo("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+renderer.getExposes()/1000000+"M    not started     ");
        gui = new GUI();
    }
    
    static void stopThreads() {
        if(threadsRunning) {
            threadsRunning=false;
            //renderer.updateInfo("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+renderer.getExposes()/1000000+"M    stopping       ");
            for(int i=0;i<threads.length;i++) {
                threads[i].run=false;
                threads[i].interrupt();
            }
            for(int i=0;i<threads.length;i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException ex) {}
            }
            gui.setGenerateStatus("stopped.");
            System.gc();
        }
        //renderer.updateInfo("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+renderer.getExposes()/1000000+"M    stopped        ");
    }
    
    static void restartThreads() {
        stopThreads();
        for(int i=0;i<numThreads;i++) {
            threads[i]=new GenerateThread();
            threads[i].setPriority(Thread.MIN_PRIORITY);
            threads[i].setName("GeneratorThread-"+i);
            threads[i].start();
        }
        threadsRunning=true;
        gui.setGenerateStatus("running...");
    }

    private static void setLwjglPath() {
        String fs=System.getProperty("file.separator","/");
        String os = System.getProperty("os.name");
        if(os.contains("Windows")) os = "windows";
        else if(os.contains("Linux")) os = "linux";
        else if(os.contains("Solaris")) os = "solaris";
        else if(os.contains("Mac")) os = "macosx";
        System.setProperty("org.lwjgl.librarypath","lib"+fs+"native"+fs+os+fs);
    }

    static void reStartPreview() {
        previewing = true;
        if(prevThread!=null)
            prevThread.run=false;
        prevThread = new PreviewThread();
        prevThread.start();
    }
    static void stopPreview() {
        previewing = false;
        prevThread.run=false;
        prevThread = null;
    }
    
    static class GenerateThread extends Thread {

        boolean run = true;
        Fractal f;
        @Override
        public void run() {
            setName("Generator-");
            f = new Buddhabrot();
            f.init(sizex, sizey,minIterations,maxIterations, renderer);
            while (run) {
                f.generateData(numToRun/maxIterations);
                gui.updateExposures(renderer.getExposes());
            }
        }
    }
    static class PreviewThread extends Thread {
        int i = 0;
        static String[] tick = {"-","\\","|","/"};
        boolean run = true;

        @Override
        public void run() {
            setName("Preview");
            gui.setRenderStatus("on");
            while (run) {
                try {
                    Thread.sleep(updateInterval);
                } catch (InterruptedException ex) {
                }
                if (threadsRunning) {
                    gui.render();
                    gui.setRenderStatus("on: "+tick[i%tick.length]);
                    i++;
                }
            }
            gui.setRenderStatus("off");
        }
    }
}
