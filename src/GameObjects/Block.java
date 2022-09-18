package GameObjects;

import Drawing.Main;

import java.awt.*;
import java.util.Objects;
import java.util.function.BinaryOperator;


public class Block {
    //координаты
    public int x, y, width, height;

    //хитбокс
    public Rectangle hitBox;
    //тип
    public Type type;

    public Block(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = (int) Constants.BlockSize.getValue();
        this.height = (int) Constants.BlockSize.getValue();

        this.hitBox = new Rectangle(x, y, width, height);
    }

    public Block(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.width = (int) Constants.BlockSize.getValue();
        this.height = (int) Constants.BlockSize.getValue();
        this.hitBox = new Rectangle(x, y, width, height);
        this.type = type;
    }

    public void move() {
        //TODO движение как в том старом говне
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;
        Block block = (Block) o;
        return x == block.x && y == block.y && width == block.width && height == block.height && hitBox.equals(block.hitBox) && type == block.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height, hitBox, type);
    }

    public void draw(Graphics g, BinaryOperator<Integer> offsetX, BinaryOperator<Integer> offsetY) {
        if (type == Type.Ladder) {
            g.drawImage(Main.Ladder, offsetX.apply(x, Main.player.x), offsetY.apply(y, (int) Main.cameraY), (int) Constants.BlockSize.getValue(), (int) Constants.BlockSize.getValue(), null);
            return;
        } else if (type == Type.Slime) {
            g.drawImage(Main.Slime, offsetX.apply(x, Main.player.x), offsetY.apply(y, (int) Main.cameraY), (int) Constants.BlockSize.getValue(), (int) Constants.BlockSize.getValue(), null);
            return;
        } else if (type == Type.Exit)
            g.setColor(Color.MAGENTA);
        else if (type == Type.Bottle) {
            g.drawImage(Main.Bottle, offsetX.apply(x, Main.player.x) + 16, offsetY.apply(y, (int) Main.cameraY) + 16, 64, 64, null);
            return;
        } else {
            g.drawImage(Main.Block, offsetX.apply(x, Main.player.x), offsetY.apply(y, (int) Main.cameraY), (int) Constants.BlockSize.getValue(), (int) Constants.BlockSize.getValue(), null);
            return;
        }

        g.drawRect(offsetX.apply(x, Main.player.x), offsetY.apply(y, (int) Main.cameraY), width, height);
    }

    public void drawHitBox(Graphics g, BinaryOperator<Integer> offsetX, BinaryOperator<Integer> offsetY) {
        if (type == Type.Ladder)
            g.setColor(new Color(128, 64, 0));
        else if (type == Type.Slime)
            g.setColor(Color.GREEN);
        else
            g.setColor(Color.WHITE);

        g.drawRect(offsetX.apply(x, x), offsetY.apply(y, (int) Main.cameraY), width, height);
    }
}
