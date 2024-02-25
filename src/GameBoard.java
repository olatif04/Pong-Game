import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import java.util.Random;


public class GameBoard extends JPanel implements ActionListener {

    private int specialBallX = -100, specialBallY = -100; // Start off-screen
    private int specialBallSize = 20;
    private Color specialBallColor = Color.BLUE; // Start as blue
    private boolean specialBallActive = false;
    private int specialBallSpeed = 2;

    private int currentSpeed = 2; // Default speed, adjust as needed

    private final int WIDTH = 800, HEIGHT = 600;
    private final int PADDLE_WIDTH = 10;
    private int PADDLE_HEIGHT = 100;
    private final int BALL_SIZE = 20;
    private int ballX = WIDTH / 2, ballY = HEIGHT / 2;
    private int ballXSpeed = 2, ballYSpeed = 2;
    private int paddle1Y = HEIGHT / 2 - PADDLE_HEIGHT / 2, paddle2Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int score1 = 0, score2 = 0; // Scores for player 1 and 2
    private Timer timer;

    public GameBoard() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    paddle1Y = Math.max(paddle1Y - 30, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    paddle1Y = Math.min(paddle1Y + 30, HEIGHT - PADDLE_HEIGHT);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    paddle2Y = Math.max(paddle2Y - 30, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    paddle2Y = Math.min(paddle2Y + 30, HEIGHT - PADDLE_HEIGHT);
                }
            }
        });
        timer = new Timer(10, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Set color and fonts for the scores
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        // Draw the scores
        g.drawString("Player 1: " + score1, 100, 50);
        g.drawString("Player 2: " + score2, WIDTH - 250, 50);
        // Draw paddles and ball
        g.fillRect(5, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH - PADDLE_WIDTH - 5, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
        if (specialBallActive) {
            g.setColor(specialBallColor);
            g.fillOval(specialBallX, specialBallY, specialBallSize, specialBallSize);
        }
    }

    private int specialBallTimer = 0;
    private final int SPECIAL_BALL_SPAWN_RATE = 900; // Example value, adjust as needed

    @Override
    public void actionPerformed(ActionEvent e) {
        moveBall();
        checkCollision();
        checkWinCondition();
        moveSpecialBall();
        specialBallTimer++;
        if (specialBallTimer >= SPECIAL_BALL_SPAWN_RATE) {
            spawnSpecialBall();
            specialBallTimer = 0; // Reset timer
        }
        repaint();
    }

    private void spawnSpecialBall() {
        specialBallActive = true;
        specialBallX = WIDTH / 2 - specialBallSize / 2; // Start from the center
        specialBallY = new Random().nextInt(HEIGHT - specialBallSize);
        specialBallColor = new Random().nextBoolean() ? Color.BLUE : Color.RED;
        // Randomize the direction
        specialBallSpeed = new Random().nextBoolean() ? 2 : -2;
    }

    private void moveSpecialBall() {
        if (specialBallActive) {
            specialBallX += specialBallSpeed;
            // Deactivate the ball if it goes off-screen
            if (specialBallX < 0 || specialBallX > WIDTH) {
                specialBallActive = false;
            }
        }
    }

    private void moveBall() {
        ballX += ballXSpeed;
        ballY += ballYSpeed;

        // Check for top or bottom collision
        if (ballY <= 0 || ballY >= HEIGHT - BALL_SIZE) {
            ballYSpeed = -ballYSpeed;
        }

        // Ball goes off the left side
        if (ballX < 0) {
            score2++; // Award point to Player 2
            resetBall();
        }
        // Ball goes off the right side
        else if (ballX > WIDTH - BALL_SIZE) {
            score1++; // Award point to Player 1
            resetBall();
        }
    }

    private void resetBall() {
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        // You can also add logic here to change the ball's direction randomly if desired
        // For simplicity, we'll just reverse the ball's horizontal direction
        ballXSpeed = -ballXSpeed;
    }


    private final int MIN_PADDLE_HEIGHT = 60;
    private final int MAX_PADDLE_HEIGHT = 140;

    private void checkCollision() {
        // Normal ball collision with paddles
        if (ballX <= 5 + PADDLE_WIDTH && ballY + BALL_SIZE >= paddle1Y && ballY <= paddle1Y + PADDLE_HEIGHT) {
            ballXSpeed = -ballXSpeed;
        }
        if (ballX + BALL_SIZE >= WIDTH - PADDLE_WIDTH - 5 && ballY + BALL_SIZE >= paddle2Y && ballY <= paddle2Y + PADDLE_HEIGHT) {
            ballXSpeed = -ballXSpeed;
        }

        // Special ball collision with paddles
        if (specialBallActive) {
            // Check collision with left paddle
            if (specialBallX <= 5 + PADDLE_WIDTH && specialBallY + specialBallSize >= paddle1Y && specialBallY <= paddle1Y + PADDLE_HEIGHT) {
                if (specialBallColor == Color.BLUE) {
                    paddle1Y = Math.max(paddle1Y - 20, 0);
                    PADDLE_HEIGHT = Math.min(PADDLE_HEIGHT + 40, MAX_PADDLE_HEIGHT);
                } else if (specialBallColor == Color.RED) {
                    PADDLE_HEIGHT = Math.max(PADDLE_HEIGHT - 40, MIN_PADDLE_HEIGHT);
                }
                specialBallActive = false; // Deactivate the special ball after collision
                return; // Exit the method to avoid processing collisions with the other paddle
            }
            // Check collision with right paddle
            if (specialBallX + specialBallSize >= WIDTH - PADDLE_WIDTH - 5 && specialBallY + specialBallSize >= paddle2Y && specialBallY <= paddle2Y + PADDLE_HEIGHT) {
                if (specialBallColor == Color.BLUE) {
                    paddle2Y = Math.max(paddle2Y - 20, 0);
                    PADDLE_HEIGHT = Math.min(PADDLE_HEIGHT + 40, MAX_PADDLE_HEIGHT);
                } else if (specialBallColor == Color.RED) {
                    PADDLE_HEIGHT = Math.max(PADDLE_HEIGHT - 40, MIN_PADDLE_HEIGHT);
                }
                specialBallActive = false; // Deactivate the special ball after collision
                return; // Exit the method to avoid processing collisions with the other paddle
            }
        }
    }






    private void checkWinCondition() {
        if (score1 == 10 || score2 == 10) {
            timer.stop();
            String winner = score1 == 10 ? "Player 1" : "Player 2";
            JOptionPane.showMessageDialog(this, winner + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void startGame(int speed) {
        score1 = 0;
        score2 = 0;
        resetBall(); // Ensure this resets the ball to the initial position
        currentSpeed = speed; // Update the current speed
        ballXSpeed = speed;
        ballYSpeed = speed;
        timer.start();
    }



    public void restartGame() {
        score1 = 0;
        score2 = 0;
        startGame(currentSpeed); // Reset the game to its initial state
    }
}
