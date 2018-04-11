define('sa/locales/ja-jp/translations', ['exports'], function (exports) {
  'use strict';

  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports.default = {
  appTitle: 'NetWitness Suite',
  pageTitle: '{{section}} - NetWitness Suite',
  empty: '',
  languages: {
    en: '英語',
    'en-us': '英語',
    ja: '日本語'
  },
  passwordPolicy: {
    passwordPolicyRequestError: 'パスワード ポリシーの取得時に問題が発生しました。',
    passwordPolicyMinChars: '{{passwordPolicyMinChars}}文字以上でなければなりません',
    passwordPolicyMinNumericChars: '数字（0～9）を{{passwordPolicyMinNumericChars}}文字以上含める必要があります',
    passwordPolicyMinUpperChars: '{{passwordPolicyMinUpperChars}}文字以上の英大文字が必要です',
    passwordPolicyMinLowerChars: '{{passwordPolicyMinLowerChars}}文字以上の英小文字が必要です',
    passwordPolicyMinNonLatinChars: '大文字でも小文字でもないUnicodeアルファベット文字を{{passwordPolicyMinNonLatinChars}}文字以上含める必要があります',
    passwordPolicyMinSpecialChars: '{{passwordPolicyMinSpecialChars}}文字以上の特殊文字を含める必要があります:（~!@#$%^&*_-+=`|(){}[]:;"\'<>,.?/)）',
    passwordPolicyCannotIncludeId: 'パスワードにユーザ名を含めることはできません'
  },
  forms: {
    cancel: 'キャンセル',
    submit: '送信',
    reset: 'リセット',
    apply: '適用',
    ok: 'OK',
    delete: '削除',
    save: '保存',
    yes: 'はい',
    no: 'いいえ'
  },
  tables: {
    noResults: '結果なし',
    columnChooser: {
      filterPlaceHolder: 'リストのフィルターのタイプ'
    }
  },
  login: {
    username: 'ユーザー名',
    password: 'パスワード',
    login: 'ログイン',
    loggingIn: 'ログイン',
    logout: 'ログアウト',
    oldPassword: '古いパスワード',
    newPassword: '新しいパスワード',
    confirmPassword: 'パスワードの確認',
    passwordMismatch: '確認用パスワードが一致しません。',
    passwordNoChange: '新しいパスワードが古いパスワードと一致していません。',
    passwordChangeFailed: 'パスワードの変更の保存中に問題が発生しました。もう一度実行してください。',
    lostPasswordLink: 'パスワードを紛失した場合',
    genericError: '認証エラー。もう一度実行してください。',
    communicationError: 'サーバに到達できませんでした。システム管理者に問い合わせてください。',
    userLocked: 'ユーザ アカウントがロックされています。',
    userDisabled: 'ユーザ アカウントが無効になっています。',
    userExpired: 'ユーザ アカウントの有効期限が切れています',
    changePasswordLink: 'パスワードの変更',
    changePasswordSoon: 'RSA NetWitnessサーバへのパスワードは{{daysRemaining}}日後に期限が切れます。期限が切れる前にパスワードを変更してください。パスワードを変更するには、アプリケーション ウィンドウの右上にある基本設定ボタンをクリックします。',
    changePasswordToday: 'RSA NetWitnessサーバへのパスワードは今日で期限が切れます。期限が切れる前にパスワードを変更してください。パスワードを変更するには、アプリケーション ウィンドウの右上にある基本設定ボタンをクリックします。',
    lostPassword: {
      title: '紛失したパスワードのリカバリ',
      description: 'ユーザ名を送信してください。'
    },
    thankYou: {
      title: 'ありがとうございました。',
      description: 'パスワードのリセットが登録ユーザのメール アカウントに送信されました。',
      back: 'ログインに戻る'
    },
    eula: {
      title: 'エンド ユーザ使用許諾契約書',
      agree: '同意する'
    },
    forcePassword: {
      warning: 'ログインする前に、新しいパスワードを作成する必要があります。',
      changePassword: 'パスワードの変更'
    }
  },
  userPreferences: {
    preferences: 'ユーザ環境設定',
    personalize: 'エクスペリエンスのカスタマイズ',
    signOut: 'サイン アウト',
    version: 'バージョン',
    username: 'ユーザー名',
    email: 'メール',
    language: '言語',
    timeZone: 'タイム ゾーン',
    dateFormatError: '選択した日付形式を保存しようとしたときにエラーが発生しました。もう一度実行してください。この問題が解消されない場合は、システム管理者に問い合わせてください。',
    landingPageError: '選択したデフォルト ランディング ページを保存しようとしたときにエラーが発生しました。もう一度実行してください。この問題が解消されない場合は、システム管理者に問い合わせてください。',
    defaultInvestigatePageError: '選択したデフォルト調査ビューを保存しようとしたときにエラーが発生しました。もう一度実行してください。この問題が解消されない場合は、システム管理者に問い合わせてください。',
    timeFormatError: '選択した時刻形式を保存しようとしたときにエラーが発生しました。もう一度実行してください。この問題が解消されない場合は、システム管理者に問い合わせてください。',
    timezoneError: '選択したタイム ゾーンを保存しようとしたときにエラーが発生しました。もう一度実行してください。この問題が解消されない場合は、システム管理者に問い合わせてください。',
    dateFormat: {
      label: '日付形式',
      dayFirst: 'DD/MM/YYYY',
      monthFirst: 'YYYY年MM月DD日',
      yearFirst: 'YYYY/MM/DD'
    },
    timeFormat: {
      label: '時間形式',
      twelveHour: '12時間制',
      twentyFourHour: '24時間制'
    },
    theme: {
      title: 'テーマ',
      dark: 'ダーク',
      light: 'ライト',
      error: '選択したテーマを保存しようとしたときにエラーが発生しました。もう一度実行してください。この問題が解消されない場合は、システム管理者に問い合わせてください。'
    },
    defaultLandingPage: {
      label: 'デフォルト ランディング ページ',
      monitor: '監視',
      investigate: '調査',
      investigateClassic: '調査',
      dashboard: '監視',
      live: '構成',
      respond: '対応',
      admin: '管理者'
    },
    defaultInvestigatePage: {
      label: 'デフォルトの［調査］ビュー',
      events: 'イベント',
      eventAnalysis: 'イベント分析',
      malware: 'Malware Analysis',
      navigate: 'ナビゲート',
      hosts: 'ホスト',
      files: 'ファイル'
    }
  },
  queryBuilder: {
    noMatches: '一致するものがありません',
    enterValue: '単一の値を入力',
    insertFilter: '新しいフィルターを挿入',
    query: 'フィルターでクエリを実行',
    open: '新しいタブで開く',
    delete: '選択したフィルターを削除',
    deleteFilter: 'このフィルターを削除',
    edit: 'このフィルターを編集',
    placeholder: 'メタ キー、演算子、値を入力（オプション）',
    querySelected: '選択したフィルターでクエリを実行',
    querySelectedNewTab: '新しいタブで、選択したフィルターでクエリを実行',
    expensive: 'この操作の実行には時間がかかる場合があります。',
    notEditable: '複雑なクエリ フィルターは編集できません。',
    validationMessages: {
      time: '有効な日付を入力する必要があります。',
      text: '文字列は「"」で囲む必要があります',
      ipv4: 'IPv4アドレスを入力する必要があります。',
      ipv6: 'IPv6アドレスを入力する必要があります。',
      uint8: '8ビットの整数を入力する必要があります。',
      uint16: '16ビットの整数を入力する必要があります。',
      uint32: '32ビットの整数を入力する必要があります。',
      float32: '32ビットの浮動小数を入力する必要があります。'
    }
  },
  ipConnections: {
    ipCount: '（{{count}}件のIP）',
    clickToCopy: 'クリックしてIPアドレスをコピー',
    sourceIp: 'ソースIP',
    destinationIp: '宛先IP'
  },
  list: {
    all: '（すべて）',
    items: 'アイテム',
    packets: 'パケット',
    packet: 'パケット',
    of: '/',
    sessions: 'セッション'
  },
  updateLabel: {
    'one': '更新',
    'other': '更新'
  },
  recon: {
    extractWarning: '<span>ダウンロードされたファイルがブラウザ トレイに添付可能になる前に、別の画面に移動しました。ダウンロードは<a href="{{url}}" target="_blank">ここ</a>で使用できます。</span>',
    extractedFileReady: 'ファイルを展開しました。ダウンロードするにはジョブ キューに移動します',
    titleBar: {
      titles: {
        endpoint: 'エンドポイント イベントの詳細',
        network: 'ネットワーク イベントの詳細',
        log: 'ログ イベントの詳細'
      },
      views: {
        text: 'テキスト分析',
        packet: 'パケット分析',
        file: 'ファイル分析',
        web: 'Web',
        mail: 'メール'
      }
    },
    meta: {
      scroller: {
        of: '/',
        results: '結果'
      }
    },
    textView: {
      compressToggleLabel: '圧縮されたペイロードの表示',
      compressToggleTitle: '圧縮または非圧縮としてHTTPペイロードを表示',
      downloadCsv: 'CSVのダウンロード',
      downloadEndpointEvent: 'エンドポイントのダウンロード',
      pivotToEndpoint: 'エンドポイント シック クライアントへの移行',
      pivotToEndpointTitle: '4.xエンドポイント エージェントがインストールされているホストに適用されます。Netwitnessエンドポイント シック クライアントをインストールしてください。',
      downloadJson: 'JSONのダウンロード',
      downloadLog: 'ログのダウンロード',
      downloadXml: 'XMLのダウンロード',
      headerShowing: 'ページあたり',
      isDownloading: 'ダウンロードしています...',
      maxPacketsReached: '<span class="darker">{{packetTotal}}</span>パケット中、<span class="darker">{{maxPacketCount}} （最大）</span>パケットをレンダリングしました',
      maxPacketsReachedTooltip: '1イベントのレンダリングでの{{maxPacketCount}}パケットの制限に達しました。このイベントには、これ以上のパケットはレンダリングされません。パケットの閾値により、最高のレンダリング エクスペリエンスが確実に得られます。',
      rawEndpointHeader: 'Raw Endpoint',
      rawLogHeader: 'Raw Log',
      renderingMore: 'さらに表示されます...',
      renderRemaining: '残り{{remainingPercent}}%のレンダリング中...',
      showRemaining: '残り{{remainingPercent}}%を表示'
    },
    packetView: {
      noHexData: 'コンテンツの再構築によって16進数データが生成されませんでした。',
      isDownloading: 'ダウンロードしています...',
      defaultDownloadPCAP: 'PCAPのダウンロード',
      downloadPCAP: 'PCAPのダウンロード',
      downloadPayload1: 'リクエスト ペイロードのダウンロード',
      downloadPayload2: 'レスポンス ペイロードのダウンロード',
      downloadPayload: 'すべてのペイロードのダウンロード',
      payloadToggleLabel: 'ペイロードのみ表示',
      payloadToggleTitle: 'パケットのヘッダーとフッターを表示から削除します',
      stylizeBytesLabel: 'バイトの濃淡化',
      stylizeBytesTitle: 'データ内のパターンを区別できるよう表示',
      commonFilePatternLabel: '一般的なファイル パターン',
      commonFilePatternTitle: '一般的なファイルの特徴的なパターンを強調表示',
      headerMeta: 'ヘッダー メタ',
      headerAttribute: 'ヘッダー属性',
      headerSignature: '興味深いバイト',
      headerDisplayLabel: '{{label}} = {{displayValue}}',
      renderingMore: 'さらに表示されます...'
    },
    reconPager: {
      packetPagnationPageFirst: '先頭',
      packetPagnationPagePrevious: '前へ',
      packetPagnationPageNext: '次',
      packetPagnationPageLast: '最後',
      packetsPerPageText: 'ページあたりのパケット数'
    },
    fileView: {
      downloadFile: 'ファイルのダウンロード',
      downloadFiles: 'ファイルのダウンロード（{{fileCount}}）',
      isDownloading: 'ダウンロードしています...',
      downloadWarning: '警告：ファイルには、元の保護されていないRAWコンテンツが含まれます。ファイルを開くときやダウンロードするときは注意してください。悪意のあるデータが含まれている場合があります。'
    },
    files: {
      fileName: 'ファイル名',
      extension: '拡張子',
      mimeType: 'MIMEタイプ',
      fileSize: 'ファイル サイズ',
      hashes: 'ハッシュ',
      noFiles: 'このイベントで使用可能なファイルはありません。',
      linkFile: 'このファイルは別のセッションに存在します。<br>新しいタブで関連セッションを表示するには、ファイル リンクをクリックします。'
    },
    error: {
      generic: 'このデータを取得しようとしたときに、予期しないエラーが発生しました。',
      missingRecon: 'このイベント（ID = {{id}}）は保存されていないか、ストレージ外にロールアウトされています。表示するコンテンツはありません。',
      noTextContentData: 'コンテンツの再構築によってテキスト データが生成されませんでした。イベント データが破損しているか、無効である可能性があります。他の表示で再構築ビューを確認してください。',
      noRawDataEndpoint: 'コンテンツの再構築によってテキスト データが生成されませんでした。イベント データが破損しているか無効である可能性があります。または、管理者がEndpoint Server構成で未フォーマットのエンドポイント イベントの送信を無効にしている可能性があります。他の表示で再構築ビューを確認してください。',
      permissionError: '要求されたデータに対する十分な権限がありません。権限があると思われる場合は、管理者に連絡して、必要な権限の付与を依頼してください。'
    },
    fatalError: {
      115: '表示できるセッションはありません。',
      124: '無効なセッションID：{{eventId}}',
      11: '処理するには大きすぎるセッションID：{{eventId}}',
      permissions: 'このコンテンツを表示するために必要な権限がありません。'
    },
    toggles: {
      header: 'ヘッダーの表示/非表示',
      request: 'リクエストの表示/非表示',
      response: 'レスポンスの表示/非表示',
      topBottom: '上/下表示',
      sideBySide: '並列表示',
      meta: 'メタの表示/非表示',
      expand: '拡大表示',
      shrink: '縮小表示',
      close: '再構築の終了'
    },
    eventHeader: {
      nwService: 'NWサービス',
      sessionId: 'セッションID',
      type: 'タイプ',
      source: 'ソースIP:PORT',
      destination: '宛先IP:PORT',
      service: 'サービス',
      firstPacketTime: '最初のパケットの時刻',
      lastPacketTime: '最後のパケットの時刻',
      packetSize: '計算済みパケット サイズ',
      payloadSize: '計算済みペイロード サイズ',
      packetCount: '計算済みパケット数',
      packetSizeTooltip: 'サマリ ヘッダー内の計算済みパケット サイズが、メタ詳細パネル内のパケット サイズと異なっている場合があります。これは、イベント解析の完了前にメタ データが書き込まれ、重複パケットが含まれることがあるためです。',
      payloadSizeTooltip: 'サマリ ヘッダー内の計算済みペイロード サイズが、メタ詳細パネル内のペイロード サイズと異なっている場合があります。これは、イベント解析の完了前にメタ データが書き込まれ、重複パケットが含まれることがあるためです。',
      packetCountTooltip: 'サマリ ヘッダー内の計算済みパケット数が、メタ詳細パネル内のパケット数と異なっている場合があります。これは、イベント解析の完了前にメタ データが書き込まれ、重複パケットが含まれることがあるためです。',
      deviceIp: 'デバイスIP',
      deviceType: 'デバイス タイプ',
      deviceClass: 'デバイス クラス',
      eventCategory: 'イベント カテゴリ',
      nweCategory: 'NWEカテゴリ',
      collectionTime: '収集時間',
      eventTime: 'イベント タイム',
      nweEventTime: 'イベント タイム',
      nweMachineName: 'マシン名',
      nweMachineIp: 'マシンIP',
      nweMachineUsername: 'マシン ユーザ名',
      nweMachineIiocScore: 'マシンIIOCスコア',
      nweEventSourceFilename: 'イベント ソース ファイル名',
      nweEventSourcePath: 'イベント ソース パス',
      nweEventDestinationFilename: 'イベント デスティネーション ファイル名',
      nweEventDestinationPath: 'イベント デスティネーション パス',
      nweFileFilename: 'ファイル名',
      nweFileIiocScore: 'ファイルIIOCスコア',
      nweProcessFilename: 'プロセス ファイル名',
      nweProcessParentFilename: '親ファイル名',
      nweProcessPath: 'プロセス パス',
      nweDllFilename: 'DLLファイル名',
      nweDllPath: 'DLLパス',
      nweDllProcessFilename: 'プロセス ファイル名',
      nweAutorunFilename: 'Autorunファイル名',
      nweAutorunPath: 'Autorunパス',
      nweServiceDisplayName: 'サービス表示名',
      nweServiceFilename: 'サービス ファイル名',
      nweServicePath: 'サービス パス',
      nweTaskName: 'タスク名',
      nweTaskPath: 'タスク パス',
      nweNetworkFilename: 'ネットワーク ファイル名',
      nweNetworkPath: 'ネットワーク パス',
      nweNetworkProcessFilename: 'ネットワーク プロセス ファイル名',
      nweNetworkProcessPath: 'ネットワーク プロセス パス',
      nweNetworkRemoteAddress: 'ネットワーク リモート アドレス'
    },
    contextmenu: {
      copy: 'コピー',
      externalLinks: '外部ルックアップ',
      livelookup: 'Liveルックアップ',
      endpointIoc: 'エンドポイント シック クライアント ルックアップ',
      applyDrill: '新しいタブでドリル ダウン',
      applyNEDrill: '新しいタブで!EQUALSドリル ダウン',
      refocus: '新しいタブで再フォーカスして調査',
      hostslookup: 'ホスト検索',
      external: {
        google: 'Google',
        sansiphistory: 'SANS IP History',
        centralops: 'CentralOps Whois for IPs and Hostnames',
        robtexipsearch: 'Robtex IP検索',
        ipvoid: 'IPVoid',
        urlvoid: 'URLVoid',
        threatexpert: 'ThreatExpert検索'
      }
    }
  },
  memsize: {
    B: 'バイト',
    KB: 'KB',
    MB: 'MB',
    GB: 'GB',
    TB: 'TB'
  },
  previousMonth: '前月',
  nextMonth: '翌月',
  months() {
    return [
      '1月',
      '2月',
      '3月',
      '4月',
      '5月',
      '6月',
      '7月',
      '8月',
      '9月',
      '10月',
      '11月',
      '12月'
    ];
  },
  monthsShort() {
    return [
      '1月',
      '2月',
      '3月',
      '4月',
      '5月',
      '6月',
      '7月',
      '8月',
      '9月',
      '10月',
      '11月',
      '12月'
    ];
  },
  weekdays() {
    return [
      '日曜日',
      '月曜日',
      '火曜日',
      '水曜日',
      '木曜日',
      '金曜日',
      '土曜日'
    ];
  },
  weekdaysShort() {
    return [
      '日',
      '月曜',
      '火',
      '水',
      '木',
      '金',
      '土'
    ];
  },
  weekdaysMin() {
    return [
      '日',
      '月曜',
      '火',
      '水',
      '木',
      '金',
      '土'
    ];
  },
  midnight: '午前0時',
  noon: '正午',
  investigate: {
    controls: {
      toggle: 'イベント パネルの表示/非表示',
      togglePreferences: '調査の選択の切り替え'
    },
    title: '調査',
    loading: '読み込み中',
    loadMore: 'さらに読み込み',
    tryAgain: 'もう一度実行してください',
    service: 'サービス',
    timeRange: '時間範囲',
    filter: 'フィルター',
    size: {
      bytes: 'バイト',
      KB: 'KB',
      MB: 'MB',
      GB: 'GB',
      TB: 'TB'
    },
    medium: {
      endpoint: 'エンドポイント',
      network: 'ネットワーク',
      log: 'ログ',
      correlation: '相関',
      undefined: '不明'
    },
    empty: {
      title: 'イベントは見つかりませんでした。',
      description: 'このフィルタ条件に一致するレコードはありませんでした。'
    },
    error: {
      title: 'データをロードできません。',
      description: 'データ レコードを取得しようとしたときに予期しないエラーが発生しました。'
    },
    invalidQueryError: {
      title: 'イベントは見つかりませんでした。',
      description: 'ご使用のフィルター条件は無効です。構文エラーがないかクエリを確認してください。'
    },
    meta: {
      title: 'メタ',
      clickToOpen: 'クリックして開きます'
    },
    events: {
      title: 'イベント',
      columnGroups: {
        custom: 'カスタム列グループ',
        customTitle: '［イベント］ビューでのカスタム列グループの管理',
        default: 'デフォルト列グループ',
        searchPlaceholder: '列グループのフィルターのタイプ'
      },
      error: 'このクエリの実行中に予期しないエラーが発生しました。',
      shrink: 'イベント パネルの縮小',
      expand: 'イベント パネルの拡大',
      close: 'イベント パネルを閉じる',
      scrollMessage: 'スクロール ダウンして、選択された青色のイベントを表示',
      eventTips: {
        noResults: '結果がまだありません。サービスと時間範囲を選択して、クエリを送信してください',
        head: {
          header: 'クエリ フィルターの例',
          text: {
            one: 'Mozillaの一部のバージョンのユーザー エージェントでアウトバウンドHTTPイベントを探す',
            two: '失敗したログイン ウィンドウ イベントを探す',
            three: 'ファイル名がexeで終わるタスクのエンドポイント イベントを探す'
          }
        },
        section: {
          mouse: {
            header: 'マウス操作',
            textOne: 'フィルターの前、後、間でクリックすると、別のフィルターを挿入できます。',
            textTwo: 'フィルターをクリックして右クリックすると、アクション メニューが表示されます。',
            textThree: 'フィルターをダブル クリックすると、編集用に開きます。',
            textFour: '複数のフィルターをクリックして［<span class="highlight">削除</span>］を押すと、選択されたフィルターが削除されます。',
            textFive: 'ブラウザの［<span class="highlight">戻る</span>］ボタンをクリックすると、前の状態に戻ります。'
          },
          keyboard: {
            header: 'キーボード操作',
            textOne: 'クエリ ビルダーでメタ キー名または説明を入力します。',
            textTwo: 'ドロップダウン メニューの<span class="highlight">上矢印</span>や<span class="highlight">下矢印</span>を使用し、<span class="highlight">Enter</span>キーを押して選択します。',
            textThree: '<span class="highlight">Enter</span>キーを押すか、［<span class="highlight">イベントのクエリ</span>］をクリックして、クエリを実行します。',
            textFour: '<span class="highlight">左矢印</span>または<span class="highlight">右矢印</span>を押してクエリ内を移動し、他のフィルターを追加するか、<span class="highlight">Enter</span>キーを押して既存のフィルターを編集します。',
            textFive: '複数のフィルターを削除するには、<span class="highlight">Shiftキー + 左矢印</span>または<span class="highlight">右矢印</span>を押して選択し、<span class="highlight">Backspaceキー</span>または<span class="highlight">Deleteキーを押します</span。'
          }
        }
      },
      logs: {
        wait: 'ログをロードしています...',
        rejected: 'ログ データがありません。'
      }
    },
    generic: {
      loading: 'データをロードしています...'
    },
    services: {
      loading: 'サービスをロードしています...',
      noData: '選択したサービスにはデータがありません',
      coreServiceNotUpdated: 'イベント分析では、すべてのコア サービスをNetWitness 11.1にする必要があります。以前のバージョンのサービスを11.1 NetWitness Serverに接続すると、機能が制限されます（物理ホスト アップグレード ガイドの「混合モードでのInvestigate」を参照）。',
      empty: {
        title: 'サービスが見つかりません。',
        description: 'Broker、Concentrator、その他のサービスはいずれも検出されませんでした。これは、構成または接続の問題が原因である可能性があります。'
      },
      error: {
        label: 'サービスを利用できません',
        description: '調査のためにBroker、Concentrator、その他のサービスのリストをロードしているときに予期しないエラーが発生しました。これは、構成または接続の問題が原因である可能性があります。'
      }
    },
    summary: {
      loading: 'サマリーをロードしています'
    },
    customQuery: {
      title: 'クエリを入力します。'
    }
  },
  configure: {
    title: '構成',
    liveContent: 'Liveコンテンツ',
    esaRules: 'ESAルール',
    respondNotifications: '対応の通知',
    incidentRulesTitle: 'インシデントのルール',
    subscriptions: 'サブスクリプション',
    customFeeds: 'カスタムFeed',
    incidentRules: {
      noManagePermissions: 'インシデントのルールを編集する権限がありません',
      confirm: '実行してもよろしいですか？',
      assignee: {
        none: '(未割り当て)'
      },
      priority: {
        LOW: '低',
        MEDIUM: '中',
        HIGH: '高',
        CRITICAL: 'クリティカル'
      },
      action: 'アクション',
      actionMessage: 'ルールがアラートと一致する場合のアクションを選択します',
      error: 'インシデントのルールをロードする際に問題が発生しました',
      noResults: 'インシデントのルールが見つかりませんでした',
      createRule: 'ルールの作成',
      deleteRule: '削除',
      cloneRule: 'クローン',
      select: '選択',
      order: '順序',
      enabled: '有効',
      name: '名前',
      namePlaceholder: 'ルールの固有の名前を入力してください',
      ruleNameRequired: 'ルールの名前を入力してください',
      description: '説明',
      descriptionPlaceholder: 'ルールの説明を入力します',
      lastMatched: '前回の一致',
      alertsMatchedCount: '一致したアラート',
      incidentsCreatedCount: 'インシデント',
      matchConditions: '一致条件',
      queryMode: 'クエリ モード',
      queryModes: {
        RULE_BUILDER: 'ルール ビルダー',
        ADVANCED: '詳細'
      },
      queryBuilderQuery: 'クエリ ビルダー',
      advancedQuery: '詳細',
      advancedQueryRequired: '詳細なクエリは空にすることができません',
      groupingOptions: 'グループ化オプション',
      groupBy: 'グループ化',
      groupByPlaceholder: 'フィールド別にグループを選択（必須）',
      groupByError: '少なくとも1つのフィールド別グループが必要で、最大2つまで選べます',
      timeWindow: 'タイム ウィンドウ',
      incidentOptions: 'インシデント オプション',
      incidentTitle: 'タイトル',
      incidentTitleRequired: 'このルールで作成されたインシデントのタイトルを提供する必要があります',
      incidentTitlePlaceholder: 'このルールで作成したインシデントのタイトルを入力します',
      incidentTitleHelp: 'タイトル テンプレートは、インシデント タイトルを作成するときに使用されます。たとえば、ルールの名前が「Rule-01」で、groupByフィールドが「重大度」、groupBy値が「50」、テンプレートが${ruleName} for ${groupByValue1}の場合は、名前が「Rule-01 for 50」のインシデントが作成されます。',
      incidentSummary: 'サマリー',
      incidentSummaryPlaceholder: 'このルールで作成したインシデントのサマリーを入力します',
      incidentCategories: 'カテゴリ',
      incidentCategoriesPlaceholder: 'カテゴリを選択（オプション）',
      incidentAssignee: '割り当て先',
      incidentAssigneePlaceholder: '割り当て先を選択（オプション）',
      incidentPriority: '優先度',
      incidentPriorityInstruction: '次のオプションを使用して、インシデントの優先度を設定します',
      incidentPriorityAverage: 'すべてのアラートの平均リスク スコア',
      incidentPriorityHighestScore: 'すべてのアラートで最高のリスク スコア',
      incidentPriorityAlertCount: 'タイム ウィンドウのアラートの数',
      priorityScoreError: '優先度スコアの範囲が無効です',
      confirmQueryChange: 'クエリ変更の確認',
      confirmAdvancedQueryMessage: 'クエリー ビルダー モードから詳細モードに切り替えると、一致条件がリセットされます。',
      confirmQueryBuilderMessage: '詳細モードからクエリー ビルダー モードに切り替えると、一致条件がリセットされます。',
      groupAction: 'インシデントに統合',
      suppressAction: 'アラートを抑制',
      timeUnits: {
        DAY: '日',
        HOUR: '時間',
        MINUTE: '分'
      },
      ruleBuilder: {
        addConditionGroup: 'グループの追加',
        removeConditionGroup: 'グループの削除',
        addCondition: '条件の追加',
        field: 'フィールド',
        operator: '演算子',
        operators: {
          '=': '等しい',
          '!=': '等しくない',
          'begins': '次の値で始まる',
          'ends': '次の値で終わる',
          'contains': '次の値を含む',
          'regex': 'regexと一致',
          'in': '次に含まれる(in)',
          'nin': '次に含まれない(not in)',
          '>': '次より大きい',
          '>=': '次の値以上',
          '<': '次より小さい',
          '<=': '次の値以下'
        },
        groupOperators: {
          and: 'これらのすべて',
          or: 'いずれかに合致',
          not: 'いずれにも合致しない'
        },
        value: '値',
        hasGroupsWithoutConditions: 'すべてのグループに少なくとも1つの条件が必要です',
        hasMissingConditionInfo: '少なくとも1つの条件でフィールド、演算子、または値がありません'
      },
      actionMessages: {
        deleteRuleConfirmation: 'このルールを削除してもよろしいですか? この削除を適用した後は、元に戻すことはできません。',
        reorderSuccess: 'ルールの順序が正常に変更されました',
        reorderFailure: 'ルールの順序を変更する際に問題が発生しました',
        cloneSuccess: '選択したルールのクローンが正常に作成されました',
        cloneFailure: '選択したルールのクローン作成で問題が発生しました',
        createSuccess: '新しいルールが正常に作成されました',
        createFailure: '新しいルールの作成で問題が発生しました',
        deleteSuccess: '選択したルールが正常に削除されました',
        deleteFailure: '選択したルールの削除で問題が発生しました',
        saveSuccess: 'ルールの変更は正常に保存されました',
        saveFailure: 'ルールの変更の保存で問題が発生しました',
        duplicateNameFailure: '同じ名前のルールが既に存在します。固有のルール名になるよう変更してください。'
      },
      missingRequiredInfo: 'インシデントのルールで必須の情報が入力されていません'
    },
    notifications: {
      settings: '対応の通知の設定',
      emailServer: 'メール サーバ',
      socEmailAddresses: 'SOCマネージャーのメール アドレス',
      noSocEmails: 'SOCマネージャーのメールが設定されていません',
      emailAddressPlaceholder: '追加するメール アドレスを入力します',
      addEmail: '追加',
      notificationTypes: '通知のタイプ',
      type: 'タイプ',
      sendToAssignee: '割り当て先に送信',
      sendToSOCManagers: 'SOCマネージャーに送信',
      types: {
        'incident-created': '作成されたインシデント',
        'incident-state-changed': '更新されたインシデント'
      },
      hasUnsavedChanges: '保存していない変更があります。［適用］をクリックして保存します。',
      emailServerSettings: 'メール サーバ設定',
      noManagePermissions: '対応の通知を編集する権限がありません。',
      actionMessages: {
        fetchFailure: '対応の通知の設定をロードする際に問題が発生しました',
        updateSuccess: '対応の通知の設定が正常にアップデートされました',
        updateFailure: '対応の通知の設定をアップデートする際に問題が発生しました'
      }
    }
  },
  respond: {
    title: '対応',
    common: {
      yes: 'はい',
      no: 'いいえ',
      true: 'はい',
      false: 'いいえ'
    },
    none: 'なし',
    select: '選択',
    close: '閉じる',
    empty: '（空）',
    filters: 'フィルター',
    errorPage: {
      serviceDown: 'Respond Serverはオフラインです',
      serviceDownDescription: 'Respond Serverは実行されていないか、アクセス不可です。この問題を解決するには、管理者に問い合わせてください。',
      fetchError: 'エラーが発生しました。Respond Serverはオフラインまたはアクセス不可である可能性があります。'
    },
    timeframeOptions: {
      LAST_5_MINUTES: '直近5分',
      LAST_10_MINUTES: '直近10分',
      LAST_15_MINUTES: '直近15分間',
      LAST_30_MINUTES: '直近30分',
      LAST_HOUR: '直近1時間',
      LAST_3_HOURS: '直近3時間',
      LAST_6_HOURS: '直近6時間',
      LAST_TWELVE_HOURS: '直近12時間',
      LAST_TWENTY_FOUR_HOURS: '直近24時間',
      LAST_FORTY_EIGHT_HOURS: '直近2日間',
      LAST_5_DAYS: '直近5日間',
      LAST_7_DAYS: '直近7日間',
      LAST_14_DAYS: '直近14日間',
      LAST_30_DAYS: '直近30日間',
      ALL_TIME: 'すべてのデータ'
    },
    entities: {
      incidents: 'インシデント',
      remediationTasks: 'タスク',
      alerts: 'アラート',
      actionMessages: {
        updateSuccess: '正常に変更されました',
        updateFailure: 'このレコードのフィールドの更新中に問題が発生しました',
        createSuccess: '新しいレコードが正常に追加されました',
        createFailure: 'このレコードの作成中に問題が発生しました',
        deleteSuccess: 'このレコードは正常に削除されました',
        deleteFailure: 'このレコードの削除中に問題が発生しました',
        saveSuccess: '変更は正常に保存されました',
        saveFailure: 'このレコードの保存中に問題が発生しました'
      },
      alert: 'アラート'
    },
    explorer: {
      noResults: '結果は見つかりませんでした。より多くの結果が含まれるように、時間の範囲を広げるか、既存のフィルタを調整してみてください。',
      confirmation: {
        updateTitle: '更新の確認',
        deleteTitle: '削除の確認',
        bulkUpdateConfrimation: '次の変更を、1つ以上のアイテムに加えようとしています',
        deleteConfirmation: '{{count}}件のレコードを削除してもよろしいですか？ この削除を適用した後は、元に戻すことはできません。',
        field: 'フィールド',
        value: '値',
        recordCountAffected: 'アイテム数'
      },
      filters: {
        timeRange: '時間範囲',
        reset: 'フィルタのリセット',
        customDateRange: 'カスタムの日付範囲',
        customStartDate: '開始日',
        customEndDate: '終了日',
        customDateErrorStartAfterEnd: '開始日時を終了日以降に設定することはできません'
      },
      inspector: {
        overview: '概要'
      },
      footer: '{{total}}件中{{count}}件を表示中'
    },
    remediationTasks: {
      loading: 'タスクの読み込み中',
      addNewTask: '新しいタスクの追加',
      noTasks: '{{incidentId}}のタスクはありません',
      openFor: 'オープン済み',
      newTaskFor: '新しいタスク：',
      delete: 'タスクの削除',
      noAccess: 'タスクを表示する権限がありません',
      actions: {
        actionMessages: {
          deleteWarning: 'NetWitnessからタスクを削除しても、他のシステムからは削除されません。他の該当するシステムからタスクを削除する操作は、' +
          'お客様の責任で行ってください'
        }
      },
      filters: {
        taskId: 'タスクID',
        idFilterPlaceholder: '例: REM-123',
        idFilterError: 'IDは、次の形式に一致している必要があります。REM-###'
      },
      list: {
        priority: '優先度',
        select: '選択',
        id: 'ID',
        name: '名前',
        createdDate: '作成日',
        status: 'ステータス',
        assignee: '割り当て先',
        noResultsMessage: '一致するタスクが見つかりませんでした',
        incidentId: 'インシデントID',
        targetQueue: 'ターゲット キュー',
        remediationType: 'タイプ',
        escalated: 'エスカレーション',
        lastUpdated: '最終更新',
        description: '説明',
        createdBy: '作成者'
      },
      type: {
        QUARANTINE_HOST: 'ホストの検疫',
        QUARANTINE_NETORK_DEVICE: 'ネットワーク デバイスの検疫',
        BLOCK_IP_PORT: 'IP/ポートのブロック',
        BLOCK_EXTERNAL_ACCESS_TO_DMZ: 'DMZへの外部アクセスのブロック',
        BLOCK_VPN_ACCESS: 'VPNアクセスのブロック',
        REIMAGE_HOST: 'ホストの再イメージ化',
        UPDATE_FIREWALL_POLICY: 'ファイアウォール ポリシーの更新',
        UPDATE_IDS_IPS_POLICY: 'IDS/IPSポリシーの更新',
        UPDATE_WEB_PROXY_POLICY: 'Webプロキシ ポリシーの更新',
        UPDATE_ACCESS_POLICY: 'アクセス ポリシーの更新',
        UPDATE_VPN_POLICY: 'VPNポリシーの更新',
        CUSTOM: 'カスタム',
        MITIGATE_RISK: 'リスクの低減',
        MITIGATE_COMPLIANCE_VIOLATION: 'コンプライアンス違反の低減',
        MITIGATE_VULNERABILITY_THREAT: '脆弱性/脅威の低減',
        UPDATE_CORPORATE_BUSINESS_POLICY: '企業/ビジネス ポリシーの更新',
        NOTIFY_BC_DR_TEAM: 'BC/DRチームへの通知',
        UPDATE_RULES: 'ルールの更新',
        UPDATE_FEEDS: 'Feedの更新'
      },
      targetQueue: {
        OPERATIONS: 'Operations',
        GRC: 'GRC',
        CONTENT_IMPROVEMENT: 'Content Improvement'
      },
      noDescription: 'このタスクの説明はありません'
    },
    incidents: {
      incidentName: 'インシデント名',
      actions: {
        addEntryLabel: 'エントリーの追加',
        confirmUpdateTitle: '更新の確認',
        changeAssignee: '割り当て先の変更',
        changePriority: '優先度の変更',
        changeStatus: 'ステータス変更',
        addJournalEntry: 'ジャーナル エントリーの追加',
        actionMessages: {
          deleteWarning: '警告：タスクがあり、エスカレーションされている可能性のある1つ以上のインシデントを削除しようとしています。' +
          'NetWitnessからインシデントを削除しても、他のシステムからは削除されません。他の該当するシステムからインシデントとそのタスクを削除する操作は、' +
          'お客様の責任で行ってください',
          addJournalEntrySuccess: 'インシデント{{incidentId}}にジャーナル エントリーを追加しました。',
          addJournalEntryFailure: 'インシデント{{incidentId}}へのジャーナル エントリーの追加中に問題が発生しました。',
          incidentCreated: '選択したアラートからインシデント{{incidentId}}が正常に作成されました。インシデントの優先度はデフォルトでLOWに設定されています。',
          incidentCreationFailed: '選択したアラートからのインシデントの作成中に問題が発生しました',
          createIncidentInstruction: '選択した{{alertCount}}個のアラートからインシデントが作成されます。インシデントの名前を入力してください。',
          addAlertToIncidentSucceeded: '選択したアラートが{{incidentId}}に正常に追加されました',
          addAlertToIncidentFailed: '選択したアラートをこのインシデントに追加する際に問題が発生しました'
        },
        deselectAll: 'すべて選択解除'
      },
      filters: {
        timeRange: '時間範囲',
        incidentId: 'インシデントID',
        idFilterPlaceholder: '例: INC-123',
        idFilterError: 'IDは、次の形式に一致している必要があります。INC-###',
        reset: 'フィルタのリセット',
        customDateRange: 'カスタムの日付範囲',
        customStartDate: '開始日',
        customEndDate: '終了日',
        customDateErrorStartAfterEnd: '開始日時を終了日以降に設定することはできません',
        showOnlyUnassigned: '割り当てられていないインシデントのみを表示します'
      },
      selectionCount: '{{selectionCount}}件選択済み',
      label: 'インシデント',
      list: {
        select: '選択',
        id: 'ID',
        name: '名前',
        createdDate: '作成日',
        status: 'ステータス',
        priority: '優先度',
        riskScore: 'リスク スコア',
        assignee: '割り当て先',
        alertCount: 'アラート',
        sources: 'ソース',
        noResultsMessage: '一致するインシデントが見つかりませんでした'
      },
      footer: '{{total}}個中{{count}}個のインシデントを表示中'
    },
    alerts: {
      createIncident: 'インシデントの作成',
      addToIncident: 'インシデントへの追加',
      incidentSearch: {
        searchInputLabel: '未解決インシデントの検索',
        searchInputPlaceholder: 'インシデントID（例：INC-123）またはインシデント名で検索',
        noResults: '未解決インシデントは見つかりませんでした',
        noQuery: '上の検索ボックスを使用して、名前またはIDで未解決インシデントを検索します 検索には（3）文字以上含める必要があります',
        error: 'インシデントの検索中に問題が発生しました'
      },
      actions: {
        actionMessages: {
          deleteWarning: '警告：インシデントに関連づけられている可能性のある1つ以上のアラートを削除しようとしています。' +
          '関連づけられているインシデントはすべて同様に更新または削除されますので注意してください。'
        }
      },
      list: {
        receivedTime: '作成日',
        severity: '重大度',
        numEvents: 'イベント数',
        id: 'ID',
        name: '名前',
        status: 'ステータス',
        source: 'ソース',
        incidentId: 'インシデントID',
        partOfIncident: 'インシデント生成',
        type: 'タイプ',
        hostSummary: 'ホスト サマリ',
        userSummary: 'ユーザ サマリ'
      },
      notAssociatedWithIncident: '（なし）',
      originalAlert: '未フォーマットのアラート',
      originalAlertLoading: '未フォーマットのアラートのロード中',
      originalAlertError: '未フォーマットのアラートのロード中に問題が発生しました。',
      alertNames: 'アラート名'
    },
    alert: {
      status: {
        GROUPED_IN_INCIDENT: 'インシデントにグループ化',
        NORMALIZED: '正規化'
      },
      type: {
        Correlation: '相関',
        Log: 'ログ',
        Network: 'ネットワーク',
        'Instant IOC': 'インスタントIOC',
        'Web Threat Detection Incident': 'Web Threat Detectionインシデント',
        'File Share': 'ファイル共有',
        'Manual Upload': '手動アップロード',
        'On Demand': 'オン デマンド',
        Resubmit: '再実行',
        Unknown: '不明'
      },
      source: {
        ECAT: 'エンドポイント',
        'Event Stream Analysis': 'Event Stream Analysis',
        'Event Streaming Analytics': 'Event Stream Analysis',
        'Security Analytics Investigator': 'Security Analytics Investigator',
        'Web Threat Detection': 'Web Threat Detection',
        'Malware Analysis': 'Malware Analysis',
        'Reporting Engine': 'Reporting Engine',
        'NetWitness Investigate': 'NetWitness Investigate'
      },
      backToAlerts: 'アラートに戻る'
    },
    incident: {
      created: '作成日',
      status: 'ステータス',
      priority: '優先度',
      riskScore: 'リスク スコア',
      assignee: '割り当て先',
      alertCount: '個のインジケータ',
      eventCount: '個のイベント',
      catalystCount: '要因',
      sealed: '封印済み',
      sealsAt: '封印場所',
      sources: 'ソース',
      categories: 'カテゴリ',
      backToIncidents: 'インシデントに戻る',
      overview: '概要',
      indicators: 'インジケータ',
      indicatorsCutoff: '{{expected}}個中{{limit}}個のインジケータを表示中',
      events: 'イベント',
      loadingEvents: 'イベントをロードしています...',
      view: {
        graph: '表示：グラフ',
        datasheet: '表示：データ シート'
      },
      journalTasksRelated: 'ジャーナル、タスク、関連',
      search: {
        tab: '関係',
        title: '関連インジケータ',
        subtext: '下の値を入力し、［検索］ボタンをクリックして、その値に関連するその他のインジケータを探します。',
        partOfThisIncident: 'このインシデント生成',
        types: {
          IP: 'IP',
          MAC_ADDRESS: 'MAC',
          HOST: 'ホスト',
          DOMAIN: 'ドメイン',
          FILE_NAME: 'ファイル名',
          FILE_HASH: 'ハッシュ',
          USER: 'ユーザー',
          label: '検索'
        },
        text: {
          label: '値',
          placeholders: {
            IP: 'IPアドレスを入力',
            MAC_ADDRESS: 'MACアドレスを入力',
            HOST: 'ホスト名を入力',
            DOMAIN: 'ドメイン名を入力',
            FILE_NAME: 'ファイル名を入力',
            FILE_HASH: 'ファイル ハッシュを入力',
            USER: 'ユーザ名を入力'
          }
        },
        timeframe: {
          label: '期間'
        },
        devices: {
          source: 'ソース',
          destination: '宛先',
          detector: '検知器',
          domain: 'ドメイン',
          label: '検査'
        },
        results: {
          title: '以下を示すインジケータ',
          openInNewWindow: '新規ウィンドウで開く'
        },
        actions: {
          search: '検索',
          cancel: 'キャンセル',
          addToIncident: 'インシデントへの追加',
          addingAlert: 'アラートをインシデントに追加中',
          unableToAddAlert: 'アラートをインシデントに追加できません。',
          pleaseTryAgain: 'もう一度実行してください。'
        }
      }
    },
    storyline: {
      loading: 'インシデントのストーリーラインをロード中',
      error: 'インシデントのストーリーラインをロードできません',
      catalystIndicator: '要因インジケータ',
      relatedIndicator: '関連インジケータ',
      source: 'ソース',
      partOfIncident: 'インシデント生成',
      relatedBy: '次により要因に関連',
      event: 'イベント',
      events: 'イベント'
    },
    details: {
      loading: 'インシデントの詳細をロード中',
      error: 'インシデントの詳細をロードできません'
    },
    journal: {
      newEntry: '新しいジャーナル エントリー',
      title: 'ジャーナル',
      close: '閉じる',
      milestone: 'マイルストーン',
      loading: 'ジャーナル エントリーのロード中',
      noEntries: '{{incidentId}}のジャーナル エントリーはありません',
      delete: 'エントリーの削除',
      deleteConfirmation: 'このジャーナル エントリーを削除してもよろしいですか？ このアクションは元に戻すことができません。',
      noAccess: 'ジャーナル エントリーを表示する権限がありません'
    },
    milestones: {
      title: 'マイルストーン',
      RECONNAISSANCE: '予備調査',
      DELIVERY: '配信',
      EXPLOITATION: '悪用',
      INSTALLATION: 'インストール',
      COMMAND_AND_CONTROL: 'コマンド&コントロール',
      ACTION_ON_OBJECTIVE: '意図されたアクション',
      CONTAINMENT: '封じ込め',
      ERADICATION: '除去',
      CLOSURE: '終了'
    },
    eventDetails: {
      title: 'イベントの詳細',
      events: 'イベント',
      in: 'in',
      indicators: 'インジケータ',
      type: {
        'Instant IOC': 'インスタントIOC',
        'Log': 'ログ',
        'Network': 'ネットワーク',
        'Correlation': '相関',
        'Web Threat Detection': 'Web Threat Detection',
        'Web Threat Detection Incident': 'Web Threat Detectionインシデント',
        'Unknown': 'イベント',
        'File Share': 'ファイル共有',
        'Manual Upload': '手動アップロード',
        'On Demand': 'オン デマンド',
        Resubmit: '再実行'
      },
      backToTable: 'テーブルに戻る',
      labels: {
        timestamp: 'タイムスタンプ',
        type: 'タイプ',
        description: '説明',
        source: 'ソース',
        destination: '宛先',
        domain: 'ドメイン/ホスト',
        detector: '検知器',
        device: 'デバイス',
        ip_address: 'IPアドレス',
        mac_address: 'MACアドレス',
        dns_hostname: 'ホスト',
        dns_domain: 'ドメイン',
        netbios_name: 'NetBIOS名',
        asset_type: '資産タイプ',
        business_unit: 'ビジネス ユニット',
        facility: 'ファシリティ',
        criticality: '重要度',
        compliance_rating: 'Compliance_rating',
        malicious: '悪意のある',
        site_categorization: 'サイト分類',
        geolocation: 'GeoLocation',
        city: '市区町村',
        country: '国',
        longitude: '経度',
        latitude: '緯度',
        organization: '組織',
        device_class: 'デバイス クラス',
        product_name: '製品名',
        port: 'ポート',
        user: 'ユーザー',
        username: 'ユーザー名',
        ad_username: 'Active Directoryユーザ名',
        ad_domain: 'Active Directoryドメイン',
        email_address: 'メール アドレス',
        os: 'オペレーティング システム',
        size: 'サイズ',
        data: 'データ',
        filename: 'ファイル名',
        hash: 'ハッシュ',
        av_hit: 'AVヒット',
        extension: '拡張子',
        mime_type: 'MIMEタイプ',
        original_path: 'オリジナル パス',
        av_aliases: 'AVエイリアス',
        networkScore: 'ネットワーク スコア',
        communityScore: 'コミュニティ スコア',
        staticScore: '静的スコア',
        sandboxScore: 'サンドボックス スコア',
        opswat_result: 'OPSWAT結果',
        yara_result: 'YARA結果',
        bit9_status: 'Bit9ステータス',
        module_signature: 'モジュール シグネチャ',
        related_links: '関連リンク',
        url: 'URL',
        ecat_agent_id: 'NWEエージェントID',
        ldap_ou: 'LDAP OU',
        last_scanned: '最終スキャン',
        enrichment: 'エンリッチメント',
        enrichmentSections: {
          domain_registration: 'ドメイン登録',
          command_control_risk: 'コマンド&コントロール',
          beaconing_behavior: 'ビーコニング',
          domain_age: 'ドメイン エイジ',
          expiring_domain: 'ドメイン有効期限',
          rare_domain: 'レア ドメイン',
          no_referers: 'リファラー',
          rare_user_agent: 'レア ユーザ エージェント'
        },
        registrar_name: 'ドメイン レジストラ',
        registrant_organization: '登録者の組織',
        registrant_name: '登録者の氏名',
        registrant_email: '登録者のメール',
        registrant_telephone: '登録者の電話',
        registrant_street1: '登録者の住所(番地)',
        registrant_postal_code: '登録者の郵便番号',
        registrant_city: '登録者の市町村',
        registrant_state: '登録者の都道府県',
        registrant_country: '登録者の国',
        whois_created_dateNetWitness: '登録日',
        whois_updated_dateNetWitness: '更新日',
        whois_expires_dateNetWitness: '有効期限',
        whois_age_scoreNetWitness: 'ドメイン登録エイジ スコア',
        whois_validity_scoreNetWitness: 'ドメイン有効期限スコア',
        whois_estimated_domain_age_daysNetWitness: 'ドメイン登録エイジ（日数）',
        whois_estimated_domain_validity_daysNetWitness: '有効期限までの残り日数',
        command_control_aggregate: 'コマンド アンド コントロール リスク スコア',
        command_control_confidence: '信頼度',
        weighted_c2_referer_score: 'レア ドメイン スコアの貢献度(このネットワーク)',
        weighted_c2_referer_ratio_score: 'リファラなしスコアの貢献度',
        weighted_c2_ua_ratio_score: 'レア ユーザ エージェント スコアの貢献度',
        weighted_c2_whois_age_score: 'ドメイン登録エイジ スコアの貢献度',
        weighted_c2_whois_validity_score: 'ドメイン有効期限スコアの貢献度',
        smooth_score: 'スコア',
        beaconing_period: '期間',
        newdomain_score: 'ドメイン エイジ スコア(このネットワーク)',
        newdomain_age: 'ドメイン エイジ(このネットワーク)',
        referer_score: 'レア スコア',
        referer_cardinality: 'レア カーディナリティ',
        referer_num_events: 'レア イベント',
        referer_ratio: 'レア 比率',
        referer_ratio_score: 'レア 比率スコア',
        referer_cond_cardinality: 'レア 条件付きカーディナリティ',
        ua_num_events: '過去1週間の発生回数',
        ua_ratio: 'レア ユーザ エージェントのIPの割合',
        ua_ratio_score: 'レア ユーザ エージェント スコア',
        ua_cond_cardinality: 'レア ユーザ エージェントのIP'
      },
      periodValue: {
        hours: '時間',
        minutes: '分',
        seconds: '秒'
      }
    },
    eventsTable: {
      time: '時間',
      type: 'タイプ',
      sourceDomain: 'ソース ドメイン',
      destinationDomain: '宛先ドメイン',
      sourceHost: 'ソース ホスト',
      destinationHost: '宛先ホスト',
      sourceIP: 'ソースIP',
      destinationIP: '宛先IP',
      detectorIP: '検知器のIP',
      sourcePort: 'ソース ポート',
      destinationPort: '宛先ポート',
      sourceMAC: 'ソースMAC',
      destinationMAC: '宛先MAC',
      sourceUser: 'ソース ユーザ',
      destinationUser: '宛先ユーザ',
      fileName: 'ファイル名',
      fileHash: 'ファイル ハッシュ',
      indicator: 'インジケータ'
    },
    entity: {
      legend: {
        user: 'ユーザー',
        host: 'ホスト',
        ip: 'IP',
        domain: 'ドメイン',
        mac_address: 'MAC',
        file_name: 'ファイル',
        file_hash: 'ハッシュ',
        selection: {
          storyPoint: '対象：選択した{{count}}個のインジケータ',
          event: '対象：選択した{{count}}個のイベント'
        },
        selectionNotShown: 'サイズ制限により、選択したノードを表示できませんでした。',
        hasExceededNodeLimit: '最初の{{limit}}ノードのみを表示中',
        showAll: 'すべてのデータの表示'
      }
    },
    enrichment: {
      uniformTimeIntervals: '通信イベント間のインターバルが非常に均一です。',
      newDomainToEnvironment: 'この環境に比較的新しいドメインです。',
      rareDomainInEnvironment: 'この環境では一般的でないドメインです。',
      newDomainRegistration: '登録日によると、比較的新しいドメインです。',
      domainRegistrationExpires: 'ドメイン登録が比較的短い期間で期限切れになります。',
      rareUserAgent: 'ドメインに接続中の高い比率のホストが、レア ユーザ エージェントを使用しているか、ユーザ エージェントを使用していません。',
      noReferers: 'ドメインに接続中の高い比率のホストがリファラーを使用していません。',
      highNumberServersAccessed: '異常に多数のサーバが今日アクセスされました。',
      highNumberNewServersAccessed: '異常に多数の新しいサーバに今日アクセスしました。',
      highNumberNewDevicesAccessed: '異常に多数の新しいデバイスに今週アクセスしました。',
      highNumberFailedLogins: '異常に多数のサーバで今日ログインが失敗しました。',
      passTheHash: '新しいデバイスによって、次に新しいサーバによって潜在的な「Pass the Hash」攻撃が示されています。',
      rareLogonType: 'これまでにめったに使用したことのないWindowsログオン タイプを使用してアクセスしました。',
      authFromRareDevice: 'めったに使用されないデバイスから認証されました。',
      authFromRareLocation: 'めったに使用されない場所からアクセスされました。',
      authFromRareServiceProvider: 'めったに使用されないサービス プロバイダを使用してアクセスされました。',
      authFromNewServiceProvider: '新しいサービス プロバイダを使用してアクセスされました。',
      highNumberVPNFailedLogins: 'VPNログインの失敗回数が多数です。',
      daysAgo: '{{days}}日前',
      days: '{{days}}日',
      domainIsWhitelisted: 'ドメインはホワイトリストに登録されています。',
      domainIsNotWhitelisted: 'ドメインはホワイトリストに登録されていません。'
    },
    sources: {
      'C2-Packet': 'ユーザ エンティティ動作分析',
      'C2-Log': 'ユーザ エンティティ動作分析',
      'UBA-WinAuth': 'ユーザ エンティティ動作分析',
      UbaCisco: 'ユーザ エンティティ動作分析',
      ESA: 'Event Stream Analyst',
      'Event-Stream-Analysis': 'Event Stream Analyst',
      RE: 'Reporting Engine',
      'Reporting-Engine': 'Reporting Engine',
      ModuleIOC: 'エンドポイント',
      ECAT: 'エンドポイント',
      generic: 'NetWitness'
    },
    status: {
      NEW: '新規',
      ASSIGNED: '割り当て済み',
      IN_PROGRESS: '対応中',
      REMEDIATION_REQUESTED: 'タスク リクエスト中',
      REMEDIATION_COMPLETE: 'タスク完了',
      CLOSED: 'クローズ',
      CLOSED_FALSE_POSITIVE: 'クローズ- False Positive',
      REMEDIATED: '改善済み',
      RISK_ACCEPTED: 'リスク受容',
      NOT_APPLICABLE: '該当なし'
    },
    priority: {
      LOW: '低',
      MEDIUM: '中',
      HIGH: '高',
      CRITICAL: 'クリティカル'
    },
    assignee: {
      none: '(未割り当て)'
    }
  },
  context: {
    noData: '一致するコンテキストがありません',
    noResults: '（結果なし）',
    notConfigured: '（未構成）',
    title: '次のコンテキスト',
    lastUpdated: '最終更新: ',
    timeWindow: 'タイム ウィンドウ: ',
    iiocScore: 'IIOCスコア',
    IP: 'IP',
    USER: 'ユーザー',
    MAC_ADDRESS: 'MACアドレス',
    HOST: 'ホスト',
    FILE_NAME: 'ファイル名',
    FILE_HASH: 'ファイル ハッシュ',
    DOMAIN: 'ドメイン',
    noValues: 'コンテキスト ソースに値がありません。',
    dsNotConfigured: 'コンテキスト ソースが構成されていません。',
    marketingText: ' は、Context Hubで現在構成されているデータ ソースではありません。この機能を有効にするには、管理者に連絡してください。Context Hubは、エンドポイント、アラート、インシデント、リスト、多くのオン デマンド ソースからのデータ ソースを一元化します。詳細については、［ヘルプ］をクリックしてください。',
    lcMarketingText: 'Live Connectは、さまざまなソースからのIPアドレス、ドメイン、ファイル ハッシュなどの脅威インテリジェンス データの収集、分析、評価を行います。Live Connectは、Context Hubのデフォルト データ ソースではないため、手動で有効にする必要があります。詳細については、［ヘルプ］をクリックしてください。',
    timeUnit: {
      allData: 'すべてのデータ',
      HOUR: '時間',
      HOURS: '時間',
      MINUTE: '分',
      MINUTES: '分',
      DAY: '日',
      DAYS: '日',
      MONTH: '月',
      MONTHS: '月',
      YEAR: '年',
      YEARS: '年',
      WEEK: '週',
      WEEKS: '週'
    },
    marketingDSType: {
      Users: 'Active Directory',
      Alerts: '対応（アラート）',
      Incidents: '対応（インシデント）',
      Machines: 'エンドポイント（マシン）',
      Modules: 'エンドポイント（モジュール）',
      IOC: 'エンドポイント（IOC）',
      Archer: 'Archer',
      LIST: 'リスト'
    },
    header: {
      title: {
        archer: 'Archer',
        users: 'Active Directory',
        alerts: 'アラート',
        incidents: 'インシデント',
        lIST: 'リスト',
        endpoint: 'NetWitness EndPoint',
        liveConnectIp: 'Live Connect',
        liveConnectFile: 'Live Connect',
        liveConnectDomain: 'Live Connect'
      },
      archer: 'Archer',
      overview: '概要',
      iioc: 'IIOC',
      users: 'ユーザー',
      categoryTags: 'カテゴリ タグ',
      modules: 'モジュール',
      incidents: 'インシデント',
      alerts: 'アラート',
      files: 'ファイル',
      lists: 'リスト',
      feeds: 'Feed',
      endpoint: 'エンドポイント',
      liveConnect: 'Live Connect',
      unsafe: '安全でない',
      closeButton: {
        title: '閉じる'
      },
      help: {
        title: 'ヘルプ'
      }
    },
    toolbar: {
      investigate: '調査',
      endpoint: 'NetWitness Endpoint',
      googleLookup: 'Googleルックアップ',
      virusTotal: 'VirusTotalルックアップ',
      addToList: 'リストに追加'
    },
    hostSummary: {
      title: 'エンドポイント',
      riskScore: 'リスク スコア',
      modulesCount: 'モジュール数',
      iioc0: 'Iioc 0',
      iioc1: 'Iioc 1',
      lastUpdated: '最終更新',
      adminStatus: '管理ステータス',
      lastLogin: '最終ログイン',
      macAddress: 'MACアドレス',
      operatingSystem: 'オペレーティング システム',
      machineStatus: 'コンピューターのステータス',
      ipAddress: 'IPAddress',
      endpoint: '4.Xエンドポイント エージェントがインストールされているホストに適用されます。Netwitnessエンドポイント シック クライアントをインストールしてください。'
    },
    addToList: {
      title: 'リストへの追加/削除',
      create: '新しいリストの作成',
      metaValue: 'メタ値',
      newList: '新しいリストの作成',
      tabAll: 'すべて',
      tabSelected: '選択済み',
      tabUnselected: '未選択',
      cancel: 'キャンセル',
      save: '保存',
      name: 'リスト名',
      listTitle: 'リスト',
      descriptionTitle: '説明',
      filter: '結果のフィルタ処理',
      listName: 'リスト名の入力',
      headerMessage: '［保存］をクリックしてリストを更新します。ページを更新して最新情報を表示します。'
    },
    ADdata: {
      title: 'ユーザ情報',
      employeeID: '従業員ID',
      department: '部門',
      location: '場所',
      manager: 'マネージャ',
      groups: 'グループ',
      company: '会社',
      email: 'メール',
      phone: '電話番号',
      jobTitle: '役職',
      lastLogon: '最終ログオン',
      lastLogonTimeStamp: '最終ログオンのタイム スタンプ',
      adUserID: 'バナー ユーザID',
      distinguishedName: '識別名',
      displayName: '表示名'
    },
    archer: {
      title: 'Archer',
      criticalityRating: '重要度評価',
      riskRating: 'リスク評価',
      deviceName: 'デバイス名',
      hostName: 'ホスト名',
      deviceId: 'デバイスID',
      deviceType: 'デバイス タイプ',
      deviceOwner: 'デバイス管理責任者',
      deviceOwnerTitle: 'デバイス管理責任者の役職',
      businessUnit: 'ビジネス ユニット',
      facility: 'ファシリティ',
      ipAddress: '内部IPアドレス'
    },
    modules: {
      title: '最も疑わしいモジュール',
      iiocScore: 'IIOCスコア',
      moduleName: 'モジュール名',
      analyticsScore: '分析スコア',
      machineCount: 'マシン数',
      signature: 'シグネチャ'
    },
    iiocs: {
      title: 'マシンIOCレベル',
      lastExecuted: 'LastExecuted',
      description: '説明',
      iOCLevel: 'IOCレベル',
      header: ''
    },
    incident: {
      title: 'インシデント',
      averageAlertRiskScore: 'リスク スコア',
      _id: 'ID',
      name: '名前',
      created: '作成日',
      status: 'ステータス',
      assignee: '割り当て先',
      alertCount: 'アラート',
      priority: '優先度',
      header: ''
    },
    alerts: {
      title: 'アラート',
      risk_score: '重大度',
      source: 'ソース',
      name: '名前',
      numEvents: 'イベント数',
      severity: '重大度',
      created: '作成日',
      id: 'インシデントID',
      timestamp: 'タイムスタンプ',
      header: ''
    },
    list: {
      title: 'リスト',
      createdByUser: '作成者',
      createdTimeStamp: '作成日',
      lastModifiedTimeStamp: '更新日',
      dataSourceDescription: '説明',
      dataSourceName: '名前',
      data: 'データ'
    },
    lc: {
      reviewStatus: 'レビュー ステータス',
      status: 'ステータス',
      notReviewed: '未レビュー',
      noFeedback1: 'フィードバック解析がまだありません',
      noFeedback2: ' - Live Connect脅威コミュニティのアクティブ メンバになって,リスク評価を行ってください',
      blankField: '-',
      modifiedDate: '変更日',
      reviewer: 'レビューアー',
      riskConfirmation: 'リスクの確認',
      safe: 'セキュリティ',
      unsafe: '安全でない',
      unknown: '不明',
      suspicious: '不審である',
      highRisk: '高リスク',
      high: '高',
      med: '中',
      low: '低',
      riskTags: 'リスク指標タグ',
      commActivity: 'コミュニティ アクティビティ',
      firstSeen: '最初の存在確認',
      activitySS: 'アクティビティのスナップショット',
      communityTrend: 'コミュニティ アクティビティのトレンド分析（過去30日間）',
      submitTrend: '送信アクティビティのトレンド分析（過去30日間）',
      communityActivityDesc1: 'コミュニティの<span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}}%</span>が<span class="rsa-context-panel__liveconnect__entity">{{value}}</span>を確認',
      communityActivityDesc2: '確認された<span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}}%</span>のうち、コミュニティの<span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}}%</span>がフィードバックを送信',
      submittedActivityDesc1: 'フィードバックを送信した<span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}}%</span>のうち、',
      submittedActivityDesc2: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__high-risk">{{highrisk}}%</span>が「高リスク」とマーク',
      submittedActivityDesc3: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unsafe">{{unsafe}}%</span>が「安全でない」とマーク',
      submittedActivityDesc4: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__suspicious">{{suspicious}}%</span>が「不審である」とマーク',
      submittedActivityDesc5: '（チャートには非表示）',
      submittedActivityDesc6: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__safe">{{safe}}%</span>が「安全」とマーク',
      submittedActivityDesc7: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unknown">{{unknown}}%</span>が「不明」とマーク',
      riskIndicators: 'リスク インジケータ',
      identity: 'ID',
      asn: 'ASN（Autonomous System Number）',
      prefix: 'プレフィックス',
      countryCode: '国コード',
      countryName: '国名',
      organization: '組織',
      fileDate: '日付',
      fileName: 'ファイル名',
      fileSize: 'ファイル サイズ',
      md5: 'MD5',
      compileTime: 'コンパイル時間',
      sh1: 'SH1',
      mimeType: 'MIMEタイプ',
      sh256: 'SH256',
      certificateInfo: '証明書情報',
      certIssuer: '証明書の発行者',
      certSubject: '証明書の主体者',
      certSerial: '証明書のシリアル番号',
      certSigAlgo: '署名のアルゴリズム',
      certThumbprint: '証明書の副署名',
      certNotValidBefore: '証明書の発効日',
      certNotValidAfter: '証明書の有効期限',
      whois: 'WHOIS',
      whoisCreatedDate: '作成日',
      whoisUpdatedDate: '更新日',
      whoisExpiredDate: '失効日',
      whoisRegType: 'タイプ',
      whoisRegName: '名前',
      whoisRegOrg: '組織',
      whoisRegStreet: '番地',
      whoisRegCity: '市区町村',
      whoisRegState: '都道府県',
      whoisPostalCode: '郵便番号',
      whoisCountry: '国',
      whoisPhone: '電話番号',
      whoisFax: 'Fax',
      whoisEmail: 'メール',
      domain: 'ドメイン',
      ipAddress: 'IPアドレス',
      errorMsg: 'Live Connectからデータを取得できませんでした: {{error}}',
      riskAssessment: 'Live Connectリスク評価',
      riskReason: 'リスクの理由',
      highRiskDesc: 'インジケータが高リスクを示しているため、特に注意する必要があります',
      safeRiskDesc: '調査と分析では、インジケータが、信頼できるリソースであることが示されています',
      unsafeRiskDesc: '調査と分析では、インジケータが、信頼できないことが示されています',
      unknownRiskDesc: '使用可能なすべての情報、調査、分析からは、決定的な結果が得られませんでした',
      suspiciousRiskDesc: '調査と分析では、脅威となるアクティビティである可能性が示されています',
      riskFeedback: 'リスク評価のフィードバック',
      relatedFiles: '関連ファイル ',
      risk: 'LCリスク評価',
      importHashFunction: 'API関数インポート ハッシュ',
      compiledTime: 'コンパイル日',
      relatedDomains: '関連ドメイン ',
      relatedIps: '関連IP ',
      country: '国',
      registeredDate: '登録日',
      expiredDate: '失効日',
      email: '登録者のメール',
      asnShort: 'ASN',
      confidenceLevel: '信頼度レベル',
      select: '選択...',
      feedbackSubmitted: 'フィードバックがLive Connectサーバに送信されました。',
      feedbackSubmissionFailed: 'フィードバックをLive Connectサーバに送信できませんでした。',
      feedbackFormInvalid: '「リスクの確認」と「信頼度レベル」を選択します。',
      noTrendingCommunityActivity: '過去30日間に、新しいコミュニティ アクティビティはありません。',
      noTrendingSubmissionActivity: '過去30日間に、新しい送信はありません。',
      skillLevel: 'アナリスト スキル レベル',
      skillLevelPrefix: '階層{{level}}',
      noRelatedData: 'このエンティティには関連{{entity}}はありません。',
      ips: 'IP',
      files: 'ファイル',
      domains: 'ドメイン'
    },
    error: {
      error: 'データを取得しようとしているときに予期しないエラーが発生しました:',
      noDataSource: '構成/有効化されているデータ ソースがありません。',
      dataSourcesFailed: '構成されたデータ ソースからデータを取得できません。',
      dataSource: 'データを取得しようとしているときに予期しないエラーが発生しました:',
      noData: 'このデータ ソースに使用可能なコンテキスト データはありません。',
      listDuplicateName: 'リスト名はすでに存在しています。',
      listValidName: '有効なリスト名（長さは最大255文字）を入力してください。',
      'mongo.error': '予期しないデータベース エラーが発生しました。',
      'total.entries.exceed.max': 'リスト サイズが、上限である100000を超えています。',
      'admin.error': 'adminサービスにアクセスできません。サービス接続性を確認してください。',
      'datasource.disk.usage.high': 'ディスク領域が少なくなっています。不要なデータを削除して空き領域を増やしてください。',
      'context.service.timeout': 'Context Hubサービスにアクセスできません。サービス接続性を確認してください。',
      'get.mongo.connect.failed': 'データベースにアクセスできません。時間をおいてから再試行してください。',
      'datasource.query.not.supported': 'このメタではコンテキスト データ ルックアップがサポートされていません。',
      'transport.http.read.failed': 'データ ソースにアクセスできないため、コンテキスト データは使用できません。',
      'transport.ad.read.failed': 'データ ソースにアクセスできないため、コンテキスト データは使用できません。',
      'transport.init.failed': 'データ ソース接続がタイムアウトになりました。',
      'transport.not.found': 'データ ソースにアクセスできないため、コンテキスト データは使用できません。',
      'transport.create.failed': 'データ ソースにアクセスできないため、コンテキスト データは使用できません。',
      'transport.refresh.failed': 'データ ソースにアクセスできないため、コンテキスト データは使用できません。',
      'transport.connect.failed': 'データ ソースにアクセスできないため、コンテキスト データは使用できません。',
      'live.connect.private.ip.unsupported': 'Live Connectでは、パブリックIPアドレスのみがサポートされています。',
      'transport.http.error': 'このデータ ソースからエラーが返されたため、コンテキスト ルックアップは失敗しました。',
      'transport.validation.error': 'データ ソースでサポートされていないデータ フォーマットです。',
      'transport.http.auth.failed': 'このデータ ソースからコンテキストを取得できませんでした。認証に失敗しました。'
    },
    footer: {
      viewAll: 'すべて表示',
      title: {
        incidents: 'インシデント',
        alerts: 'アラート',
        lIST: 'リスト',
        users: 'ユーザー',
        endpoint: 'ホスト',
        archer: '資産'
      },
      resultCount: '（最初の{{count}}件の結果）'
    },
    tooltip: {
      contextHighlights: 'コンテキストのハイライト',
      viewOverview: 'コンテンツの表示',
      actions: 'アクション',
      investigate: '調査への移行',
      addToList: 'リストへの追加/削除',
      virusTotal: 'VirusTotalルックアップ',
      googleLookup: 'Googleルックアップ',
      ecat: 'エンドポイント シック クライアントへの移行',
      events: 'イベントへの移行',
      contextUnavailable: '現時点では、コンテキスト データは使用できません。',
      dataSourceNames: {
        Incidents: 'インシデント',
        Alerts: 'アラート',
        LIST: 'リスト',
        Users: 'ユーザー',
        IOC: 'IOC',
        Machines: 'エンドポイント',
        Modules: 'モジュール',
        'LiveConnect-Ip': 'LiveConnect',
        'LiveConnect-File': 'LiveConnect',
        'LiveConnect-Domain': 'LiveConnect'
      }
    }
  },
  preferences: {
    'investigate-events': {
      panelTitle: 'イベントの基本設定',
      triggerTip: 'イベントの基本設定の表示/非表示',
      defaultEventView: 'デフォルトの［イベント分析］ビュー',
      defaultLogFormat: 'デフォルトのログ形式',
      defaultPacketFormat: 'デフォルトのパケット形式',
      LOG: 'ログのダウンロード',
      CSV: 'CSVのダウンロード',
      XML: 'XMLのダウンロード',
      JSON: 'JSONのダウンロード',
      PCAP: 'PCAPのダウンロード',
      PAYLOAD: 'すべてのペイロードのダウンロード',
      PAYLOAD1: 'リクエスト ペイロードのダウンロード',
      PAYLOAD2: 'レスポンス ペイロードのダウンロード',
      FILE: 'ファイル分析',
      TEXT: 'テキスト分析',
      PACKET: 'パケット分析',
      queryTimeFormat: 'クエリの時間形式',
      DB: 'データベースの時間',
      WALL: '実時間',
      'DB-tooltip': 'インベントが保存される場所のデータベース時間',
      'WALL-tooltip': 'タイムゾーンがユーザーの基本設定に設定された現在の時間',
      autoDownloadExtractedFiles: '展開されたファイルを自動でダウンロード'
    },
    'endpoint-preferences': {
      visibleColumns: '表示する列',
      sortField: 'ソート フィールド',
      sortOrder: 'ソート順',
      filter: 'フィルター'
    }
  },
  packager: {
    errorMessages: {
      invalidServer: '有効なIPアドレスまたはホスト名を入力してください',
      invalidPort: '有効なポート番号を入力してください',
      invalidName: '特殊文字を含まない有効な名前を入力してください',
      passwordEmptyMessage: '証明書のパスワードを入力してください',
      invalidPasswordString: '使用できる文字は英数字と特殊文字で、最低3文字必要です。',
      NAME_EMPTY: '警告：構成名が空です。',
      SERVERS_EMPTY: '警告：サーバが見つかりませんでした。',
      EVENT_ID_INVALID: '警告：イベントIDが無効です。',
      CHANNEL_EMPTY: '警告：チャネルが空です。',
      FILTER_EMPTY: '警告：フィルターが空です。',
      FILTER_INVALID: '警告：フィルターが無効です。',
      INVALID_HOST: '警告：ホストが無効です。',
      CONFIG_NAME_INVALID: '警告：構成名が無効です。',
      INVALID_PROTOCOL: '警告：プロトコルが無効です。',
      CHANNEL_NAME_INVALID: '警告：チャネル名が無効です。',
      EMPTY_CHANNELS: '警告：チャネル名が空です。',
      CHANNEL_FILTER_INVALID: '警告：チャネル フィルターが無効です。'
    },
    packagerTitle: 'Packager',
    serviceName: 'サービス名*',
    server: 'Endpoint Server*',
    port: 'HTTPSポート*',
    certificateValidation: 'サーバの検証',
    certificatePassword: '証明書のパスワード*',
    none: 'なし',
    fullChain: 'フル チェーン',
    thumbprint: '証明書の拇印',
    reset: 'リセット',
    generateAgent: 'エージェントの生成',
    generateLogConfig: 'ログ構成のみ生成',
    loadExistingLogConfig: '既存の構成をロード',
    description: '説明',
    title: 'Packager',
    becon: 'Becon',
    displayName: '表示名*',
    upload: {
      success: '構成ファイルが正常にロードされました。',
      failure: '構成ファイルをアップロードできません。'
    },
    error: {
      generic: 'このデータを取得しようとしたときに、予期しないエラーが発生しました。'
    },
    autoUninstall: '自動アンインストール',
    forceOverwrite: '強制上書き',
    windowsLogCollectionCongfig: 'ウィンドウ ログ収集の構成',
    enableWindowsLogCollection: 'ウィンドウ ログ コレクションを有効にする',
    configurationName: '構成名*',
    primaryLogDecoder: 'プライマリLog Decoder/Log Collector*',
    secondaryLogDecoder: 'セカンダリLog Decoder/Log Collector',
    protocol: 'プロトコル',
    channels: 'チャネル フィルター',
    eventId: '含める/除外するイベントID（?）',
    heartbeatLogs: 'ハートビート ログを送信',
    heartbeatFrequency: 'ハートビートの頻度',
    testLog: 'テスト ログを送信',
    placeholder: '選択する',
    searchPlaceholder: 'フィルター オプションを入力',
    emptyName: '構成名が空です',
    channelFilter: 'チャネル フィルター',
    specialCharacter: '構成名に特殊文字が含まれています。',
    channel: {
      add: '新しいチャネルを追加',
      name: 'チャンネルの名前*',
      filter: 'フィルター*',
      event: 'イベントID*',
      empty: ''
    }
  },
  investigateFiles: {
    title: 'ファイル',
    deleteTitle: '削除の確認',
    button: {
      exportToCSV: 'CSVにエクスポート',
      downloading: 'ダウンロード中',
      save: '保存',
      reset: 'リセット',
      cancel: 'キャンセル'
    },
    message: {
      noResultsMessage: '一致するファイルが見つかりませんでした'
    },
    errorPage: {
      serviceDown: 'Endpoint Serverがオフラインです',
      serviceDownDescription: 'Endpoint Serverは実行されていないか、アクセス不可です。この問題を解決するには、管理者に問い合わせてください。'
    },
    footer: '{{count}}/{{total}} {{label}}',
    filter: {
      filter: 'フィルター',
      filters: '保存されたフィルター',
      newFilter: '新しいフィルター',
      windows: 'Windows',
      mac: 'Mac',
      linux: 'Linux',
      favouriteFilters: 'お気に入りのフィルター',
      addMore: 'フィルタの追加',
      invalidFilterInput: '無効なフィルター入力',
      invalidFilterInputLength: 'フィルター入力が256文字を超えています',
      invalidCharacters: '英数字または特殊文字を含められます',
      invalidCharsAlphabetOnly: '数字と特殊文字は使えません',
      invalidCharsAlphaNumericOnly: '特殊文字は使えません',
      restrictionType: {
        moreThan: 'より大きい',
        lessThan: 'より小さい',
        between: '次の範囲に含まれる',
        equals: '等しい',
        contains: '次の値を含む'
      },
      customFilters: {
        save: {
          description: '保存する検索の名前を提供します この名前は検索ボックス リストに表示されます',
          name: '名前*',
          errorHeader: '検索を保存できませんでした',
          header: '検索の保存',
          errorMessage: '検索は保存できませんでした。',
          emptyMessage: '名フィールドが空です。',
          nameExistsMessage: '同じ名前の保存した検索があります。',
          success: '検索クエリが正常に保存されました。',
          filterFieldEmptyMessage: '新たに追加した1つ以上のフィルター フィールドが空です。保存するには、フィルターを追加するか、フィールドを削除してください。',
          invalidInput: '有効な名前を追加してください（\'-\'および\'_\'の特殊文字しか使えません。）'
        },
        delete: {
          successMessage: 'クエリが正常に削除されました。',
          confirmMessage: '選択したクエリを削除してもよろしいですか?'
        }
      }
    },
    fields: {
      panelTitle: 'ファイルの基本設定',
      triggerTip: 'ファイルの基本設定の表示/非表示',
      id: 'ID',
      companyName: '会社名',
      checksumMd5: 'MD5',
      checksumSha1: 'SHA1',
      checksumSha256: 'SHA256',
      machineOsType: 'オペレーティング システム',
      elf: {
        classType: 'ELF.Class Type',
        data: 'ELF.Data',
        entryPoint: 'ELF.Entry Point',
        features: 'ELF.Features',
        type: 'ELF.Type',
        sectionNames: 'ELF.Section Names',
        importedLibraries: 'ELF.Imported Libraries'
      },
      pe: {
        timeStamp: 'PE.Timestamp',
        imageSize: 'PE.Image Size',
        numberOfExportedFunctions: 'PE.Exported Functions',
        numberOfNamesExported: 'PE.Exported Names',
        numberOfExecuteWriteSections: 'PE.Execute Write Sections',
        features: 'PE.Features',
        sectionNames: 'PE.Section Names',
        importedLibraries: 'PE.Imported Libraries',
        resources: {
          originalFileName: 'PE.Resources.Filename',
          company: 'PE.Resources.Company',
          description: 'PE.Resources.Description',
          version: 'PE.Resources.Version'
        }
      },
      macho: {
        uuid: 'MachO.Uuid',
        identifier: 'MachO.Identifier',
        minOsxVersion: 'MachO.Osx Version',
        features: 'MachO.Features',
        flags: 'MachO.Flags',
        numberOfLoadCommands: 'MachO.Loaded Commands',
        version: 'MachO.Version',
        sectionNames: 'MachO.Section Names',
        importedLibraries: 'MachO.Imported Libraries'
      },
      signature: {
        timeStamp: 'Signature.Timestamp',
        thumbprint: 'Signature.Thumbprint',
        features: 'シグネチャ',
        signer: '署名者'
      },
      owner: {
        userName: '所有者',
        groupName: '所有者グループ'
      },
      rpm: {
        packageName: 'パッケージ'
      },
      path: 'パス',
      entropy: 'エントロピー',
      fileName: 'ファイル名',
      firstFileName: 'ファイル名',
      firstSeenTime: '最初の表示時刻',
      timeCreated: '作成日',
      format: '形式',
      sectionNames: 'セクション名',
      importedLibraries: 'インポートされたライブラリ',
      size: 'サイズ'
    },
    sort: {
      fileNameDescending: 'ファイル名（降順）',
      fileNameAscending: 'ファイル名（昇順）',
      sizeAscending: 'サイズ（昇順）',
      sizeDescending: 'サイズ（降順）',
      formatAscending: '形式（昇順）',
      formatDescending: '形式（降順）',
      signatureAscending: 'シグネチャ（昇順）',
      signatureDescending: 'シグネチャ（降順）'
    }
  },
  investigateHosts: {
    title: '調査',
    loading: '読み込み中',
    loadMore: 'さらに読み込み',
    deleteTitle: '削除の確認',
    noSnapshotMessage: 'スキャン ヒストリは見つかりませんでした。',
    common: {
      save: '保存',
      enable: '有効化',
      saveSuccess: '正常に保存されました。',
      emptyMessage: '一致する結果がありません'
    },
    errorPage: {
      serviceDown: 'Endpoint Serverがオフラインです',
      serviceDownDescription: 'Endpoint Serverは実行されていないか、アクセス不可です。この問題を解決するには、管理者に問い合わせてください。'
    },
    property: {
      file: {
        companyName: '会社名',
        checksumMd5: 'MD5',
        checksumSha1: 'SHA1',
        checksumSha256: 'SHA256',
        machineOsType: 'オペレーティング システム',
        timeCreated: '作成日',
        timeModified: '変更済み',
        timeAccessed: 'アクセス済み',
        createTime: 'プロセスが作成済み',
        pid: 'PID',
        eprocess: 'EPROCESS',
        path: 'フル パス',
        sameDirectoryFileCounts: {
          nonExe: '# 非実行プログラム',
          exe: '# 実行プログラム',
          subFolder: '# フォルダー',
          exeSameCompany: '# 同じ企業の実行プログラム'
        },
        elf: {
          classType: 'クラス タイプ',
          data: 'データ',
          entryPoint: 'エントリー ポイント',
          features: '機能',
          type: 'タイプ',
          sectionNames: 'セクション名',
          importedLibraries: 'インポートされたライブラリ'
        },
        pe: {
          timeStamp: 'タイムスタンプ',
          imageSize: '画面サイズ',
          numberOfExportedFunctions: 'エクスポートされた関数',
          numberOfNamesExported: 'エクスポート名',
          numberOfExecuteWriteSections: '書き込みセクションの実行',
          features: '機能',
          sectionNames: 'セクション名',
          importedLibraries: 'インポートされたライブラリ',
          resources: {
            originalFileName: 'ファイル名',
            company: '会社',
            description: '説明',
            version: 'バージョン'
          }
        },
        macho: {
          uuid: 'UUID',
          identifier: '識別子',
          minOsxVersion: 'OS Xのバージョン',
          features: '機能',
          flags: 'フラグ',
          numberOfLoadCommands: 'ロードされたコマンド',
          version: 'バージョン',
          sectionNames: 'セクション名',
          importedLibraries: 'インポートされたライブラリ'
        },
        signature: {
          timeStamp: 'タイムスタンプ',
          thumbprint: '拇印',
          features: '機能',
          signer: '署名者'
        },
        process: {
          title: 'プロセス',
          processName: 'プロセス名',
          eprocess: 'EPROCESS',
          integrityLevel: '完全性',
          parentPath: '親パス',
          threadCount: 'スレッド数',
          owner: '所有者',
          sessionId: 'セッションID',
          createUtcTime: '作成日',
          imageBase: '画像ベース',
          imageSize: '画面サイズ'
        },
        entropy: 'エントロピー',
        firstFileName: 'ファイル名',
        fileName: 'ファイル名',
        format: '形式',
        sectionNames: 'セクション名',
        importedLibraries: 'インポートされたライブラリ',
        size: 'サイズ',
        imageBase: '画像ベース',
        imageSize: '画面サイズ',
        loaded: 'ロード済み',
        fileProperties: {
          entropy: 'エントロピー',
          size: 'サイズ',
          format: '形式'
        }
      }
    },
    process: {
      title: 'プロセス',
      processName: 'プロセス名',
      properties: 'プロセスのプロパティ',
      pid: 'PID',
      parentId: 'PPID',
      owner: '所有者',
      hostCount: 'ホスト カウント',
      creationTime: '作成時間',
      hashlookup: 'ハッシュ ルックアップ',
      signature: 'シグネチャ',
      path: 'パス',
      launchArguments: '起動の引数',
      message: {
        noResultsMessage: 'プロセス情報が見つかりませんでした'
      },
      dll: {
        dllName: 'DLL名',
        filePath: 'ファイル パス',
        title: 'ロード済みのライブラリ',
        message: {
          noResultsMessage: 'ロード済みのライブラリの情報が見つかりませんでした'
        },
        note: {
          windows: '注：Microsoftが署名していないライブラリを表示します',
          mac: '注：Appleが署名していないライブラリを表示します。'
        }
      }
    },
    tabs: {
      overview: '概要',
      process: 'プロセス',
      autoruns: 'Autoruns',
      files: 'ファイル',
      drivers: 'ドライバー',
      systemInformation: 'システム情報',
      services: 'サービス',
      tasks: 'タスク',
      hostFileEntries: 'ホスト ファイル エントリー',
      mountedPaths: 'マウント パス',
      networkShares: 'ネットワーク共有',
      bashHistories: 'Bashヒストリ',
      libraries: 'ライブラリ',
      explore: 'エクスプローラ',
      securityProducts: 'セキュリティ製品',
      windowsPatches: 'Windowsパッチ'
    },
    systemInformation: {
      ipAddress: 'IPアドレス',
      dnsName: 'DNS名',
      fileSystem: 'ファイル システム',
      path: 'パス',
      remotePath: 'リモート パス',
      options: 'オプション',
      name: '名前',
      description: '説明',
      permissions: '権限',
      type: 'タイプ',
      maxUses: '最大ユーザー数',
      currentUses: '現在のユーザー',
      userName: 'ユーザー名',
      command: 'コマンド',
      commandNote: '注：最新のコマンドは一番上にあります',
      filterUser: 'ユーザーのフィルターのタイプ',
      filterBy: 'ユーザー別フィルター',
      patches: 'パッチ',
      securityProducts: {
        type: 'タイプ',
        instance: 'インスタンス',
        displayName: '表示名',
        companyName: '会社名',
        version: 'バージョン',
        features: '機能'
      }
    },
    hosts: {
      title: 'ホスト',
      search: 'フィルター',
      button: {
        addMore: 'フィルタの追加',
        loadMore: 'さらに読み込み',
        exportCSV: 'CSVにエクスポート',
        export: 'JSONにエクスポート',
        exportTooltip: 'ホスト用にすべてのスキャン データ カテゴリをエクスポートします。',
        downloading: 'ダウンロード中',
        initiateScan: 'スキャン開始',
        cancelScan: 'スキャンの停止',
        delete: '削除',
        cancel: 'キャンセル',
        save: '保存',
        saveAs: '名前を付けて保存...',
        clear: 'クリア',
        search: '検索',
        ok: 'OK',
        moreActions: 'その他のアクション',
        explore: 'エクスプローラ',
        gearIcon: 'ここをクリックして列を管理',
        overview: '［概要］パネルの表示/非表示',
        settings: '設定',
        meta: 'メタの表示/非表示',
        close: 'ホストの詳細を閉じる',
        shrink: '縮小表示',
        update: '更新',
        reset: 'リセット'
      },
      autoruns: {
        services: {
          initd: 'INIT.D',
          systemd: 'SYSTEM.D'
        }
      },
      ranas: {
        ranas: '次として実行',
        categories: {
          Process: 'プロセス',
          Libraries: 'ライブラリ',
          Autorun: 'Autorun',
          Service: 'サービス',
          Task: 'タスク',
          Driver: 'ドライバ',
          Thread: 'スレッド'
        }
      },
      explore: {
        input: {
          placeholder: 'ファイル名、パス、ハッシュで検索'
        },
        noResultsFound: '結果がありません。',
        fileName: 'ファイル名: ',
        path: 'パス：',
        hash: 'ハッシュ：',
        search: {
          minimumtext: {
            required: 'ファイル名またはパスでは最低3文字を入力 ハッシュではSHA-256ハッシュ文字列全体を入力'
          }
        }
      },
      footerLabel: {
        autoruns: {
          autoruns: 'autoruns',
          services: 'サービス',
          tasks: 'タスク'
        },
        files: 'ファイル',
        drivers: 'ドライバ',
        libraries: 'ライブラリ'
      },
      summary: {
        snapshotTime: 'スナップショット時間',
        overview: {
          typeToFilterOptions: 'フィルター オプションを入力',
          noSnapShots: '利用できるスナップショットはありません'
        },
        body: {
          ipAddresses: 'IPアドレス（{{count}}）',
          securityConfig: 'セキュリティの構成',
          loggedUsers: 'ログイン ユーザー（{{count}}）',
          user: {
            administrator: '管理者',
            sessionId: 'セッションID',
            sessionType: 'セッション タイプ',
            groups: 'グループ',
            host: 'ホスト',
            deviceName: 'デバイス名'
          }
        },
        securityConfig: {
          arrangeBy: '次で配置',
          alphabetical: 'アルファベット順',
          status: 'ステータス'
        }
      },
      selected: '選択済み（{{count}}）',
      list: {
        noResultsMessage: '結果がありません。',
        errorOffline: 'エラーが発生しました。Endpoint Serverはオフラインまたはアクセス不可である可能性があります。'
      },
      filters: {
        systemFilter: 'この検索はシステム定義で編集できません。',
        since: '基準',
        customDateRange: 'カスタムの日付範囲',
        customStartDate: '開始日',
        customEndDate: '終了日',
        customDate: 'カスタム日付',
        operator: '演算子',
        searchPlaceHolder: 'フィルター オプションを入力',
        mutlipleValuesNote: '注：複数の値を検索するには、「||」を区切り記号として使用します',
        invalidFilterInput: '無効なフィルター入力',
        invalidFilterInputLength: 'フィルター入力が256文字を超えています',
        invalidIP: '有効なIPアドレスを入力してください。',
        invalidAgentID: '有効なエージェントIDを入力してください',
        invalidAgentVersion: '有効なエージェント バージョンを入力してください',
        invalidMacAddress: '有効なMACアドレスを入力してください',
        invalidOsDescription: 'アルファベット、数字、.、-、()を使用できます',
        invalidCharacters: '英数字または特殊文字を含められます',
        invalidCharsAlphabetOnly: '数字と特殊文字は使えません',
        invalidCharsAlphaNumericOnly: '特殊文字は使えません',
        inTimeRange: '次に含まれる（In）',
        notInTimeRange: '次に含まれない（Not In）',
        agentStatus: {
          lastSeenTime: 'エージェントが最後に表示された時間'
        }
      },
      restrictionTypeOptions: {
        EQUALS: '等しい',
        CONTAINS: '次の値を含む',
        GT: '>',
        LT: '<',
        GTE: '>=',
        LTE: '<=',
        NOT_EQ: '!=',
        LESS_THAN: 'より小さい',
        GREATER_THAN: 'より大きい',
        BETWEEN: '次の範囲に含まれる',
        LAST_5_MINUTES: '直近5分',
        LAST_10_MINUTES: '直近10分',
        LAST_15_MINUTES: '直近15分間',
        LAST_30_MINUTES: '直近30分',
        LAST_HOUR: '直近1時間',
        LAST_3_HOURS: '直近3時間',
        LAST_6_HOURS: '直近6時間',
        LAST_TWELVE_HOURS: '直近12時間',
        LAST_TWENTY_FOUR_HOURS: '直近24時間',
        LAST_FORTY_EIGHT_HOURS: '直近2日間',
        LAST_5_DAYS: '直近5日間',
        LAST_7_DAYS: '直近7日間',
        LAST_14_DAYS: '直近14日間',
        LAST_30_DAYS: '直近30日間',
        LAST_HOUR_AGO: '1時間前',
        LAST_TWENTY_FOUR_HOURS_AGO: '24時間前',
        LAST_5_DAYS_AGO: '5日前',
        ALL_TIME: 'すべてのデータ'
      },
      footer: '{{count}}/{{total}}ホスト',
      column: {
        panelTitle: 'ホストの基本設定',
        triggerTip: 'ホストの基本設定の表示/非表示',
        id: 'エージェントID',
        analysisData: {
          iocs: 'IOCアラート',
          machineRiskScore: 'リスク スコア'
        },
        agentStatus: {
          scanStatus: 'エージェント スキャン ステータス',
          lastSeenTime: 'エージェントが最後に表示された時間'
        },
        machine: {
          machineOsType: 'オペレーティング システム',
          machineName: 'ホスト名',
          id: 'エージェントID',
          agentVersion: 'エージェント バージョン',
          scanStartTime: '最後のスキャン時間',
          scanRequestTime: 'スキャン リクエスト時間',
          scanType: 'scanType',
          scanTrigger: 'スキャン トリガー',
          securityConfigurations: 'セキュリティの構成',
          hostFileEntries: {
            ip: 'ホスト ファイルIP',
            hosts: 'ホスト エントリー'
          },
          users: {
            name: 'ユーザー名',
            sessionId: 'ユーザー セッションID',
            sessionType: 'ユーザー セッション タイプ',
            isAdministrator: 'ユーザーは管理者',
            groups: 'ユーザー グループ',
            domainUserQualifiedName: 'ユーザー完全修飾名',
            domainUserId: 'ユーザー ドメイン ユーザーID',
            domainUserOu: 'ユーザー ドメイン ユーザーOU',
            domainUserCanonicalOu: 'ユーザー ドメイン ユーザー正規OU',
            host: 'ユーザー ホスト',
            deviceName: 'ユーザー デバイス名'
          },
          errors: {
            time: 'エラー - 時間',
            fileID: 'エラー - ファイルID',
            line: 'エラー - 行',
            number: 'エラー - 番号',
            value: 'エラー - 値',
            param1: 'エラー - パラメーター1',
            param2: 'エラー - パラメーター2',
            param3: 'エラー - パラメーター3',
            info: 'エラー - 情報',
            level: 'エラー - レベル',
            type: 'エラー - タイプ'
          },
          networkShares: {
            path: 'ネットワーク共有 - パス',
            name: 'ネットワーク共有 - 名前',
            description: 'ネットワーク共有 -  説明',
            type: 'ネットワーク共有 - タイプ',
            permissions: 'ネットワーク共有 - 許可',
            maxUses: 'ネットワーク共有 - 最大の使用',
            currentUses: 'ネットワーク共有 - 現在の使用'
          },
          mountedPaths: {
            path: 'マウント パス - パス',
            fileSystem: 'マウント パス - ファイル システム',
            options: 'マウント パス - オプション',
            remotePath: 'マウント パス - リモート パス'
          },
          securityProducts: {
            type: 'セキュリティ製品 - タイプ',
            instance: 'セキュリティ製品 - インスタンス',
            displayName: 'セキュリティ製品 - 表示名',
            companyName: 'セキュリティ製品 - 企業名',
            version: 'セキュリティ製品 - バージョン',
            features: 'セキュリティ製品 - 機能'
          },
          networkInterfaces: {
            name: 'NIC名',
            macAddress: 'NIC MACアドレス：',
            networkId: 'ネットワーク インターフェイス - ネットワークID',
            ipv4: 'IPv4',
            ipv6: 'IPv6',
            gateway: 'ネットワーク インターフェイス - ゲートウェイ',
            dns: 'ネットワーク インターフェイス - DNS',
            promiscuous: 'NICプロミスキャス'
          }
        },
        riskScore: {
          moduleScore: 'モジュール スコア',
          highestScoringModules: '最高スコアのモジュール'
        },
        machineIdentity: {
          machineName: 'ホスト名',
          group: 'エージェント グループ',
          agentMode: 'エージェント モード',
          agent: {
            exeCompileTime: 'エージェント - ユーザー モード コンパイル時間',
            sysCompileTime: 'エージェント - ドライバ コンパイル時間',
            packageTime: 'エージェント - パッケージ時間',
            installTime: 'エージェント - インストール時間',
            serviceStartTime: 'エージェント - サービス開始時間',
            serviceProcessId: 'エージェント - サービス プロセスID',
            serviceStatus: 'エージェント - サービス ステータス',
            driverStatus: 'エージェント - ドライバ ステータス',
            blockingEnabled: 'エージェント - ブロックの有効化',
            blockingUpdateTime: 'エージェント - ブロック アップデート時間'
          },
          operatingSystem: {
            description: 'OS - 説明',
            buildNumber: 'OS - ビルド番号',
            servicePack: 'OS - サービス パック',
            directory: 'OS - ディレクトリ',
            kernelId: 'OS - カーネルID',
            kernelName: 'OS - カーネル名',
            kernelRelease: 'OS - カーネル リリース',
            kernelVersion: 'OS - カーネル バージョン',
            distribution: 'OS - 配布',
            domainComputerId: 'OS - ドメイン コンピューターID',
            domainComputerOu: 'OS - ドメイン コンピューターOU',
            domainComputerCanonicalOu: 'OS - ドメイン コンピューター正規OU',
            domainOrWorkgroup: 'OS - ドメインまたはワークグループ',
            domainRole: 'OS - ドメイン ロール',
            lastBootTime: 'OS - 前回の起動時間'
          },
          hardware: {
            processorArchitecture: 'ハードウェア - プロセッサー アーキテクチャ',
            processorArchitectureBits: 'ハードウェア - プロセッサー アーキテクチャ ビット',
            processorCount: 'ハードウェア - プロセッサー カウント',
            processorName: 'ハードウェア - プロセッサー名',
            totalPhysicalMemory: 'ハードウェア - 合計物理メモリ',
            chassisType: 'ハードウェア - シャーシ タイプ',
            manufacturer: 'ハードウェア - メーカー',
            model: 'ハードウェア - モデル',
            serial: 'ハードウェア - シリアル',
            bios: 'ハードウェア - BIOS'
          },
          locale: {
            defaultLanguage: 'ロケール - デフォルト言語',
            isoCountryCode: 'ロケール - 国コード',
            timeZone: 'ロケール - タイム ゾーン'
          },
          knownFolder: {
            appData: 'フォルダー - アプリ データ',
            commonAdminTools: 'フォルダー - 共通管理ツール',
            commonAppData: 'フォルダー - 共通アプリ データ',
            commonDestop: 'フォルダー - 共通デスクトップ',
            commonDocuments: 'フォルダー - 共通ドキュメント',
            commonProgramFiles: 'フォルダー - 共通プログラム ファイル',
            commonProgramFilesX86: 'フォルダー - 共通プログラム ファイル（x86）',
            commonPrograms: 'フォルダー - 共通プログラム',
            commonStartMenu: 'フォルダー - 共通スタート メニュー',
            commonStartup: 'フォルダー - 共通スタートアップ',
            desktop: 'フォルダー - デスクトップ',
            localAppData: 'フォルダー - ローカル アプリ データ',
            myDocuments: 'フォルダー - マイドキュメント',
            programFiles: 'フォルダー - プログラム ファイル',
            programFilesX86: 'フォルダー - プログラム ファイル（x86）',
            programs: 'フォルダー - プログラム',
            startMenu: 'フォルダー - スタート メニュー',
            startup: 'フォルダー - スタートアップ',
            system: 'フォルダー - システム',
            systemX86: 'フォルダー - システム（x86）',
            windows: 'フォルダー - Windows'
          }
        },
        markedForDeletion: '削除対象としてマーク'
      },

      properties: {
        title: 'ホスト プロパティ',
        filter: 'リストのフィルターのタイプ',
        checkbox: '値のあるプロパティのみを表示',
        machine: {
          securityConfigurations: 'セキュリティの構成',
          hostFileEntries: {
            title: 'ホスト ファイル エントリー',
            ip: 'ホスト ファイルIP',
            hosts: 'ホスト エントリー'
          },
          users: {
            title: 'ユーザー',
            name: '名前',
            sessionId: 'セッションID',
            sessionType: 'セッション タイプ',
            isAdministrator: '管理者である',
            administrator: '管理者である',
            groups: 'グループ',
            domainUserQualifiedName: '完全修飾名',
            domainUserId: 'ドメイン ユーザーID',
            domainUserOu: 'ドメイン ユーザーOU',
            domainUserCanonicalOu: 'ドメイン ユーザー正規OU',
            host: 'ホスト',
            deviceName: 'デバイス名'
          },
          networkInterfaces: {
            title: 'ネットワーク インターフェイス',
            name: '名前',
            macAddress: 'MACアドレス',
            networkId: 'ネットワークID',
            ipv4: 'IPv4',
            ipv6: 'IPv6',
            gateway: 'ゲートウェイ',
            dns: 'DNS',
            promiscuous: 'プロミスキャス'
          }
        },
        machineIdentity: {
          agent: {
            agentId: 'エージェントID',
            agentMode: 'エージェント モード',
            agentVersion: 'エージェント バージョン',
            title: 'エージェント',
            exeCompileTime: 'ユーザー モード コンパイル時間',
            sysCompileTime: 'ドライバ コンパイル時間',
            packageTime: 'パッケージ時間',
            installTime: 'インストール時間',
            serviceStartTime: 'サーバの開始時間',
            serviceProcessId: 'サービス プロセスID',
            serviceStatus: 'サービス ステータス',
            driverStatus: 'ドライバ ステータス',
            blockingEnabled: 'グロックの有効化',
            blockingUpdateTime: 'ブロック アップデート時間'
          },
          operatingSystem: {
            title: 'オペレーティング システム',
            description: '説明',
            buildNumber: 'ビルド番号',
            servicePack: 'サービス パック',
            directory: 'ディレクトリ',
            kernelId: 'カーネルID',
            kernelName: 'カーネル名',
            kernelRelease: 'カーネル リリース',
            kernelVersion: 'カーネル バージョン',
            distribution: '配布',
            domainComputerId: 'ドメイン コンピューターID',
            domainComputerOu: 'ドメイン コンピューターOU',
            domainComputerCanonicalOu: 'ドメイン コンピューター正規OU',
            domainOrWorkgroup: 'ドメインまたはワークグループ',
            domainRole: 'ドメイン ロール',
            lastBootTime: '前回の起動時間'
          },
          hardware: {
            title: 'ハードウェア',
            processorArchitecture: 'プロセッサー アーキテクチャ',
            processorArchitectureBits: 'プロセッサー アーキテクチャ ビット',
            processorCount: 'プロセッサー カウント',
            processorName: 'プロセッサー名',
            totalPhysicalMemory: '合計物理メモリ',
            chassisType: 'シャーシ タイプ',
            manufacturer: '製造業者',
            model: 'モデル',
            serial: 'シリアル',
            bios: 'BIOS'
          },
          locale: {
            title: 'ロケール',
            defaultLanguage: 'デフォルト言語',
            isoCountryCode: '国コード',
            timeZone: 'タイム ゾーン'
          }
        }
      },
      propertyPanelTitles: {
        autoruns: {
          autorun: 'Autorunプロパティ',
          services: 'サービス プロパティ',
          tasks: 'タスク プロパティ'
        },
        files: 'ファイル プロパティ',
        drivers: 'ドライバ プロパティ',
        libraries: 'ライブラリ プロパティ'
      },
      medium: {
        network: 'ネットワーク',
        log: 'ログ',
        correlation: '相関'
      },
      empty: {
        title: 'イベントは見つかりませんでした。',
        description: 'このフィルタ条件に一致するレコードはありませんでした。'
      },
      error: {
        title: 'データをロードできません。',
        description: 'データ レコードを取得しようとしたときに予期しないエラーが発生しました。'
      },
      meta: {
        title: 'メタ',
        clickToOpen: 'クリックして開きます'
      },
      events: {
        title: 'イベント',
        error: 'このクエリの実行中に予期しないエラーが発生しました。'
      },
      services: {
        loading: '使用可能なサービスのリストをロードしています',
        empty: {
          title: 'サービスが見つかりません。',
          description: 'Broker、Concentrator、その他のサービスはいずれも検出されませんでした。これは、構成または接続の問題が原因である可能性があります。'
        },
        error: {
          title: 'サービスをロードできません。',
          description: '調査のためにBroker、Concentrator、その他のサービスのリストをロードしているときに予期しないエラーが発生しました。これは、構成または接続の問題が原因である可能性があります。'
        }
      },
      customQuery: {
        title: 'クエリを入力します。'
      },
      customFilter: {
        save: {
          description: '検索の名前を入力します。この名前は検索リストに表示されます。',
          name: '名前*',
          errorHeader: '検索を保存できませんでした',
          header: '検索の保存',
          errorMessage: '検索は保存できませんでした。',
          emptyMessage: '名フィールドが空です。',
          nameExistsMessage: '同じ名前の保存した検索があります。',
          success: '検索クエリが正常に保存されました。',
          filterFieldEmptyMessage: '新たに追加した1つ以上のフィルター フィールドが空です。保存するには、フィルターを追加するか、フィールドを削除してください。',
          invalidInput: '\'-\'および\'_\'の特殊文字しか使用できません。'
        },
        update: {
          success: '検索クエリが正常にアップデートされました。'
        }
      },
      initiateScan: {
        modal: {
          title: '{{count}}個のホストのスキャンを開始',
          modalTitle: '{{name}}のスキャンを開始',
          description: '選択したホストでスキャンのタイプを選択します。',
          error1: '*少なくとも1つのホストを選択します。',
          error2: 'スキャンの開始には最大100のホストが使用可能です。',
          infoMessage: '選択したホストの一部は既にスキャンされているため、それらのホストに対して新しいスキャンは開始されません。',
          ecatAgentMessage: '選択したホストの一部は4.4エージェントで、この機能はサポートされていません。',
          quickScan: {
            label: 'クイック スキャン（デフォルト）',
            description: 'メモリにロードされたすべての実行可能モジュールに対してクイック スキャンを実行します。約10分かかります。'
          }
        },
        success: 'スキャンが正常に開始されました',
        error: 'スキャンの開始に失敗しました'
      },
      cancelScan: {
        modal: {
          title: '{{count}}個のホストのスキャンを停止',
          description: '選択したホストのスキャンを停止してもよろしいですか？',
          error1: '*少なくとも1つのホストを選択します。'
        },
        success: 'スキャンの停止が正常に開始されました',
        error: 'スキャンの停止の開始に失敗しました'
      },
      deleteHosts: {
        modal: {
          title: '{{count}}個のホストを削除',
          message: 'ホスト スキャン データが必要なくなった場合、またはエージェントがアンインストールされた場合に、ホストを削除します。' +
          'ホストに関連づけられたすべてのスキャン データは削除されます。続行しますか？ '
        },
        success: 'ホストが正常に削除されました',
        error: 'ホストの削除に失敗しました'
      },
      moreActions: {
        openIn: 'エンドポイントへの移行',
        openInErrorMessage: '少なくとも1つのホストを選択します。',
        notAnEcatAgent: '4.4エージェントのみを選択',
        cancelScan: 'スキャンの停止'
      }
    },
    savedQueries: {
      headerContent: '保存したクエリを実行するには、リストから選択します。保存したクエリの名前を編集するには、名前の横にある鉛筆アイコンをクリックします。デフォルトに設定するには、星形アイコンをクリックします。',
      deleteBtn: '選択した項目を削除',
      runBtn: '選択項目を実行',
      yesBtn: 'はい',
      noBtn: 'いいえ',
      delete: {
        successMessage: 'クエリが正常に削除されました。',
        confirmMessage: '選択したクエリを削除してもよろしいですか?'
      },
      edit: {
        successMessage: 'クエリ名が正常にアップデートされました',
        errorMessage: 'クエリ名のアップデートに失敗しました',
        nameExistsMessage: 'クエリ名はすでに存在します'
      }
    },
    files: {
      footer: '{{count}}/{{total}} {{label}}',
      filter: {
        filters: '保存されたフィルター',
        newFilter: '新しいフィルター',
        windows: 'Windows',
        mac: 'Mac',
        linux: 'Linux',
        favouriteFilters: 'お気に入りのフィルター',
        restrictionType: {
          moreThan: 'より大きい',
          lessThan: 'より小さい',
          between: '次の範囲に含まれる',
          equals: '等しい',
          contains: '次の値を含む'
        },
        save: '保存',
        reset: 'リセット',
        customFilters: {
          save: {
            description: '検索の名前を入力します。この名前は検索リストに表示されます。',
            name: '名前*',
            errorHeader: '検索を保存できませんでした',
            header: '検索の保存',
            errorMessage: '検索は保存できませんでした。',
            emptyMessage: '名フィールドが空です。',
            nameExistsMessage: '同じ名前の保存した検索があります。',
            success: '検索クエリが正常に保存されました。',
            filterFieldEmptyMessage: 'フィルター フィールドが空です',
            invalidInput: '\'-\'および\'_\'の特殊文字しか使用できません。'
          }
        },
        button: {
          cancel: 'キャンセル',
          save: '保存'
        }
      },
      fields: {
        id: 'ID',
        firstSeenTime: '最初の表示時刻',
        companyName: '会社名',
        checksumMd5: 'MD5',
        checksumSha1: 'SHA1',
        checksumSha256: 'SHA256',
        machineOsType: 'オペレーティング システム',
        elf: {
          classType: 'ELF.Class Type',
          data: 'ELF.Data',
          entryPoint: 'ELF.Entry Point',
          features: 'ELF.Features',
          type: 'ELF.Type',
          sectionNames: 'ELF.Section Names',
          importedLibraries: 'ELF.Imported Libraries'
        },
        pe: {
          timeStamp: 'PE.Timestamp',
          imageSize: 'PE.Image Size',
          numberOfExportedFunctions: 'PE.Exported Functions',
          numberOfNamesExported: 'PE.Exported Names',
          numberOfExecuteWriteSections: 'PE.Execute Write Sections',
          features: 'PE.Features',
          sectionNames: 'PE.Section Names',
          importedLibraries: 'PE.Imported Libraries',
          resources: {
            originalFileName: 'PE.Resources.Filename',
            company: 'PE.Resources.Company',
            description: 'PE.Resources.Description',
            version: 'PE.Resources.Version'
          }
        },
        macho: {
          uuid: 'MachO.Uuid',
          identifier: 'MachO.Identifier',
          minOsxVersion: 'MachO.Osx Version',
          features: 'MachO.Features',
          flags: 'MachO.Flags',
          numberOfLoadCommands: 'MachO.Loaded Commands',
          version: 'MachO.Version',
          sectionNames: 'MachO.Section Names',
          importedLibraries: 'MachO.Imported Libraries'
        },
        signature: {
          timeStamp: 'Signature.Timestamp',
          thumbprint: 'Signature.Thumbprint',
          features: 'シグネチャ',
          signer: '署名者'
        },
        owner: {
          userName: '所有者',
          groupName: '所有者グループ'
        },
        rpm: {
          packageName: 'パッケージ'
        },
        path: 'パス',
        entropy: 'エントロピー',
        fileName: 'ファイル名',
        firstFileName: 'ファイル名',
        timeCreated: '作成日',
        format: '形式',
        sectionNames: 'セクション名',
        importedLibraries: 'インポートされたライブラリ',
        size: 'サイズ'
      }
    },
    pivotToInvestigate: {
      title: 'サービスを選択',
      buttonText: 'ナビゲート',
      buttonText2: 'イベント分析',
      iconTitle: 'ナビゲートまたはイベント分析に移行'
    }
  },
  hostsScanConfigure: {
    title: 'スキャン スケジュール',
    save: '保存',
    enable: '有効化',
    saveSuccess: '正常に保存されました。',
    startDate: '開始日',
    recurrenceInterval: {
      title: '繰り返し間隔',
      options: {
        daily: '日単位',
        weekly: '週単位',
        monthly: '月単位'
      },
      every: '間隔',
      on: '日時：',
      intervalText: {
        DAYS: '日',
        WEEKS: '週',
        MONTHS: 'ヶ月間'
      },
      week: {
        monday: '月',
        tuesday: '火',
        wednesday: '水',
        thursday: '木',
        friday: '金',
        saturday: '土',
        sunday: '日'
      }
    },
    startTime: '‬開始時刻',
    cpuThrottling: {
      title: 'エージェントCPUスロットル',
      cpuMax: 'CPUの最大値（%）',
      vmMax: '仮想マシンの最大値（%） '
    },
    error: {
      generic: 'このデータを取得しようとしたときに、予期しないエラーが発生しました。'
    }
  }
};
});
