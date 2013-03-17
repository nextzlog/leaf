「localize」ディレクトリには各言語への対応ファイルを配置します。
配置は以下の規則に従ってください。LocalizeManagerはこの通りに言語セットを検索します。

resource / localize / <ISO3166に定める3文字の言語コード> / パッケージ階層 / クラス名.xml

例えば、leaf.dialog.LeafGrepDialogクラスの日本語対応ファイルは以下の場所に配置します。
resource/localize/jpn/leaf/dialog/LeafGrepDialog.xml