package Drawing;
//TODO анимации

import Utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class Sprite {
    public final int columns, rows, framesCount;
    private final Image[] frames;
    public int w, h;
    public Image texture;
    public Image curFrame;
    public boolean canAnimate;
    private Image[] effectFrames;
    private Image[] transparencyFrames;
    private boolean effect, transparent;

    public Sprite(int w, int h, Image texture, int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        this.framesCount = rows * columns;
        this.canAnimate = true;
        this.w = w;
        this.h = h;
        this.texture = texture;
        this.frames = new Image[framesCount];
        this.effectFrames = new Image[framesCount];
        this.transparencyFrames = new Image[framesCount];
        initFrames();
    }

    public void initFrames() {
        BufferedImage textureBuffer = Utils.toBufferedImage(texture);
        int counter = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                frames[counter] = textureBuffer.getSubimage(w * col, h * row, w, h);
                counter++;
            }
        }
    }

    public void setTransparency(int transparency) {
        if (transparency == 255)
            transparent = false;
        else
            transparent = true;

        this.transparencyFrames = new Image[framesCount];
        for (int i = 0; i < framesCount; i++) {
            Image original;

            if (effect)
                original = effectFrames[i];
            else
                original = frames[i];
            BufferedImage alphaMask = new BufferedImage(original.getWidth(null), original.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = alphaMask.createGraphics();

            g2d.setPaint(new Color(0, 0, 0, transparency));
            g2d.fillRect(0, 0, alphaMask.getWidth(), alphaMask.getHeight());
            g2d.dispose();
            transparencyFrames[i] = Utils.applyMask(Utils.toBufferedImage(original), alphaMask, AlphaComposite.DST_IN);
        }
    }


    public void removeEffects() {
        if (effect) {
            System.out.println("Effects removed");
            effectFrames = new Image[framesCount];
            this.effect = false;
        }
    }

    public void applyEffect(UnaryOperator<BufferedImage> effect) {
        if (!this.effect) {
            if (!transparent) {
                for (int i = 0; i < framesCount; i++) {
                    effectFrames[i] = effect.apply(Utils.toBufferedImage(frames[i]));
                }
            } else {
                for (int i = 0; i < framesCount; i++) {
                    effectFrames[i] = effect.apply(Utils.toBufferedImage(transparencyFrames[i]));
                }
            }
            System.out.println("Effect applied");
            this.effect = true;
        }
    }

    public void drawTileSet(Graphics g) {
        for (int i = 0; i < frames.length; i++) {
            if (transparent) {
                g.drawImage(transparencyFrames[i], w * i, h * i, w, h, null);
                return;
            }
            if (effect) {
                g.drawImage(effectFrames[i], w * i, h * i, w, h, null);
                return;
            }
            g.drawImage(frames[i], w * i, h * i, w, h, null);
        }
    }


    public void animate(long globalClock) {
        int currentFrameNumber;
        if (canAnimate)
            currentFrameNumber = Math.toIntExact(globalClock % frames.length);
        else
            currentFrameNumber = 0;

        if (transparent) {
            curFrame = transparencyFrames[currentFrameNumber];
            return;
        }

        if (effect) {
            curFrame = effectFrames[currentFrameNumber];
            return;
        }
        curFrame = frames[currentFrameNumber];
    }


    public void draw(Graphics g, int x, int y, int w, int h) {
        g.drawImage(curFrame, x, y, w, h, null);
    }
}
