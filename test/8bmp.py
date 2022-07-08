import numpy as np
from PIL import Image, ImageOps
 
# 24-bit to 8-bit grayscale
image1 = Image.open(r'lena.bmp')
image2 = Image.fromarray(np.uint8(image1))
print(image2.mode)
t = image2.convert("L")
print(t.mode)
image3 = Image.fromarray(np.uint8(t)*255)
print(image3.mode)
image3 = ImageOps.invert(image3)
image3.save(r'8b_lena.bmp')
image3