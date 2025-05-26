// the typescript definitions are not written yet
// /// <reference no-default-lib="true" />
// /// <reference lib="es2022" />
// /// <reference types="./definitions.d.ts" />

templates.report = {
    render(id, params) {
        return {
            type: "minecraft:simple_input_form",
            title: [
                {
                    text: "Report rule-breaking behavior to "
                },
                {
                    text: "Staging",
                    color: "#f5c144",
                    bold: true
                },
                {
                    text: "Serv",
                    color: "#f4e8d1"
                }
            ],
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
                    contents: [
                        {
                            text: "This report does not go to Mojang, but to the moderators of ",
                            italic: true
                        },
                        {
                            text: "Staging",
                            color: "#f5c144",
                            italic: true,
                            bold: true
                        },
                        {
                            text: "Serv",
                            color: "#f4e8d1",
                            italic: true
                        },
                        {
                            text: ". If you wish to report a skin or username to Mojang too, use the player reporting menu after reporting to us first.",
                            italic: true
                        }
                    ],
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
                    key: "other_accounts",
                    width: 275,
                    label: [
                        { text: "Other account names and Discord IDs " },
                        { text: "(optional)", color: "gray", italic: true }
                    ],
                    initial: ""
                },
                {
                    type: "minecraft:text",
                    key: "details",
                    width: 275,
                    label: [
                        { text: "Details " },
                        { text: "(markdown) (optional)", color: "gray", italic: true }
                    ],
                    initial: "",

                    max_length: 8192,
                    multiline: {
                        height: 80,
                        max_lines: 30
                    }
                },
                {
                    type: "minecraft:boolean",
                    key: "report_my_location",
                    label: `Report my current location (${params.x}, ${params.z}) as a location of interest`,
                    width: 275
                }
            ],
            action: {
                label: "Submit Report",
                tooltip: {
                    text: "Send report to server admins"
                },
                id: "submit_report",
                on_submit: {
                    id: id,
                    type: "custom_form"
                }
            }
        };
    },
    interpret(formSubmission) {
        const perpetrators = Object.entries(formSubmission)
            .filter(([key, isSelected]) => isSelected === "true" && key.startsWith("perpetrator_"))
            .map(([key, _]) => key.slice(12).replaceAll("_", "-"));

        return {
            reason: formSubmission.reason,
            perpetrators,
            otherAccounts: formSubmission.other_accounts,
            details: formSubmission.details,
            reportMyLocation: formSubmission.report_my_location === "true",
        };
    },
};
