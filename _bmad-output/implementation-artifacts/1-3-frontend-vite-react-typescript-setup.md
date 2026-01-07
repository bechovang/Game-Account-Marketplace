# Story 1.3: Frontend Vite + React + TypeScript Setup

Status: review

## Story

As a developer,
I want to initialize a React frontend with Vite, TypeScript, and Tailwind CSS,
so that we have a modern, type-safe frontend foundation.

## Acceptance Criteria

1. **Given** the monorepo structure from Story 1.1
**When** I initialize the Vite + React + TypeScript project
**Then** `package.json` includes React 18.x, React Router DOM 6.x, TypeScript 5.x
**And** dependencies include: @apollo/client 3.x, graphql 16.x, axios 1.x, sockjs-client, @stomp/stompjs
**And** dev dependencies include: Vite 5.x, Tailwind CSS 3.x, autoprefixer, postcss
**And** `vite.config.ts` has proxy setup for backend (port 8080)
**And** `tsconfig.json` is configured for strict mode
**And** `tailwind.config.js` is configured with content paths
**And** project runs successfully with `npm run dev` on port 3000

## Tasks / Subtasks

- [x] Initialize Vite + React + TypeScript project (AC: #)
  - [x] Run `npm create vite@latest frontend-react -- --template react-ts`
  - [x] Create in frontend-react/ directory
- [x] Install core dependencies (AC: #)
  - [x] Install React Router DOM 6.x
  - [x] Install @apollo/client 3.x and graphql 16.x
  - [x] Install axios 1.x
  - [x] Install sockjs-client and @stomp/stompjs
- [x] Install dev dependencies (AC: #)
  - [x] Install Tailwind CSS 3.x
  - [x] Install autoprefixer and postcss
  - [x] Install additional libs: react-hook-form, yup, @hookform/resolvers
- [x] Configure Vite (AC: #)
  - [x] Create vite.config.ts with proxy to port 8080
  - [x] Configure path aliases (@/ for src/)
- [x] Configure TypeScript (AC: #)
  - [x] Set tsconfig.json to strict mode
  - [x] Configure path mapping for @/ alias
- [x] Configure Tailwind (AC: #)
  - [x] Create tailwind.config.js
  - [x] Create postcss.config.js
  - [x] Add content paths for all files
- [x] Create folder structure (AC: #)
  - [x] Create src/components/, src/pages/, src/services/, src/hooks/, src/contexts/, src/types/
- [x] Setup and verify (AC: #)
  - [x] Run `npm install`
  - [x] Run `npm run dev` successfully
  - [x] Verify app runs on port 3000
  - [x] Verify proxy to backend works

## Dev Notes

**Location:** `frontend-react/` directory created in Story 1.1

### Project Structure Notes

```
frontend-react/
├── package.json                    (CREATE)
├── vite.config.ts                  (CREATE)
├── tsconfig.json                   (CREATE)
├── tsconfig.node.json              (CREATE)
├── tailwind.config.js              (CREATE)
├── postcss.config.js               (CREATE)
├── index.html                      (VITE CREATES)
└── src/
    ├── main.tsx                    (VITE CREATES - MODIFY)
    ├── App.tsx                     (VITE CREATES - MODIFY)
    ├── vite-env.d.ts               (VITE CREATES)
    ├── assets/                     (CREATE DIR)
    ├── components/                 (CREATE DIR)
    │   ├── common/                 (CREATE DIR)
    │   ├── layout/                 (CREATE DIR)
    │   └── features/               (CREATE DIR)
    ├── pages/                      (CREATE DIR)
    ├── services/                   (CREATE DIR)
    │   ├── graphql/                (CREATE DIR)
    │   ├── rest/                   (CREATE DIR)
    │   └── websocket/              (CREATE DIR)
    ├── contexts/                   (CREATE DIR)
    ├── hooks/                      (CREATE DIR)
    ├── types/                      (CREATE DIR)
    ├── utils/                      (CREATE DIR)
    └── styles/                     (CREATE DIR)
        └── index.css               (CREATE)
```

### package.json Template [Source: ARCHITECTURE.md#4.2.1]

```json
{
  "name": "gameaccount-marketplace-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "type-check": "tsc --noEmit"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.21.1",
    "@apollo/client": "^3.8.10",
    "graphql": "^16.8.1",
    "axios": "^1.6.5",
    "sockjs-client": "^1.6.1",
    "@stomp/stompjs": "^7.0.0",
    "react-hook-form": "^7.49.2",
    "yup": "^1.3.3",
    "@hookform/resolvers": "^3.3.4",
    "react-hot-toast": "^2.4.1",
    "zustand": "^4.4.7",
    "date-fns": "^3.0.6"
  },
  "devDependencies": {
    "@types/react": "^18.2.43",
    "@types/react-dom": "^18.2.17",
    "@types/sockjs-client": "^1.5.3",
    "@typescript-eslint/eslint-plugin": "^6.14.0",
    "@typescript-eslint/parser": "^6.14.0",
    "@vitejs/plugin-react": "^4.2.1",
    "autoprefixer": "^10.4.16",
    "eslint": "^8.55.0",
    "eslint-plugin-react-hooks": "^4.6.0",
    "eslint-plugin-react-refresh": "^0.4.5",
    "postcss": "^8.4.32",
    "tailwindcss": "^3.4.0",
    "typescript": "^5.3.3",
    "vite": "^5.0.8"
  }
}
```

### vite.config.ts Template [Source: ARCHITECTURE.md]

```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/graphql': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true,
      },
    },
  },
})
```

### tsconfig.json Template

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

### tailwind.config.js Template

```javascript
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

### postcss.config.js Template

```javascript
export default {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
  },
}
```

### src/styles/index.css Template

```css
@tailwind base;
@tailwind components;
@tailwind utilities;

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#root {
  font-family: Inter, system-ui, Avenir, Helvetica, Arial, sans-serif;
}
```

### src/main.tsx Modifications

```typescript
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './styles/index.css'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
```

### Common Pitfalls to Avoid

🚨 **CRITICAL WARNINGS:**
1. **Wrong Vite version** - MUST use Vite 5.x for React 18 + TypeScript 5 compatibility
2. **Missing @ path alias** - Will cause import errors everywhere
3. **Proxy not configured** - Frontend won't reach backend API during development
4. **Tailwind content paths** - Must include all paths where components exist or styles won't apply
5. **TypeScript strict mode** - Must be enabled for type safety (this is good, not a bug)
6. **WebSocket proxy** - Needs ws: true flag for STOMP protocol to work

### Testing Standards

```bash
cd frontend-react
npm install
npm run dev
# Should see: VITE ready in xxx ms at http://localhost:3000
# Test proxy: http://localhost:3000/api/xxx should proxy to backend
```

### Requirements Traceability

**NFR33:** Frontend responsive ✅ Tailwind CSS provides mobile-first design
**NFR50:** Code splitting ✅ Vite automatically code-splits by route

### Next Story Dependencies

Story 1.8 (Frontend Auth Pages) - Depends on this skeleton + Story 1.7 REST API

### References

- Architecture.md Section 4.1: Frontend Architecture Diagram
- Architecture.md Section 4.2: Key Frontend Files

---

## Dev Agent Record

### Agent Model Used
Claude Opus 4.5

### Completion Notes List
Story 1.3 completed successfully on 2026-01-07.

**Completed Tasks:**
1. Initialized Vite React TypeScript project
2. Installed all core dependencies (React Router, Apollo Client, GraphQL, Axios, WebSocket, React Hook Form, etc.)
3. Installed dev dependencies (Tailwind CSS, PostCSS, Autoprefixer, ESLint, TypeScript)
4. Configured Vite with proxy to backend (port 8080) and path aliases
5. Configured TypeScript with strict mode and path mapping
6. Configured Tailwind CSS with PostCSS
7. Created complete folder structure (components, pages, services, hooks, contexts, types, utils, assets)
8. Verified setup with npm install and npm run dev

**Environment Notes:**
- Vite 5.4.21 (newer than specified 5.0.8, but compatible)
- React 18.2.0
- TypeScript 5.3.3
- Dev server running successfully on port 3000
- Proxy configured for /api, /graphql, and /ws endpoints

**All acceptance criteria met.**

### File List

Files created/modified in this story:
- `frontend-react/package.json` (CREATED - updated with all dependencies)
- `frontend-react/vite.config.ts` (CREATED - with proxy and path aliases)
- `frontend-react/tsconfig.json` (VITE CREATED - kept as is)
- `frontend-react/tsconfig.app.json` (MODIFIED - added path aliases)
- `frontend-react/tailwind.config.js` (CREATED)
- `frontend-react/postcss.config.js` (CREATED)
- `frontend-react/src/styles/index.css` (CREATED)
- `frontend-react/src/main.tsx` (MODIFIED - updated CSS import)
- Directory structure created:
  - `src/components/common/`
  - `src/components/layout/`
  - `src/components/features/`
  - `src/pages/`
  - `src/services/graphql/`
  - `src/services/rest/`
  - `src/services/websocket/`
  - `src/contexts/`
  - `src/hooks/`
  - `src/types/`
  - `src/utils/`
  - `src/assets/`
