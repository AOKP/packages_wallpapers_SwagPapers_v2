
package com.aokp.swagpapers;

import java.util.ArrayList;
import java.util.List;

public class WallpaperCategory {

    static int totalCategoryCount = 0;
    final int localIndex;
    String id;
    String name;
    List<Wallpaper> wallpapers;

    public WallpaperCategory(String i, String n) {
        localIndex = totalCategoryCount++;
        if (i == null)
            id = totalCategoryCount + "";
        else
            id = i;

        if (n == null)
            n = totalCategoryCount + "";
        else
            name = n;

        wallpapers = new ArrayList<Wallpaper>();
    }

    public void addWallpaper(Wallpaper wall) {
        wallpapers.add(wall);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Wallpaper> getWallpapers() {
        return wallpapers;
    }

    public void setWallpapers(List<Wallpaper> wallpapers) {
        this.wallpapers = wallpapers;
    }

    public String toString() {
        return name;
    }
}
