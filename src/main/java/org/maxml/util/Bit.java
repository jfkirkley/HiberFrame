package org.maxml.util;

import java.util.BitSet;


class Bit {
	
	public static String getBitSetRep(BitSet bitSet) {
		int sz = bitSet.length()/8+1;
		char b[] = new char[sz];
		//System.out.println(bitSet.length());
		int j = 0;
		char currByte = 0;
		for (int i = 0; i <= bitSet.length(); i++) {
			j++;
			if(j==8){
				j=0;
				b[i/8] = currByte;
			}
			
			if (bitSet.get(i) ) {
				//System.out.print("1");
				currByte |= (1 << j);
			} else {
				//System.out.print("0");
			}


		}
		//System.out.println();
		return new String(b);
	}

	public static void main(String args[]) {
		BitSet bits1 = new BitSet(16);
		BitSet bits2 = new BitSet(16);
		// set some bits
		for (int i = 0; i < 16; i++) {
			if ((i % 2) == 0)
				bits1.set(i);
			if ((i % 5) != 0)
				bits2.set(i);
		}
		System.out.println("Initial pattern in bits1: ");
		System.out.println(bits1);
		System.out.println(getBitSetRep(bits1));
		System.out.println("\\nInitial pattern in bits2: ");
		System.out.println(bits2);
		// AND bits
		bits2.and(bits1);
		System.out.println("\\nbits2 AND bits1: ");
		System.out.println(bits2);
		// OR bits
		bits2.or(bits1);
		System.out.println("\\nbits2 OR bits1: ");
		System.out.println(bits2);
		// XOR bits
		bits2.xor(bits1);
		System.out.println("\\nbits2 XOR bits1: ");
		System.out.println(bits2);
	}
}
