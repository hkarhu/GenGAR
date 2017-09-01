Generic (Board/Tabletop) Game Augmenter

ECLIPSE:
Add to VM-Args:
-Djava.library.path=target/natives

Requirements:
Maven
ae library
TabARE library

Setting screen:
xrandr --output VGA1 --mode 1280x800 --pos 1920x0

Quick setup copypasta:
´´´
git clone http://github.org/irah-000/ae
cd ae
mvn install
cd ..
git clone http://github.org/irah-000/tabare
cd tabare
mvn install
cd ..
git clone http://github.org/irah-000/gengar
cd gengar
mvn package
´´´

Better documentation, if requested. :D

<a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style="border-width:0" src="http://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a><br /><span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">GenGAR</span> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
