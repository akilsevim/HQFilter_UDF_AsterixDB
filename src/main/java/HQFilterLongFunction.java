import org.apache.asterix.external.api.IExternalScalarFunction;
import org.apache.asterix.external.api.IFunctionHelper;
import org.apache.asterix.external.library.java.JBuiltinType;
import org.apache.asterix.external.library.java.base.JInt;
import org.apache.asterix.external.library.java.base.JUnorderedList;

public class HQFilterLongFunction implements IExternalScalarFunction {

    private JUnorderedList list = null;
    private int k;
    private int m;

    @Override
    public void deinitialize() {
        // nothing to do here
    }

    @Override
    public void evaluate(IFunctionHelper functionHelper) throws Exception {

        list.clear();
        int state = ((JInt) functionHelper.getArgument(0)).getValue();
        m = ((JInt) functionHelper.getArgument(1)).getValue();
        k = ((JInt) functionHelper.getArgument(2)).getValue();

        getIndices((long) state);
        /*int[] test = new int[3];
        test[0] = 1;
        test[1] = 2;
        test[2] = 3;
        
        JInt a = new JInt(1);
        JInt b = new JInt(2);
        
        list.add(a);
        list.add(b);
        
        JRecord result = (JRecord) functionHelper.getResultObject();
        result.addField("indices", list);*/
        functionHelper.setResult(list);
    }

    @Override
    public void initialize(IFunctionHelper functionHelper) {
        list = new JUnorderedList(JBuiltinType.JINT);

    }

    public void getIndices(long element) {

        int hash1 = MurmurHash3_32_Long(element, 0);
        int hash2 = MurmurHash3_32_Long(element, 1);

        long index;
        for (int i = 0; i < k; i++) {
            index = Math.abs(((long) hash1 + (long) i * (long) hash2) % m);
            list.add(new JInt((int) index));
        }
    }

    private int MurmurHash3_32_Long(long key, int seed) {
        int low = (int) key;
        int high = (int) (key >>> 32);

        //Constants to calculate MurmurHash3_32_Long
        int c1 = 0xcc9e2d51;
        int c2 = 0x1b873593;

        int k1 = low * c1;
        k1 = Integer.rotateLeft(k1, 15);
        k1 *= c2;

        int h1 = seed ^ k1;
        h1 = Integer.rotateLeft(h1, 13);
        h1 = (h1 * 5) + 0xe6546b64;

        k1 = high * c1;
        k1 = Integer.rotateLeft(k1, 15);
        k1 *= c2;

        h1 ^= k1;
        h1 = Integer.rotateLeft(h1, 13);
        h1 = (h1 * 5) + 0xe6546b64;

        //For long type (8 byte)
        h1 ^= 8;

        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;

        return h1;
    }

}
