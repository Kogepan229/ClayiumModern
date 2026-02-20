# Clayium レーザーシステム仕様調査（Unofficial / Original）

## 1. 目的と範囲

この文書は、ClayiumModern 実装のための参照として、以下 2 系統のレーザー仕様を整理したものです。

- `ClayiumUnofficial`（1.12.2）
- `ClayiumOriginal`（1.7.10）

対象は以下です。

- レーザー生成
- レーザー伝播
- 受光側挙動
- ブロック照射変換
- 関連マシン（Reactor / Miner 系 / PAN）

## 2. ClayiumUnofficial の仕様

### 2.1 コアデータ: `ClayLaser`

参照:

- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/api/laser/ClayLaser.kt`

`ClayLaser` は以下を保持します。

- `red: Int`
- `green: Int`
- `blue: Int`
- `age: Int`（デフォルト `0`）
- `energy: Double`（計算値）

エネルギーは各色の関数積で計算されます。

- `E = E_blue * E_green * E_red - 1`
- 係数:
  - blue: base `2.5`, max `1000.0`
  - green: base `1.8`, max `300.0`
  - red: base `1.5`, max `100.0`

同期:

- パケット用 `writeClayLaser` / `readClayLaser`
- GUI 同期用 `ClayLaserByteBufAdapter`（null 許容）

### 2.2 発振側（レーザーを出す側）

参照:

- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/api/metatileentity/ClayLaserMetaTileEntity.kt`
- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/api/capability/impl/ClayLaserSourceMteTrait.kt`
- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/common/metatileentities/MetaTileEntities.kt`

発振は `IClayLaserSource` capability で提供されます。

Tier と色:

- Tier 7（CLAY_STEEL）: blue `(0,0,1)`
- Tier 8（CLAYIUM）: green `(0,1,0)`
- Tier 9（ULTIMATE）: red `(1,0,0)`
- Tier 10（ANTIMATTER）: white 相当 `(3,3,3)`

赤石条件:

- デフォルトは「無通電時に照射」
- `invertClayLaserRsCondition=true` で「通電時に照射」

照射中は CE を毎 tick 消費して `sampleLaser` を出力します。

### 2.3 伝播エンジン: `ClayLaserIrradiator`

参照:

- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/api/capability/impl/ClayLaserIrradiator.kt`

#### 2.3.1 射線判定

- 向き方向へ直進
- 通過可能: `AIR`, `GLASS` のみ
- 最大長: `ConfigCore.misc.maxClayLaserLength`（デフォルト 32）

#### 2.3.2 TileEntity への照射

ヒット位置に TileEntity がある場合:

- `CLAY_LASER_ACCEPTOR` をヒット面で取得
- 毎 tick `acceptLaser(irradiatedSide, laser)` を呼ぶ
- ターゲット切替/停止時は旧ターゲットへ `acceptLaser(..., null)` を送る

#### 2.3.3 ブロック照射（TileEntity がない場合）

TileEntity がないときのみ `CRecipes.LASER` でブロック変換を行います。

- 状態保持: `previousTargetPos`, `totalEnergyIrradiated`, `transformationCt`
- ターゲット座標が変わると蓄積リセット
- 毎 tick のレーザー energy を `totalEnergyIrradiated` に加算
- レシピ一致は「その tick の energy が `energyMin..energyMax` に入るか」で判定
- 変換条件:
  - `requiredEnergy <= totalEnergyIrradiated`
  - かつ `transformationCt > 10`（追加ゲート）
- 変換実行時:
  - 対象ブロックをドロップなし破壊
  - `outputState` を設置
  - カウンタ類をリセット

### 2.4 ブロック変換レシピ（レーザー）

参照:

- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/common/recipe/LaserRecipe.kt`
- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/common/recipe/registry/LaserRecipeRegistry.kt`
- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/common/loaders/recipe/LaserRecipeLoader.kt`

特徴:

- 変換仕様はデータ駆動（レシピ登録）
- 現行の組み込み内容は主に sapling 系
  - vanilla sapling -> clay tree sapling（高 energy）
  - sapling メタ循環（`energyMax` 制約あり）

### 2.5 レーザー反射器

参照:

- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/common/blocks/TileEntityClayLaserReflector.kt`
- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/common/blocks/BlockClayLaserReflector.kt`

反射器は以下を兼ねます。

- 受光側 `IClayLaserAcceptor`
- 発振側 `IClayLaserSource`

入力レーザーを面ごと `Map<EnumFacing, ClayLaser>` で保持し、毎 tick 合成して再照射します。

合成ルール（`MAX_LASER_AGE = 10`）:

- `maxAge < 10`: RGB は総和、age は `maxAge + 1`
- `maxAge >= 10`: RGB は各色 max、age は `maxAge` 維持

### 2.6 受光側マシン

#### 2.6.1 Clay Reactor

参照:

- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/api/metatileentity/multiblock/ClayReactorMetaTileEntity.kt`
- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/api/metatileentity/multiblock/LaserProxyMetaTileEntity.kt`

- `CLAY_LASER_ACCEPTOR` で受光
- マルチブロック入力位置は `LaserProxyMetaTileEntity`
- レシピ進捗/ tick は `1 + floor(laser.energy)`

#### 2.6.2 Miner 系

参照:

- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/api/metatileentity/AbstractMinerMetaTileEntity.kt`
- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/common/metatileentities/RangedMinerMetaTileEntity.kt`

`IClayLaserAcceptor` を持つ Miner 系は以下を使用:

- 加速率 `r = 1 + 4 * log10(laserEnergy / 1000 + 1)`
- CE 消費と進捗加算の両方に `r` を適用

#### 2.6.3 PAN

参照:

- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/common/metatileentities/PanAdapterMetaTileEntity.kt`
- `ClayiumUnofficial/src/main/kotlin/io/github/trcdevelopers/clayium/common/pan/factories/CPanRecipeFactory.kt`

PAN アダプタはレーザー枠に挿した Clay Laser アイテムから:

- 合成レーザー energy
- 追加 CE/t

を計算します。white レーザーはこの経路では除外されます。

## 3. ClayiumOriginal の仕様

### 3.1 コアデータ: `ClayLaser`

参照:

- `ClayiumOriginal/src/main/java/mods/clayium/block/laser/ClayLaser.java`

`ClayLaser` は以下を保持:

- `numbers[3]`（ロジック上は blue/green/red 順）
- `age`

エネルギーは `calculateEnergyPerColor` の色別積（最後に `-1`）で算出。
係数は以下で、Unofficial と同じです。

- bases `{2.5, 1.8, 1.5}`
- max energies `{1000, 300, 100}`
- damping `{0.1, 0.1, 0.1}`

### 3.2 発振側

参照:

- `ClayiumOriginal/src/main/java/mods/clayium/block/tile/TileClayEnergyLaser.java`
- `ClayiumOriginal/src/main/java/mods/clayium/block/ClayEnergyLaser.java`
- `ClayiumOriginal/src/main/java/mods/clayium/block/CBlocks.java`

Tier と色:

- Tier 7: blue `(1,0,0)` in `numbers`
- Tier 8: green `(0,1,0)`
- Tier 9: red `(0,0,1)`
- Tier 10: white `(3,3,3)`

赤石条件:

- デフォルトは「通電で停止」
- `InvertClayLaserRSCondition` で反転

### 3.3 伝播エンジン: `ClayLaserManager`

参照:

- `ClayiumOriginal/src/main/java/mods/clayium/block/laser/ClayLaserManager.java`

基本挙動:

- 向き方向へ直進
- 通過可能: air / glass
- 最大長: `laserLengthMax = 32`（固定値）

ターゲット block id/meta や長さが変わると、内部の履歴蓄積をリセットします。

### 3.4 Original 固有のハードコード照射効果

参照:

- `ClayiumOriginal/src/main/java/mods/clayium/block/laser/ClayLaserManager.java`

`ClayLaserManager` 内に直接ハードコードされた効果があります。

- **硬度と閾値:** 岩系ブロック（`Material.rock`、bedrock 除く）では、変換/採掘の条件が次の式で与えられる:
  - `totalIrradiatedEnergy >= (block.getBlockHardness(world, x, y, z) + 1.0F) * 100L`
  - つまり **閾値 = (硬度 + 1.0) × 100**。硬度が高いブロックほど、蓄積が必要。
- 岩系（bedrock 除く）:
  - 蓄積 energy が上記閾値を超えると採掘処理
  - 通常岩はドロップ
  - 特定鉱石は以下の圧縮ブロックへ直接変換
- 鉱石変換:
  - coal ore -> coal block
  - iron ore -> iron block
  - gold ore -> gold block
  - diamond ore -> diamond block
  - redstone ore -> redstone block
  - lapis ore -> lapis block
  - emerald ore -> emerald block
- sapling:
  - `300 <= total < 1000`: vanilla sapling メタ循環
  - `>= 300000`: clay tree sapling 化

また、ヒット先 TileEntity が `IClayLaserMachine` なら `irradiateClayLaser` を呼びます。

### 3.5 反射器

参照:

- `ClayiumOriginal/src/main/java/mods/clayium/block/tile/TileLaserReflector.java`

挙動:

- 受光レーザーを tick 時刻付きリストで保持
- update で「前 tick 入力分」を合成
- 合成後に `age++`
- `ClayLaserManager` で再照射

### 3.6 受光側マシン

#### 3.6.1 Clay Reactor

参照:

- `ClayiumOriginal/src/main/java/mods/clayium/block/tile/TileClayReactor.java`
- `ClayiumOriginal/src/main/java/mods/clayium/block/tile/TileClayLaserInterface.java`

- `TileClayLaserInterface` 経由でレーザーを中継受信
- 進捗/ tick に `floor(laser.energy)` を加算（基礎 +1 は別）

#### 3.6.2 Area Miner / Area Activator

参照:

- `ClayiumOriginal/src/main/java/mods/clayium/block/tile/TileAreaMiner.java`
- `ClayiumOriginal/src/main/java/mods/clayium/block/tile/TileAreaActivator.java`

両方ともレーザー受光対象で:

- 受光で `laserEnergy` を蓄積
- `1 + 4*log10(laserEnergy/1000 + 1)` で加速
- 作業処理時に `laserEnergy` を消費リセット

#### 3.6.3 PAN

参照:

- `ClayiumOriginal/src/main/java/mods/clayium/pan/PANACFactoryClayMachines.java`

PAN の Reactor 見積では、挿入レーザーブロックから合成レーザー効果を計算し、時間/消費を補正します。

## 4. 仕様差分（Unofficial vs Original）

| 項目 | Unofficial | Original |
|---|---|---|
| API モデル | capability (`IClayLaserSource` / `IClayLaserAcceptor`) | 直接 interface (`IClayLaserMachine` / `IClayLaserManager`) |
| ブロック変換実装 | `CRecipes.LASER` によるレシピ駆動 | `ClayLaserManager` ハードコード |
| 既定の変換内容 | 主に sapling 系 | 鉱石->ブロック変換 + sapling 循環 + clay tree |
| 停止通知 | `acceptLaser(..., null)` を明示送信 | null 通知 API なし |
| 最大長 | `maxClayLaserLength`（設定可能） | `laserLengthMax=32`（固定） |
| 最大長端の扱い | `1..max` で走査（最大距離ブロックも対象） | `< max` 走査（最大距離ちょうどは対象外） |
| 反射器入力管理 | 面ごとの map | 時刻付きリスト |
| 反射器 age | 10 まで増加後は合成経路で頭打ち | 合成後に毎回 `age++` |
| Reactor 入力プロキシ | `LaserProxyMetaTileEntity` | `TileClayLaserInterface` |
| Miner 系の受光管理 | `ClayLaser?` を保持して動的加速 | `laserEnergy` スカラー蓄積 |
| Activator のレーザー加速 | `ActivatorMetaTileEntity` は固定 `1.0` | `TileAreaActivator` はレーザー加速あり |
| PAN のレーザー入力モデル | PanAdapter のレーザー枠（white 除外） | レーザーブロック本体から算出（7-9 を使用） |

## 5. ClayiumModern 採用仕様（確定）

本章は ClayiumModern の実装スコープを明示する確定仕様です。
以降の実装はここを正として扱います。

### 5.1 Modern 採用（初期スコープ）

- ベース方針:
  - `ClayiumUnofficial` 準拠で実装する。
- コアデータ:
  - `Laser` 値オブジェクト（RGB, age, energy）を採用し、energy は Unofficial と同式で計算する。
- API モデル:
  - capability ベース（`IClayLaserSource` / `IClayLaserAcceptor` 相当）を採用する。
- 発振側:
  - Tier 7-10 の色レーザー割当、赤石反転設定（`invertClayLaserRsCondition`）を採用する。
- 伝播:
  - 直進、`air/glass` 通過、最大長設定（`maxClayLaserLength`）を採用する。
  - ターゲット切替/停止時の `acceptLaser(..., null)` 通知を採用する。
  - TileEntity 非存在時のブロック照射はレシピ駆動（`CRecipes.LASER` 相当）を採用する。
  - 鉱石変換の基本仕様は Original を踏襲しつつ、ハードコードではなく JSON データパックで定義可能にする。
- 反射器:
  - 面別入力管理と Unofficial の合成ルール（`age < 10` は加算、`age >= 10` は色ごと max）を採用する。
- 受光側:
  - Reactor の進捗補正 `1 + floor(laser.energy)` を採用する。
  - Miner 系の加速式 `1 + 4*log10(laserEnergy/1000 + 1)` を採用する。
- 設定値:
  - `invertClayLaserRsCondition`、`maxClayLaserLength`、`laserQuality` を採用する。

### 5.2 未採用（将来対応）

- `ClayiumOriginal` のハードコード実装方式:
  - `ClayLaserManager` 内への直接ハードコードは採用しない（挙動は JSON データパック駆動で再現する）。
- `ClayiumOriginal` の岩系採掘ロジック:
  - `ClayLaserManager` 内ハードコード採掘処理は採用しない。
- Activator のレーザー加速:
  - Original の `Area Activator` レーザー加速は初期スコープでは採用しない。
- Original 由来の実装様式:
  - `IClayLaserMachine` など直接 interface ベース配線は採用しない。
  - 固定最大長（`laserLengthMax=32`）は採用しない。
- 上記未採用項目は、必要時に「互換モード（feature flag）」として追加検討する。
