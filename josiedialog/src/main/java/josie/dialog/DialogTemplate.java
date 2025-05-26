package josie.dialog;

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInJson;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;
import java.util.Map;
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

    public JsonElement render(final String id, @Nullable final Object params) {
        // maybe some day we can get Gson support in javet
        try (final V8ValueObject parsed = globalJson.invoke("parse", gson.toJson(params))) {
            try (final V8ValueString idString = v8Runtime.createV8ValueString(id)) {
                try (final V8ValueObject result = templateObj.invoke("render", idString, parsed)) {
                    return JsonParser.parseString(result.toJsonString());
                }
            }
        } catch (final Exception err) {
            throw new RuntimeException(err);
        }
    }

    public Object interpret(final Map<String, String> params, final Type type) {
        try (final V8ValueObject parsed = globalJson.invoke("parse", gson.toJson(params))) {
            try (final V8ValueObject result = templateObj.invoke("interpret", parsed)) {
                return gson.fromJson(result.toJsonString(), type);
            }
        } catch (final Exception err) {
            throw new RuntimeException(err);
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
