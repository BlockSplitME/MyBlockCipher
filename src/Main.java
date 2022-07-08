package src;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;


public class Main {
	public static void main(String[] args) 	{
		byte[] key = BlockCipher.generatingKey(16);
		System.out.println(Arrays.toString(key));

		Main.testImage("8b_lena.bmp", key);
		// Main.testString("abcdefgh", key);
		// Main.test(key);
	}
	public static void testImage(String name, byte[] key) {
		Image image = new Image();
		byte[] message = new byte[1];
		try {
			message = image.readImage(name);
		} catch(IOException e) {
			System.out.println("Image reading error.");
		}

		BlockCipher enc =  new BlockCipher(message, key);
		enc.encrypt();

		try {
			image.writeImage("result/encrypt.bmp", enc.getMessage());	
		} catch(IOException e) {
			System.out.println("Image reading error.");
		}
		
		BlockCipher dec =  new BlockCipher(enc.getMessage(), key);
		dec.decrypt();
		try {
			image.writeImage("result/decrypt.bmp", dec.getMessage());	
		} catch(IOException e) {
			System.out.println("Image reading error.");
		}
//------------- Resheto -------------------------------------------------
		try {
			Main.resheto(message, "result/test/reshetoOrig.txt");
			Main.resheto(enc.getMessage(), "result/test/reshetoEnc.txt");
		} catch(FileNotFoundException fileNot) {
			System.out.println("File not found");
		} catch(UnsupportedEncodingException e) {
			System.out.println("UnsupportedEncodingException");
		}
//------------- AutoCorrelation -------------------------------------------------\
		try {
			Main.autocorrelationTest(dec.getBlocks(), "result/test/autocorrelationOrig.txt");
			Main.autocorrelationTest(enc.getBlocks(), "result/test/autocorrelationEnc.txt");
		} catch(FileNotFoundException fileNot) {
			System.out.println("File not found");
		} catch(UnsupportedEncodingException e) {
			System.out.println("UnsupportedEncodingException");
		}
//-------------BruteForce------------------------------------------------- 
		// int resForce = Main.bruteForce(enc.getMessage(), dec.getMessage());
	}
	public static int bruteForce(byte[] enMessage, byte[] decMessage) {
		int n = 0;
		// Vector<byte[]> keys = Operations.force(16);
		for(byte i = 0; i < 256; i++) {
			for(byte j = 0; j < 256; j++) {
				byte[] ar = {i,j};
				BlockCipher dec =  new BlockCipher(enMessage, ar);
				dec.decrypt();
				if(Arrays.equals(decMessage, dec.getMessage())) {
					return n;
				}
				System.out.println(Arrays.toString(ar));

				System.out.println(n);
				n++;
			}
		}
		return 0;
	}
	private static void resheto(byte[] data, String filename) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        for(int i = 0; i < data.length/10; i++) {
            int a = (int)data[i];
            int b = (int)data[i+1];
            a &=  0b00000000000000000000000011111111;
            b &=  0b00000000000000000000000011111111;
            writer.println(a + " " + b);
        }
        writer.close();
    }

	private static byte[] getCor(Vector<byte[]> vec, int index) {
		if(index < 0) {
			index = vec.size() + index;
		}
		return vec.get(index);
	}
	private static void autocorrelationTest(Vector<byte[]> blocks, String name) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(name, "UTF-8");
		int size = 4000;
		int matches = 0;
		int noMatches = 0;
		Vector<byte[]> shiftBlock = new Vector<byte[]>();
		for(int i = -(size/2); i < size/2; i++) {
			shiftBlock.add(Main.getCor(blocks, i));
		}
		for (int i = -(size/2); i < (size/2); i++) {
			
			if(i != 0) {
				byte[] tmp = shiftBlock.get(0);
				
				for(int j = 0; j < shiftBlock.size() - 1; j++) {
					shiftBlock.set(j, shiftBlock.get(j+1));
				}

				shiftBlock.set(shiftBlock.size() - 1, tmp);
			}

			for(int j = 0; j < shiftBlock.size(); j++) {
				int tmp1 = Operations.concat2Bytes(shiftBlock.get(j)[0], shiftBlock.get(j)[1]);
				int tmp2 = Operations.concat2Bytes(blocks.get(j)[0], blocks.get(j)[1]);
				
				// System.out.println(tmp1);
				// System.out.println(tmp2);

				String str11 = 	Integer.toBinaryString(tmp1 | 0b10000000000000000000000000000000);	
				String str22 = 	Integer.toBinaryString(tmp2 | 0b10000000000000000000000000000000);	

				char[] str1 = str11.toCharArray();
				char[] str2 = str22.toCharArray();			


				for(int k = 16; k < 32; k++) {
					if(str1[k] == str2[k]) {
						matches++;
					} else {
						noMatches++;
					}
				}			

			}
			double diff = (matches - noMatches) / (16.0 * size);
			// System.out.println("Посчитал " + i + "/" + blocks.size() + " = " + diff);
			writer.println(i + " " + diff);
			matches = 0;
			noMatches = 0;
		}
		writer.close();
	}
}