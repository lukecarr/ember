import {$} from "bun";

const E2E_ROOT = `${import.meta.dir}/../..`;

export async function startServer(): Promise<void> {
    await $`docker compose up -d --wait`.cwd(E2E_ROOT);
}

export async function stopServer(): Promise<void> {
    await $`docker compose down -v`.cwd(E2E_ROOT).nothrow();
}

export async function dumpServerLogs(destination: string): Promise<void> {
    await $`docker compose logs --no-color paper > ${destination}`.cwd(E2E_ROOT).nothrow();
}
