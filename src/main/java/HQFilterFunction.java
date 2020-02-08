import org.apache.asterix.external.api.IExternalScalarFunction;
import org.apache.asterix.external.api.IFunctionHelper;
import org.apache.asterix.external.library.java.JBuiltinType;
import org.apache.asterix.external.library.java.base.JInt;
import org.apache.asterix.external.library.java.base.JLong;
import org.apache.asterix.external.library.java.base.JRecord;
import org.apache.asterix.external.library.java.base.JString;
import org.apache.asterix.external.library.java.base.JUnorderedList;

public class HQFilterFunction implements IExternalScalarFunction {

    private JUnorderedList list = null;

    private int k;
    private int m;
    private int prefixLength;

    @Override
    public void evaluate(IFunctionHelper functionHelper) throws Exception {
        list.clear();
        JRecord inputRecord = (JRecord) functionHelper.getArgument(0);
        m = 1000;
        k = 4;
        // prefixLength = ((JInt) functionHelper.getArgument(3)).getValue();

        JString text = (JString) inputRecord.getValueByName("keywords");

        int state = ((JInt) inputRecord.getValueByName("state")).getValue();

        getIndicies((long) state);

        inputRecord.addField("hqfilter_indices", list);
        functionHelper.setResult(inputRecord);

        /*JRecord inputRecord = (JRecord) functionHelper.getArgument(0);
        JString text = (JString) inputRecord.getValueByName(Datatypes.Tweet.MESSAGE);
        
        String[] tokens = text.getValue().split(" ");
        for (String tk : tokens) {
            if (tk.startsWith("#")) {
                JString newField = (JString) functionHelper.getObject(JTypeTag.STRING);
                newField.setValue(tk);
                list.add(newField);
            }
        }
        inputRecord.addField("topics", list);
        functionHelper.setResult(inputRecord);*/
    }

    @Override
    public void initialize(IFunctionHelper functionHelper) throws Exception {
        list = new JUnorderedList(JBuiltinType.JLONG);
    }

    @Override
    public void deinitialize() {

    }

    public void getIndicies(long element) {

        int hash1 = MurmurHash3_32_Long(element, 0);
        int hash2 = MurmurHash3_32_Long(element, 1);

        long index;
        for (int i = 0; i < k; i++) {
            index = Math.abs(((long) hash1 + (long) i * (long) hash2) % m);
            JLong in = new JLong(index);
            list.add(in);
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
