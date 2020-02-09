package com.github.qrcode;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void asdf() {
        int imageHeight=3;
        int imageWidth=6;
            byte[] data=  new byte[imageHeight*imageWidth];
            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    int i = x + y * imageWidth + (x + y * imageWidth) % imageHeight;

                    System.out.println((x * imageHeight + imageHeight - y - 1)+"===="+i+"===="+(x + y * imageWidth));
                }
            }
    }
}