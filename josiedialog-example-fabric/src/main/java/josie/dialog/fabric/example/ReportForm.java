package josie.dialog.fabric.example;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import josie.dialog.api.FormLifecycleHandler;
import org.jspecify.annotations.Nullable;

public class ReportForm implements FormLifecycleHandler {
    public record Parameters(Map<UUID, String> recentPlayers, int x, int z) {}

    public record HiddenState(net.minecraft.world.phys.Vec3 position, net.minecraft.world.phys.Vec2 rotation) {}

    public record SubmissionData(
            String reason, List<UUID> perpetrators, String otherAccounts, String details, boolean reportMyLocation) {}

    @Override
    public Class<?> parametersType() {
        return Parameters.class;
    }

    @Override
    public Class<?> hiddenStateType() {
        return HiddenState.class;
    }

    @Override
    public Class<?> submissionDataType() {
        return SubmissionData.class;
    }

    @Override
    public String formId() {
        return "report";
    }

    @Override
    public void handleSubmit(final UUID uuid, final Object dynSubmissionData, @Nullable final Object dynHiddenState) {
        // TODO: is there any way to make this dynamic dispatch situation less dynamic dispatch-y? probably not
        final var hiddenState = (HiddenState) dynHiddenState;
        final var submissionData = (SubmissionData) dynSubmissionData;

        System.out.println("UUID: " + uuid);
        System.out.println("HiddenState: " + hiddenState);
        System.out.println("SubmissionData: " + submissionData);
    }
}
