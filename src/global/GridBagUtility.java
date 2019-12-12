package global;

import org.intellij.lang.annotations.MagicConstant;

import java.awt.*;

public class GridBagUtility {

    public static void setXY(int x, int y, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        parent.add(child, gbc);
    }

    public static void setXYInsets(int x, int y, Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = insets;
        parent.add(child, gbc);
    }

    public static void setXYWidthHeightInsets(int x, int y, int width, int height, Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.insets = insets;
        gbc.fill = GridBagConstraints.BOTH;
        parent.add(child, gbc);
    }

    public static void setXYWidthHeightFillInsets(int x, int y, int width, int height, @MagicConstant int fill, Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = fill;
        gbc.insets = insets;
        parent.add(child, gbc);
    }

    public static void setXYWidthHeightAnchorInsets(int x, int y, int width, int height, @MagicConstant int anchor, Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.anchor = anchor;
        gbc.insets = insets;
        parent.add(child, gbc);
    }

    public void set


}
