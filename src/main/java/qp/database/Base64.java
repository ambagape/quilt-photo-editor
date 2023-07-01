package qp.database;
import qp.DATABASE;


/**
 *
 * @author Maira57
 */
public class Base64 {



    public static String encode(String string) throws Exception {
        StringBuilder encodedBuidler;
        String encoded;
        byte[] vaux;
        byte[] stringArray;
        int paddingCount;
        int x;


        // initialize
        encodedBuidler = new StringBuilder();


        // pad the input
        vaux = string.getBytes();
        paddingCount = (3 - (vaux.length % 3)) % 3;
        stringArray = new byte[vaux.length + paddingCount];
        System.arraycopy(vaux, 0, stringArray, 0, vaux.length);

        // encode
        for (int i = 0; i < stringArray.length; i += 3) {

            x = ((stringArray[i] & 0xff) << 16) +
                ((stringArray[i + 1] & 0xff) << 8) +
                (stringArray[i + 2] & 0xff);

            encodedBuidler.append(DATABASE.code.charAt((x >> 18) & 0x3f));
            encodedBuidler.append(DATABASE.code.charAt((x >> 12) & 0x3f));
            encodedBuidler.append(DATABASE.code.charAt((x >> 6) & 0x3f));
            encodedBuidler.append(DATABASE.code.charAt(x & 0x3f));

        }


        // replace encoded padding nulls with padding character
        encodedBuidler.delete(encodedBuidler.length() - paddingCount,
                            encodedBuidler.length());
        if (paddingCount == 1) {
            encodedBuidler.append(DATABASE.paddingChar);
        }
        else if (paddingCount == 2) {
            encodedBuidler.append(DATABASE.paddingChar);
            encodedBuidler.append(DATABASE.paddingChar);
        }


        encoded = encodedBuidler.toString();


        return encoded;
    }

    public static String decode(String string) throws Exception {
        byte[] decodedArray;
        int len;
        byte c1, c2, c3, c4;
        byte cd1, cd2, cd3;
        String decoded;
        String pad;


        // initialize
        decodedArray = new byte[string.getBytes().length];
        len = 0;


        // decode
        for (int i=0; i<string.length(); i+=4) {

            c1 = (byte)(DATABASE.code.indexOf(string.charAt(i)) & 0x3f);
            c2 = (byte)(DATABASE.code.indexOf(string.charAt(i+1)) & 0x3f);
            c3 = (byte)(DATABASE.code.indexOf(string.charAt(i+2)) & 0x3f);
            c4 = (byte)(DATABASE.code.indexOf(string.charAt(i+3)) & 0x3f);

            int x = (c1 << 18) + (c2 << 12) + (c3 << 6) + c4;

            cd1 = (byte)((x >> 16) & 0xff);
            cd2 = (byte)((x >> 8) & 0xff);
            cd3 = (byte)(x & 0xff);

            decodedArray[len++] = cd1;
            decodedArray[len++] = cd2;
            decodedArray[len++] = cd3;

        }


        // ignore the padding characters
        pad = new String(new char[] { DATABASE.paddingChar,
                                    DATABASE.paddingChar });
        if (string.endsWith(pad)) {
            len = len - 2;
        }
        else {
            pad = new String(new char[] { DATABASE.paddingChar });
            if (string.endsWith(pad)) {
                len = len - 1;
            }
        }
        decoded = new String(decodedArray, 0, len);


        return decoded;
    }





}


