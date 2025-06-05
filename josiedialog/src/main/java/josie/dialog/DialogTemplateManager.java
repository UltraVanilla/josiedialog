package josie.dialog;

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogTemplateManager {
    private static final List<DialogTemplateManager> instances = new ArrayList<>();

    public static void reloadAll() {
        for (final var dialogTemplateManager : instances) {
            dialogTemplateManager.reload();
        }
    }

    private final Path configPath;
    private final V8Runtime v8Runtime;
    private final Map<String, DialogTemplate> templates = new HashMap<>();
    private final Gson gson = new Gson();

    public DialogTemplateManager(final Path configPath) {
        this.configPath = configPath;
        v8Runtime = V8RuntimeHolder.getV8Runtime();
        reload();
        instances.add(this);
    }

    public DialogTemplate getTemplate(final String name) {
        return templates.get(name);
    }

    public void reload() {
        try {
            for (final var entry : templates.entrySet()) {
                entry.getValue().close();
            }
            templates.clear();

            // don't worry this is literally how Node.js used to do commonjs
            // but there's maybe a better way to preserve line numbers? wait, is our js engine even giving line numbers?
            final var js = v8Runtime.getExecutor("(function() {\n"
                    + "function josieMinimessage(text) { return { type: 'josiedialog_minimessage', text }; }\n"
                    + "function josieForm(additions) { return { type: 'josiedialog_form', additions }; }\n"
                    + "const templates = {}; (function() { \n%s\n })(); return templates; })()"
                            .formatted(Files.readString(configPath)));

            try (final V8ValueObject jsTemplates = js.execute()) {
                for (final var templateName : jsTemplates.getOwnPropertyNames().batchGet()) {
                    final V8ValueObject templateObj = jsTemplates.get(templateName);
                    final var dialogTemplate = new DialogTemplate(v8Runtime, gson, templateObj);
                    templates.put(templateName.toString(), dialogTemplate);
                }
            }
        } catch (final Exception err) {
            throw new RuntimeException(err);
        }
    }
}
