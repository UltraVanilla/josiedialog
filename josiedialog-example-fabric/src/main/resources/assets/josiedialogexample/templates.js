// the typescript definitions are not written yet
// /// <reference no-default-lib="true" />
// /// <reference lib="es2022" />
// /// <reference types="./definitions.d.ts" />

templates.rules = {
    render() {
        return {
            type: "minecraft:notice",
            title: "Rules",
        };
    }
}

templates.report = {
    render(params) {
        return {
            type: "minecraft:confirmation",
            title: {
                josiedialog_minimessage: "Report rule-breaking behavior to <#f5c144><bold>Staging</bold><#f4e8d1>Serv"
            },
            body: [
                {
                    type: "minecraft:item",
                    item: {
                        id: "minecraft:mace",
                        count: 1
                    },
                    description: {
                        contents: {
                            text: "Use this form to report players who are violating server rules."
                        },
                        width: 275
                    },
                    show_decorations: false,
                    show_tooltip: false,
                    width: 20,
                    height: 20
                },
                {
                    type: "minecraft:plain_message",
                    contents: {
                        josiedialog_minimessage: "This report does not go to Mojang, but to the moderators of <#f5c144><bold>Staging</bold></#f5c144><#f4e8d1>Serv</#f4e8d1>. If you wish to report a skin or username to Mojang too, use the player reporting menu after reporting to us first.",
                    },
                    width: 275
                }
            ],
            inputs: [
                {
                    type: "minecraft:single_option",
                    key: "reason",
                    width: 275,
                    label: "Reason",
                    options: [
                        { id: "other", display: "Other", initial: true },
                        { id: "griefing", display: "Griefing" },
                        { id: "stealing", display: "Stealing" },
                        { id: "spam", display: "Spam or flooding" },
                        { id: "non_consentual_pvp", display: "Non-consentual PVP" },
                        { id: "alt_account", display: "Alt accounts used for gameplay benefit" },
                        { id: "exploits", display: "Exploits" },
                        { id: "hacking", display: "Hacking" },
                        { id: "nsfw", display: "Graphic or gross NSFW" },
                        { id: "harassment", display: "Bullying or harassment" },
                        { id: "bigotry", display: "Bigotry or hate speech" },
                        { id: "warns", display: "Not listening to staff warns" }
                    ]
                },
                {
                    type: "minecraft:boolean",
                    key: "unknown_perpetrator",
                    label: "Perpetrator: [Unknown]",
                    width: 275
                },
                ...Object.entries(params.recentPlayers).map(([uuid, name]) => ({
                    type: "minecraft:boolean",
                    key: `perpetrator_${uuid.replaceAll("-", "_")}`,
                    label: `Perpetrator: ${name}`,
                    width: 275
                })),
                {
                    type: "minecraft:text",
                    key: "otherAccounts",
                    width: 275,
                    label: {
                        josiedialog_minimessage: "Other account names and Discord IDs <gray><italic>(optional)"
                    },
                },
                {
                    type: "minecraft:text",
                    key: "details",
                    width: 275,
                    label: {
                        josiedialog_minimessage: "Details <gray><italic>(markdown) (optional)"
                    },
                    max_length: 8192,
                    multiline: {
                        height: 80,
                        max_lines: 30
                    }
                },
                {
                    type: "minecraft:boolean",
                    key: "reportMyLocation",
                    label: `Report my current location (${params.x}, ${params.z}) as a location of interest`,
                    width: 275
                }
            ],
            yes: {
                label: "Submit Report",
                tooltip: {
                    text: "Send report to server admins"
                },
                action: {
                    type: "josiedialog_form",
                },
            },
            no: {
                label: "Cancel",
            },
        };
    },
    interpret(formSubmission) {
        const perpetrators = Object.entries(formSubmission)
            .filter(([key, isSelected]) => isSelected === "true" && key.startsWith("perpetrator_"))
            .map(([key, _]) => key.slice(12).replaceAll("_", "-"));

        return {
            reason: formSubmission.reason,
            perpetrators,
            otherAccounts: formSubmission.otherAccounts,
            details: formSubmission.details,
            reportMyLocation: formSubmission.reportMyLocation,
            action: formSubmission.action
        };
    },
};
