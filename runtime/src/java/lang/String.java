package java.lang;

/** A placeholder implementation of the String class. */
public final class String {
    private final char value[];

    public String(char value[]) {
        this(value, false);
    }

    public String(char value[], boolean share) {
        if(share) {
            this.value = value;
        } else {
            this.value = new char[value.length];
            charArrayCopy(value, this.value);
        }
    }

    public String(byte bytes[]) {
        // A naiive implementation for now.
        value = new char[bytes.length];
        for (int i = 0; i < bytes.length; ++i) {
            value[i] = (char) bytes[i];
        }
    }

    public String concat(String other){
        char value[] = new char[this.value.length + other.value.length];
        for (int i = 0; i < this.value.length; ++i) {
            value[i] = this.value[i];
        }
        for (int i = this.value.length; i <this.value.length + other.value.length; ++i) {
            value[i] = other.value[i-this.value.length];
        }
        return new String(value);
    }

    @Override
    public String toString() {
        return this;
    }

    public char[] toCharArray() {
        // Cannot use Arrays.copyOf because of class initialization order issues
        char result[] = new char[value.length];
        charArrayCopy(value, result);
        return result;
    }

    private void charArrayCopy(char[] source, char[] result) {
        for(int i=0; i< source.length; i++){
            result[i] = source[i];
        }
    }

    public static native String valueOf(byte i);
    public static native String valueOf(short i);
    public static native String valueOf(int i);
    public static native String valueOf(long i);
    public static native String valueOf(boolean i);
    public static native String valueOf(char i);
    public static native String valueOf(float i);
    public static native String valueOf(double i);
}
