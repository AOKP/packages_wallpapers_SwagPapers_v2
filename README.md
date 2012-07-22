#Fruits And Veggies Wallpapers
###1.0 Beta 2

###Why It's Cool
FNV Wallpaers pulls the wallpapers from the internet, allowing for small installed filesize, and unlimited wallpapers.

###What It Does Now
* Views, sets, and saves wallpapers

###What It Will Do Later
* Give you a larger preview
* Use a ViewPager so you can swipe to change pages
* Jump to any page

###Can I Use This In My ROM?
Sure thing. Just make the changes necessary to make it fit your ROM. The hosting side of the images is fairly easy to understand.

####How It Works
The app looks at a folder somewhere for a files called "fnv_1_small.png" through "fnv_4_small.png"

It then downloads those four images into the cache with koush's great library, [UrlImageViewHelper](https://github.com/koush/UrlImageViewHelper) (Which means you'll need to have it to build the app)

When a user clicks on an image, the app (for now) asks if they want to set it as their wallpaper. If they say yes, the app then downloads "fnv_#.png" and sets it as their wallpaper.

If a user long presses on a preview, the app asks if they would like to save it to their internal storage. If they say yes, it downloads the wallpaper with a name that reflects the current time.

In the future, when a user clicks on a preview, it will open a new activity with a larger preview, where the user will then have the option to set or save