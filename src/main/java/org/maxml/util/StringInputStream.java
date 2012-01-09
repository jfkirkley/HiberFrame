package org.maxml.util;

//package src;

import java.io.*;

public class StringInputStream extends java.io.InputStream
{
   static final String versionInfo1900 = "@(#) CME $Id: StringInputStream.java,v 1.8 1998/05/21 18:02:12 graydon Exp $";

   private ByteArrayInputStream byteStream;   
   private java.io.IOException invalidEncoding;

   public StringInputStream(String streamSource) {
      this(streamSource, "UTF8"); // FIXME: pick a real, supported encoding
          //if(pandect.world.World.tracingEnabled) {Trace.messageOut(readln(byteStream)); }
   }
/*
   public String readln(ByteArrayInputStream infile) {
      byte buf[] = new byte[8192];
      int len=0;
      while(true) {
            byte b;
            if ( (b = (byte) infile.read() ) == '\n' ) {
               return new String( buf, 0, len );
            }
            buf[len++] = b;
            if(pandect.world.World.tracingEnabled) {Trace.messageOut("byte: " + b); }
      }
   }
   */

   public StringInputStream(String source, String charEncoding) {
      invalidEncoding = null;
      try {
         byteStream = new ByteArrayInputStream(
           source.getBytes(charEncoding)
         );
      } catch(Exception e) {

         /*** NOTE!!!!!
         The point of this class was to catch this exception, so that we 
         don't have a constructor that throws an exception. Now, if we had
         used the StringInputStream() constructor we picked our own 
         encoding that we believe works. Either we're wrong, or some dork
         has called this function with an encoding that is NOT VALID. We
         do *NOT* want to throw an exception, so we had better go ahead
         and construct the stream, but we had better indicate somewhere 
         somehow that things are not good. What we're going to do is save
         the exception and throw it the first time someone tries to read
         from this. At least this way the exception will get raised, even
         if conceptually it was not the right time.
         ***/
         invalidEncoding = 
            new IOException("Invalid character encoding: " + charEncoding);
      }
   }
 
   public final int read()
      throws IOException   
   {
      if (invalidEncoding != null) {
         throw invalidEncoding; // see long comment above
      }
      return byteStream.read(); 
   }

   public static void main(String args[])
      throws Exception
   {

      StringInputStream s = new StringInputStream("hello, world!", "noway");

      int c; 
      while ((c = s.read()) != -1) {
         System.out.print((char) c);
      }
   }
}
