import org.apache.asterix.external.api.IExternalScalarFunction;
import org.apache.asterix.external.api.IFunctionFactory;

public class HQFilterLongFactory implements IFunctionFactory {

    @Override
    public IExternalScalarFunction getExternalFunction() {
        return new HQFilterLongFunction();
    }

}
