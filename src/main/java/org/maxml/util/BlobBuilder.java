package org.maxml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

interface Twiddler {
    public byte[] encode(byte[] buf);

    public byte[] decode(byte[] buf);

    public int addSeqNum(int[] a, int n);
}

class T1 implements Twiddler {
    public byte[] encode(byte[] buf) {
        for (int i = 0; i < buf.length; ++i) {
            byte b = buf[i];
            byte c = (byte) ~b;
            byte d = (byte) ~c;
            buf[i] = c;
        }
        return buf;
    }

    public byte[] decode(byte[] buf) {
        for (int i = 0; i < buf.length; ++i) {
            byte b = buf[i];
            byte c = (byte) ~b;
            buf[i] = c;
        }
        return buf;
    }

    public int addSeqNum(int[] a, int n) {
        a[n] = 1;
        return n + 1;
    }

}

class T2 implements Twiddler {
    public byte[] encode(byte[] buf) {
        for (int i = 0; i < buf.length / 2; ++i) {
            int end = buf.length - 1 - i;
            byte b = buf[i];
            byte c = buf[end];
            buf[i] = c;
            buf[end] = b;
        }
        return buf;
    }

    public byte[] decode(byte[] buf) {
        for (int i = 0; i < buf.length / 2; ++i) {
            int end = buf.length - 1 - i;
            byte b = buf[i];
            byte c = buf[end];
            buf[i] = c;
            buf[end] = b;
        }
        return buf;
    }

    public int addSeqNum(int[] a, int n) {
        a[n] = 2;
        return n + 1;
    }

}

class T3 implements Twiddler {
    int sz;
    int slen;

    public T3(int sz, int slen) {
        this.sz = sz;
        this.slen = slen;
    }

    public byte[] encode(byte[] buf) {
        int islen = 8 - slen;
        int m = (255 >>> islen) & 255;
        int m2 = (255 >> slen) & 255;
        // BlobBuilder.pb((byte)m);
        // System.out.println("");
        int[] preShiftBuf = new int[sz];
        int[] shiftBuf = new int[sz];
        for (int i = 0; i < buf.length - sz; i += sz) {

            // for(int j = 0; j < sz; ++j) BlobBuilder.pb(buf[i+j]);
            // System.out.println("");

            for (int j = 0; j < sz; ++j)
                preShiftBuf[j] = (int) buf[i + j];
            for (int j = 0; j < sz; ++j)
                shiftBuf[j] = (int) (buf[i + j] & m);

            for (int j = 0; j < sz; ++j)
                preShiftBuf[j] = (((preShiftBuf[j] >>> slen) & m2) | (shiftBuf[(j
                        + sz - 1)
                        % sz] << islen));
            for (int j = 0; j < sz; ++j)
                buf[i + j] = (byte) preShiftBuf[j];

            // for(int j = 0; j < sz; ++j) BlobBuilder.pb(buf[i+j]);
            // System.out.println("");
        }
        return buf;
    }

    public byte[] decode(byte[] buf) {
        int islen = 8 - slen;
        int m = (255 << islen) & 255;
        // BlobBuilder.pb((byte)m);
        // System.out.println("");
        int[] preShiftBuf = new int[sz];
        int[] shiftBuf = new int[sz];
        for (int i = 0; i < buf.length - sz; i += sz) {

            // for(int j = 0; j < sz; ++j) BlobBuilder.pb(buf[i+j]);
            // System.out.println("");

            for (int j = 0; j < sz; ++j)
                preShiftBuf[j] = (int) buf[i + j];
            for (int j = 0; j < sz; ++j)
                shiftBuf[j] = (int) (buf[i + j] & m);

            for (int j = 0; j < sz; ++j)
                preShiftBuf[j] = ((preShiftBuf[j] << slen) | (shiftBuf[(j + 1)
                        % sz] >>> islen));
            for (int j = 0; j < sz; ++j)
                buf[i + j] = (byte) preShiftBuf[j];

            // for(int j = 0; j < sz; ++j) BlobBuilder.pb(buf[i+j]);
            // System.out.println("");
        }
        return buf;
    }

    public int addSeqNum(int[] a, int n) {
        a[n++] = 3;
        a[n++] = sz;
        a[n] = slen;
        return n + 1;
    }

}

class T4 implements Twiddler {
    public byte[] encode(byte[] buf) {
        for (int i = 0; i < buf.length; i += 1) {
            int b = (int) buf[i];
            int nb = 0;
            for (int j = 0; j < 8; j += 1) {
                nb |= ((((1 << j) & b) >> j) << (7 - j));
            }
            buf[i] = (byte) nb;
        }
        return buf;
    }

    public byte[] decode(byte[] buf) {
        return encode(buf);
    }

    public int addSeqNum(int[] a, int n) {
        a[n] = 4;
        return n + 1;
    }

}

class TChain implements Twiddler {

    ArrayList twList;

    public TChain(Twiddler[] tw) {
        this.twList = new ArrayList();
        for (int i = 0; i < tw.length; ++i)
            twList.add(tw[i]);
    }

    public byte[] encode(byte[] buf) {
        // BlobBuilder.pbb(buf, "o");
        for (int i = 0; i < twList.size(); ++i) {
            Twiddler tw = (Twiddler) twList.get(i);
            tw.encode(buf);
            // BlobBuilder.pbb(buf, ("" + i));
        }
        return buf;
    }

    public byte[] decode(byte[] buf) {
        // BlobBuilder.pbb(buf, "o");
        for (int i = twList.size() - 1; i >= 0; --i) {
            Twiddler tw = (Twiddler) twList.get(i);
            tw.decode(buf);
            // BlobBuilder.pbb(buf, ("" + i));
        }
        return buf;
    }

    public int addSeqNum(int[] a, int n) {
        for (int i = twList.size() - 1; i >= 0; --i) {
            Twiddler tw = (Twiddler) twList.get(i);
            n = tw.addSeqNum(a, n);
        }
        return n;
    }
}

public class BlobBuilder {
    public final static int CHUNKSIZE = 64;

    public final static int GBZ       = 1024 * 1024;
    public final static int GSZ       = 1024;
    public final static int GFZ       = GBZ/CHUNKSIZE;

    byte[]                  grog      = new byte[GBZ];
    byte[]                  blog      = new byte[GBZ];
    int[]                   slog      = new int[GSZ];
    int[]                   gslog     = new int[16 * GSZ];
    int[]                   tslog     = new int[16 * GSZ];
    int[]                   sslog     = new int[16 * GSZ];
    char[]                  flog      = new char[GFZ];
    int                     slogi;
    int                     gslogi    = 0;
    int                     tslogi    = 0;
    TChain                  tc;

    public static void pbb(byte[] b, String p) {
        System.out.print(p + ": ");
        for (int i = 0; i < b.length; ++i)
            pb(b[i]);
        System.out.println("");
    }

    public static void pb(byte b) {
        int m = 128;
        for (int i = 0; i < 8; ++i) {
            if ((b & m) == m)
                System.out.print("1");
            else
                System.out.print("0");
            m = m >> 1;
        }
        System.out.print(" ");
    }

    private void encEnc() {
        int og = gslogi;
        gslogi = tc.addSeqNum(gslog, gslogi + 1);
        gslog[og] = gslogi - og - 1;
    }

    public BlobBuilder() {
        T2 rt2 = new T2();
        T3 t3 = new T3(3, 5);
        tc = new TChain(new Twiddler[] { new T4(), t3, rt2, new T1(),
                new T3(2, 4), new T4(), rt2, t3 });

        int i;
        for (i = 0; i < 1024; ++i) {
            slog[i] = 0;
        }
        for (i = 0; i < GBZ; ++i) {
            blog[i] = 0;
            // if(blog[i]!=0) {System.out.println(" zero: " + (i) + ":" +
            // blog[i]);}
        }
        for (i = 0; i < GFZ; ++i) {
            flog[i] = 'f';
        }
        slogi = 0;
    }

    static int rint(int max) {
        double mx = (double) max;
        return (int) (mx * Math.random());
    }

    int mapb(int r) {
        int n = rint(GFZ);
        int s = rint(12) + 4;

        int amount = s * CHUNKSIZE;

        if (r / CHUNKSIZE < s) {
            s = r / CHUNKSIZE + 1;
            amount = r;
        }

        while (true) {
            int i = n;

            while ((i - n) < s && i < GFZ)
                if (flog[i++] != 'f')
                    n = i;

            if (i - n == s) {
                slog[slogi++] = n * CHUNKSIZE;
                slog[slogi++] = amount;
                int j = i - s;
                for (; j < i; ++j)
                    flog[j] = 't';
                break;
            } else {
                while (i < GFZ && flog[i] != 'f')
                    ++i;
                if (i == GFZ)
                    n = 0;
                else
                    n = i - 1;
            }
        }
        return (int) (s * CHUNKSIZE);
    }

    void wmapb(int sz) {
        int r = sz;
        while ((r -= mapb(r)) > 0)
            ;
    }

    void backFill() {
        int last = 0, nextBlockPtr = 0;
        int j = 0;
        for (int i = 0; i < tslogi; i += 2) {
            int start = tslog[i];
            // System.out.print(start + ",");
            sslog[j++] = start;
        }
        System.out.println("");
        for (; j < 16 * GSZ; j++) {
            sslog[j] = Integer.MAX_VALUE;
        }
        Arrays.sort(sslog);
        j = 0;
        int start = (getEnd(0) == -1) ? 0 : sslog[j++];
        while (start != GBZ) {
            int next = sslog[j++];
            int end = (j == 1) ? 0 : getEnd(start);
            if (next == Integer.MAX_VALUE)
                next = GBZ;
            int cnt = 0;
            // System.out.println("filling from " + start+ " - " + end + " to "
            // + next );

            for (int i = start + end; i < next; i++) {
                if (blog[i] != 0) {
                    ++cnt;
                }
                blog[i] = (byte) rint(Integer.MAX_VALUE);
            }
            // if(cnt>0)System.out.println( "num ERRORs: " + cnt );
            start = next;
        }
    }

    int getEnd(int forThis) {
        for (int i = 0; i < tslogi; i += 2) {
            int start = tslog[i];
            if (start == forThis) {
                return tslog[i + 1];
            }
        }
        return -1;
    }

    void showit() {
        tc.encode(fbuf);
        int ptr = 0;
        int[] sz = { 896, 960, 704, 768, 448, 384, 704, 704, 576, 576, 832,
                832, 832, 640, 256, 320, 768, 399 };
        int szi = 0;
        for (int i = 0; i < slogi && szi < sz.length; i += 2) {
            int start = slog[i];
            int nb = sz[szi++];// slog[i + 1];
            tslog[tslogi] = start;
            tslog[tslogi + 1] = nb;
            tslogi += 2;
            byte[] bf = new byte[nb];
            // System.out.println("packing from " + start + " to " +
            // (nb+start));
            for (int j = 0; j < nb; ++j) {
                if (blog[start + j] != 0) {
                    System.out.println("non zero: " + ptr + ":"
                            + blog[start + j] + ":" + (start + j));
                }
                // blog[start + j] = fbuf[ptr++];
                bf[j] = fbuf[ptr++];
                // System.out.print(fbuf[ptr++]+",");
            }
            putFile2("/home/jkirkley/jrp/c/gbc.it" + i, bf);
        }
        // tc.decode(fbuf);
        putFile2("/home/jkirkley/jrp/c/gbc.it.claz", fbuf);
    }

    void pack() {
        tc.encode(fbuf);
        int ptr = 0;
        for (int i = 0; i < slogi; i += 2) {
            int start = slog[i];
            int nb = slog[i + 1];
            tslog[tslogi] = start;
            tslog[tslogi + 1] = nb;
            tslogi += 2;
            // System.out.println("packing from " + start + " to " +
            // (nb+start));
            for (int j = 0; j < nb; ++j) {
                if (blog[start + j] != 0) {
                    System.out.println("non zero: " + ptr + ":"
                            + blog[start + j] + ":" + (start + j));
                }
                blog[start + j] = fbuf[ptr++];
            }
        }
    }

    void unpack() {
        int ptr = 0;
        for (int i = 0; i < slogi; i += 2) {
            int start = slog[i];
            int nb = slog[i + 1];
            for (int j = 0; j < nb; ++j) {
                obuf[ptr] = blog[start + j];
                ++ptr;
                // if(obuf[ptr-1]!=fbuf[ptr-1]) {
                // System.out.println("out at: " + (ptr-1));
                // }
            }
        }
        tc.decode(obuf);
    }

    void pr() {
        int i;
        char lastVal = 'x';
        for (i = 0; i < 16 * 1024; ++i) {
            if (flog[i] != lastVal) {
                if (flog[i] == 't') {
                    System.out.println("start: " + i);
                } else {
                    System.out.println("stop: " + i);
                }
                lastVal = flog[i];
            }
        }
        for (i = 0; i < slogi; i += 2) {
            System.out.println("set: " + slog[i] + ":" + slog[i + 1]);
        }
    }

    byte[] fbuf;
    byte[] obuf;

    public static byte[] toByteBuf(int ib[], int len) {
        byte[] b = new byte[len * 4];
        for (int i = 0; i < len; ++i) {
            // System.out.print(ib[i] + ",");
            int bi = i * 4;
            int x = ib[i];
            b[bi++] = (byte) ((x >> 24) & 255);
            b[bi++] = (byte) ((x >> 16) & 255);
            b[bi++] = (byte) ((x >> 8) & 255);
            b[bi++] = (byte) (x & 255);
            // if (i % 10 == 0)
            // System.out.println("");
        }
        // System.out.println("");
        return b;
    }

    public int getFile(String fname) {
        try {

            File f = new File(fname);
            fbuf = new byte[(int) f.length()];
            obuf = new byte[(int) f.length()];
            FileInputStream fis = new FileInputStream(f);
            return fis.read(fbuf);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public int putFile(String fname, byte[] buf) {
        try {

            File f = new File(fname);// + ".out");
            FileOutputStream fos = new FileOutputStream(f);

            // System.out.println("gslogi: " + gslogi);

            byte[] bl = toByteBuf(new int[] { gslogi }, 1);
            fos.write(bl);
            fos.write(toByteBuf(gslog, gslogi));
            fos.write(buf);
            fos.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public void putInfo() {
        System.out.println("table size: " + gslogi);
        // byte [] buf = toByteBuf(gslog, gslogi);
        for (int i = 0; i < gslogi;) {
            System.out.println("encName: " + gslog[i]);
            int len = gslog[i + 1];
            i += 2;
            System.out.println("block table len: " + len);
            for (int k = 0; k < len; k += 2) {
                System.out.print(gslog[k + i] + " & " + gslog[k + i + 1] + ", ");
            }
            System.out.println();
            i += len;
            len = gslog[i];
            i++;
            System.out.println("decode len: " + len);
            for (int k = 0; k < len; k++) {
                System.out.print(gslog[k + i] + ", ");
            }
            i += len;
            System.out.println();
        }
    }

    public int putFile2(String fname, byte[] buf) {
        try {

            File f = new File(fname);// + ".out.orig");
            FileOutputStream fos = new FileOutputStream(f);

            fos.write(buf);
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int putFile3(String fname, byte[] buf, int len) {
        try {

            File f = new File(fname + ".out.orig");
            FileOutputStream fos = new FileOutputStream(f);

            fos.write(buf, 0, len);
            fos.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public int encName(String n) {
        int v = 0;
        for (int i = 0; i < n.length(); ++i) {
            // System.out.println("c: " + (int) n.charAt(i));
            v += (int) n.charAt(i);
        }
        return v;
    }

    public void checkJarNames(String jarName) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = new FileInputStream(new File(jarName));
        JarInputStream jis = new JarInputStream(fis);
        int s = 0;
        JarEntry entry;
        HashMap cm = new HashMap();
        while ((entry = jis.getNextJarEntry()) != null) {
            if (!entry.isDirectory()) {

                String name = entry.getName();
                if (name.endsWith(".class")) {
                    name = name.substring(0, name.lastIndexOf('.')).replace(
                            '/', '.');

                    Integer nk = new Integer(encName(name));
                    if (cm.containsKey(nk)) {
                        System.out.println("Conflict: " + name + " <> "
                                + cm.get(nk));
                    } else {
                        cm.put(nk, name);
                    }
                }
            }
        }
        jis.close();
    }

    public void checkJarClasses(String jarName) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = new FileInputStream(new File(jarName));
        JarInputStream jis = new JarInputStream(fis);
        int s = 0;
        JarEntry entry;
    
        while ((entry = jis.getNextJarEntry()) != null) {
            if (!entry.isDirectory()) {

                String name = entry.getName();
                if (name.endsWith(".class")) {
                    name = name.substring(0, name.lastIndexOf('.')).replace(
                            '/', '.');
                    System.out.println("check class: " + name );
                }
            }
        }
        jis.close();
    }

    public void addJar(String jarName) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = new FileInputStream(new File(jarName));
        JarInputStream jis = new JarInputStream(fis);
        int s = 0;
        JarEntry entry;
        while ((entry = jis.getNextJarEntry()) != null) {
            if (!entry.isDirectory()) {

                String name = entry.getName();
                if (name.endsWith(".class")) {
                    name = name.substring(0, name.lastIndexOf('.')).replace(
                            '/', '.');
                    // name = "/home/jkirkley/jrp/c/tmp/" + name;
                    // System.out.println(name);

                    int tlen = 0, t = 0;
                    while ((t = jis.read(grog, tlen, grog.length - tlen)) != -1) {
                        tlen += t;
                        // System.out.println("t: " + t);
                    }
                    wmapb(tlen);
                    fbuf = new byte[tlen];
                    for (int i = 0; i < tlen; ++i) {
                        fbuf[i] = grog[i];
                    }
                    pack();
                    gslog[gslogi++] = encName(name);
                    gslog[gslogi++] = slogi;
                    // System.out.println("endName: " + encName(name) + " slogi:
                    // "
                    // + slogi);
                    for (int i = gslogi; i < slogi + gslogi; ++i) {
                        gslog[i] = slog[i - gslogi];
                    }
                    gslogi += slogi;
                    s = slogi;
                    encEnc();
                    slogi = 0;

                    // System.out.println("endName: " + encName(name) + " <> "
                    // + name);
                }
            }
        }
        jis.close();
        slogi = s;
    }
    
    public void encodeFile(String fileName) {
    	try {
			StringBuffer buf = FileUtils.i().getFileContents(fileName);
			byte[] bbuf = buf.toString().getBytes();
			tc.encode(bbuf);
			FileUtils.i().writeContents(fileName + ".enc", bbuf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void decodeFile(String fileName) {
    	try {
			
			byte[] bbuf = FileUtils.i().getStreamBytes(fileName, 1024*1024*2);
			tc.decode(bbuf);
			FileUtils.i().writeContents(fileName + ".dec", bbuf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void analyze(String jarName, String filename) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = new FileInputStream(new File(jarName));
        JarInputStream jis = new JarInputStream(fis);
        int s = 0;
        JarEntry entry;
        while ((entry = jis.getNextJarEntry()) != null) {
            if (!entry.isDirectory()) {

                String name = entry.getName();
                if (name.endsWith(".class") && filename.equals(name)) {
                    name = name.substring(0, name.lastIndexOf('.')).replace(
                            '/', '.');
                    // name = "/home/jkirkley/jrp/c/tmp/" + name;
                    // System.out.println(name);

                    int tlen = 0, t = 0;
                    while ((t = jis.read(grog, tlen, grog.length - tlen)) != -1) {
                        tlen += t;
                        System.out.println("t: " + t);
                    }
                    System.out.println("t: " + t);
                    System.out.println("tlen: " + tlen);
                    // wmapb(tlen);
                    fbuf = new byte[tlen];
                    for (int i = 0; i < tlen; ++i) {
                        fbuf[i] = grog[i];
                    }

                    showit();
                    // putFile2("/home/jkirkley/jrp/c/gbc.it", fbuf);
                    gslog[gslogi++] = encName(name);
                    gslog[gslogi++] = slogi;
                    // System.out.println("endName: " + encName(name) + " slogi:
                    // "
                    // + slogi);
                    for (int i = gslogi; i < slogi + gslogi; ++i) {
                        gslog[i] = slog[i - gslogi];
                    }
                    gslogi += slogi;
                    s = slogi;
                    encEnc();
                    slogi = 0;

                    // System.out.println("endName: " + encName(name) + " <> "
                    // + name);
                }
            }
        }
        jis.close();
        slogi = s;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        // T2 rt2 = new T2();
        // T3 t2 = new T3(2, 3);
        // T3 t3 = new T3(9, 7);
        //
        // int[] jb = new int[] { 1024 + 4096 + 2, 3 * 4096 - 1 };
        //
        // BlobBuilder.pbb(toByteBuf(jb, 1), "r");
        //
        // TChain tc = new TChain(new Twiddler[] { t3, t3, new T4(), new T1(),
        // rt2, t2 });
        // byte[] bs = tc.encode("aboslutely no sense".getBytes());
        // System.out.println("----------------------------------------------------");
        //
        // tc.decode(bs);
        // System.out.println(new String(bs));
        // // BlobBuilder.pb((byte)(224>>>5));

        // byte [] bs =t3.encode( "aboslutely no sense".getBytes());
        // BlobBuilder.pbb(bs,"x: ");
        // bs =t2.encode( bs );
        // BlobBuilder.pbb(bs,"x: ");
        // bs =t2.decode( bs );
        // BlobBuilder.pbb(bs,"x: ");
        // bs =t3.decode( bs );
        // System.out.println(new String(bs));
        // System.out.println(" ---------------------------------------- " );

        // int b = (int) 46;
        // BlobBuilder.pb((byte)b);
        // int nb = 0;
        // for (int j = 0; j < 8; j += 1) {
        // BlobBuilder.pb((byte)(((1<<j)&b)));
        // BlobBuilder.pb((byte)(((1<<j))));
        // BlobBuilder.pb((byte)((((1<<j)&b)>>j)<<(7-j)));
        // nb |= ((((1<<j)&b)>>j)<<(7-j));
        // System.out.println("");
        // BlobBuilder.pb((byte)nb);
        // }
        // System.out.println("");
        // BlobBuilder.pb((byte)nb);
        // System.out.println("");

        // //////////////////////////////////////////////
        // //////// REMEMBER - FILL IN SPACES OF BLOB WITH RANDOM CHARS
        // /////////////////////////////////////////////////

        if (true) {
            BlobBuilder bb = new BlobBuilder();
            try {
                bb.addJar("/home/jkirkley/jrp/c/f.jar");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // bb.putInfo();
            // bb.wmapb(bb.getFile(args[0]));
            // bb.pack();
            // bb.unpack();
            bb.backFill();
            bb.putFile(args[0], bb.blog);
        } else if (false) {
            BlobBuilder bb = new BlobBuilder();
            try {
                bb.checkJarNames("/home/jkirkley/jrp/c/f.jar");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (true) {
            BlobBuilder bb = new BlobBuilder();
            try {
                bb.checkJarClasses("/home/jkirkley/jrp/c/f.jar");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            BlobBuilder bb = new BlobBuilder();
            try {
                bb.analyze("/home/jkirkley/jrp/c/sff.jar",
                        "org.maxml/gui/layout/GridBagConstraintBuilder.class");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        // bb.putFile2(args[0], bb.blog);
        // bb.pr();
    }

}
