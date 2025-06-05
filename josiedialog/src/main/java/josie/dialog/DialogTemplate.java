package josie.dialog;

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInJson;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;
import josie.dialog.api.InterpretException;
import josie.dialog.api.RenderException;
import org.jspecify.annotations.Nullable;

public class DialogTemplate implements AutoCloseable {
    private final V8ValueObject templateObj;
    private final V8ValueBuiltInJson globalJson;
    private final V8Runtime v8Runtime;
    private final Gson gson;

    public DialogTemplate(final V8Runtime v8Runtime, final Gson gson, final V8ValueObject templateObj) {
        this.gson = gson;
        this.templateObj = templateObj;
        this.v8Runtime = v8Runtime;
        try {
            this.globalJson = v8Runtime.getGlobalObject().getBuiltInJson();
        } catch (final Exception err) {
            throw new RuntimeException(err);
        }
    }

    public JsonElement render(@Nullable final Object params) throws RenderException {
        V8ValueObject parsedParams = null;
        try {
            // maybe some day we can get Gson support in javet
            if (params != null) {
                parsedParams = globalJson.invoke("parse", gson.toJson(params));
            }

            try (final V8ValueObject result = templateObj.invoke("render", parsedParams)) {
                if (parsedParams != null) parsedParams.close();
                return JsonParser.parseString(result.toJsonString());
            }
        } catch (final Exception err) {
            throw new RenderException(err);
        }
    }

    public Object interpret(final JsonElement params, final Type type) throws InterpretException {
        try (final V8ValueObject parsed = globalJson.invoke("parse", gson.toJson(params))) {
            try (final V8ValueObject result = templateObj.invoke("interpret", parsed)) {
                return gson.fromJson(result.toJsonString(), type);
            }
        } catch (final Exception err) {
            throw new InterpretException(err);
        }
    }

    public void close() {
        try {
            globalJson.close();
            templateObj.close();
        } catch (final Exception err) {
            throw new RuntimeException(err);
        }
    }
}
