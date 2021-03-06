/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import Common.PositionAndOrientation;
import Entities.Character;
import Entities.Entity;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class PlayerFrame extends javax.swing.JFrame {

    private FormToPlayerComm ftpc;

    /**
     * Creates new form PlayerFrame
     */
    public PlayerFrame() {
        initComponents();
    }

    public void UpdateForm(final PositionAndOrientation pAo, final int state, final int tracks) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                PositionXTextField.setText(String.valueOf(pAo.getLongitude()));
                PositionYTextField.setText(String.valueOf(pAo.getLatitude()));
                switch (pAo.getOrientation()) {
                    case 0:
                        OrientationTextField.setText("NORTH");
                        break;
                    case 1:
                        OrientationTextField.setText("SOUTH");
                        break;
                    case 2:
                        OrientationTextField.setText("EAST");
                        break;
                    case 3:
                        OrientationTextField.setText("WEST");
                        break;
                }
                StateTextField.setText(state == 0 ? "ALIVE" : "DEAD");
                if (state == 1) {
                    MoveButton.setEnabled(false);
                    TrackButton.setEnabled(false);
                    RotateLeftButton.setEnabled(false);
                    RotateRightButton.setEnabled(false);
                    AttackButton.setEnabled(false);
                    UseButton.setEnabled(false);
                } else {
                    MoveButton.setEnabled(true);
                    TrackButton.setEnabled(true);
                    RotateLeftButton.setEnabled(true);
                    RotateRightButton.setEnabled(true);
                    AttackButton.setEnabled(true);
                    UseButton.setEnabled(true);
                }
                String tracked;
                switch (tracks) {
                    case Entity.GLITTER:
                        tracked = "Treasure";
                        break;
                    case Entity.HEALER_TRACKS:
                        tracked = "Healer";
                        break;
                    case Entity.GOBLIN_TRACKS:
                        tracked = "Goblin";
                        break;
                    case Entity.PLAYER_TRACKS:
                        tracked = "Player";
                        break;
                    case Entity.NO_TRACKS:
                        tracked = "No Tracks";
                        break;
                    case Entity.TRAP_TRACKS:
                        tracked = "Trap";
                        break;
                    default:
                        tracked = "ERROR";
                }
                SmellTextField.setText(tracked);
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PositionXLabel = new javax.swing.JLabel();
        PositionXTextField = new javax.swing.JTextField();
        PositionYLabel = new javax.swing.JLabel();
        PositionYTextField = new javax.swing.JTextField();
        PositionYLabel1 = new javax.swing.JLabel();
        OrientationTextField = new javax.swing.JTextField();
        RotateLeftButton = new javax.swing.JButton();
        RotateRightButton = new javax.swing.JButton();
        MoveButton = new javax.swing.JButton();
        AttackButton = new javax.swing.JButton();
        UseButton = new javax.swing.JButton();
        TrackButton = new javax.swing.JButton();
        StartContainerButton = new javax.swing.JButton();
        HostTextField = new javax.swing.JTextField();
        HostLabel = new javax.swing.JLabel();
        PortLabel = new javax.swing.JLabel();
        PortTextField = new javax.swing.JTextField();
        PlayerNameLabel = new javax.swing.JLabel();
        PlayerNameTextField = new javax.swing.JTextField();
        RegisterButton = new javax.swing.JButton();
        PositionYLabel2 = new javax.swing.JLabel();
        StateTextField = new javax.swing.JTextField();
        SmellLabel = new javax.swing.JLabel();
        SmellTextField = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        PositionXLabel.setText("Longitude");

        PositionXTextField.setEditable(false);
        PositionXTextField.setText("jTextField1");

        PositionYLabel.setText("Latitude");

        PositionYTextField.setEditable(false);
        PositionYTextField.setText("jTextField1");

        PositionYLabel1.setText("Orientation");

        OrientationTextField.setEditable(false);
        OrientationTextField.setText("jTextField1");

        RotateLeftButton.setText("Rotate Left");
        RotateLeftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RotateLeftButtonActionPerformed(evt);
            }
        });

        RotateRightButton.setText("Rotate Right");
        RotateRightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RotateRightButtonActionPerformed(evt);
            }
        });

        MoveButton.setText("Move");
        MoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MoveButtonActionPerformed(evt);
            }
        });

        AttackButton.setText("Attack");
        AttackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AttackButtonActionPerformed(evt);
            }
        });

        UseButton.setText("Use");
        UseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UseButtonActionPerformed(evt);
            }
        });

        TrackButton.setLabel("Track");
        TrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TrackButtonActionPerformed(evt);
            }
        });

        StartContainerButton.setText("Start Container & Agent");
        StartContainerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartContainerButtonActionPerformed(evt);
            }
        });

        HostTextField.setText("192.168.92.1");
        HostTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HostTextFieldActionPerformed(evt);
            }
        });

        HostLabel.setText("Host");

        PortLabel.setText("Port");

        PortTextField.setText("1099");

        PlayerNameLabel.setText("Player Name");

        PlayerNameTextField.setText("Player1");

        RegisterButton.setText("Register");
        RegisterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegisterButtonActionPerformed(evt);
            }
        });

        PositionYLabel2.setText("State");

        StateTextField.setEditable(false);
        StateTextField.setText("jTextField1");

        SmellLabel.setText("Track");

        SmellTextField.setEditable(false);
        SmellTextField.setText("jTextField1");

        jCheckBox1.setText("Auto Play");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(StartContainerButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(PortLabel)
                                .addGap(18, 18, 18))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(HostLabel)
                                .addGap(16, 16, 16)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(HostTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                            .addComponent(PortTextField)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PlayerNameLabel)
                        .addGap(18, 18, 18)
                        .addComponent(PlayerNameTextField))
                    .addComponent(RegisterButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(PositionYLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(OrientationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(PositionYLabel2)
                                                .addGap(38, 38, 38))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(SmellLabel)
                                                .addGap(40, 40, 40)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(SmellTextField)
                                            .addComponent(StateTextField))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 11, Short.MAX_VALUE)
                                        .addComponent(jCheckBox1))
                                    .addComponent(MoveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(PositionXLabel)
                                .addGap(18, 18, 18)
                                .addComponent(PositionXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RotateLeftButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(PositionYLabel)
                                .addGap(26, 26, 26)
                                .addComponent(PositionYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RotateRightButton, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(AttackButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(UseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(TrackButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HostTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HostLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PortLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PlayerNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PlayerNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(StartContainerButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RegisterButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PositionXLabel)
                    .addComponent(PositionXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RotateLeftButton)
                    .addComponent(AttackButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PositionYLabel)
                    .addComponent(PositionYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RotateRightButton)
                    .addComponent(UseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PositionYLabel1)
                    .addComponent(OrientationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MoveButton)
                    .addComponent(TrackButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PositionYLabel2)
                    .addComponent(StateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SmellLabel)
                    .addComponent(SmellTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void MoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MoveButtonActionPerformed
        ftpc.doMove();
    }//GEN-LAST:event_MoveButtonActionPerformed

    private void AttackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AttackButtonActionPerformed
        ftpc.doAttack();
    }//GEN-LAST:event_AttackButtonActionPerformed

    private void UseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UseButtonActionPerformed
        ftpc.doUse();
    }//GEN-LAST:event_UseButtonActionPerformed

    private void TrackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TrackButtonActionPerformed
        ftpc.doTrack();
    }//GEN-LAST:event_TrackButtonActionPerformed

    private void StartContainerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartContainerButtonActionPerformed
        jade.core.Runtime rt = jade.core.Runtime.instance();
        String host = HostTextField.getText();
        int port = Integer.parseInt(PortTextField.getText());
        Profile p = new ProfileImpl(host, port, null, false);
        ContainerController cc = rt.createAgentContainer(p);
        if (cc != null) {
            try {
                Player pl = new Player(PlayerNameTextField.getText(),
                        "human player", 100, 100, Character.ALIVE,
                        Entity.NO_TRACKS, new PositionAndOrientation(1, 1, 1),
                        new PositionAndOrientation(1, 1, 1), this);
                AgentController ac = cc.acceptNewAgent(PlayerNameTextField.getText(), pl);
                ac.start();
                ftpc = pl;
            } catch (StaleProxyException ex) {
                Logger.getLogger(PlayerFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_StartContainerButtonActionPerformed

    private void RegisterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegisterButtonActionPerformed
        if (this.jCheckBox1.isSelected()) {
            ftpc.doRegisterAuto();
        } else {
            ftpc.doRegister();
        }
    }//GEN-LAST:event_RegisterButtonActionPerformed

    private void HostTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HostTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_HostTextFieldActionPerformed

    private void RotateRightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RotateRightButtonActionPerformed
        ftpc.doRotateRight();
    }//GEN-LAST:event_RotateRightButtonActionPerformed

    private void RotateLeftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RotateLeftButtonActionPerformed
        ftpc.doRotateLeft();
    }//GEN-LAST:event_RotateLeftButtonActionPerformed
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PlayerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlayerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlayerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlayerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PlayerFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AttackButton;
    private javax.swing.JLabel HostLabel;
    private javax.swing.JTextField HostTextField;
    private javax.swing.JButton MoveButton;
    private javax.swing.JTextField OrientationTextField;
    private javax.swing.JLabel PlayerNameLabel;
    private javax.swing.JTextField PlayerNameTextField;
    private javax.swing.JLabel PortLabel;
    private javax.swing.JTextField PortTextField;
    private javax.swing.JLabel PositionXLabel;
    private javax.swing.JTextField PositionXTextField;
    private javax.swing.JLabel PositionYLabel;
    private javax.swing.JLabel PositionYLabel1;
    private javax.swing.JLabel PositionYLabel2;
    private javax.swing.JTextField PositionYTextField;
    private javax.swing.JButton RegisterButton;
    private javax.swing.JButton RotateLeftButton;
    private javax.swing.JButton RotateRightButton;
    private javax.swing.JLabel SmellLabel;
    private javax.swing.JTextField SmellTextField;
    private javax.swing.JButton StartContainerButton;
    private javax.swing.JTextField StateTextField;
    private javax.swing.JButton TrackButton;
    private javax.swing.JButton UseButton;
    private javax.swing.JCheckBox jCheckBox1;
    // End of variables declaration//GEN-END:variables
}
