import {createBot, type Bot} from "mineflayer";

const HOST = "127.0.0.1";
const PORT = 25565;

const DEFAULT_TIMEOUT_MS = 5_000;

export async function connect(username: string): Promise<Bot> {
    const bot = createBot({host: HOST, port: PORT, username, auth: "offline"});

    return new Promise<Bot>((resolveBot, rejectBot) => {
        const cleanup = () => {
            bot.removeListener("spawn", onSpawn);
            bot.removeListener("kicked", onKicked);
            bot.removeListener("error", onError);
        };
        const onSpawn = () => {
            cleanup();
            resolveBot(bot);
        };
        const onKicked = (reason: string) => {
            cleanup();
            rejectBot(new Error(`kicked while joining as ${username}: ${reason}`));
        };
        const onError = (err: Error) => {
            cleanup();
            rejectBot(err);
        };
        bot.once("spawn", onSpawn);
        bot.once("kicked", onKicked);
        bot.once("error", onError);
    });
}

export const opBot = (): Promise<Bot> => connect("EmberOp");
export const guestBot = (): Promise<Bot> => connect("EmberGuest");

export function waitForMessage(
    bot: Bot,
    predicate: (text: string) => boolean,
    timeoutMs: number = DEFAULT_TIMEOUT_MS,
): Promise<string> {
    return new Promise((resolveMsg, rejectMsg) => {
        const timer = setTimeout(() => {
            bot.removeListener("messagestr", onMessage);
            rejectMsg(new Error(`no matching message within ${timeoutMs}ms`));
        }, timeoutMs);

        const onMessage = (text: string) => {
            if (!predicate(text)) return;
            clearTimeout(timer);
            bot.removeListener("messagestr", onMessage);
            resolveMsg(text);
        };
        bot.on("messagestr", onMessage);
    });
}
