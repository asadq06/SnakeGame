package snakegame;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.*;

public class Game {
    
    public Game() {
        init();
        
        //Calculate dimensions
        windowWidth = (blockSpace + blockSize) * widthBlocks + blockSpace;
        windowHeight = (blockSpace + blockSize) * heightBlocks + blockSpace;
        
    }
    public void run(){
        long time, lastTime = System.currentTimeMillis();    
        double deltaTime = 0;
        double fps = 10;

        createWindow();
        while(true) {
            time = System.currentTimeMillis();
            deltaTime += (int) ((time - lastTime));
            lastTime = time;
            
            if(deltaTime >  1 / fps * 1000){
                deltaTime = 0;
                update();
                drawFrame();
            }
            
        }
            
        
    }
    private enum Direction {
        up,down,left,right,center
    }
    private int blockSize;
    private int widthBlocks,heightBlocks;
    private int blockSpace;
    private int windowHeight, windowWidth;
    private double maxSnakeSize;
    private Frame mainFrame;
    private Panel gamePanel;
    private Dialog gameOverDialog;
    private Color backgroundColor, snakeColor, appleColor;
    private ArrayList<Rectangle> snake;
    private Rectangle apple;
    private Direction snakeDirection;
    private void init(){
        maxSnakeSize = 1;
        blockSize = 15;
        blockSpace = 1;
        widthBlocks = 30;
        heightBlocks = 30;
        backgroundColor = Color.BLACK;
        snakeColor = Color.GREEN;
        appleColor = Color.RED;
        snakeDirection = Direction.center;
        snake = new ArrayList<Rectangle>();
        snake.add(createGameRectangle(widthBlocks / 2, heightBlocks / 2));
        generateApple();
    }
    private void createWindow() {        
        mainFrame = new Frame("SnakeGame");
        gamePanel = new Panel();
        gameOverDialog = new Dialog(mainFrame);
        
        gameOverDialog.setBackground(Color.gray);
        gameOverDialog.setLayout(new BorderLayout());
        gameOverDialog.add(new Button("Close"));
        
        gamePanel.setPreferredSize(new Dimension(windowWidth, windowHeight));
        gamePanel.setBackground(backgroundColor);
        
        mainFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                Direction newDirection = snakeDirection;
                
                switch (e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        newDirection = Direction.up;
                        break;
                    case KeyEvent.VK_DOWN:
                        newDirection = Direction.down;
                        break;
                    case KeyEvent.VK_LEFT:
                        newDirection = Direction.left;
                        break;
                    case KeyEvent.VK_RIGHT:
                        newDirection = Direction.right;
                        break;
                    default:
                        break;
                }
                
                if(!isIllegalMove(newDirection))
                    snakeDirection = newDirection;
            }
        });
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(gamePanel, BorderLayout.CENTER);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.dispose();
            }
        });
        mainFrame.setResizable(false);
        mainFrame.pack();
        mainFrame.setVisible(true);
        centerWindow();
    }
    private void centerWindow(){
        int centerX, centerY;
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        centerX = (int) ((dimension.getWidth() - mainFrame.getWidth()) / 2);
        centerY = (int) ((dimension.getHeight() - mainFrame.getHeight()) / 2);
        mainFrame.setLocation(centerX, centerY);
    }
    private void drawApple() {
        Graphics g = gamePanel.getGraphics();
        Graphics2D g2 = (Graphics2D)g;
        if(apple == null) {
            generateApple();
        }
 
        g2.setColor(appleColor);
        g2.draw(apple);
        g2.fill(apple);
    }
    private void generateApple() {
        Random rand = new Random();
        
        apple = createGameRectangle(rand.nextInt(widthBlocks),
                rand.nextInt(heightBlocks));
        
        if(snake.contains(apple))
            generateApple();
    }
    private void drawSnake(){
        Graphics g = gamePanel.getGraphics();
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(snakeColor);
        
        snake.stream().map((rect) -> {
            g2.draw(rect);
            return rect;
        }).forEach((rect) -> {
            g2.fill(rect);
        });
    }
    private void drawFrame(){
        Graphics g = gamePanel.getGraphics();
        Graphics2D g2 = (Graphics2D)g;
        
        g2.clearRect(0,0,windowWidth,windowHeight);
        drawApple();
        drawSnake();
        
    }
    private Rectangle createGameRectangle(int x, int y) {
        Rectangle rect = new Rectangle();
        

        
        rect.setRect(x * (blockSize + blockSpace ) + blockSpace,
                y * (blockSize + blockSpace ) + blockSpace,
                blockSize - 1, blockSize - 1);
        return rect;
    }
    private void update(){
        Rectangle head = new Rectangle();
        head.setRect(snake.get(snake.size() - 1));
        switch(snakeDirection){
            case up:
                head.y -= blockSize + blockSpace;
                break;
            case down:
                head.y += blockSize + blockSpace;
                break;
            case left:
                head.x -= blockSize + blockSpace;
                break;
            case right:
                head.x += blockSize + blockSpace;
                break;
            default:
                break;
        }
        
        //Game over?
        if((head.x < 0 || head.x > windowWidth - 1) ||
            (head.y < 0 || head.y > windowHeight - 1) ||
            (snake.contains(head) && snake.size() > 1)) {
            init();
            return;
        }
        else
            snake.add(head);
        
        
        //Check if the head finds the apple
        if(head.equals(apple)){
            generateApple();
            maxSnakeSize += 3 + maxSnakeSize * 0.1;
            System.out.println(maxSnakeSize);
        }
        
        //Remove snake parts if snake is max size
        if(maxSnakeSize < snake.size()){
            snake.remove(0);
        }
    }
    private Boolean isIllegalMove(Direction d){
        
        if(snake.size() <= 1)
            return false;
        
        switch(d){
            case up:
                if(snakeDirection == Direction.down)
                    return true;
                break;
            case down:
                if(snakeDirection == Direction.up)
                    return true;
                break;
            case left:
                if(snakeDirection == Direction.right)
                    return true;
                break;
            case right:
                if(snakeDirection == Direction.left)
                    return true;
                break;
            default:
                return false;
        }
        
        return false;
    }
}

