/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

/**
 *
 * @author cfstras
 */
public class GUI extends javax.swing.JFrame {
    
    BufferStrategy bufStrat;
    
    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents();
        setLocationByPlatform(true);
        setVisible(true);
        
        canvas.createBufferStrategy(2);
        bufStrat = canvas.getBufferStrategy();
        previewButton.setSelected(true);
        previewButtonActionPerformed(null);
    }
    
    public void setRenderStatus(String s) {
        renderStatus.setText(s);
    }
    public void setGenerateStatus(String s) {
        generateStatus.setText(s);
    }
    public void setExportStatus(String s) {
        exportLabel.setText(s);
    }
    
    static String siPrefix = " KMGTP";
    
    public void updateExposures(long e) {
        int prefix = 0;
        while(e>1000 && prefix < siPrefix.length()-1) {
            prefix++;
            e /= 1000;
        }
        exposuresLabel.setText(e+" "+siPrefix.charAt(prefix));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        generateButton = new javax.swing.JToggleButton();
        previewButton = new javax.swing.JToggleButton();
        clearButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        loadButton = new javax.swing.JButton();
        renderStatus = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        generateStatus = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        sliderR = new javax.swing.JSlider();
        sliderG = new javax.swing.JSlider();
        sliderB = new javax.swing.JSlider();
        colorButton = new javax.swing.JButton();
        canvas = new UpdateCanvas();
        minItField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        maxItField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        exposuresLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        resolutionField = new javax.swing.JTextField();
        exportButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        exportLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Buddha");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setToolTipText("");

        jLabel1.setText("Preview Status:");

        generateButton.setText("Generate");
        generateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateButtonActionPerformed(evt);
            }
        });

        previewButton.setText("Preview");
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });

        clearButton.setText("Apply Values & Clear Data");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        loadButton.setText("Load");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        renderStatus.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        renderStatus.setText("on");

        jLabel2.setText("Generate Status:");

        generateStatus.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        generateStatus.setText("off");

        jLabel3.setText("Color:");

        sliderR.setBackground(java.awt.Color.red);
        sliderR.setForeground(java.awt.Color.white);
        sliderR.setMajorTickSpacing(32);
        sliderR.setMaximum(255);
        sliderR.setValue(255);
        sliderR.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderRStateChanged(evt);
            }
        });

        sliderG.setBackground(java.awt.Color.green);
        sliderG.setMajorTickSpacing(32);
        sliderG.setMaximum(255);
        sliderG.setValue(255);
        sliderG.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderGStateChanged(evt);
            }
        });

        sliderB.setBackground(java.awt.Color.blue);
        sliderB.setMajorTickSpacing(32);
        sliderB.setMaximum(255);
        sliderB.setValue(255);
        sliderB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderBStateChanged(evt);
            }
        });

        colorButton.setBackground(new java.awt.Color(255, 255, 255));
        colorButton.setBorderPainted(false);
        colorButton.setEnabled(false);

        canvas.setPreferredSize(new java.awt.Dimension(500, 500));

        minItField.setText("5");
        minItField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minItFieldActionPerformed(evt);
            }
        });
        minItField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                minItFieldPropertyChange(evt);
            }
        });

        jLabel4.setText("min Iterations");

        jLabel5.setText("max Iterations");

        maxItField.setText("200");
        maxItField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxItFieldActionPerformed(evt);
            }
        });
        maxItField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                maxItFieldPropertyChange(evt);
            }
        });

        jLabel6.setText("max Exposures (Mio.)");

        jTextField1.setText("0");

        jLabel7.setText("Exposures:");

        exposuresLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        exposuresLabel.setText("0");

        jLabel8.setText("Resolution");

        resolutionField.setText("1024");
        resolutionField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resolutionFieldActionPerformed(evt);
            }
        });

        exportButton.setText("Export Image");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        jLabel9.setText("Export Status:");

        exportLabel.setText("                                  ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(jLabel9))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(exportLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(renderStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(resolutionField))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField1))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(maxItField, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel4)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(18, 18, 18)
                            .addComponent(colorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(minItField, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(exportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(clearButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(generateButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(previewButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(saveButton)
                                .addGap(141, 141, 141)
                                .addComponent(loadButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(exposuresLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(generateStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(sliderG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sliderR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sliderB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(generateButton)
                            .addComponent(previewButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exportButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(saveButton)
                            .addComponent(loadButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(exportLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(renderStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(generateStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(exposuresLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                            .addComponent(colorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sliderR, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sliderG, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sliderB, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(minItField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(maxItField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(resolutionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed
        if(generateButton.isSelected() && !Buddha.threadsRunning) {
            setGenerateStatus("starting...");
        } else if(Buddha.threadsRunning) {
            setGenerateStatus("stopping...");
        }
        new Thread() {
            @Override public void run() {
                applyGenerateButton();
            }
        }.start();
    }//GEN-LAST:event_generateButtonActionPerformed
    
    private void applyGenerateButton() {
        if(generateButton.isSelected() && !Buddha.threadsRunning) {
            Buddha.restartThreads();
        } else if(Buddha.threadsRunning) {
            Buddha.stopThreads();
        }
    }
    
    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        if(previewButton.isSelected() && !Buddha.previewing) {
            setRenderStatus("starting...");
        } else if(Buddha.previewing) {
            setRenderStatus("stopping...");
        }
        
        new Thread() {
            @Override public void run() {
                applyPreviewButton();
            }
        }.start();
    }//GEN-LAST:event_previewButtonActionPerformed
    
    private void applyPreviewButton() {
        if(previewButton.isSelected() && !Buddha.previewing) {
            Buddha.reStartPreview();
        } else if(Buddha.previewing) {
            Buddha.stopPreview();
        }
    }
    
    private void sliderRStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderRStateChanged
        int r = sliderR.getValue();
        sliderR.setBackground(new Color(r,0,0));
        Color old = Buddha.fgColor;
        Buddha.fgColor = new Color(r, old.getGreen(), old.getBlue());
        colorButton.setBackground(Buddha.fgColor);
        Buddha.renderer.setColor(Buddha.fgColor, Buddha.bgColor);
        Buddha.prevThread.redraw=true;
        Buddha.prevThread.interrupt();
    }//GEN-LAST:event_sliderRStateChanged

    private void sliderGStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderGStateChanged
        int g = sliderG.getValue();
        sliderG.setBackground(new Color(0,g,0));
        Color old = Buddha.fgColor;
        Buddha.fgColor = new Color(old.getRed(), g, old.getBlue());
        colorButton.setBackground(Buddha.fgColor);
        Buddha.renderer.setColor(Buddha.fgColor, Buddha.bgColor);
        Buddha.prevThread.redraw=true;
        Buddha.prevThread.interrupt();
    }//GEN-LAST:event_sliderGStateChanged

    private void sliderBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderBStateChanged
        int b = sliderB.getValue();
        sliderB.setBackground(new Color(0,0,b));
        Color old = Buddha.fgColor;
        Buddha.fgColor = new Color(old.getRed(), old.getGreen(), b);
        colorButton.setBackground(Buddha.fgColor);
        Buddha.renderer.setColor(Buddha.fgColor, Buddha.bgColor);
        Buddha.prevThread.redraw=true;
        Buddha.prevThread.interrupt();
    }//GEN-LAST:event_sliderBStateChanged

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        new Thread() {
            @Override public void run() {
                clearAndApply();
            }
        }.start();
    }//GEN-LAST:event_clearButtonActionPerformed
    
    public void fetchProperties() {
        minItField.setText(Integer.toString(Buddha.minIterations));
        maxItField.setText(Integer.toString(Buddha.maxIterations));
        resolutionField.setText(Integer.toString(Buddha.sizex));
    }
    
    private void clearAndApply() {
        if(Buddha.threadsRunning) {
            generateButton.setSelected(false);
            setGenerateStatus("stopping...");
            Buddha.stopThreads();
        }
        try {
            Buddha.minIterations = Integer.parseInt(minItField.getText());
        } catch (NumberFormatException ex) {
            System.out.println(minItField.getText()+"is no valid number.");
        }
        try {
            Buddha.maxIterations = Integer.parseInt(maxItField.getText());
        } catch (NumberFormatException ex) {
            System.out.println(maxItField.getText()+"is no valid number.");
        }
        try {
            int res = Integer.parseInt(resolutionField.getText());
            Buddha.sizex = Buddha.sizey = res;
        } catch (NumberFormatException ex) {
            System.out.println(resolutionField.getText()+"is no valid number.");
        }
        Buddha.renderer.init(Buddha.sizex, Buddha.sizey);
        updateExposures(0);
    }
    
    private void minItFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_minItFieldPropertyChange

    }//GEN-LAST:event_minItFieldPropertyChange

    private void maxItFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_maxItFieldPropertyChange

    }//GEN-LAST:event_maxItFieldPropertyChange

    private void minItFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minItFieldActionPerformed
        
    }//GEN-LAST:event_minItFieldActionPerformed

    private void maxItFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxItFieldActionPerformed
    }//GEN-LAST:event_maxItFieldActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        generateButton.setSelected(false);
        generateButtonActionPerformed(null);
        Buddha.renderer.save();
        
    }//GEN-LAST:event_saveButtonActionPerformed

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        generateButton.setSelected(false);
        generateButtonActionPerformed(null);
        Buddha.renderer.load();
        updateExposures(Buddha.renderer.getExposes());
    }//GEN-LAST:event_loadButtonActionPerformed

    private void resolutionFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resolutionFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resolutionFieldActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        new Thread() {
            @Override public void run() {
                Buddha.exportImage();
            }
        }.start();
    }//GEN-LAST:event_exportButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Canvas canvas;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton colorButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JLabel exportLabel;
    private javax.swing.JLabel exposuresLabel;
    private javax.swing.JToggleButton generateButton;
    private javax.swing.JLabel generateStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton loadButton;
    private javax.swing.JTextField maxItField;
    private javax.swing.JTextField minItField;
    private javax.swing.JToggleButton previewButton;
    private javax.swing.JLabel renderStatus;
    private javax.swing.JTextField resolutionField;
    private javax.swing.JButton saveButton;
    private javax.swing.JSlider sliderB;
    private javax.swing.JSlider sliderG;
    private javax.swing.JSlider sliderR;
    // End of variables declaration//GEN-END:variables

    void render() {
        pack();
        Graphics2D g = (Graphics2D) bufStrat.getDrawGraphics();
        g.setTransform(AffineTransform.getScaleInstance(
                (double)canvas.getWidth()/Buddha.sizex,
                (double)canvas.getHeight()/Buddha.sizey));
        Buddha.renderer.render(g);
        bufStrat.show();
    }
    
    void startExporting() {
        sliderR.setEnabled(false);
        sliderG.setEnabled(false);
        sliderB.setEnabled(false);
        loadButton.setEnabled(false);
        clearButton.setEnabled(false);
        
    }
    
    void stopExporting() {
        sliderR.setEnabled(true);
        sliderG.setEnabled(true);
        sliderB.setEnabled(true);
        loadButton.setEnabled(true);
        clearButton.setEnabled(true);
    }
    
    private class UpdateCanvas extends Canvas {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void paint(Graphics g) {
            if(Buddha.prevThread==null) {
                //hmm... not yet initialized.
                return;
            }
            Buddha.prevThread.redraw=true;
            Buddha.prevThread.interrupt();
        }
                
    }
}
