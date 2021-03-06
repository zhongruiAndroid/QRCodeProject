/*
 * Copyright 2007 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing;

import com.google.zxing.aztec.AztecReader;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.maxicode.MaxiCodeReader;
import com.google.zxing.oned.CodaBarReader;
import com.google.zxing.oned.Code128Reader;
import com.google.zxing.oned.Code39Reader;
import com.google.zxing.oned.Code93Reader;
import com.google.zxing.oned.EAN13Reader;
import com.google.zxing.oned.EAN8Reader;
import com.google.zxing.oned.ITFReader;
import com.google.zxing.oned.UPCAReader;
import com.google.zxing.oned.UPCEReader;
import com.google.zxing.oned.rss.RSS14Reader;
import com.google.zxing.oned.rss.expanded.RSSExpandedReader;
import com.google.zxing.pdf417.PDF417Reader;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * MultiFormatReader is a convenience class and the main entry point into the library for most uses.
 * By default it attempts to decode all barcode formats that the library supports. Optionally, you
 * can provide a hints object to request different behavior, for example only decoding QR codes.
 *
 * @author Sean Owen
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class MultiFormatReader implements Reader {

    private static final Reader[] EMPTY_READER_ARRAY = new Reader[0];

    private Map<DecodeHintType, ?> hints;
    private Reader[] readers;
    private Map<Object, Reader> formatMap;
    private List<BarcodeFormat>codeFormat=new ArrayList<>();

    /**
     * Decode an image using the state set up by calling setHints() previously. Continuous scan
     * clients will get a <b>large</b> speed increase by using this instead of decode().
     *
     * @param image The pixel data to decode
     * @return The contents of the image
     * @throws NotFoundException Any errors which occurred
     */
    public Result decodeWithState(BinaryBitmap image) throws NotFoundException {
        // Make sure to set up the default state so we don't crash
        Collection<Reader> readers = new ArrayList<>();
        if (this.readers == null) {
            if (this.codeFormat != null && !this.codeFormat.isEmpty()) {
                for (int i = 0; i < codeFormat.size(); i++) {
                    BarcodeFormat barcodeFormat = codeFormat.get(i);
                    Reader reader = getReaderForFormat(barcodeFormat);
                    if(reader!=null&&!readers.contains(reader)){
                        readers.add(reader);
                    }
                }
            } else {
                readers.add(new QRCodeReader());
            }
//            setHints(null);
        }
        this.readers = readers.toArray(EMPTY_READER_ARRAY);
        return decodeInternal(image);
    }

    private Reader getReaderForFormat(BarcodeFormat format){
        if(format==null){
            return null;
        }
        Reader reader=null;
        switch (format){
            case AZTEC:
                reader= new AztecReader();
            break;
            case CODABAR:
                reader=new CodaBarReader();
            break;
            case CODE_39:
                reader=new Code39Reader(false);
            break;
            case CODE_93:
                reader=new Code93Reader();
            break;
            case CODE_128:
                reader=new Code128Reader();
            break;
            case DATA_MATRIX:
                reader=new DataMatrixReader();
            break;
            case EAN_8:
                reader=new EAN8Reader();
            break;
            case EAN_13:
                reader=new EAN13Reader();
            break;
            case ITF:
                reader=new ITFReader();
            break;
            case MAXICODE:
                reader=new MaxiCodeReader();
            break;
            case PDF_417:
                reader=new PDF417Reader();
            break;
            case QR_CODE:
                reader=new QRCodeReader();
            break;
            case RSS_14:
                reader=new RSS14Reader();
            break;
            case RSS_EXPANDED:
                reader=new RSSExpandedReader();
            break;
            case UPC_A:
                reader=new UPCAReader();
            break;
            case UPC_E:
                reader=new UPCEReader();
            break;
        }
        return reader;
    }

    public void setCodeFormat(List<BarcodeFormat> codeFormatList) {
        if(codeFormatList==null){
            codeFormatList=new ArrayList<>();
        }
        this.codeFormat = codeFormatList;
    }

    @Override
    public Result decode(BinaryBitmap image) throws NotFoundException {
        return decodeWithState(image);
    }

    @Override
    public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
        return decodeWithState(image);
    }

    @Override
    public void reset() {
        if (readers != null) {
            for (Reader reader : readers) {
                if(reader!=null){
                    reader.reset();
                }
            }
        }
    }

    private Result decodeInternal(BinaryBitmap image) throws NotFoundException {
        if (readers != null) {
            for (Reader reader : readers) {
                try {
                    return reader.decode(image, hints);
                } catch (ReaderException re) {
                    // continue
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

}
