
package com.aokp.swagpapers;

import java.util.ArrayList;
import java.util.List;

public class WallpaperCategory {

    String id;
    String name;
    List<Wallpaper> wallpapers;

    public WallpaperCategory(String i, String n) {
        id = i;
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
}
