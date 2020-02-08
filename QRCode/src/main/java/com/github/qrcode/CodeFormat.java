package com.github.qrcode;

/***
 *   created by zhongrui on 2020/2/8
 */

public interface CodeFormat {
    /**
     * Aztec 2D barcode format.
     */
    String AZTEC = "AZTEC";

    /**
     * CODABAR 1D format.
     */
    String CODABAR = "CODABAR";

    /**
     * Code 39 1D format.
     */
    String CODE_39 = "CODE_39";

    /**
     * Code 93 1D format.
     */
    String CODE_93 = "CODE_93";

    /**
     * Code 128 1D format.
     */
    String CODE_128 = "CODE_128";

    /**
     * Data Matrix 2D barcode format.
     */
    String DATA_MATRIX = "DATA_MATRIX";

    /**
     * EAN-8 1D format.
     */
    String EAN_8 = "EAN_8";

    /**
     * EAN-13 1D format.
     */
    String EAN_13 = "EAN_13";

    /**
     * ITF (Interleaved Two of Five) 1D format.
     */
    String ITF = "ITF";

    /**
     * MaxiCode 2D barcode format.
     */
    String MAXICODE = "MAXICODE";

    /**
     * PDF417 format.
     */
    String PDF_417 = "PDF_417";

    /**
     * QR Code 2D barcode format.
     */
    String QR_CODE = "QR_CODE";

    /**
     * RSS 14
     */
    String RSS_14 = "RSS_14";

    /**
     * RSS EXPANDED
     */
    String RSS_EXPANDED = "RSS_EXPANDED";

    /**
     * UPC-A 1D format.
     */
    String UPC_A = "UPC_A";

    /**
     * UPC-E 1D format.
     */
    String UPC_E = "UPC_E";

    /**
     * UPC/EAN extension format. Not a stand-alone format.
     */
    String UPC_EAN_EXTENSION = "UPC_EAN_EXTENSION";
}