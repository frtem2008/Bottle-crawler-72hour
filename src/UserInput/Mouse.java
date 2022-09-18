package UserInput;

import Drawing.Main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {
    //позиция + кол-во прокрученных тиков
    public static boolean mouseClicked;
    public static int x, y, scroll;

    @Override
    public void mouseClicked(MouseEvent e) {
        if (Main.gameState == 1) {
            if (e.getX() >= Main.frameSize.width / 2 - 128 && e.getX() <= Main.frameSize.width / 2 + 256) {
                if (e.getY() >= 400 && e.getY() <= 500) {
                    System.out.println("Starting game");
                    Main.gameState = 2;
                }
            }
        }

        if (Main.gameState == 3 || Main.gameState == 5) {
            if (e.getX() >= Main.frameSize.width - 400 && e.getX() <= Main.frameSize.width) {
                if (e.getY() >= Main.frameSize.height - 94 && e.getY() <= Main.frameSize.height) {
                    System.out.println("Exiting");
                    System.exit(20);
                }
            }
        }
        System.out.println("X: " + e.getX() + " Y: " + e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseClicked = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseClicked = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }
}
