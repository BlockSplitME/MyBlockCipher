package src;

import java.util.*;

public class Operations {
	
	public static int add(int x, int y) {
		return (x + y) & 0xFFFF;
	}

	public static int addInv(byte[] bx) {
		int x = Operations.concat2Bytes(bx[0], bx[1]);
		return (0x10000 - x) & 0xFFFF;
	}
	
	public static byte addByte(byte x, byte y) {
		int b = (int)x;
		int s = (int)y;
		int res = (b + s) & 0xFF;
		return (byte)res;
	}

	public static byte addInvByte(byte x) {
		int b = (int)x;
		int res = (0x100 - b) & 0xFF;		
		return (byte)res;
	}

	public static int mul(int x, int y) {
		long m = (long) x * y;
		if(m != 0) {
			return (int)(m % 0x10001) & 0xFFFF;
		} else {
			if(x != 0 || y != 0) {
				return (1 - x - y) & 0xFFFF;
			}
			return 1;
		}
	}
	
	public static int mulInv(byte[] bx) {
		int x = Operations.concat2Bytes(bx[0], bx[1]);
        if (x <= 1) {
            // 0 and 1 are their own inverses
            return x;
        }
        try {
            int y = 0x10001;
            int t0 = 1;
            int t1 = 0;
            while (true) {
                t1 += y / x * t0;
                y %= x;
                if (y == 1) {
                    return (1 - t1) & 0xffff;
                }
                t0 += x / y * t1;
                x %= y;
                if (x == 1) {
                    return t0;
                }
            }
        } catch (ArithmeticException e) {
            return 0;
        }
    }
 
	public static byte[] concat2Bytes(byte[] x, byte[] y) {
		byte[] out  = new byte[x.length + y.length];
		int i = 0;
		for(byte a: x) {
			out[i++] = a;
		}
		for(byte a: y) {
			out[i++] = a;
		}
		return out;
			
	} 
	public static int concat2Bytes(int x, int y) {
		x = (x & 0xFF) << 8;
		y = y & 0xFF;
		return (x | y);
	}
	
	public static Vector<byte[]> force(int size) {
		Vector<byte[]> result = new Vector<byte[]>((int)Math.pow(2, size));

		for(byte i = 0; i < 256; i++) {
			for(byte j = 0; j < 256; j++) {
				byte[] ar = {i,j};
				result.add(ar);
			}
		}
		return result;
	}
}