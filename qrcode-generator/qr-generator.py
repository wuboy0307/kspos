#!/usr/bin/python

import os
import Image, ImageDraw, ImageFont
from PyQRNative import *
from verhoeff import *

# retrieving QR codes from 100-999 with verhoeff check digit appended.
codesDir = 'codes'
if not os.path.isdir(codesDir):
    os.makedirs(codesDir)

codeFont = ImageFont.truetype("/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-B.ttf", 60)

bkImg = Image.open("k3-klub-kartya.png")

for i in range(100,399):
    code = str(i)+calc_check_digit(i)
    print "Generating QR code: "+code
    qr = QRCode(2, QRErrorCorrectLevel.H)
    qr.addData(code)
    qr.make()
    qrImg = qr.makeImage()
#    qrImg.save(codesDir+'/'+code+'.png')

    img = bkImg.copy()
    img.paste(qrImg, (87,253))
    draw = ImageDraw.Draw(img)
    draw.text((600,380), code, fill='#02508d', font=codeFont)
    img.save(codesDir+'/k3-klub-kartya-'+code+'.png')


