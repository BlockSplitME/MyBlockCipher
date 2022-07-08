package src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Image {

    public byte[] image;
    public int headLength;
    private int width;
    private int height;
    private int bpp;

    public Image() {
        headLength = 0;
        image = new byte[1];
    }

    public byte[] readImage(String filename) throws IOException {
        File file = new File(filename);
        BufferedImage img = ImageIO.read(file);

        this.width = img.getWidth();
        this.height = img.getHeight();
        this.bpp = getBitsPerPixel(img);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "bmp", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        this.image = imageInByte;
        this.headLength = imageInByte.length - (width * height * bpp);
        // System.out.println("Image headlength: " + this.headLength);
        byte[] noHeadImage = this.cutHead();
        if(noHeadImage.length%8 != 0) {
            System.out.println("\n[x] Bad image size!!!!!!!!!!\n");
            return null;
        } else {
            return noHeadImage;
        }
    }

    private byte[] cutHead() {
        byte[] imageNoHead = new byte[image.length - headLength];

        for(int i = headLength, j = 0; i < imageNoHead.length; i++, j++) {
            imageNoHead[j] = image[i];
        }
        // System.out.println("newImage size: " + imageNoHead.length);
        return imageNoHead;
    }

    public void writeImage(String filename, byte[] imageInByte) throws IOException {
        this.addHead(imageInByte);
        BufferedImage newimg = ImageIO.read(new ByteArrayInputStream(this.image));
        ImageIO.write(newimg, "bmp", new File(filename));
    }

    private void addHead(byte[] img) {
        for(int i = headLength, j = 0; i < img.length; i++, j++) {
            this.image[i] = img[j];
        }
        // System.out.println("end");
    }

    public int getBitsPerPixel(BufferedImage img) {
        return img.getColorModel().getPixelSize() / Byte.SIZE;
    }
    public void infoOfImage() {
        System.out.println("Input image size: " + this.width + " x " + this.height + " (bpp = " + this.bpp + ")");
        System.out.println("Image size = " + this.width * this.height * this.bpp + "\n");
    }
}