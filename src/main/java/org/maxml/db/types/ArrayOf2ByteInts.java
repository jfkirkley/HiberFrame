package org.maxml.db.types;


public class ArrayOf2ByteInts {
    private static final int MAXSIZE   = 255;
    
    private Integer id;
    private byte[]           intArray  = new byte[MAXSIZE];
    private int              currIndex = 0;


    
    public ArrayOf2ByteInts(){}
    
    public ArrayOf2ByteInts(ArrayOf2ByteInts arrayOf2ByteInts) {
        setData(arrayOf2ByteInts.getIntArray(), arrayOf2ByteInts.getCurrIndex());
        this.id = arrayOf2ByteInts.getId(); 
    }
    
    public ArrayOf2ByteInts( byte[] b ) {
        setData(b, b.length);
    }

    private void setData( byte[] b, int len ) {
        if (len <= MAXSIZE) {
            for (currIndex = 0; currIndex < len; currIndex++) {
                intArray[currIndex] = b[currIndex];
            }
        }
        else {
            throw new Error( "ArrayOf2ByteInts: byte array size " + b.length + ", larger than max size: " + MAXSIZE);
        }
    }

    public void set(ArrayOf2ByteInts arrayOf2ByteInts) {
        setData(arrayOf2ByteInts.getIntArray(), arrayOf2ByteInts.getCurrIndex());
    }
    
    public void addInt(int s) {
        int upper = (s >> 8) & 255;
        int lower = s & 255;
        if (upper < 0)
            upper += 256;
        if (lower < 0)
            lower += 256;

        // System.out.println("lower: " + lower + " upper: " + upper );
        // System.out.println("num is: " + (lower + upper * 256));

        if (currIndex < MAXSIZE) {
            intArray[currIndex++] =  (byte)lower;
            intArray[currIndex++] =  (byte)upper;
        } 
        else {
            throw new Error( "ArrayOf2ByteInts: full, cannot add another value");
        }
    }
    
    public int [] getAsIntArray() {
        int [] ints = new int[currIndex/2];
        for (int i = 0; i < currIndex-1; i+=2) {
            int lower = (int)intArray[i];
            int upper = (int)intArray[i+1];
            if (upper < 0)
                upper += 256;
            if (lower < 0)
                lower += 256;
            //System.out.println("#: " + (lower + upper * 256));
            ints[i/2] = (lower + upper * 256);
        }
        return ints;
    }  
    
    public int getNumInts() { return currIndex/2; } 

    public void printOutInts() {
        int [] ints = getAsIntArray();
        for (int i = 0; i < ints.length; i++) {
            System.out.println("#: " + ints[i] );
        }
    }
    
    public void removeInt(int intVal) {
        int i = 0;
        for (; i < currIndex-1; i+=2) {
            int lower = (int)intArray[i];
            int upper = (int)intArray[i+1];
            if (upper < 0)
                upper += 256;
            if (lower < 0)
                lower += 256;
            if( intVal == (lower + upper * 256) ) {
                break;
            }
        }
        if( i < currIndex - 1 ) {
            currIndex-=2;
            for (; i < currIndex-1; i+=2) {
                intArray[i] = intArray[i+2];
                intArray[i+1] = intArray[i+3];
            }
        }
    }
    
    public void clear() {
        currIndex = 0;
    }
    
    public void comp() {
        byte [] c = toString().getBytes();
        for (int i = 0; i < c.length; i++) {
            if(intArray[i] != c[i]) {
                System.out.println(intArray[i] + " : " + c[i]);
            }
        }
    }

    public String toString() {
        return new String(intArray, 0, currIndex - 1);
    }

    public static void main(String[] args) {

        ArrayOf2ByteInts stringOf2ByteInts = new ArrayOf2ByteInts();
        for (short i = 0; i < 100; i++) {
            stringOf2ByteInts.addInt(i * 1000 + 13);
        }
        //stringOf2ByteInts.printOutInts();
//        System.out.println("str: \n" + stringOf2ByteInts);
//        ArrayOf2ByteInts stringOf2ByteInts2 = new ArrayOf2ByteInts(stringOf2ByteInts.toString());
//        System.out.println("\n\n----------------------------------\nstr2: \n" + stringOf2ByteInts2);
        //stringOf2ByteInts2.printOutInts();
//        stringOf2ByteInts.comp();
        
        TypeSet typeSet = new TypeSet(stringOf2ByteInts.getIntArray());
        for (int i = 0; i < 100; i++) {
            if( !typeSet.contains(i * 1000 + 13) ) {
                System.out.println((i * 1000 + 13) + " : is not in the set dude!!!" );
            }
        }
        
    }

    public byte[] getIntArray() {
        return intArray;
    }

    public void setIntArray(byte[] intArray) {
        this.intArray = intArray;
    }

    public int getCurrIndex() {
        return currIndex;
    }

    public void setCurrIndex(int currIndex) {
        this.currIndex = currIndex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
