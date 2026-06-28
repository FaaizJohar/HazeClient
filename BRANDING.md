# Haze Client - Brand & Design System

This document outlines the core branding, UI/UX tokens, and design language for Haze Client. Use this guide as a single source of truth when creating new websites, applications, or companion projects under the Haze brand to ensure a consistent, premium user experience.

## 1. Core Principles
- **Modern & Stealthy:** Haze employs a deep dark mode by default. The design should feel sleek, responsive, and uncluttered.
- **Micro-Animations:** Use subtle, fluid animations (fade, scale-in, slide-up) to make the UI feel alive and highly responsive.
- **Vibrant Accents:** The core brand uses striking violet/blue accents against the deep black canvas to draw the user's attention to primary actions.

## 2. Color Palette

The Haze theme uses a combination of deep blacks, slate greys, and vibrant violet/blue accents.

### Background & Surfaces
- **Background Primary:** `#0D0D0D` — Used for the main app background.
- **Surface / Cards:** `#1A1A1A` — Used for modals, cards, floating menus, and elevated elements.

### Brand Accents
- **Primary (Violet/Blue):** `#6B32EC` (Web) / `#5B6EE1` (In-Game) — Used for primary buttons, active states, and highlights.
- **Secondary:** `#4D23A9` (Web) / `#36454F` (In-Game) — Used for secondary actions, subtle borders, or hover states on primary elements.
- **Accent (Light Violet):** `#8B5CF6` (Web) / `#8892F1` (In-Game) — Used for gradients, active icons, or glowing effects.

### Status Colors
- **Success:** `#10B981`
- **Info:** `#3B82F6`
- **Warning:** `#F59E0B`
- **Error:** `#EF4444`

### Typography Colors
- **Text Primary:** `#FFFFFF` — For headings and primary content.
- **Text Secondary:** `#B3B3B3` — For descriptions, subtitles, and standard paragraphs.
- **Text Muted:** `#666666` — For disabled states, placeholders, and watermarks.

## 3. Typography

The Haze brand relies on modern, clean sans-serif fonts to maintain legibility and a premium feel.

- **Display & Headings:** `"Sora", "Inter", sans-serif`
  *Use for hero text, large titles, and prominent section headers.*
- **Base / Body Text:** `"Inter", "General Sans", "Segoe UI", sans-serif`
  *Use for all paragraph text, buttons, inputs, and general UI elements.*

## 4. Animation & Motion (Web/UI)

Transitions should be fluid and use custom easing.

### Timing & Easing
- **Ease Out:** `ease-out` (Used for fading)
- **Spring / Smooth:** `cubic-bezier(0.16, 1, 0.3, 1)` (Used for sliding and scaling)

### Keyframes
- **Fade In:** Duration `0.3s`, Easing `ease-out`
- **Slide Up:** Duration `0.4s`, Transform `translateY(10px) -> 0`, Easing `cubic-bezier(0.16, 1, 0.3, 1)`
- **Scale In:** Duration `0.4s`, Transform `scale(0.95) -> 1`, Easing `cubic-bezier(0.16, 1, 0.3, 1)`

## 5. UI Components

- **Cards:** Background `#1A1A1A`, no visible border, subtle drop shadow for depth.
- **Buttons (Primary):** Background `#6B32EC`, text `#FFFFFF`. Hover state should brighten slightly to `#8B5CF6`.
- **Inputs:** Background slightly lighter than surface (e.g., `rgba(255,255,255,0.05)`), text `#FFFFFF`, border-bottom on focus with Primary color.
- **Switches/Toggles:** Inset style, active color `#6B32EC`.

## 6. Implementation References
- **Web/Vue:** Vuetify configuration uses custom aliases for the Haze theme. Refer to `uno.config.ts` and `vuetify.ts`.
- **Java (In-Game):** Refer to `Theme.java` for the core UI rendering palette implementation.
