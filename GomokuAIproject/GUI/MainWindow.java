package GomokuAIproject.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import GomokuAIproject.Board;
import GomokuAIproject.Manager;
import GomokuAIproject.Greg0.Greg0;
import GomokuAIproject.Greg1.Greg1;
import GomokuAIproject.Greg2.Greg2;
import GomokuAIproject.Greg3.Greg3;

public class MainWindow {
    
    private static JFrame window;
    private JPanel startMenu;
    private JPanel boardMenu;
    
    public MainWindow(){
        window = new JFrame();
        window.setTitle("Gomoku with Greg");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setSize(800, 900);
        window.setLocationRelativeTo(null); // centers screen

        boardMenu = new JPanel();    // allows board to render properly if inside boardMenu
        boardMenu.setLayout(new BoxLayout(boardMenu, BoxLayout.Y_AXIS));
        boardMenu.setBackground(Color.BLACK);

        startMenu = new JPanel();
        startMenu.setLayout(new FlowLayout());
        JButton blackPlay = new JButton("Play as Black");
        JButton whitePlay = new JButton("Play as White");
        JButton playEngines = new JButton("Engine vs Engine");
        blackPlay.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                boardMenu.add(DisplayBoard.getInstance().getBoardPanel());
                Manager.runPlayerVEngine(new Greg3(true, 7, false, true), true);
                transition();
            }
        });
        whitePlay.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                boardMenu.add(DisplayBoard.getInstance().getBoardPanel());
                Manager.runPlayerVEngine(new Greg3(false, 7, false, true), false);
                transition();
            }
        });
        playEngines.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                boardMenu.add(DisplayBoard.getInstance().getBoardPanel());
                Manager.runEngineVEngine(new Greg3(true, 7, false, true), new Greg2(false, 5), 100, true, true);
                transition();
            }
        });

        startMenu.add(blackPlay);
        startMenu.add(whitePlay);
        startMenu.add(playEngines);

        window.setContentPane(startMenu);
    }

    public void show(){
        window.setVisible(true);
    }

    // transitions from start menu to board menu
    public void transition(){
        window.setContentPane(boardMenu);
        window.pack();
    }
}
