# Product

<!-- impeccable:product-schema 1 -->

## Platform

adaptive

## Users
Household or couple sharing one spending log. They open the app to record an expense as it happens, then check monthly totals and category breakdowns together.

## Product Purpose
FinLog is a personal finance tracker for logging expenses manually or by scanning a receipt, then seeing spending by category, payment method, and month. Success means an expense takes seconds to log and the totals are trustworthy at a glance.

## Positioning
Fully offline and local-first: all data lives on the device in SQLDelight, with no accounts, backend, or network access. Receipt scanning runs on-device (ML Kit on Android, Vision on iOS).

## Operating Context
- Logging happens in the moment, often one-handed, from a phone.
- Data is shared across two people in one household, so the log must read clearly to either of them.
- Export to CSV from Settings for outside tools.

## Capabilities and Constraints
- Manual expense entry and receipt scanning with line items.
- Custom categories (name, icon, color) and payment methods.
- Monthly totals and insights.
- CSV export of all expenses.
- Built with Kotlin Multiplatform and Compose Multiplatform; shared UI in `composeApp/src/commonMain`.
- Offline-only (stated in README; not separately confirmed by the user).

## Brand Commitments
- Name: FinLog (package `dev.similin.finlog`).
- Receipt scanning must keep working as-is.

## Evidence on Hand
No marketing copy, customer evidence, or brand assets exist in the repository. Do not invent testimonials, benchmarks, or claims.

## Product Principles
- Logging an expense must be faster than remembering it.
- Totals must be readable at a glance by either household member.
- Everything stays on the device.

## Accessibility & Inclusion
Not established yet; open decision. Default to standard platform accessibility (text scaling, contrast, touch target size) until the user says otherwise.
