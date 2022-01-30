package com.dm4nk.track_library_vaadin.converters;

public class ByteArrayToWrappedByteArray {
    public static Byte[] convert(byte[] data) {
        Byte[] wrappedData = new Byte[data.length];

        int i = 0;
        for (byte b : data)
            wrappedData[i++] = b;
        return wrappedData;
    }
}
