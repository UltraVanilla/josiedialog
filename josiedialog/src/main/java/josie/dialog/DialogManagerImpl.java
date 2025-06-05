package josie.dialog;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import josie.dialog.api.*;
import org.jspecify.annotations.Nullable;

public class DialogManagerImpl implements DialogManager {
    private static final SecureRandom rand = new SecureRandom();

    private final DialogTemplateManager dialogTemplateManager;

    private final String namespace;

    private final Map<String, ParametizableFormLifecycleHandler> lifecycledFormHandlers = new HashMap<>();

    private final Map<String, ParametizableDialog> dialogs = new HashMap<>();

    private final Cache<UUID, Object> hiddenStateStore =
            CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.HOURS).build();

    public DialogManagerImpl(final String namespace, final Path path) {
        this.namespace = namespace;
        dialogTemplateManager = new DialogTemplateManager(path);

        registerHandler();
    }

    private void registerHandler() {
        final var platform = PlatformHolder.platform();

        platform.registerClickActionHandler((uuid, id, payload) -> {
            // TODO: rate limiting logic
            final var split = id.split(":");
            if (!split[0].equals(namespace)) return;

            final var formId = split[1];
            final var stateId = UUID.fromString(payload.get("stateId").getAsString());

            final var formHandler = this.lifecycledFormHandlers.get(formId);

            final var submissionDataType = formHandler.submissionDataType();

            final Object interpretedPayload;
            try {
                interpretedPayload = dialogTemplateManager.getTemplate(formId).interpret(payload, submissionDataType);
            } catch (final InterpretException e) {
                e.printStackTrace();
                return;
            }

            final var hiddenState = hiddenStateStore.getIfPresent(stateId);

            if (hiddenState == null && formHandler.hiddenStateType() != null) {
                try {
                    platform.sendDialog(uuid, InternalDialogs.expired());
                } catch (final SendDialogException e) {
                    e.printStackTrace();
                }
                return;
            }

            formHandler.handleSubmit(uuid, interpretedPayload, hiddenState);

            hiddenStateStore.invalidate(stateId);
        });
    }

    @Override
    public DialogManager registerDialog(final ParametizableDialog dialog) {
        dialogs.put(dialog.formId(), dialog);
        if (dialog instanceof final ParametizableFormLifecycleHandler form) {
            lifecycledFormHandlers.put(form.formId(), form);
        }
        return this;
    }

    @Override
    public void sendDialog(final UUID user, final String id, @Nullable final Object parameters)
            throws SendDialogException, RenderException {
        final var renderedDialog = renderDialog(id, parameters, null);

        PlatformHolder.platform().sendDialog(user, renderedDialog);
    }

    @Override
    public JsonElement renderDialog(final String id, @Nullable final Object parameters, @Nullable final UUID stateId)
            throws RenderException {
        final ParametizableDialog dialog = dialogs.get(id);

        if (parameters != null && !dialog.parametersType().isInstance(parameters)) {
            throw new IllegalArgumentException("Parameters type must match registered dialog specification");
        }

        final var dialogTemplate = dialogTemplateManager.getTemplate(id);

        return DialogPostprocessingUtils.postProcess(dialogTemplate.render(parameters), namespace + ":" + id, stateId);
    }

    @Override
    public void sendLifecycledForm(
            final UUID user, final String id, @Nullable final Object parameters, @Nullable final Object hiddenState)
            throws SendDialogException, RenderException {
        final ParametizableFormLifecycleHandler formHandler = lifecycledFormHandlers.get(id);

        if (hiddenState != null && !formHandler.hiddenStateType().isInstance(hiddenState)) {
            throw new IllegalArgumentException("Hidden state type must match registered form specification");
        }

        final var stateId = UUID.randomUUID();
        if (hiddenState != null) hiddenStateStore.put(stateId, hiddenState);

        final var renderedDialog = renderDialog(id, parameters, stateId);

        PlatformHolder.platform().sendDialog(user, renderedDialog);
    }
}
