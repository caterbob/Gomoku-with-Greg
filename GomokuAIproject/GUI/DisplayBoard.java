package GomokuAIproject.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import GomokuAIproject.Board;
import GomokuAIproject.Constants.BoardConstants;
import GomokuAIproject.Manager;

public class DisplayBoard {

    private JPanel boardPanel = new JPanel();
    private Cell[] cellArray;
    private Board board;
    private static DisplayBoard instance = null;
    
    private DisplayBoard(){

        board = Board.getInstance();

        boardPanel.setLayout(new GridLayout(13, 13, 0, 0));
        boardPanel.setBackground(Color.LIGHT_GRAY);
        boardPanel.setSize(650, 650);
        boardPanel.setMaximumSize(new Dimension(650, 650));

        initializeCellArray();
        for(int i = 0; i < 169; i++){
            boardPanel.add(cellArray[i]);
        }

    }

    public static DisplayBoard getInstance(){
        if(instance == null)
            instance = new DisplayBoard();
        return instance;
    }

    public JPanel getBoardPanel(){
        return boardPanel;
    }

    public void updateDisplay(){
        for(int i = 0; i < 169; i++){
            cellArray[i].changeState(board.getCellValue(i));
        }
    }

    private void initializeCellArray(){
        cellArray = new Cell[169];
        for(int i = 0; i < 169; i++){
            cellArray[i] = new Cell(Cell.CellType.NORMAL, i);
        }
        for(int i = 0; i < 13; i++){
            cellArray[i] = new Cell(Cell.CellType.TOP, i);
        }
        for(int i = 156; i < 169; i++){
            cellArray[i] = new Cell(Cell.CellType.LOW, i);
        }
        for(int i = 0; i < 157; i += 13){
            cellArray[i] = new Cell(Cell.CellType.LEFT, i);
        }
        for(int i = 12; i < 169; i += 13){
            cellArray[i] = new Cell(Cell.CellType.RIGHT, i);
        }
        cellArray[0] = new Cell(Cell.CellType.TOP_LEFT, 0);
        cellArray[12] = new Cell(Cell.CellType.TOP_RIGHT, 12);
        cellArray[156] = new Cell(Cell.CellType.LOW_LEFT, 156);
        cellArray[168] = new Cell(Cell.CellType.LOW_RIGHT, 168);
        cellArray[84] = new Cell(Cell.CellType.MARKED, 84);
        cellArray[42] = new Cell(Cell.CellType.MARKED, 42);
        cellArray[48] = new Cell(Cell.CellType.MARKED, 48);
        cellArray[120] = new Cell(Cell.CellType.MARKED, 120);
        cellArray[126] = new Cell(Cell.CellType.MARKED, 126);
    }

    private class Cell extends JButton{

        private int myType;
        private int myLocation;
        private ImageIcon emptyState;
        private ImageIcon whiteState;
        private ImageIcon blackState;

        public static class CellType{
            public static final int NORMAL = 0;
            public static final int LEFT = 1;
            public static final int TOP = 2;
            public static final int RIGHT = 3;
            public static final int LOW = 4;
            public static final int TOP_LEFT = 5;
            public static final int TOP_RIGHT = 6;
            public static final int LOW_LEFT = 7;
            public static final int LOW_RIGHT = 8;
            public static final int MARKED = 9;
        }

        public Cell(int cellType, int location){
            myType = cellType;
            myLocation = location;
            initialize();
        }

        public ActionListener myAction(){
            return new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    if(Manager.canPlayerMove() && !board.isGameOver()){
                        board.placeStone(myLocation);
                        updateDisplay();
                    }
                }
            };
        }

        public void changeState(int state){
            if(state == BoardConstants.EMPTY)
                setIcon(emptyState);
            else if(state == BoardConstants.BLACK)
                setIcon(blackState);
            else if(state == BoardConstants.WHITE)
                setIcon(whiteState);
        }

        // helper method of constructor
        public void initialize(){
            setFocusable(false);
            setBackground(Color.RED);
            setPreferredSize(new Dimension(50, 50));
            setBorderPainted(false);
            setMargin(new Insets(0, 0, 0, 0));
            addActionListener(myAction());

            if(myType == CellType.NORMAL){
                emptyState = new ImageIcon("GomokuAIproject/Assets/EmptyCell.png");
                blackState = new ImageIcon("GomokuAIproject/Assets/BlackCell.png");
                whiteState = new ImageIcon("GomokuAIproject/Assets/WhiteCell.png");
            }else if(myType == CellType.LEFT){
                emptyState = new ImageIcon("GomokuAIproject/Assets/EmptyLeftCell.png");
                blackState = new ImageIcon("GomokuAIproject/Assets/BlackLeftCell.png");
                whiteState = new ImageIcon("GomokuAIproject/Assets/WhiteLeftCell.png");
            }else if(myType == CellType.TOP){
                emptyState = new ImageIcon("GomokuAIproject/Assets/EmptyTopCell.png");
                blackState = new ImageIcon("GomokuAIproject/Assets/BlackTopCell.png");
                whiteState = new ImageIcon("GomokuAIproject/Assets/WhiteTopCell.png");
            }else if(myType == CellType.RIGHT){
                emptyState = new ImageIcon("GomokuAIproject/Assets/EmptyRightCell.png");
                blackState = new ImageIcon("GomokuAIproject/Assets/BlackRightCell.png");
                whiteState = new ImageIcon("GomokuAIproject/Assets/WhiteRightCell.png");
            }else if(myType == CellType.LOW){
                emptyState = new ImageIcon("GomokuAIproject/Assets/EmptyLowCell.png");
                blackState = new ImageIcon("GomokuAIproject/Assets/BlackLowCell.png");
                whiteState = new ImageIcon("GomokuAIproject/Assets/WhiteLowCell.png");
            }else if(myType == CellType.TOP_LEFT){
                emptyState = new ImageIcon("GomokuAIproject/Assets/EmptyTopLeftCell.png");
                blackState = new ImageIcon("GomokuAIproject/Assets/BlackTopLeftCell.png");
                whiteState = new ImageIcon("GomokuAIproject/Assets/WhiteTopLeftCell.png");
            }else if(myType == CellType.TOP_RIGHT){
                emptyState = new ImageIcon("GomokuAIproject/Assets/EmptyTopRightCell.png");
                blackState = new ImageIcon("GomokuAIproject/Assets/BlackTopRightCell.png");
                whiteState = new ImageIcon("GomokuAIproject/Assets/WhiteTopRightCell.png");
            }else if(myType == CellType.LOW_LEFT){
                emptyState = new ImageIcon("GomokuAIproject/Assets/EmptyLowLeftCell.png");
                blackState = new ImageIcon("GomokuAIproject/Assets/BlackLowLeftCell.png");
                whiteState = new ImageIcon("GomokuAIproject/Assets/WhiteLowLeftCell.png");
            }else if(myType == CellType.LOW_RIGHT){
                emptyState = new ImageIcon("GomokuAIproject/Assets/EmptyLowRightCell.png");
                blackState = new ImageIcon("GomokuAIproject/Assets/BlackLowRightCell.png");
                whiteState = new ImageIcon("GomokuAIproject/Assets/WhiteLowRightCell.png");
            }else if(myType == CellType.MARKED){
                emptyState = new ImageIcon("GomokuAIproject/Assets/EmptyMarkedCell.png");
                blackState = new ImageIcon("GomokuAIproject/Assets/BlackCell.png");
                whiteState = new ImageIcon("GomokuAIproject/Assets/WhiteCell.png");
            }

            setIcon(emptyState);
        }

    }
}
