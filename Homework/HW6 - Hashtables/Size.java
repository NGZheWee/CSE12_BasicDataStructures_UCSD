public class Size {
    public static long of (byte theByte) { return Byte.BYTES; }
    public static long of (short theShort) { return Short.BYTES; }
    public static long of (int theInt) { return Integer.BYTES; }
    public static long of (long theLong) { return Long.BYTES; }
    public static long of (long[] longArray) { 
        return Long.BYTES * longArray.length + Long.BYTES; 
    }
    public static long of (int[] intArray) { 
        return Integer.BYTES * intArray.length + Long.BYTES; 
    }
    public static long of (Object[] objectArray) { 
        return Long.BYTES * objectArray.length + Long.BYTES; 
    }
    public static long of (Object ref) { 
        return Long.BYTES; 
    }
}

