# Clayium Original: Tier Multipliers (Crafting Machines)

Summary of how **energy** and **time** multipliers are applied per tier for each crafting machine type in Clayium Original (1.7.10). Source: `UtilTier.TierRegistry`, `UtilTier.SpecialTierManager`, and `CBlocks.applyMachineTier`.

---

## Overview

- **TierManager** (e.g. `tierBasic`, `tierGeneric`, `tierSmelter`, `tierCACondenser`) defines multiplier arrays for tier indices **0–13**.
- **Actual CE per tick** = recipe base energy × **energy multiplier**[tier].  
- **Actual craft time (ticks)** = recipe base time × **time multiplier**[tier].
- Only machines that receive `applyMachineTier(...)` use tier-based multipliers; all others use the **ClayMachines** default `multCraftTime = 1.0`, `multConsumingEnergy = 1.0` (effectively “tierBasic”: always 1.0).

---

## 1. Machines with tier-based multipliers

These blocks are passed to `applyMachineTier` and thus use the listed multiplier set.

| Machine            | Tier manager      | Tiers (block exists) | Source (CBlocks) |
|--------------------|-------------------|----------------------|-------------------|
| Condenser          | tierGeneric (BASE)| 2, 3, 4, 5, 10      | `blocksCondenser` |
| Grinder            | tierGeneric (BASE)| 2, 3, 4, 5, 6, 10   | `blocksGrinder`   |
| Centrifuge         | tierGeneric (BASE)| 3, 4, 5, 6          | `blocksCentrifuge`|
| Smelter            | tierSmelter       | 4, 5, 6, 7, 8, 9    | `blocksSmelter`   |
| CA Condenser       | tierCACondenser   | 9, 10, 11            | `blocksCACondenser`|

---

## 2. Multiplier arrays (defaults)

All arrays are indexed by **tier** (0–13). Values are from `TierRegistry.*_DEFAULT` (config can override).

### 2.1 Base (tierGeneric) — Condenser, Grinder, Centrifuge

**Constant names:** `CRAFTING_BASE_ENERGY_MULTIPLIER_DEFAULT`, `CRAFTING_BASE_TIME_MULTIPLIER_DEFAULT`

| Tier | Energy multiplier | Time multiplier |
|------|-------------------|-----------------|
| 0    | 0    | 0     |
| 1    | 1.0  | 1.0   |
| 2    | 1.0  | 1.0   |
| 3    | 1.0  | 1.0   |
| 4    | 1.0  | 1.0   |
| 5    | 5.0  | 0.25  |
| 6    | 25.0 | 0.0625|
| 7    | 0    | 0     |
| 8    | 0    | 0     |
| 9    | 0    | 0     |
| 10   | 250  | 0.01  |
| 11   | 0    | 0     |
| 12   | 0    | 0     |
| 13   | 0    | 0     |

### 2.2 Smelter (tierSmelter)

**Constant names:** `CRAFTING_SMELTER_ENERGY_MULTIPLIER_DEFAULT`, `CRAFTING_SMELTER_TIME_MULTIPLIER_DEFAULT`

| Tier | Energy multiplier | Time multiplier |
|------|-------------------|-----------------|
| 0    | 0   | 0     |
| 1    | 0   | 0     |
| 2    | 0   | 0     |
| 3    | 0   | 0     |
| 4    | 1.0 | 2.0   |
| 5    | 14.0   | 0.5   |
| 6    | 200.0  | 0.125 |
| 7    | 2800.0 | 0.03  |
| 8    | 40000.0| 0.01  |
| 9    | 560000.0 | 0.0025 |
| 10   | 0   | 0     |
| 11   | 0   | 0     |
| 12   | 0   | 0     |
| 13   | 0   | 0     |

### 2.3 CA Condenser (tierCACondenser)

**Constant names:** `CRAFTING_CA_CONDENSER_ENERGY_MULTIPLIER_DEFAULT`, `CRAFTING_CA_CONDENSER_TIME_MULTIPLIER_DEFAULT`

| Tier | Energy multiplier | Time multiplier |
|------|-------------------|-----------------|
| 0–9  | 0   | 0   |
| 10   | 10.0 | 0.1  |
| 11   | 100.0 | 0.01 |
| 12   | 0   | 0   |
| 13   | 0   | 0   |

---

## 3. Machines with no tier multipliers (default 1.0)

These do **not** get `applyMachineTier`; they use `ClayMachines` defaults: `multCraftTime = 1.0F`, `multConsumingEnergy = 1.0F`. So effective multipliers are **1.0** for both energy and time at every tier.

| Machine               | Tiers (block exists)     |
|-----------------------|--------------------------|
| Bending Machine       | 1, 2, 3, 4, 5, 6, 7, 9   |
| Wire Drawing Machine  | 1, 2, 3, 4               |
| Pipe Drawing Machine  | 1, 2, 3, 4               |
| Cutting Machine       | 1, 2, 3, 4               |
| Lathe                 | 1, 2, 3, 4               |
| Decomposer            | 2, 3, 4                  |
| Milling Machine       | 3, 4                     |
| Assembler             | 3, 4, 6, 10              |
| Inscriber             | 3, 4                     |
| Chemical Reactor      | 4, 5, 8                  |
| Alloy Smelter         | 6                        |
| Electrolysis Reactor  | 6, 7, 8, 9               |
| Matter Transformer    | 7, 8, 9, 10, 11, 12      |
| CA Injector           | 9, 10, 11, 12, 13        |
| (Other non-crafting or special-logic blocks) | — |

---

## 4. Recipe tierManager (NEI / display)

Recipes hold a `tierManager` used for **display** (e.g. NEI) and for any logic that uses the recipe’s manager instead of the block’s multipliers:

- **Recipes** (base): `tierManager = UtilTier.tierBasic` (always 1.0).
- **SmeltingRecipe** (Smelter): `tierManager = UtilTier.tierSmelter`.

So only Smelter recipes use a non-basic manager for recipe-side display; actual in-world behaviour is still driven by the **block’s** multipliers set via `applyMachineTier`.

---

## 5. Reference (code locations)

- **UtilTier.java**: `tierBasic`, `tierGeneric`, `tierSmelter`, `tierCACondenser`; `TierRegistry.*_DEFAULT` arrays; `SpecialTierManager.applyMachineTier`.
- **CBlocks.java**: `applyMachineTier(blocksCondenser)`, `applyMachineTier(blocksGrinder)`, `applyMachineTier(blocksCentrifuge)`, `applyMachineTier(blocksSmelter)`, `applyMachineTier(blocksCACondenser)`.
- **ClayMachines.java**: default `multCraftTime = 1.0F`, `multConsumingEnergy = 1.0F`.
- **TileClayMachines.java**: `proceedCraft()` uses `getEnergy(...) * multConsumingEnergy` and `getTime(...) * multCraftTime`.
