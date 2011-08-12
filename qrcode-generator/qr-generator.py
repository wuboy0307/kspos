#!/usr/bin/python

import os
import Image, ImageDraw, ImageFont
from PyQRNative import *
from verhoeff import *

# retrieving QR codes from 100-999 with verhoeff check digit appended.
codesDir = 'codes'
if not os.path.isdir(codesDir):
    os.makedirs(codesDir)

codeFont = ImageFont.truetype("/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-B.ttf", 23)

for i in range(100,400):
    code = str(i)+calc_check_digit(i)
    print "Generating QR code: "+code
    qr = QRCode(2, QRErrorCorrectLevel.H)
    qr.addData(code)
    qr.make()
    im = qr.makeImage()
    draw = ImageDraw.Draw(im)
    draw.text((20,-4), code, fill='black', font=codeFont)
    im.save(codesDir+'/'+code+'.png')


