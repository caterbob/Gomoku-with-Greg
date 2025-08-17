package GomokuAIproject.GUI;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import GomokuAIproject.Manager;
import GomokuAIproject.Greg0.Greg0;

public class Launcher {

    public static void main(String args[]){

        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                MainWindow main = new MainWindow();
                main.show();
            }
        });

    }
    
}