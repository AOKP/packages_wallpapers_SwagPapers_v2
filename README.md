Wallpaper Manifest XML Format
=============================

All XML tags (like id, name, etc) are optional

Adding Categories
-----------------
* `id="<string>"` unique id string 
* `name="<string>"` visual name 

### Example
	<category id="<string>" name="<string>" />


Adding Wallpapers
-----------------
* `url="<url of wallpaper>"`
* `thumbUrl="<url of thumbnail>"`
* `author="<author>"` may be used for filtering later
* `date="<date added>"` may be used for filtering later
* `name="<wallpaper name>"`

### Example
	<wallpaper 
		url="someurl.jpg"
		thumbUrl="someurl_small.jpg"
		author="exmaple"
		date="May 19, 2012"
		name="Some Wallpaper" />