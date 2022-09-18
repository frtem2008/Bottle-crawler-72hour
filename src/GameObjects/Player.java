package GameObjects;

import Drawing.Main;
import Drawing.Sprite;
import UserInput.Keyboard;
import Utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BinaryOperator;

public class Player {
    public static double lives = 3;
    private final int width;
    private final int height;
    private final Rectangle hitBox;
    private final ArrayList<Block> blocks;
    public int x;
    public int y;
    public double xSpeed, ySpeed;
    public double fallHeight;
    public Sprite sprite;
    private boolean onSlime;

    public Player(int x, int y, ArrayList<Block> blocks, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.width = (int) Constants.PlayerSize.getValue();
        this.height = (int) Constants.PlayerSize.getValue();
        this.hitBox = new Rectangle(x, y, width, height);
        this.sprite = sprite;
        this.blocks = blocks;
    }

    public void move() {
        double jumpHeight = Constants.PlayerJumpHeight.getValue();
        double maxXSpeed = Constants.PlayerMaxXSpeed.getValue();
        double maxYSpeed = Constants.PlayerMaxYSpeed.getValue();
        double gravitation = Constants.PlayerGravitation.getValue();

        boolean left;
        if (xSpeed < 0)
            left = true;
        if (xSpeed > 0)
            left = false;


        boolean respawning = false;
        if (Keyboard.getA() && Keyboard.getD() || !Keyboard.getA() && !Keyboard.getD()) {
            xSpeed *= 0.8;
        } else if (Keyboard.getA()) {
            xSpeed--;
        } else {
            xSpeed++;
        }
        //прыжок
        if (Keyboard.getW() || Keyboard.getSpace()) {
            hitBox.y++;
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i).hitBox.intersects(hitBox)) {
                    ySpeed = -jumpHeight;
                }
            }
            hitBox.y--;
        }

        //гравитация
        ySpeed += gravitation;


        //ограничения скорости
        if (xSpeed > 0 && xSpeed < 0.75) {
            xSpeed = 0;
        }
        if (xSpeed < 0 && xSpeed > -0.75) {
            xSpeed = 0;
        }
        if (xSpeed > maxXSpeed) {
            xSpeed = maxXSpeed;
        }
        if (xSpeed < -maxXSpeed) {
            xSpeed = -maxXSpeed;
        }
        if (ySpeed > maxYSpeed) {
            ySpeed = maxYSpeed;
        }

        //горизонтальные столкновения
        hitBox.x += xSpeed;

        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).hitBox.intersects(hitBox)) {
                if (blocks.get(i).type == Type.Ladder) {
                    //TODO special ladder actions
                } else if (blocks.get(i).type == Type.Bottle) {
                    Main.collectedBottles.add(blocks.get(i));
                    blocks.remove(i);
                    Main.bottles++;
                    Main.bottlesToCollect--;
                    lives += 0.25;
                    sprite.applyEffect(Utils::edges);
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            sprite.removeEffects();
                            t.cancel();
                        }
                    }, 300);
                } else if (blocks.get(i).type == Type.Exit) {
                    if (Main.bottlesToCollect == 0) {
                        Main.frames = 0;
                        Main.gameState = 5;
                    }
                } else {
                    hitBox.x -= xSpeed;
                    while (!blocks.get(i).hitBox.intersects(hitBox)) {
                        hitBox.x += Math.signum(xSpeed);
                    }
                    hitBox.x -= Math.signum(xSpeed);
                    xSpeed = 0;
                    x = hitBox.x;

                    if (Constants.EnablePlayerWallJump.getValue() == 1) {
                        if (blocks.get(i).type != Type.Slime) {
                            /*ySpeed = 0;
                            if (Keyboard.getE()) {
                                ySpeed -= 10;
                                if (left) {
                                    xSpeed += 15;
                                } else {
                                    xSpeed -= 15;
                                }
                            }*/
                        }
                    }

                    //методы после касания
                    //оставить пустым, чтобы просто не проходить через блок
                /* respawn();
                if (!blocks.get(i).type.equals("block"))
                    respawn();*/
                }
                fallHeight = 0;
            }
        }

        if (ySpeed < 0) {
            onSlime = false;
        } else {
            fallHeight += ySpeed;
        }

        //вертикальные столкновения
        hitBox.y += ySpeed;
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).hitBox.intersects(hitBox)) {
                if (blocks.get(i).type == Type.Ladder) {
                    //TODO special ladder actions
                } else if (blocks.get(i).type == Type.Slime && !Keyboard.getShift()) {
                    if (!onSlime) {
                        onSlime = true;
                        ySpeed -= Math.abs(ySpeed) * Constants.SlimeBounce.getValue();
                    }
                } else if (blocks.get(i).type == Type.Bottle) {
                    Main.collectedBottles.add(blocks.get(i));
                    blocks.remove(i);
                    Main.bottles++;
                    Main.bottlesToCollect--;
                    lives += 0.25;
                    sprite.applyEffect(Utils::edges);
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            sprite.removeEffects();
                            t.cancel();
                        }
                    }, 300);
                } else if (blocks.get(i).type == Type.Exit) {
                    if (Main.bottlesToCollect == 0) {
                        Main.frames = 0;
                        Main.gameState = 5;
                    }
                } else {
                    hitBox.y -= ySpeed;
                    while (!blocks.get(i).hitBox.intersects(hitBox)) {
                        hitBox.y += Math.signum(ySpeed);
                    }
                    hitBox.y -= Math.signum(ySpeed);
                    ySpeed = 0;
                    y = hitBox.y;


                    //методы после касания
                    //оставить пустым, чтобы просто не проходить через блок
                /*if (!blocks.get(i).type.equals("block"))
                    respawn();
                 if (ySpeed < -2) {
                    respawn();
                }
                */
                }
                fallHeight = 0;
            }
        }
        if (fallHeight > Constants.MaxFallingHeight.getValue()) {
            sprite.applyEffect(Utils::gray);
        }
        if (fallHeight > Constants.MaxFallingHeight.getValue() * 1.5) {
            sprite.removeEffects();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (lives > 1) {
                lives--;
                System.out.println("Lives = " + lives);
                Main.reload();
            } else {
                Main.frames = 0;
                Main.gameState = 3;
            }
        }

        //изменение координат относительно скорости
        x += xSpeed;
        y += ySpeed;
        hitBox.x = x;
        hitBox.y = y;

    }

    public void draw(Graphics g, BinaryOperator<Integer> offsetX, BinaryOperator<Integer> offsetY) {
        sprite.draw(g, offsetX.apply(x, x), offsetY.apply(y, (int) Main.cameraY), width, height);
    }

    /*  public void draw(Graphics g, BinaryOperator<Integer> offsetX, BinaryOperator<Integer> offsetY) {
          g.setColor(Color.blue);
          g.drawRect(offsetX.apply(x, x), offsetY.apply(y, (int) Main.cameraY), width, height);
      }
  */
    public void drawHitBox(Graphics g, BinaryOperator<Integer> offsetX, BinaryOperator<Integer> offsetY) {
        g.setColor(Color.MAGENTA);
        g.drawRect(offsetX.apply(x, x), offsetY.apply(y, (int) Main.cameraY), width, height);
    }
}