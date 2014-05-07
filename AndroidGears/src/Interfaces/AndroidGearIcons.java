package Interfaces;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by aaronfleshner on 4/22/14.
 */
public interface AndroidGearIcons {
    public Icon ANDROID_GEAR_ICON = IconLoader.getIcon("/gears.png");
    public Icon declaredIcon =IconLoader.getIcon("/GearStateDeclared.png");//Fix
    public Icon installedIcon =IconLoader.getIcon("/GearStateInstalled.png");//Fix
    public Icon jarfile =IconLoader.getIcon("/jarfile.png");//Fix
}
