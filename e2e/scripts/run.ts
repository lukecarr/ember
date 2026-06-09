import {dumpServerLogs, startServer, stopServer} from "@/harness/server.ts";

async function runBunTest(): Promise<number> {
    const proc = Bun.spawn(["bun", "test"], {stdio: ["inherit", "inherit", "inherit"]});
    await proc.exited;
    return proc.exitCode ?? 1;
}

let exitCode = 1;
try {
    await startServer();
    exitCode = await runBunTest();
} finally {
    if (exitCode !== 0) {
        await dumpServerLogs("compose-logs.txt");
    }
    await stopServer();
}

process.exit(exitCode);
