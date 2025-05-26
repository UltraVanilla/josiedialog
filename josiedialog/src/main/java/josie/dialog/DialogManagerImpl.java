package josie.dialog;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import josie.blockgamekeyvalue.BlockGameKeyValue;
import josie.blockgamekeyvalue.exceptions.ParseException;
import josie.dialog.api.DialogManager;
import josie.dialog.api.FormHandler;
import josie.dialog.api.PlatformHolder;
import org.jspecify.annotations.Nullable;

public class DialogManagerImpl implements DialogManager {
    private static final SecureRandom rand = new SecureRandom();

    private final DialogTemplateManager dialogTemplateManager;

    private final Map<String, FormHandler> formHandlers = new HashMap<>();

    private final Cache<UUID, Object> hiddenStateStore =
            CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.HOURS).build();

    public DialogManagerImpl(final Path path) {
        dialogTemplateManager = new DialogTemplateManager(path);

        final var platform = PlatformHolder.platform();

        platform.registerClickActionHandler((uuid, id, payload) -> {
            // TODO: maybe shunt this off to a worker thread?
            // TODO: rate limiting logic

            if (!id.startsWith("josie:")) return;

            final var split = id.split(":|___");

            final var formId = split[1];
            final var stateId = UUID.fromString(split[2]);

            final var formHandler = this.formHandlers.get(formId);

            final Map<String, String> parsedPayload;
            try {
                parsedPayload = BlockGameKeyValue.parse(payload);
            } catch (final ParseException err) {
                return;
            }

            final var submissionDataType = formHandler.submissionDataType();

            // TODO: handle errors
            final var interpretedPayload =
                    dialogTemplateManager.getTemplate(formId).interpret(parsedPayload, submissionDataType);

            final var hiddenState = hiddenStateStore.getIfPresent(stateId);

            if (hiddenState == null && formHandler.hiddenStateType() != null) {
                platform.sendDialog(uuid, InternalDialogs.expired());
            }

            formHandler.handleSubmit(uuid, interpretedPayload, hiddenState);

            hiddenStateStore.invalidate(stateId);
        });
    }

    @Override
    public void registerForm(final FormHandler form) {
        formHandlers.put(form.formId(), form);
    }

    @Override
    public void sendForm(
            final UUID user, final String id, @Nullable final Object parameters, @Nullable final Object hiddenState) {
        final FormHandler formHandler = formHandlers.get(id);

        if (hiddenState != null && !formHandler.hiddenStateType().isInstance(hiddenState)) {
            throw new IllegalArgumentException("Hidden state type must match registered form specification");
        }

        if (parameters != null && !formHandler.parametersType().isInstance(parameters)) {
            throw new IllegalArgumentException("Parameters type must match registered form specification");
        }

        final var dialogTemplate = dialogTemplateManager.getTemplate(id);

        final var stateId = UUID.randomUUID();

        if (hiddenState != null) hiddenStateStore.put(stateId, hiddenState);

        final var formId = "josie:" + id + "___" + stateId;

        // TODO: handle errors
        final var renderedDialog = dialogTemplate.render(formId, parameters);

        PlatformHolder.platform().sendDialog(user, renderedDialog);
    }
}
