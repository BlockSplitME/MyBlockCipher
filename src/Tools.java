package src;

import java.nio.ByteBuffer;
import java.util.*;


public class Tools {
    public static void printBlocks(Vector<byte[]> vec) {
		for(byte[] a: vec) {
			Tools.printBits(a);
			System.out.println();
		}
	}
	public static void printBits(byte[] data) {
		for(int i = 0; i < data.length; i++) {
			// System.out.print(Tools.toBinaryString(data[i]) + " ");
			// System.out.print(Integer.toBinaryString((int)data[i]) + " ");
			System.out.print(Integer.toString((int)data[i] )+ " ");
		}
		System.out.println();
	}
	public static String toBinaryString(byte i) {
		// if(i < 0) {
		// 	i = (byte)128 - i;
		// }
	    char digits[] = {'0', '1'};
	    char[] buf = new char[8];
	    int charPos = 8;
	    byte radix = (byte)2;
	    byte mask = (byte)(radix - 1);
	    do {
	   		buf[--charPos] = digits[i & mask];
	        i >>>= 1;
	    } while (i != 0);
	    for (int j = 0; j < charPos; j++) {
	    	buf[j] = '0';
	    }
	    return new String(buf/*, charPos, (8 - charPos)*/);
	}
	public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a) 
            // sb.append(String.format("%02x", b));
        	sb.append(Integer.toHexString(b & 0xFF));
        return sb.toString();
    }

}