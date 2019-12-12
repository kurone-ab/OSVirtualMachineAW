package global;

import org.intellij.lang.annotations.MagicConstant;

import java.awt.*;

public class GridBagUtility {

    public static void setCoordinate(int x, int y, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        parent.add(child, gbc);
    }

    public static void setCoordinateInsets(int x, int y, Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = insets;
        parent.add(child, gbc);
    }

    public static void setCoordinateSpanInsets(int x, int y, int width, int height, Insets insets,
                                               Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.insets = insets;
        gbc.fill = GridBagConstraints.BOTH;
        parent.add(child, gbc);
    }

    public static void setCoordinateSpanFillInsets(int x, int y, int width, int height, @MagicConstant int fill,
                                                   Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = fill;
        gbc.insets = insets;
        parent.add(child, gbc);
    }

    public static void setCoordinateSpanAnchorInsets(int x, int y, int width, int height, @MagicConstant int anchor,
                                                     Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.anchor = anchor;
        gbc.insets = insets;
        parent.add(child, gbc);
    }

    public static void setFillWeightAttribute(int x, int y, int width, int height, @MagicConstant int fill,
                                 int weightx, int weighty, Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = fill;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.insets = insets;
        parent.add(child, gbc);
    }

    public static void setAnchorWeightAttribute(int x, int y, int width, int height, @MagicConstant int anchor,
                                   int weightx, int weighty, Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.anchor = anchor;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.insets = insets;
        parent.add(child, gbc);
    }

    public static void setFillIpadAttribute(int x, int y, int width, int height, @MagicConstant int fill, int ipadx, int ipady,
                                            Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = fill;
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        gbc.insets = insets;
        parent.add(child, gbc);
    }

    public static void setAnchorIpadAttribute(int x, int y, int width, int height, @MagicConstant int anchor, int ipadx, int ipady,
                                              Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.anchor = anchor;
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        gbc.insets = insets;
        parent.add(child, gbc);
    }

    public static void setFillAttribute(int x, int y, int width, int height, @MagicConstant int fill, int ipadx, int ipady,
                                 int weightx, int weighty, Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = fill;
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.insets = insets;
        parent.add(child, gbc);
    }

    public static void setAnchorAttribute(int x, int y, int width, int height, @MagicConstant int anchor, int ipadx, int ipady,
                                   int weightx, int weighty, Insets insets, Container parent, Component child) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.anchor = anchor;
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.insets = insets;
        parent.add(child, gbc);
    }


}
