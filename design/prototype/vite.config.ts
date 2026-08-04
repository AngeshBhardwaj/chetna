import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "node:path";

// Prototype lives in design/prototype/ but reads durable tokens and assets
// from its sibling design/tokens/ and design/assets/ (see ADR-0013) — allow
// Vite's dev server to serve files from the design/ parent, not just this
// package's own root.
export default defineConfig({
  plugins: [react()],
  server: {
    fs: {
      allow: [path.resolve(import.meta.dirname, "..")],
    },
    // Dockerized (compose.yaml) — requests can arrive with Host: design-prototype
    // (the compose service name), which Vite's default host-header check rejects.
    // Throwaway dev-only prototype, not internet-facing, so disabling it outright
    // is fine rather than enumerating every hostname it might be reached as.
    allowedHosts: true,
  },
});
