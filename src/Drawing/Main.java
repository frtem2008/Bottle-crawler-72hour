package Drawing;

//основной игровой класс

import GameObjects.Block;
import GameObjects.Constants;
import GameObjects.Player;
import GameObjects.Type;
import UserInput.Keyboard;
import UserInput.Mouse;
import Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {
    //клавиатура + мышь
    public static final Mouse mouse = new Mouse();
    public static final Keyboard keyboard = new Keyboard();
    //размер кастомного шрифта (на потом)
    private static final float FONTSIZE = 35f;
    //все блоки
    private static final ArrayList<Block> blocks = new ArrayList<>();
    //смещение камеры (положения игрока) относительно левого верхнего угла экрана
    public static double cameraX = Constants.DefaultCameraX.getValue();
    public static double cameraY = Constants.DefaultCameraY.getValue();
    //размер JFrame
    public static Dimension frameSize;
    public static Player player;
    public static int gameState = 1; //1 - загрузочный экран, 2 - игра, 3 - конец игры, 4 - перезагрузка
    //для анимаций
    public static long frames = 0;
    //изображения
    public static Image MapImage, Logo, Die, Win, Exit, LoadScreenBg, ButtonPlay, Average, Bottle, Ladder, Block, Slime, Heart, Arseny;
    public static Point finish;
    public static int bottles, bottlesToCollect = 0;
    public static ArrayList<Block> collectedBottles = new ArrayList<>();
    private static Sprite playerSprite;
    private static long globalClock = 0;

    //инициализация миникарты (замена цветов)
    private static void initMap() {
        //Изображение карты
        BufferedImage mapPixel = Utils.toBufferedImage(MapImage);
        System.out.println("Initialising map");
        BufferedImage miniMap = new BufferedImage(mapPixel.getWidth(), mapPixel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int checkColor;
        for (int i = 0; i < mapPixel.getWidth(); i++) {
            for (int j = 0; j < mapPixel.getHeight(); j++) {
                checkColor = mapPixel.getRGB(i, j);
                if (checkColor == new Color(255, 255, 255).getRGB() ||
                        checkColor == new Color(255, 0, 0).getRGB()
                ) {
                    miniMap.setRGB(i, j, new Color(0, 0, 0, 128).getRGB());
                } else {
                    miniMap.setRGB(i, j, mapPixel.getRGB(i, j));
                }
            }
        }

        System.out.println("Initialising map finished");
        MapImage = miniMap;
    }

    private static void initGame() {
        System.out.println("Initialising game...");
        blocks.clear();
        bottlesToCollect = 0;
        BufferedImage mapPixel = Utils.toBufferedImage(MapImage);
        for (int i = 0; i < mapPixel.getWidth(); i++) {
            for (int j = 0; j < mapPixel.getHeight(); j++) {
                if (mapPixel.getRGB(i, j) == new Color(0, 0, 0).getRGB()) {
                    blocks.add(new Block(i * (int) Constants.BlockSize.getValue(), j * (int) Constants.BlockSize.getValue()));
                } else if (mapPixel.getRGB(i, j) == new Color(0, 0, 255).getRGB()) {
                    System.out.println("player");
                    System.out.println("I = " + i);
                    System.out.println("J = " + j);
                    player = new Player((int) (i * Constants.BlockSize.getValue()), (int) (j * Constants.BlockSize.getValue() + 25), blocks, playerSprite);
                    System.out.println("(" + player.x + ", " + player.y + ")");
                } else if (mapPixel.getRGB(i, j) == new Color(255, 0, 255).getRGB()) {
                    cameraX = i * Constants.BlockSize.getValue();
                    cameraY = j * Constants.BlockSize.getValue();
                } else if (mapPixel.getRGB(i, j) == new Color(128, 64, 0).getRGB()) {
                    blocks.add(new Block(i * (int) Constants.BlockSize.getValue(), j * (int) Constants.BlockSize.getValue(), Type.Ladder));
                } else if (mapPixel.getRGB(i, j) == new Color(0, 128, 0).getRGB() || mapPixel.getRGB(i, j) == new Color(0, 128, 64).getRGB()) {
                    blocks.add(new Block(i * (int) Constants.BlockSize.getValue(), j * (int) Constants.BlockSize.getValue(), Type.Slime));
                } else if (mapPixel.getRGB(i, j) == new Color(0, 255, 255).getRGB()) {
                    System.out.println("Bottle");
                    blocks.add(new Block(i * (int) Constants.BlockSize.getValue(), j * (int) Constants.BlockSize.getValue(), Type.Bottle));
                } else if (mapPixel.getRGB(i, j) == new Color(128, 0, 64).getRGB()) {
                    finish = new Point((int) (i * Constants.BlockSize.getValue()), (int) (j * Constants.BlockSize.getValue()));
                    blocks.add(new Block(i * (int) Constants.BlockSize.getValue(), j * (int) Constants.BlockSize.getValue(), Type.Exit));
                }
            }
        }

        blocks.removeAll(collectedBottles);

        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).type != null) {
                if (blocks.get(i).type.equals(Type.Bottle)) {
                    bottlesToCollect++;
                }
            }
        }

        System.out.println("Blocks: " + blocks.size());
        System.out.println("Initialising game finished");
    }

    //функция загрузки изображений
    public static void loadImages() {
        System.out.println("Loading images");
        Average = Utils.getImage(Main.class, "PlayerSheet.png");
        MapImage = Utils.getImage(Main.class, "Map5.png");
        Logo = Utils.getImage(Main.class, "logo.png");
        Die = Utils.getImage(Main.class, "die.png");
        Win = Utils.getImage(Main.class, "win.png");
        Exit = Utils.getImage(Main.class, "exit.png");
        Bottle = Utils.getImage(Main.class, "bottle.png");
        Slime = Utils.getImage(Main.class, "Slime.png");
        Block = Utils.getImage(Main.class, "tile.png");
        Ladder = Utils.getImage(Main.class, "Ladder.png");
        Heart = Utils.getImage(Main.class, "Heart.png");
        Arseny = Utils.getImage(Main.class, "end.jpg");

        LoadScreenBg = Utils.getImage(Main.class, "Background.png");
        ButtonPlay = Utils.getImage(Main.class, "ButtonPlay.png");
        System.out.println("Finished loading images");
    }

    public static void reload() {
        System.out.println("Reloading...");
        loadImages();
        initMap();
        initGame();
        frames = 0;
        globalClock = 0;
        gameState = 4;
        System.out.println("Reloading finished");
    }

    //начало игры ()
    public void startDrawing(JFrame frame) {
        new Thread(() -> {
            //подгружаем изображения и прогружаем игру
            loadImages();
            //setFont();
            initMap();
            initGame();
            playerSprite = new Sprite(32, 32, Average, 2, 3);
            player.sprite = playerSprite;
            //привязываем слушатели
            frame.addKeyListener(keyboard);
            frame.addMouseListener(mouse);
            frame.addMouseMotionListener(mouse);

            //изображение для отрисовки (для изменения пикселей после рисования объектов)
            BufferedImage frameImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);

            //создание буфера
            frame.createBufferStrategy(2);
            BufferStrategy bs = frame.getBufferStrategy();

            //для использования tab, alt и т.д
            frame.setFocusTraversalKeysEnabled(false);

            //для стабилизации и ограничения фпс
            long start, end, len;
            double frameLength;

            int innerX, innerY;
            //графика итогового окна
            Graphics2D frameGraphics;

            //длина кадра (число после дроби - фпс)
            frameLength = 1000.0 / 60;

            //главный игровой цикл
            while (true) {
                //время начала кадра
                start = System.currentTimeMillis();

                //обновление размера JFrame
                frameSize = frame.getContentPane().getSize();

                if (Display.isFullScreen) {
                    innerX = 0;
                    innerY = 0;
                } else {
                    innerX = 8;
                    innerY = 30;
                }
                //получение информации о буфере
                frameGraphics = (Graphics2D) bs.getDrawGraphics();

                //очистка экрана перед рисованием
                frameGraphics.clearRect(0, 0, frame.getWidth(), frame.getHeight());
                frameImage.getGraphics().clearRect(0, 0, frameImage.getWidth(), frameImage.getHeight());
                //фон
                //frameImage.getGraphics().drawImage(BackGround, 0, 0, frame.getWidth(), frame.getHeight(), null);
                //рисование на предварительном изображении
                this.draw(frameImage.getGraphics(), gameState);
                //отрисовка миникарты
                //сделать если надо
                //рисование на итоговом окне
                //frameImage = Utils.gray(frameImage); // какой-нибудь эффект
                frameGraphics.setColor(Color.BLACK);
                frameGraphics.fillRect(innerX, innerY, 1600, 900);

                if (gameState == 1) {
                    frameGraphics.drawImage(Utils.setTransparency(Utils.toBufferedImage(frameImage), (int) (frames * 1.5)), innerX, innerY, frameImage.getWidth(), frameImage.getHeight(), null);
                } else if (gameState == 2) {
                    frameGraphics.drawImage(frameImage, innerX, innerY, frameImage.getWidth(), frameImage.getHeight(), null);
                } else if (gameState == 3 || gameState == 5) {
                    frameGraphics.drawImage(Utils.setTransparency(Utils.toBufferedImage(frameImage), (int) (frames * 1.5)), innerX, innerY, frameImage.getWidth(), frameImage.getHeight(), null);
                } else if (gameState == 4) {
                    frameGraphics.drawImage(Utils.setTransparency(Utils.toBufferedImage(frameImage), (int) (frames * 3.5)), innerX, innerY, frameImage.getWidth(), frameImage.getHeight(), null);
                    if (frames * 3.5 >= 255) {
                        gameState = 2;
                    }
                }

                //очистка мусора
                frameImage.getGraphics().dispose();
                frameGraphics.dispose();


                //показ буфера на холсте
                bs.show();

                //разворот на полный экран
                if (Keyboard.getF11()) {
                    while (Keyboard.getF11()) {
                        keyboard.update();
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    frame.dispose();
                    if (Display.isFullScreen) {
                        frame.setUndecorated(false);
                        frame.setExtendedState(Frame.NORMAL);
                        frame.setBounds(Display.x, Display.y, Display.w, Display.h);
                        cameraX = 500;
                    } else {
                        cameraX = frameSize.getWidth() / 1.2;
                        frame.setUndecorated(true);
                        frame.setExtendedState(6);
                    }
                    Display.isFullScreen = !Display.isFullScreen;
                    frame.setVisible(true);
                }

                //код для выхода из игры
                /*if (Keyboard.getQ()) {
                    System.out.println("Выход");
                    System.exit(20);
                }*/
                if (gameState == 2) {
                    //перезагрузка игры
                    if (Keyboard.getR()) {
                        reload();
                    }

                    //обновления клавиатуры и игрока
                    keyboard.update();
                    player.move();
                    moveCamera();
                    playerSprite.animate(globalClock);
                }

                if (frames % 5 == 0) {
                    globalClock++;
                }

                frames++;
                keyboard.update();

                //замер времени, ушедшего на отрисовку кадра
                end = System.currentTimeMillis();
                len = end - start;

                //стабилизация фпс
                if (len < frameLength) {
                    try {
                        Thread.sleep((long) (frameLength - len));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    //движение камеры по Y
    public void moveCamera() {
        if (player.fallHeight < Constants.MaxFallingHeight.getValue()) {
            if (player.y - cameraY >= Constants.MinCameraY.getValue() - Constants.PlayerSize.getValue()) {
                if (player.y - cameraY < frameSize.getHeight() - 200) {
                    if (player.ySpeed >= 2) {
                        cameraY += player.ySpeed;
                    } else {
                        cameraY += 4;
                    }
                } else {
                    cameraY += player.ySpeed;
                }
            } else if (player.y - cameraY <= Constants.MaxCameraY.getValue() + Constants.PlayerSize.getValue()) {
                if (player.y - cameraY < frameSize.getHeight() - 200) {
                    if (player.ySpeed >= 2) {
                        cameraY -= player.ySpeed / 2;
                    } else {
                        cameraY -= 4;
                    }
                } else {
                    cameraY -= player.ySpeed;
                }
            }
        }
    }

    //рисование
    public void draw(Graphics g, int gameState) {
        if (gameState == 1) {
            g.drawImage(LoadScreenBg, 0, 0, frameSize.width, frameSize.height, null);
            g.drawImage(Logo,
                    frameSize.width / 2 - Logo.getWidth(null),
                    150,
                    Logo.getWidth(null) * 2,
                    Logo.getHeight(null) * 2,
                    null
            );
            g.drawImage(ButtonPlay,
                    frameSize.width / 2 - 128,
                    400,
                    256, 64,
                    null
            );


        }
        if (gameState == 2 || gameState == 4) {
            g.setColor(Color.white);
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i).hitBox.intersects(new Rectangle(player.x - 1000, player.y - 1000, 2500, 2000)))
                    blocks.get(i).draw(g, Utils::toScreenX, Utils::toScreenY);
            }

            g.drawImage(Bottle, 20, 20, 64, 64, null);
            g.setFont(new Font("Calibri", Font.BOLD, 32));
            g.drawImage(Heart, frameSize.width - 88 - 64, 20, 64, 64, null);
            g.setColor(new Color(128, 0, 0));
            g.drawString(" X " + (int) Player.lives, frameSize.width - 88, 64);

            g.setColor(Color.CYAN);
            if (bottlesToCollect == 0) {
                g.drawString(" all, now find the exit", 84, 64);
            } else {
                g.drawString(bottlesToCollect + " to collect", 84, 64);
            }
            player.draw(g, Utils::toScreenX, Utils::toScreenY);
        }
        if (gameState == 3 || gameState == 5) {
            g.drawImage(Logo,
                    frameSize.width / 2 - Logo.getWidth(null),
                    150,
                    Logo.getWidth(null) * 2,
                    Logo.getHeight(null) * 2,
                    null
            );

            if (gameState == 3) {
                g.drawImage(Arseny,
                        frameSize.width - 1100,
                        -800,
                        3840 / 2,
                        3840 / 2,
                        null
                );

                g.drawImage(Die,
                        frameSize.width / 2 - Die.getWidth(null),
                        350,
                        Die.getWidth(null) * 2,
                        Die.getHeight(null) * 2,
                        null
                );

            } else {
                System.out.println("WIN");
                g.drawImage(Win,
                        frameSize.width / 2 - Die.getWidth(null),
                        350,
                        Die.getWidth(null) * 2,
                        Die.getHeight(null) * 2,
                        null
                );
            }

            g.drawImage(Exit,
                    frameSize.width - 400,
                    frameSize.height - 94,
                    Exit.getWidth(null) * 2,
                    Exit.getHeight(null) * 2,
                    null
            );
        }
    }

    //загрузка кастомного шрифта (на потом)
    public void setFont() {
        try {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            File fontFile = new File(Utils.getFileUrl(Main.class, "Fonts/Undertale Font.ttf").toURI());
            Font undertaleFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    fontFile
            ).deriveFont(Font.BOLD, FONTSIZE);

            ge.registerFont(undertaleFont);
        } catch (IOException | FontFormatException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //старый блок генератор (для тестов)
    public void initBlocks() {
        for (int i = 0; i < 25; i++) {
            for (int j = 5; j < 15; j++) {
                if (i != 3 && j != 6) {
                    blocks.add(new Block(i * 100 + 2 * i, j * 100 + 2 * j));
                }
            }
        }
    }
}