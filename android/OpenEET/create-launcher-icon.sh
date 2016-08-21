#!/bin/sh
#---------------------------------------------------------------
# Given an xxhdpi image or an App Icon (launcher), this script
# creates different dpis resources
#
# Place this script, as well as the source image, inside res
# folder and execute it passing the image filename as argument
#
# Example:
# ./drawables_dpis_creation.sh ic_launcher.png
# OR
# ./drawables_dpis_creation.sh my_cool_xxhdpi_image.png
#---------------------------------------------------------------

DIR=$(dirname $0)

echo " Creating different dimensions (dips) of "$1" ..."

if [ $1 = "ic_launcher.png" ]; then
    echo "  App icon detected"

    convert ic_launcher.png -resize 192x192 $DIR/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
    convert ic_launcher.png -resize 144x144 $DIR/app/src/main/res/mipmap-xxhdpi/ic_launcher.png
    convert ic_launcher.png -resize 96x96   $DIR/app/src/main/res/mipmap-xhdpi/ic_launcher.png
    convert ic_launcher.png -resize 72x72   $DIR/app/src/main/res/mipmap-hdpi/ic_launcher.png
    convert ic_launcher.png -resize 48x48   $DIR/app/src/main/res/mipmap-mdpi/ic_launcher.png
else

    convert $1 -resize 67% drawable-xhdpi/$1
    convert $1 -resize 50% drawable-hdpi/$1
    convert $1 -resize 33% drawable-mdpi/$1
    mv $1 drawable-xxhdpi/$1

fi

echo " Done"