# ClayiumModern レーザー実装タスク分解

## 1. 目的

`ClayiumModern` にレーザーシステムを段階的に実装するための作業計画。

参照仕様:

- `docs/laser_system_spec.md`

## 2. 実装方針（前提）

- 基本は `ClayiumUnofficial` 準拠で実装する。
- 鉱石変換の基本仕様は `ClayiumOriginal` を踏襲するが、実装はハードコードせず JSON データパック駆動で行う。
- `ClayiumOriginal` 固有要素のうち Activator 加速は初期スコープ外とし、必要なら互換モードとして後段で追加する。

## 3. タスク一覧（実装順）

### Phase 0: 仕様固定

1. Modern 採用仕様の確定
- 内容: `docs/laser_system_spec.md` に「Modern採用」「未採用（将来対応）」を明記。
- 完了条件: 実装対象/対象外が曖昧でない状態。

### Phase 1: コア基盤

2. レーザードメインモデル作成
- 内容: `Laser` 値オブジェクト（RGB, age, energy）と計算式実装。
- 完了条件: 代表値テストで energy が期待値一致。

3. 同期/シリアライズ基盤作成
- 内容: NBT 保存、network 同期（Codec / StreamCodec 相当）を整備。
- 完了条件: encode/decode 往復で値が一致。

4. capability/API 追加
- 内容: `IClayLaserSource` / `IClayLaserAcceptor` 相当を `capability` 層に追加。
- 完了条件: 任意 BlockEntity が source/acceptor を expose 可能。

5. 設定値追加
- 内容: `invertClayLaserRsCondition`, `maxClayLaserLength`, `laserQuality` を `Config` に追加。
- 完了条件: 設定変更がゲーム内挙動へ反映。

### Phase 2: 伝播・発振・反射

6. 伝播エンジン実装
- 内容: 直進、air/glass 通過、最大長、ターゲット切替時 null 通知、ブロック照射分岐。
- 完了条件: テストワールドで照射長・停止通知・切替通知が再現。

7. Clay Laser 発振機実装
- 内容: Block/BlockEntity、CE 消費、赤石条件、Tier ごとの色レーザー出力。
- 完了条件: 各 Tier が想定色を照射し、赤石反転が動作。

8. Laser Reflector 実装
- 内容: 面別受光、合成（age<10 で加算 / age>=10 で max）、再照射。
- 完了条件: 複数入力時の合成結果が仕様通り。

### Phase 3: ブロック照射レシピ

9. レーザー照射レシピ型実装
- 内容: RecipeType/Serializer/JSON 定義（`CRecipes.LASER` 相当）を実装し、sapling 変換に加えて「鉱石 -> 圧縮ブロック」相当の定義をデータパックで表現可能にする。
- 完了条件: JSON 定義した照射レシピを runtime で引ける。

10. 初期レシピ投入（sapling 系 + 鉱石系）
- 内容: datagen で sapling 変換レシピと鉱石変換レシピを登録。
- 完了条件: `runData` で生成され、ゲーム内変換が成立。

### Phase 4: 受光側統合

11. Reactor 受光対応
- 内容: レーザー受光で進捗補正（`1 + floor(laser.energy)`）。
- 完了条件: 未受光/受光で進捗速度差が確認できる。

12. Miner 系受光対応
- 内容: 加速式 `1 + 4*log10(laserEnergy/1000 + 1)` を適用。
- 完了条件: 受光時のみ CE 消費/進捗が加速。

### Phase 5: クライアント表示・同期品質

13. レーザービーム描画実装
- 内容: 色正規化、quality 反映、長さ反映、反射器描画。
- 完了条件: サーバー/クライアントで表示が一致。

14. 永続化・同期仕上げ
- 内容: 初期同期・差分同期・再読み込み整合性確認。
- 完了条件: 再ログイン/チャンク再読み込み後に破綻しない。

### Phase 6: テストと仕上げ

15. 自動テスト追加
- 内容: energy 計算、照射長、レシピ一致、反射合成、赤石条件をテスト化。
- 完了条件: `./gradlew check` が通る。

16. 互換拡張（任意）
- 内容: Original 互換モード（鉱石直変換、Activator 加速）を feature flag で追加。
- 完了条件: ON/OFF で挙動切替可能。

## 4. 実装優先順位（短期）

短期 MVP は以下を推奨:

1. Phase 1（2〜5）
2. Phase 2（6〜8）
3. Phase 3（9〜10）
4. Phase 4 の Reactor（11）
5. Phase 5（13〜14）

## 5. 検証コマンド

- `./gradlew runData`（レシピ/モデル生成）
- `./gradlew check`（コンパイル・検証）
- `./gradlew spotlessApply`（必要時の整形）
