import javax.swing.*;
import java.awt.*;

public class PongGame {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong Game");
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem startItem = new JMenuItem("Start");
        JMenuItem restartItem = new JMenuItem("Restart");
        JMenuItem quitItem = new JMenuItem("Quit");

        gameMenu.add(startItem);
        gameMenu.add(restartItem);
        gameMenu.add(quitItem);
        menuBar.add(gameMenu);

        frame.setJMenuBar(menuBar);

        GameBoard gameBoard = new GameBoard();
        frame.add(gameBoard);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);


        startItem.addActionListener(e -> showSpeedSelectionDialog(gameBoard));
        restartItem.addActionListener(e -> gameBoard.restartGame());
        quitItem.addActionListener(e -> System.exit(0));

        showSpeedSelectionDialog(gameBoard);

        frame.setVisible(true);
    }

    private static void showSpeedSelectionDialog(GameBoard gameBoard) {
        JDialog speedDialog = new JDialog();
        speedDialog.setTitle("Select Ball Speed");
        speedDialog.setLayout(new FlowLayout());
        speedDialog.setSize(300, 200);
        speedDialog.setResizable(false);
        speedDialog.setLocationRelativeTo(null);
        speedDialog.setModal(true);

        JLabel speedLabel = new JLabel("Speed: ");
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 2);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            gameBoard.startGame(speedSlider.getValue());
            speedDialog.dispose();
        });

        speedDialog.add(speedLabel);
        speedDialog.add(speedSlider);
        speedDialog.add(startButton);
        speedDialog.setVisible(true);
    }
}
