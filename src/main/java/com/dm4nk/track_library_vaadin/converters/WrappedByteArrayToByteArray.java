package com.dm4nk.track_library_vaadin.converters;

public class WrappedByteArrayToByteArray {
    public static byte[] convert(Byte[] wrappedData) {
        byte[] data = new byte[wrappedData.length];

        int i = 0;
        for (Byte b : wrappedData)
            data[i++] = b;
        return data;
    }
}
