/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * The Original Software is PNG Image Writer.
 * The Initial Developer of the Original Software is Alexandre Iline
 * Portions created by Alexandre Iline are Copyright (C) 2006
 * All Rights Reserved.
 *
 * Contributor(s): Alexandre Iline
 * Created on 30 August 2005  23:39
 * PNGImageWriter.java
 */

package org.cesilko.rachota.gui;

import java.awt.image.BufferedImage;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/** Encoder for writing BufferedImage as true color PNG
 * image with maximum compression.
 * @author Alexandre Iline
 */
public class PNGImageWriter {
    
    /** Checksum computer. */
    CRC32 checksum;
    /** Output stream representing file where image should be saved. */
    OutputStream outputStream;
    
    /** Transforms given number to byte array and writes it.
     * @param number Number to be transformed and written.
     * @throws java.io.IOException Exception thrown when any I/O problem occurs.
     */
    void write(int number) throws IOException {
        byte array[] = {
            (byte)((number >> 24) & 0xff),
            (byte)((number >> 16) & 0xff),
            (byte)((number >> 8) & 0xff),
            (byte)(number & 0xff)
        };
        write(array);
    }
    
    /** Writes given byte array to output stream and updates checksum.
     * @param array Byte array to be written.
     * @throws java.io.IOException Exception thrown when any I/O problem occurs.
     */
    void write(byte[] array) throws IOException {
        outputStream.write(array);
        checksum.update(array);
    }
    
    /** Writes given buffered image to given output stream.
     * @param image Buffered image to be written.
     * @param outputStream Output stream representing file where image should be saved.
     * @throws IOException Exception thrown when any I/O problem occurs.
     */
    public void write(BufferedImage image, OutputStream outputStream) throws IOException {
        checksum = new CRC32();
        this.outputStream = outputStream;
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        final byte id[] = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13};
        write(id);
        checksum.reset();
        write("IHDR".getBytes());
        write(width);
        write(height);
        byte head[]=null;
        head=new byte[]{8, 2, 0, 0, 0};
        write(head);
        write((int) checksum.getValue());
        ByteArrayOutputStream compressed = new ByteArrayOutputStream(65536);
        BufferedOutputStream bos = new BufferedOutputStream(new DeflaterOutputStream(compressed, new Deflater(9)));
        int pixel;
        int color;
        int colorset;
        for (int y=0;y<height;y++) {
            bos.write(0);
            for (int x=0;x<width;x++) {
                pixel=image.getRGB(x,y);
                bos.write((byte)((pixel >> 16) & 0xff));
                bos.write((byte)((pixel >> 8) & 0xff));
                bos.write((byte)(pixel & 0xff));
            }
        }
        bos.close();
        write(compressed.size());
        checksum.reset();
        write("IDAT".getBytes());
        write(compressed.toByteArray());
        write((int) checksum.getValue());
        write(0);
        checksum.reset();
        write("IEND".getBytes());
        write((int) checksum.getValue());
        outputStream.flush();
        outputStream.close();
    }
}