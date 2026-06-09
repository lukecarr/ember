import {afterEach, beforeEach, describe, expect, it} from "bun:test";
import type {Bot} from "mineflayer";
import {guestBot, waitForMessage} from "@/harness/bot.ts";

const EXPECTED_VERSION = process.env.EMBER_VERSION;
if (!EXPECTED_VERSION) {
    throw new Error("EMBER_VERSION env var is required (set by Gradle :plugin:e2eTest task)");
}

describe("/ember version permission gate", () => {
    let bot: Bot;

    beforeEach(async () => {
        bot = await guestBot();
    });

    afterEach(() => {
        bot.quit();
    });

    it("does not reveal the version to a non-op", async () => {
        // Brigadier `.requires` makes the subcommand invisible, so the parser
        // reports "Unknown or incomplete command" rather than a permission denial.
        const message = waitForMessage(bot, (text) => /unknown/i.test(text));
        bot.chat("/ember version");
        const response = await message;
        expect(response).not.toContain(EXPECTED_VERSION);
    });
});
