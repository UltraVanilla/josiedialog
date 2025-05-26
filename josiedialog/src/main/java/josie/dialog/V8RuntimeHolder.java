package josie.dialog;

import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import org.jspecify.annotations.Nullable;

public class V8RuntimeHolder {
    @Nullable
    private static V8Runtime v8Runtime;

    static V8Runtime getV8Runtime() {
        if (v8Runtime == null) {
            try {
                v8Runtime = V8Host.getV8Instance().createV8Runtime();
            } catch (final Exception err) {
                throw new RuntimeException(err);
            }
        }
        return v8Runtime;
    }
}
