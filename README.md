#Fruits And Veggies Wallpapers
###1.0 Beta 6

###Why It's Cool
FNV Wallpaers pulls the wallpapers from the internet, allowing for small installed filesize, and unlimited wallpapers.

###What It Does Now
* Views, sets, and saves wallpapers
* Shows a larger preview when you click on a wallpaper
* Jumps to any page
* Swipe left and right to change pages

###What It Will Do Later
* End world hunger

###Can I Use This In My ROM?
Sure thing. Just make the changes necessary to make it fit your ROM. The hosting side of the images is fairly easy to understand.

####How It Works
The app looks at a folder somewhere on the internet for a files called "fnv_1_small.png" through "fnv_4_small.png"

It then downloads those four images into the cache with koush's great library, [UrlImageViewHelper](https://github.com/koush/UrlImageViewHelper) (Which means you'll need to have it to build the app)

When a user swipes right/next button, the app loads fnv_5_small.png to fnv_8_small.png and so on

When a user clicks on an image, the app start a new activity which shows the selected wallpaper a little bigger than the initial previews.

At the bottom of that activity are two buttons. "Save to Storage" and "Set as Wallpaper".