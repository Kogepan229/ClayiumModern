# Laser Block Conversion: ClayiumModern vs ClayiumOriginal

This document compares the block conversion (laser irradiation) behaviour of ClayiumModern with ClayiumOriginal (1.7.10) as described in `laser_system_spec.md`.

## 現時点での Original との違い（一覧）

- **ブロック変換の実装方式**  
  Original は `ClayLaserManager` 内のハードコード。Modern はレシピ駆動（JSON データパック）。

- **鉱石変換のトリガー**  
  Original は蓄積 energy が硬度ベースの閾値を超えたら変換（と推測）。Modern は Original 同様に**蓄積 total のみ**で判定し、各レシピで `required_energy`（硬度相当）を指定。鉱石は 100、サンプリングは 300。照射 tick 数 > 10 は共通。

- **サンプリング循環の条件**  
  Original は `300 <= total < 1000` のときのみ変換。Modern は `total >= required_energy`（300）かつ tick 数 > 10 で変換。**蓄積の上限（total < 1000）は見ていない**点は従来どおり。

- **Clay tree sapling**  
  Original は `total >= 300000` で clay tree サンプリングに変換。Modern は clay tree サンプリングブロックを未実装のため**未対応**。

- **岩系ブロックの採掘**  
  Original は岩系（bedrock 除く）で蓄積が硬度ベース閾値を超えると採掘（ドロップ or 鉱石→ブロック）。Modern は**意図的に未実装**（スコープ外）。

- **鉱石・ブロックの種類**  
  Original（1.7.10）は copper / deepslate なし。Modern は copper ore→block と deepslate 系鉱石を追加。サンプリングは 1.21 の種類（cherry, mangrove_propagule 等）を追加。

- **API モデル**  
  Original は `IClayLaserMachine` / `IClayLaserManager` の直接 interface。Modern は capability（`IClayLaserSource` / `IClayLaserAcceptor`）で Unofficial 準拠。

- **レーザー最大長**  
  Original は固定 32。Modern は設定 `maxClayLaserLength`（デフォルト 32）で変更可能。

- **最大長の先のブロック**  
  Original は距離 `< max` まで走査（最大距離のブロックは対象外）。Modern は `1..max` で走査（最大距離のブロックも対象）。

- **ターゲット切替・停止時の通知**  
  Original は null 通知の明示 API なし。Modern は旧ターゲットに `acceptLaser(..., null)` を送る。

- **反射器の入力管理**  
  Original は受光レーザーを「時刻付きリスト」で保持。Modern は面ごとの map（Unofficial 準拠）。

- **反射器の age 合成**  
  Original は合成後に毎 tick `age++`。Modern は age < 10 で RGB 加算・age = max+1、age >= 10 で色ごと max・age 維持（Unofficial 準拠）。

- **Reactor のレーザー入力**  
  Original は `TileClayLaserInterface` で中継。Modern は Unofficial 同様のプロキシ構成（Reactor 実装時に合わせる想定）。

- **Miner 系の受光**  
  Original は `laserEnergy` スカラーを蓄積。Modern は Unofficial 同様に `Laser?` を保持して動的加速。

- **Activator のレーザー加速**  
  Original の Area Activator はレーザーで加速。Modern は**採用していない**（Activator は固定 1.0、スコープ外）。

- **PAN のレーザー入力**  
  Original はレーザーブロック本体から 7–9 の効果を算出。Modern は Unofficial 同様、PanAdapter のレーザー枠（white 除外）を想定。

---

## Summary

| Aspect | ClayiumOriginal | ClayiumModern |
|--------|-----------------|---------------|
| Implementation | Hardcoded in `ClayLaserManager` | Recipe-driven (JSON data pack) |
| Ore list | coal, iron, gold, diamond, redstone, lapis, emerald | Same + copper; stone + deepslate variants |
| Ore trigger | Hardness-based accumulated energy threshold (implied) | Accumulated total only; `required_energy` (100) per recipe (hardness-equivalent) + ticks > 10 |
| Sapling cycle | `300 <= total < 1000` | Accumulated total only; `required_energy = 300` per recipe + ticks > 10 (no total upper bound) |
| Clay tree sapling | `total >= 300000` → clay tree sapling | Not implemented (no clay tree sapling block in mod) |
| Rock mining | Hardness-based mining (drops or ore→block) | Not implemented (intentionally out of scope) |

## Original behaviour (from spec §3.4)

- **Ore conversion:** Coal, iron, gold, diamond, redstone, lapis, emerald ore → corresponding block. Trigger is implied to be when accumulated energy exceeds some (hardness-based?) threshold.
- **Sapling:**
  - `300 <= total < 1000`: vanilla sapling “meta cycle” (same sapling).
  - `total >= 300000`: convert to clay tree sapling.
- **Rock/mining:** For non-ore stone-like blocks, accumulated energy above a hardness-based threshold causes mining (drops). Spec states this is **not** adopted in Modern.

## Modern behaviour (current implementation)

- **Condition model (Original-style: accumulated total only):**
  - Recipe match: current block matches recipe input block.
  - Transform when: `totalEnergyIrradiated >= required_energy` (per-recipe threshold, hardness-equivalent) and `irradiationTicks > 10`.
  - No per-tick energy min/max; only accumulated total is used for the condition.
  - On transform: destroy block (no drops), set output block state, irradiator resets counters for that target.
- **Ore recipes:** All ore → block use `required_energy=100` (hardness-equivalent threshold per recipe). Any tier can trigger after enough ticks.
- **Sapling cycle:** `required_energy=300` per recipe. We do **not** enforce `total < 1000`; once total ≥ 300 and ticks > 10 we transform.
- **Clay tree sapling:** Not added because the mod does not register a clay tree sapling block. Can be added later via recipe JSON when that block exists.
- **Copper / deepslate / extra saplings:** Added as natural extensions for 1.21 (Original was 1.7.10).

## Alignments

- Same ore→block set as Original (plus copper and deepslate variants).
- Sapling cycle uses the same lower bound (300) for when the cycle can trigger.
- No drops on block replacement (destroy without drops, then set block).
- Rock/mining and clay tree sapling intentionally omitted per Modern scope.

## Deviations

1. **Sapling cycle total upper bound:** Original: `300 <= total < 1000`. Modern: we only require `total >= required_energy` (300) and ticks > 10. We do not prevent transformation when total ≥ 1000.
2. **Ore trigger:** Original likely used block-hardness–based thresholds; Modern uses a per-recipe `required_energy` (100 for all ores) as a configurable hardness-equivalent.
3. **Clay tree sapling:** Not implemented; add when the block is available and a recipe is defined.
