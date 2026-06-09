import {afterEach, beforeEach, describe, expect, it} from "bun:test";
import type {Bot} from "mineflayer";
import {opBot, waitForMessage} from "@/harness/bot.ts";

const EXPECTED_VERSION = process.env.EMBER_VERSION;
if (!EXPECTED_VERSION) {
    throw new Error("EMBER_VERSION env var is required (set by Gradle :plugin:e2eTest task)");
}

describe("/ember version", () => {
    let bot: Bot;

    beforeEach(async () => {
        bot = await opBot();
    });

    afterEach(() => {
        bot.quit();
    });

    it("returns the running plugin version to an op", async () => {
        const message = waitForMessage(bot, (text) => text.includes(EXPECTED_VERSION));
        bot.chat("/ember version");
        expect(await message).toContain(EXPECTED_VERSION);
    });
});
