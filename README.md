# Android TextEditor
Kotlin勉強用に作成しているテキストエディター

## 主要な機能
- 入力文字数のカウント
- データの保存(現状ローカル)

# TODO
- 保存データを構造体で定義する
- 保存しているデータ形式をJsonにする
- レイアウトをリッチにする
- 最終編集日情報の追加
- 単体でデータを消去できるようにする
- アカウントでデータの同期を取れるようにする
- スライダーの左に音量を調整するボタンを追加（10毎）
- QRコード読み込むと、QRコードが閉じる仕様に変更
- exeを叩くとQRコードが表示される仕様に変更

# 不具合
- ~~本文がない状態で保存すると、読み込むときにエラーでアプリが落ちる~~
- ~~一覧からテキストを開いて、再度一覧に戻るとデータが増殖する~~
- ~~QRコードのスレッドとメインスレッドが衝突し、PCにデータ送信が正常に行えない~~