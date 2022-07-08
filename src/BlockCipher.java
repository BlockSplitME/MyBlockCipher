package src;

import java.util.*;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class BlockCipher {
	private Vector<byte[]> key;
	private Vector<byte[]> blockOfMessage;
	private static int sizeOfBlocks = 2;
	private static int numRounds = 8;
	
	public BlockCipher(byte[] message, byte[] key) {
		this.key = splitOfBlocks(key, this.sizeOfBlocks);
		this.blockOfMessage = splitOfBlocks(message, this.sizeOfBlocks);
		// Tools.printBlocks(this.key);
	}

	public BlockCipher(byte[] message) {
		this.key = splitOfBlocks(generatingKey(this.sizeOfBlocks*this.numRounds), this.sizeOfBlocks);
		this.blockOfMessage = splitOfBlocks(message, this.sizeOfBlocks);
		// Tools.printBlocks(this.blockOfMessage);
	}
	public Vector<byte[]> getBlocks() {
		return this.blockOfMessage;
	}
	public byte[] getMessage() {
		ByteBuffer res = ByteBuffer.allocate(this.blockOfMessage.size()*this.sizeOfBlocks);
		for(byte[] block: this.blockOfMessage) {
			res.put(block);
		}
		return res.array();	
	}

	public byte[] getKey() {
		ByteBuffer res = ByteBuffer.allocate(this.key.size()*this.sizeOfBlocks);
		for(byte[] block: this.key) {
			res.put(block);
		}
		return res.array();	
	}

	private Vector<byte[]> splitOfBlocks(byte[] data, int size) {
		Vector<byte[]> result = new Vector<byte[]>();
		int num = data.length / size;
		// System.out.println("Size data: " + data.length);
		if(data.length % size != 0) {
			int addToSize = (num + 1) * size - data.length; 
			ByteBuffer buf = ByteBuffer.allocate(data.length + addToSize);
			buf.put(data);
			for(int i = 0; i < addToSize; i++) {
				byte nu = 0;
				buf.put(nu);
			}
			data = buf.array();
			num = data.length / size;
		}

		for(int i = 0; i < num; i++) {
			byte[] tmp = new byte[size];
			for(int j = 0; j < size; j++) {
				tmp[j] = data[i*size + j];
			}
			result.add(tmp);
		} 
		return result;
	}
	
	public static byte[] generatingKey(int size) {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[size];
		random.nextBytes(bytes);
		// System.out.println(Arrays.toString(bytes));
		return bytes;
	}

	public byte[] searchForShift() {
		byte min = (byte)255;
		byte max = (byte)0;
		for(byte[] b: this.key) {
			byte tmp1 = b[0];//Operations.concat2Bytes(b[0], b[1]);
			byte tmp2 = b[1];
			// System.out.println(Tools.toBinaryString(b[0]) + " " + Tools.toBinaryString(b[1]));
			if(tmp1 < min){
				min = tmp1;
			}
			if(tmp2 > max) {
				max = tmp2;
			}
		}
		int res = Operations.concat2Bytes(max, min);
		if(res <= 0) {
			res = -res;
		}
		byte[] resByte = new byte[2];
		resByte[0] = (byte)(res >> 8);
		resByte[1] = (byte)(res);

		return resByte;

	}
	
	public void encrypt() {

		for(byte[] block: this.blockOfMessage) {
			
			for(int i = 0; i < this.numRounds; i++) {
				if((i % 2) == 0) {	
					roundAddByte(block, this.key.get(i));
				} else {
					roundMul(block, this.key.get(i));
				}
			}
			roundMul(block, searchForShift());
		}
	}
	
	public void decrypt() {		
		
		for(byte[] block: this.blockOfMessage) {
			
			roundMul(block, roundInvMul(searchForShift()));
			for(int i = (this.numRounds - 1); i > -1; --i) {
				if((i % 2) == 0) {		
					roundAddInvByte(block, this.key.get(i));
				} else {
					roundMul(block, roundInvMul(this.key.get(i)));
				}
			}

		}
	}	

	public byte[] roundXor(byte[] block, byte[] key) {
		byte l = block[0];
		byte r = block[1];

		byte tmpl = (byte)(r ^ key[0]);
		byte tmpr = (byte)(l);
		block[0] = tmpl;
		block[1] = tmpr;
		
		return block;
	}
	public byte[] roundXorInv(byte[] block, byte[] key) {
		byte l = block[0];
		byte r = block[1];

		byte tmpr = (byte)(l ^ key[0]);
		byte tmpl = (byte)(r);
		block[0] = tmpl;
		block[1] = tmpr;
		
		return block;
	}
	private byte[] roundMul(byte[] block, byte[] key) {
		int x = Operations.concat2Bytes(block[0], block[1]);
		x = Operations.mul(x, Operations.concat2Bytes(key[0], key[1]));
		block[0] = (byte)(x >> 8);
		block[1] = (byte) x;
		return block;
	}
	
	private byte[] roundAdd(byte[] block, byte[] key) {
		int x = Operations.concat2Bytes(block[0], block[1]);
		x = Operations.add(x, Operations.concat2Bytes(key[0], key[1]));
		block[0] = (byte)(x >> 8);
		block[1] = (byte) x;
		return block;
	}

	private byte[] roundAddByte(byte[] block, byte[] key) {
		byte l = block[0];
		byte r = block[1];

		byte tmpl = Operations.addByte(r, key[0]);
		byte tmpr = (byte)(l ^ key[1]);
		
		block[0] = tmpl;
		block[1] = tmpr;
		
		return block;
	}
	private byte[] roundAddInvByte(byte[] block, byte[] key) {
		byte l = block[0];
		byte r = block[1];

		byte tmpr =  Operations.addByte(l , Operations.addInvByte(key[0]));
		byte tmpl = (byte)(r ^ key[1]);
		
		block[0] = tmpl;
		block[1] = tmpr;
		
		return block;
	}
	private byte[] roundInvMul(byte[] key) {
		byte[] tmp = new byte[this.sizeOfBlocks];
		int tmpInt = Operations.mulInv(key);
		tmp[0] = (byte)(tmpInt >> 8);
		tmp[1] = (byte)(tmpInt); 
		return tmp;
	}
	
	private byte[] roundInvAdd(byte[] key) {
		byte[] tmp = new byte[this.sizeOfBlocks];
		int tmpInt = Operations.addInv(key);
		tmp[0] = (byte)(tmpInt >> 8);
		tmp[1] = (byte)(tmpInt); 
		return tmp;
	}
}



