define('sa/locales/de-de/translations', ['exports'], function (exports) {
  'use strict';

  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports.default = {
  appTitle: 'NetWitness Platform',
  pageTitle: '{{section}} – NetWitness Platform',
  empty: '',
  languages: {
    en: 'Englisch',
    'en-us': 'Englisch',
    ja: 'Japanisch'
  },
  passwordPolicy: {
    passwordPolicyRequestError: 'Beim Abrufen der Passwortrichtlinie ist ein Problem aufgetreten.',
    passwordPolicyMinChars: 'Muss mindestens {{passwordPolicyMinChars}} Zeichen lang sein',
    passwordPolicyMinNumericChars: 'Muss mindestens {{passwordPolicyMinNumericChars}} Zahlen (0 bis 9) enthalten',
    passwordPolicyMinUpperChars: 'Muss mindestens {{passwordPolicyMinUpperChars}} Großbuchstaben enthalten',
    passwordPolicyMinLowerChars: 'Muss mindestens {{passwordPolicyMinLowerChars}} Kleinbuchstaben enthalten',
    passwordPolicyMinNonLatinChars: 'Muss mindestens {{passwordPolicyMinNonLatinChars}} Zeichen aus dem Unicode-Alphabet enthalten, die weder Großbuchstaben noch Kleinbuchstaben sind',
    passwordPolicyMinSpecialChars: 'Muss mindestens {{passwordPolicyMinSpecialChars}} nicht-alphanumerische Zeichen enthalten: (~!@#$%^&*_-+=`|(){}[]:;"\'<>,.?/)',
    passwordPolicyCannotIncludeId: 'Ihr Passwort darf nicht Ihren Benutzernamen enthalten.'
  },
  forms: {
    cancel: 'Abbrechen',
    submit: 'Senden',
    reset: 'Zurücksetzen',
    apply: 'Anwenden',
    ok: 'OK',
    delete: 'Löschen',
    save: 'Speichern',
    yes: 'Ja',
    no: 'Nein'
  },
  tables: {
    noResults: 'Keine Ergebnisse',
    columnChooser: {
      filterPlaceHolder: 'Tippen, um die Liste zu filtern'
    }
  },
  login: {
    username: 'Benutzername',
    password: 'Passwort',
    login: 'Anmeldung',
    loggingIn: 'Anmeldung erfolgt ...',
    logout: 'Abmeldung',
    oldPassword: 'Altes Passwort',
    newPassword: 'Neue Passphrase',
    confirmPassword: 'Passwort bestätigen',
    passwordMismatch: 'Die Passwörter stimmen nicht überein.',
    passwordNoChange: 'Das neue Passwort darf nicht mit dem alten Passwort identisch sein.',
    passwordChangeFailed: 'Beim Speichern der Passwortänderung ist ein Problem aufgetreten. Wiederholen Sie den Vorgang.',
    lostPasswordLink: 'Passwort vergessen?',
    genericError: 'Authentifizierungsfehler. Wiederholen Sie den Vorgang.',
    communicationError: 'Der Server ist nicht erreichbar. Bitte wenden Sie sich an Ihren Systemadministrator.',
    userLocked: 'Benutzerkonto ist gesperrt',
    userDisabled: 'Benutzerkonto ist deaktiviert',
    userExpired: 'Benutzerkonto ist abgelaufen',
    changePasswordLink: 'Eigenes Passwort ändern',
    changePasswordSoon: 'Beachten Sie, dass Ihr Passwort für den RSA NetWitness-Server in {{daysRemaining}} Tag(en) abläuft. Sie sollten Ihr Passwort vor dem Ablaufen ändern. Um das Passwort zu ändern, klicken Sie oben rechts im Anwendungsfenster auf „Einstellungen“.',
    changePasswordToday: 'Beachten Sie, dass Ihr Passwort für den RSA NetWitness-Server heute abläuft. Sie sollten Ihr Passwort vor dem Ablaufen ändern. Um das Passwort zu ändern, klicken Sie oben rechts im Anwendungsfenster auf „Einstellungen“.',
    lostPassword: {
      title: 'Passwortwiederherstellung',
      description: 'Bitte geben Sie Ihren Benutzernamen an.'
    },
    thankYou: {
      title: 'Vielen Dank!',
      description: 'Eine E-Mail zum Zurücksetzen des Passworts wurde an die registrierte E-Mail-Adresse des Benutzers gesendet.',
      back: 'Zurück zur Anmeldung'
    },
    eula: {
      title: 'Anwenderlizenzvereinbarung',
      agree: 'Annehmen'
    },
    forcePassword: {
      warning: 'Sie müssen ein neues Passwort erstellen, bevor Sie sich anmelden können.',
      changePassword: 'Kennwort ändern'
    }
  },
  userPreferences: {
    preferences: 'Benutzereinstellungen',
    personalize: 'Personalisieren Sie Ihre Erfahrung.',
    signOut: 'Abmelden',
    version: 'Version',
    username: 'Benutzername',
    email: 'E-Mail',
    language: 'Sprache',
    timeZone: 'Zeitzone',
    dateFormatError: 'Beim Speichern des ausgewählten Datumsformats ist ein Fehler aufgetreten. Wiederholen Sie den Vorgang. Falls das Problem weiterhin besteht, wenden Sie sich an Ihren Systemadministrator.',
    landingPageError: 'Beim Speichern der standardmäßigen Landingpage ist ein Fehler aufgetreten. Wiederholen Sie den Vorgang. Falls das Problem weiterhin besteht, wenden Sie sich an Ihren Systemadministrator.',
    defaultInvestigatePageError: 'Beim Speichern der standardmäßigen Ansicht „Untersuchen“ ist ein Fehler aufgetreten. Wiederholen Sie den Vorgang. Falls das Problem weiterhin besteht, wenden Sie sich an Ihren Systemadministrator.',
    timeFormatError: 'Beim Speichern des ausgewählten Zeitformats ist ein Fehler aufgetreten. Wiederholen Sie den Vorgang. Falls das Problem weiterhin besteht, wenden Sie sich an Ihren Systemadministrator.',
    timezoneError: 'Beim Speichern der ausgewählten Zeitzone ist ein Fehler aufgetreten. Wiederholen Sie den Vorgang. Falls das Problem weiterhin besteht, wenden Sie sich an Ihren Systemadministrator.',
    dateFormat: {
      label: 'Datumsformat',
      dayFirst: 'TT/MM/JJJJ',
      monthFirst: 'MM/TT/JJJJ',
      yearFirst: 'JJJJ/MM/TT'
    },
    timeFormat: {
      label: 'Zeitformat',
      twelveHour: '12 Stunden',
      twentyFourHour: '24 Stunden'
    },
    theme: {
      title: 'Thema',
      dark: 'Dunkel',
      light: 'Hell',
      error: 'Beim Speichern des ausgewählten Themas ist ein Fehler aufgetreten. Wiederholen Sie den Vorgang. Falls das Problem weiterhin besteht, wenden Sie sich an Ihren Systemadministrator.'
    },
    defaultLandingPage: {
      label: 'Standardmäßige Landingpage',
      monitor: 'Überwachung',
      investigate: 'Untersuchen',
      investigateClassic: 'Untersuchen',
      dashboard: 'Überwachung',
      live: 'Konfigurieren',
      respond: 'Reagieren',
      admin: 'Admin'
    },
    defaultInvestigatePage: {
      label: 'Standardmäßige Ansicht „Untersuchen“',
      events: 'Ereignisse',
      eventAnalysis: 'Ereignisanalyse',
      malware: 'Malware Analysis',
      navigate: 'Navigieren',
      hosts: 'Hosts',
      files: 'Dateien'
    }
  },
  queryBuilder: {
    noMatches: 'Keine Treffer gefunden',
    enterValue: 'Einzelwert eingeben',
    insertFilter: 'Neuen Filter einfügen',
    query: 'Abfrage mit Filtern durchführen',
    open: 'In neuer Registerkarte öffnen',
    delete: 'Ausgewählte Filter löschen',
    deleteFilter: 'Filter löschen',
    edit: 'Filter bearbeiten',
    placeholder: 'Metaschlüssel, Operator und Wert (optional) eingeben',
    querySelected: 'Abfrage mit ausgewählten Filtern durchführen',
    querySelectedNewTab: 'Abfrage mit ausgewählten Filtern auf neuer Registerkarte durchführen',
    expensive: 'Dieser Vorgang kann länger dauern.',
    notEditable: 'Komplexe Abfragefilter können nicht bearbeitet werden.',
    validationMessages: {
      time: 'Sie müssen ein gültiges Datum eingeben.',
      text: 'Zeichenfolgen müssen in Anführungszeichen (") gesetzt werden.',
      ipv4: 'Sie müssen eine IPv4-Adresse eingeben.',
      ipv6: 'Sie müssen eine IPv6-Adresse eingeben.',
      uint8: 'Sie müssen eine 8-Bit-Ganzzahl eingeben.',
      uint16: 'Sie müssen eine 16-Bit-Ganzzahl eingeben.',
      uint32: 'Sie müssen eine 32-Bit-Ganzzahl eingeben.',
      float32: 'Sie müssen eine 32-Bit-Gleitkommazahl eingeben.'
    }
  },
  ipConnections: {
    ipCount: '({{count}} IPs)',
    clickToCopy: 'Zum Kopieren der IP-Adresse klicken',
    sourceIp: 'Quell-IP',
    destinationIp: 'Ziel-IP'
  },
  list: {
    all: '(Alle)',
    items: 'Elemente',
    packets: 'Pakete',
    packet: 'Paket',
    of: 'von',
    sessions: 'Sitzungen'
  },
  updateLabel: {
    'one': 'Aktualisieren',
    'other': 'Updates'
  },
  recon: {
    extractWarning: '<span>Sie haben die Seite verlassen, bevor die heruntergeladenen Dateien an das Browser-Tray angehängt werden konnten. Ihr Download ist <a href="{{url}}" target="_blank">hier</a> verfügbar.</span>',
    extractedFileReady: 'Die Datei wurde extrahiert. Wechseln Sie zum Herunterladen zur Jobwarteschlange.',
    titleBar: {
      titles: {
        endpoint: 'Endpunktereignisdetails',
        network: 'Netzwerkereignisdetails',
        log: 'Protokollereignisdetails'
      },
      views: {
        text: 'Textanalyse',
        packet: 'Paketanalyse',
        file: 'Dateianalyse',
        web: 'Web',
        mail: 'E-Mail'
      }
    },
    meta: {
      scroller: {
        of: 'von',
        results: 'Ergebnisse'
      }
    },
    textView: {
      compressToggleLabel: 'Komprimierte Nutzdaten anzeigen',
      compressToggleTitle: 'HTTP-Nutzdaten als komprimiert oder nicht anzeigen',
      downloadCsv: 'CSV herunterladen',
      downloadEndpointEvent: 'Endpoint herunterladen',
      pivotToEndpoint: 'Zu Endpunkt-Thick-Client wechseln',
      pivotToEndpointTitle: 'Gilt für Hosts mit installierten 4.x-Endpunkt-Agents. Installieren Sie den NetWitness Endpunkt-Thick-Client.',
      downloadJson: 'JSON herunterladen',
      downloadLog: 'Download-Protokoll',
      downloadXml: 'XML herunterladen',
      headerShowing: 'Wird angezeigt',
      isDownloading: 'Downloadvorgang läuft...',
      maxPacketsReached: '<span class="darker">{{maxPacketCount}} (max.)</span> von <span class="darker">{{packetTotal}}</span> Paketen gerendert',
      maxPacketsReachedTooltip: 'Der Grenzwert von {{maxPacketCount}} Paketen zum Rendering eines einzelnen Ereignisses wurde erreicht. Für dieses Ereignis werden keine weiteren Pakete gerendert. Der Paketschwellenwert garantiert die bestmögliche Rendering-Erfahrung.',
      rawEndpointHeader: 'Rohdatenendpunkt',
      rawLogHeader: 'Rohdatenprotokoll',
      renderingMore: 'Mehr anzeigen...',
      renderRemaining: 'Verbleibende {{remainingPercent}} % werden gerendert...',
      showRemaining: 'Verbleibende {{remainingPercent}} % anzeigen'
    },
    packetView: {
      noHexData: 'Während der Inhaltsrekonstruktion wurden keine HEX-Daten generiert.',
      isDownloading: 'Downloadvorgang läuft...',
      defaultDownloadPCAP: 'PCAP herunterladen',
      downloadPCAP: 'PCAP herunterladen',
      downloadPayload1: 'Anforderungsnutzdaten herunterladen',
      downloadPayload2: 'Antwortnutzdaten herunterladen',
      downloadPayload: 'Alle Nutzdaten herunterladen',
      payloadToggleLabel: 'Nur Nutzdaten anzeigen',
      payloadToggleTitle: 'Entfernt Paketkopfzeilen und -fußzeilen aus der Anzeige',
      stylizeBytesLabel: 'Byte schattieren',
      stylizeBytesTitle: 'Aktivieren, um Datenmuster besser unterscheiden zu können',
      commonFilePatternLabel: 'Gebräuchliche Dateimuster',
      commonFilePatternTitle: 'Aktivieren, um gebräuchliche Dateisignaturmuster zu markieren',
      headerMeta: 'Kopfzeilenmetadaten',
      headerAttribute: 'Kopfzeilenattribut',
      headerSignature: 'Interessante Byte',
      headerDisplayLabel: '{{label}} = {{displayValue}}',
      renderingMore: 'Mehr anzeigen...'
    },
    reconPager: {
      packetPagnationPageFirst: 'Erste',
      packetPagnationPagePrevious: 'Zurück',
      packetPagnationPageNext: 'Weiter',
      packetPagnationPageLast: 'Letzter',
      packetsPerPageText: 'Pakete pro Seite'
    },
    fileView: {
      downloadFile: 'Datei herunterladen',
      downloadFiles: 'Dateien herunterladen ({{fileCount}})',
      isDownloading: 'Downloadvorgang läuft...',
      downloadWarning: 'Warnung: Die Dateien enthalten den ursprünglichen, ungesicherten Rohcontent. Seien Sie vorsichtig beim Öffnen oder Herunterladen von Dateien, da sie schädliche Daten enthalten können.'
    },
    files: {
      fileName: 'Dateiname',
      extension: 'Erweiterung',
      mimeType: 'MIME-Typ',
      fileSize: 'Dateigröße',
      hashes: 'Hashes',
      noFiles: 'Für dieses Ereignis sind keine Dateien vorhanden.',
      linkFile: 'Diese Datei ist in einer anderen Sitzung.<br>Klicken Sie auf den Dateilink, um die zugehörige Sitzung in einer neuen Registerkarte anzuzeigen.'
    },
    error: {
      generic: 'Beim Versuch, diese Daten abzurufen, ist ein unerwarteter Fehler aufgetreten.',
      missingRecon: 'Dieses Ereignis (ID = {{id}}) wurde nicht gespeichert oder wurde aus dem Speicher genommen. Kein anzuzeigender Inhalt vorhanden.',
      noTextContentData: 'Während der Inhaltsrekonstruktion wurden keine Textdaten generiert. Das kann bedeuten, dass die Ereignisdaten beschädigt oder ungültig waren. Prüfen Sie die anderen Rekonstruktionsansichten.',
      noRawDataEndpoint: 'Während der Inhaltsrekonstruktion wurden keine Textdaten generiert. Das könnte bedeuten, dass die Ereignisdaten beschädigt/ungültig waren oder dass ein Administrator die Übertragung von Rohdaten-Endpunktereignissen in der Endpunktserverkonfiguration deaktiviert hat. Prüfen Sie die anderen Rekonstruktionsansichten.',
      permissionError: 'Unzureichende Berechtigungen für die angeforderten Daten. Wenn Sie der Ansicht sind, dass Sie darauf zugreifen können sollten, bitten Sie den Administrator um die Bereitstellung der erforderlichen Berechtigungen.'
    },
    fatalError: {
      115: 'Die Sitzung ist nicht zur Anzeige verfügbar.',
      124: 'Ungültige Sitzungs-ID: {{eventId}}',
      11: 'Die Sitzungs-ID ist zu groß für die Behandlung: {{eventId}}',
      permissions: 'Sie verfügen nicht über ausreichende Berechtigungen, um diese Inhalte anzeigen zu können.'
    },
    toggles: {
      header: 'Kopfzeile ein-/ausblenden',
      request: 'Anforderung ein-/ausblenden',
      response: 'Antwort ein-/ausblenden',
      topBottom: 'Übereinander anzeigen',
      sideBySide: 'Nebeneinander anzeigen',
      meta: 'Metadaten ein-/ausblenden',
      expand: 'Mehr anzeigen',
      shrink: 'Weniger anzeigen',
      close: 'Rekonstruktion abschließen'
    },
    eventHeader: {
      nwService: 'NW-Service',
      sessionId: 'Sitzungs-ID',
      type: 'Typ',
      source: 'Quell-IP:PORT',
      destination: 'Ziel-IP:PORT',
      service: 'Service',
      firstPacketTime: 'Zeit erstes Paket',
      lastPacketTime: 'Zeit letztes Paket',
      packetSize: 'Berechnete Paketgröße',
      payloadSize: 'Berechnete Nutzdatengröße',
      packetCount: 'Berechnete Paketanzahl',
      packetSizeTooltip: 'Die berechnete Paketgröße in der Kopfzeile der Zusammenfassung kann sich von der Paketgröße im Detailsbereich der Metadaten unterscheiden, da die Metadaten gelegentlich bereits vor Abschluss der Ereignisanalyse geschrieben werden und daher duplizierte Pakete enthalten können.',
      payloadSizeTooltip: 'Die berechnete Nutzdatengröße in der Kopfzeile der Zusammenfassung kann sich von der Nutzdatengröße im Detailsbereich der Metadaten unterscheiden, da die Metadaten gelegentlich bereits vor Abschluss der Ereignisanalyse geschrieben werden und daher duplizierte Pakete enthalten können.',
      packetCountTooltip: 'Die berechnete Paketanzahl in der Kopfzeile der Zusammenfassung kann sich von der Paketanzahl im Detailsbereich der Metadaten unterscheiden, da die Metadaten gelegentlich bereits vor Abschluss der Ereignisanalyse geschrieben werden und daher duplizierte Pakete enthalten können.',
      deviceIp: 'Device-IP',
      deviceType: 'Gerätetyp',
      deviceClass: 'Geräteklasse',
      eventCategory: 'Ereigniskategorie',
      nweCategory: 'NWE-Kategorie',
      collectionTime: 'Erfassungszeit',
      eventTime: 'Eventzeit',
      nweEventTime: 'Eventzeit',
      nweMachineName: 'Rechnername',
      nweMachineIp: 'IP-Adresse des Rechners',
      nweMachineUsername: 'Benutzername des Rechners',
      nweMachineIiocScore: 'IIOC-Wert des Rechners',
      nweEventSourceFilename: 'Dateiname der Quelle des Ereignisses',
      nweEventSourcePath: 'Quellpfad des Ereignisses',
      nweEventDestinationFilename: 'Dateiname des Ziels des Ereignisses',
      nweEventDestinationPath: 'Zielpfad des Ereignisses',
      nweFileFilename: 'Dateiname',
      nweFileIiocScore: 'IIOC-Wert der Datei',
      nweProcessFilename: 'Name der Prozessdatei',
      nweProcessParentFilename: 'Name der übergeordneten Datei',
      nweProcessPath: 'Prozesspfad',
      nweDllFilename: 'DLL-Dateiname',
      nweDllPath: 'DLL-Pfad',
      nweDllProcessFilename: 'Name der Prozessdatei',
      nweAutorunFilename: 'Name der Autorun-Datei',
      nweAutorunPath: 'Autorun-Pfad',
      nweServiceDisplayName: 'Anzeigename des Service',
      nweServiceFilename: 'Name der Servicedatei',
      nweServicePath: 'Servicepfad',
      nweTaskName: 'Aufgabenname',
      nweTaskPath: 'Aufgabenpfad',
      nweNetworkFilename: 'Name der Netzwerkdatei',
      nweNetworkPath: 'Netzwerkpfad',
      nweNetworkProcessFilename: 'Dateiname des Netzwerkprozesses',
      nweNetworkProcessPath: 'Pfad des Netzwerkprozesses',
      nweNetworkRemoteAddress: 'Remoteadresse des Netzwerks'
    },
    contextmenu: {
      copy: 'Kopieren',
      externalLinks: 'Externe Suche',
      livelookup: 'Live-Suche',
      endpointIoc: 'Endpunkt Thick-Clientsuche',
      applyDrill: 'Drill-down in neuer Registerkarte anwenden',
      applyNEDrill: '!=Drill-down in neuer Registerkarte anwenden',
      refocus: 'Ermittlungen in neuer Registerkarte neu fokussieren',
      hostslookup: 'Hosts ermitteln',
      external: {
        google: 'Google',
        sansiphistory: 'SANS-IP-Verlauf',
        centralops: 'CentralOps WHOIS für IP-Adressen und Hostnamen',
        robtexipsearch: 'Robtex IP-Suche',
        ipvoid: 'IPVoid',
        urlvoid: 'URLVoid',
        threatexpert: 'ThreatExpert-Suche'
      }
    }
  },
  memsize: {
    B: 'Byte',
    KB: 'KB',
    MB: 'MB',
    GB: 'GB',
    TB: 'TB'
  },
  midnight: '0:00',
  noon: '12:00',
  investigate: {
    controls: {
      toggle: 'Ereignisbereich ein-/ausblenden',
      togglePreferences: 'Einstellungen für Untersuchung umschalten'
    },
    title: 'Untersuchen',
    loading: 'Wird geladen ...',
    loadMore: 'Weitere laden',
    tryAgain: 'Erneut versuchen',
    service: 'Service',
    timeRange: 'Zeitbereich',
    filter: 'Filter',
    size: {
      bytes: 'Byte',
      KB: 'KB',
      MB: 'MB',
      GB: 'GB',
      TB: 'TB'
    },
    medium: {
      endpoint: 'Endpunkt',
      network: 'Netzwerk',
      log: 'Protokoll',
      correlation: 'Korrelation',
      undefined: 'Unbekannt'
    },
    empty: {
      title: 'Keine Ereignisse gefunden',
      description: 'Es wurden keine Datensätze gefunden, die den Filterkriterien entsprechen.'
    },
    error: {
      title: 'Daten können nicht geladen werden.',
      description: 'Beim Versuch, die Datensätze abzurufen, ist ein unerwarteter Fehler aufgetreten.'
    },
    meta: {
      title: 'Meta',
      clickToOpen: 'Zum Öffnen klicken'
    },
    events: {
      title: 'Ereignisse',
      columnGroups: {
        custom: 'Benutzerdefinierte Spaltengruppen',
        customTitle: 'Managen von benutzerdefinierten Spaltengruppen in der Ereignisansicht',
        default: 'Standardmäßige Spaltengruppen',
        searchPlaceholder: 'Tippen, um Spaltengruppen zu filtern'
      },
      error: 'Beim Ausführen dieser Anfrage ist ein unerwarteter Fehler aufgetreten.',
      shrink: 'Ereignisbereich verkleinern',
      expand: 'Ereignisbereich vergrößern',
      close: 'Ereignisbereich schließen',
      scrollMessage: 'Blättern Sie nach unten, um das ausgewählte, blau hervorgehobene Ereignis anzuzeigen.',
      eventTips: {
        noResults: 'Keine Ergebnisse bisher. Wählen Sie einen Service und einen Zeitbereich aus und senden Sie eine Abfrage.',
        head: {
          header: 'BEISPIELE FÜR ABFRAGEFILTER',
          text: {
            one: 'Suchen Sie nach ausgehenden HTTP-Ereignissen mit einem User-Agent einer Version von Mozilla.',
            two: 'Suchen Sie nach fehlgeschlagenen Windows-Anmeldeversuchen.',
            three: 'Suchen Sie nach Endpunktereignissen mit Aufgaben, deren Dateinamen auf „exe“ enden.'
          }
        },
        section: {
          mouse: {
            header: 'MAUSINTERAKTIONEN',
            textOne: 'Klicken Sie vor, hinter oder zwischen Filter, um einen weiteren Filter einzufügen.',
            textTwo: 'Klicken Sie auf einen Filter und klicken Sie dann mit der rechten Maustaste, um das Aktionsmenü anzuzeigen.',
            textThree: 'Doppelklicken Sie auf einen Filter, um ihn zum Bearbeiten zu öffnen.',
            textFour: 'Wählen Sie durch Klicken mehrere Filter aus und drücken Sie die Taste <span class="highlight">Entf</span>, um die ausgewählten Filter zu löschen.',
            textFive: 'Klicken Sie im Browser auf <span class="highlight">Zurück</span>, um zur vorherigen Seite zurückzukehren.'
          },
          keyboard: {
            header: 'TASTATURINTERAKTIONEN',
            textOne: 'Geben Sie im Abfragegenerator einen Namen oder eine Beschreibung eines Metaschlüssels ein.',
            textTwo: 'Verwenden Sie die <span class="highlight">Auf-</span> und <span class="highlight">Abwärtspfeile</span> in den Drop-down-Menüs und drücken Sie zum Auswählen die <span class="highlight">Eingabetaste</span>.',
            textThree: 'Drücken Sie die <span class="highlight">Eingabetaste</span> oder klicken Sie auf <span class="highlight">Ereignisse abfragen</span>, um die Abfrage auszuführen.',
            textFour: 'Drücken Sie die <span class="highlight">linke</span> oder <span class="highlight">rechte Pfeiltaste</span>, um zum Hinzufügen weiterer Filter durch die Abfrage zu blättern, oder drücken Sie die <span class="highlight">Eingabetaste</span>, um bestehende Filter zu bearbeiten.',
            textFive: 'Drücken Sie gleichzeitig die <span class="highlight">Umschalttaste und die linke</span> oder die <span class="highlight">rechte Pfeiltaste</span>, um mehrere Filter auszuwählen und durch Drücken der <span class="highlight">Rücktaste</span> oder der Taste <span class="highlight">Entf</span> zu löschen.'
          }
        }
      },
      logs: {
        wait: 'Protokoll wird geladen ...',
        rejected: 'Keine Protokolldaten.'
      }
    },
    generic: {
      loading: 'Daten werden geladen ...'
    },
    services: {
      loading: 'Services werden geladen  …',
      noData: 'Der ausgewählte Service verfügt über keine Daten.',
      coreServiceNotUpdated: 'Für Ereignisanalysen sind alle Core-Services in der Version von NetWitness 11.1 erforderlich. Das Verbinden früherer Versionen von Services mit dem Server von NetWitness 11.1 schränkt den Funktionsumfang ein (siehe „Investigate im gemischten Modus“ im Physical Host Upgrade Guide).',
      empty: {
        title: 'Services können nicht gefunden werden.',
        description: 'Es wurden keine Brokers, Concentrators oder sonstigen Services gefunden. Ursache hierfür kann ein Problem mit der Konfiguration oder der Verbindung sein.'
      },
      error: {
        label: 'Services nicht verfügbar',
        description: 'Beim Laden der Liste mit Brokers, Concentrators und sonstigen zu untersuchenden Services ist ein unerwarteter Fehler aufgetreten. Ursache hierfür kann ein Problem mit der Konfiguration oder der Verbindung sein.'
      }
    },
    summary: {
      loading: 'Zusammenfassung wird geladen'
    },
    customQuery: {
      title: 'Geben Sie eine Anfrage ein.'
    }
  },
  configure: {
    title: 'Konfigurieren',
    liveContent: 'Live Content',
    esaRules: 'ESA-Regeln',
    respondNotifications: 'Auf Benachrichtigungen antworten',
    incidentRulesTitle: 'Incident-Regeln',
    subscriptions: 'Abonnements',
    customFeeds: 'Benutzerdefinierte Feeds',
    incidentRules: {
      noManagePermissions: 'Sie verfügen nicht über ausreichende Berechtigungen, um Incident-Regeln bearbeiten zu können.',
      confirm: 'Möchten Sie das wirklich?',
      assignee: {
        none: '(Nicht zugewiesen)'
      },
      priority: {
        LOW: 'Niedrig',
        MEDIUM: 'Mittel',
        HIGH: 'Hoch',
        CRITICAL: 'Kritisch'
      },
      action: 'Aktion',
      actionMessage: 'Wählen Sie die auszuführende Aktion aus, falls die Regel einer Warnmeldung entspricht.',
      error: 'Beim Laden der Incident-Regeln ist ein Problem aufgetreten.',
      noResults: 'Keine Incident-Regeln gefunden',
      createRule: 'Regel erstellen',
      deleteRule: 'Löschen',
      cloneRule: 'Klonen',
      select: 'Auswählen',
      order: 'Auftrag',
      enabled: 'Aktiviert',
      name: 'Name',
      namePlaceholder: 'Geben Sie einen eindeutigen Namen für die Regel an.',
      ruleNameRequired: 'Sie müssen einen Namen für die Regel angeben.',
      description: 'Beschreibung',
      descriptionPlaceholder: 'Geben Sie eine Beschreibung der Regel an.',
      lastMatched: 'Zuletzt abgestimmt',
      alertsMatchedCount: 'Abgestimmte Warnmeldungen',
      incidentsCreatedCount: 'Incidents',
      matchConditions: 'Bedingungen abstimmen',
      queryMode: 'Abfragemodus',
      queryModes: {
        RULE_BUILDER: 'Regelerstellung',
        ADVANCED: 'Erweitert'
      },
      queryBuilderQuery: 'Abfrageerstellung',
      advancedQuery: 'Erweitert',
      advancedQueryRequired: '„Erweiterte Abfrage“ darf nicht leer sein',
      groupingOptions: 'Gruppierungsoptionen',
      groupBy: 'Gruppieren nach',
      groupByPlaceholder: 'Wählen Sie ein Feld „Gruppieren nach“ aus (erforderlich).',
      groupByError: 'Mindestens ein Feld „Gruppieren nach“ muss und maximal zwei solcher Felder können ausgewählt werden.',
      timeWindow: 'Zeitfenster',
      incidentOptions: 'Vorfallsoptionen',
      incidentTitle: 'Titel',
      incidentTitleRequired: 'Sie müssen einen Titel für Incidents angeben, die von dieser Regel erstellt wurden.',
      incidentTitlePlaceholder: 'Geben Sie einen Titel für den Incident ein, der von dieser Regel erstellt wurde.',
      incidentTitleHelp: 'Mit der Titelvorlage wird der Vorfallstitel erstellt. Beispiel: Wenn die Regel den Namen „Rule-01“ hat und das Feld „Gruppieren nach“ auf „Schweregrad“ gesetzt ist, der Wert für „Gruppieren nach“ 50 ist und ${ruleName} die Vorlage für ${groupByValue1} ist, dann wird der Incident mit dem Namen „Rule-01 for 50“ erstellt.',
      incidentSummary: 'Zusammenfassung',
      incidentSummaryPlaceholder: 'Geben Sie eine Zusammenfassung für den Incident ein, der von dieser Regel erstellt wurde.',
      incidentCategories: 'Kategorien',
      incidentCategoriesPlaceholder: 'Wählen Sie eine Kategorie aus (optional).',
      incidentAssignee: 'Zuweisungsempfänger',
      incidentAssigneePlaceholder: 'Wählen Sie einen Zuweisungsempfänger aus (optional).',
      incidentPriority: 'Priorität',
      incidentPriorityInstruction: 'Stellen Sie die Priorität für den Incident folgendermaßen ein:',
      incidentPriorityAverage: 'Durchschn. Risikobewertung für alle Warnmeldungen',
      incidentPriorityHighestScore: 'Höchste verfügbare Risikobewertung für alle Warnmeldungen',
      incidentPriorityAlertCount: 'Anzahl der Warnmeldungen im Zeitfenster',
      priorityScoreError: 'Die Bewertungsbereiche für die Priorität sind ungültig.',
      confirmQueryChange: 'Änderung der Abfrage bestätigen',
      confirmAdvancedQueryMessage: 'Beim Umschalten vom Abfrageerstellungsmodus in den erweiterten Modus werden Ihre Abstimmungskriterien zurückgesetzt.',
      confirmQueryBuilderMessage: 'Beim Umschalten vom erweiterten Modus in den Abfrageerstellungsmodus werden Ihre Abstimmungskriterien zurückgesetzt.',
      groupAction: 'In einem Vorfall gruppieren',
      suppressAction: 'Warnmeldung unterdrücken',
      timeUnits: {
        DAY: 'Tage',
        HOUR: 'Stunden',
        MINUTE: 'Minuten'
      },
      ruleBuilder: {
        addConditionGroup: 'Gruppe hinzufügen',
        removeConditionGroup: 'Gruppe entfernen',
        addCondition: 'Bedingung hinzufügen',
        field: 'Feld',
        operator: 'Operator',
        operators: {
          '=': 'Entspricht',
          '!=': 'ist nicht gleich',
          'begins': 'beginnt mit',
          'ends': 'endet in',
          'contains': 'enthält',
          'regex': 'entspricht regex',
          'in': 'in',
          'nin': 'nicht in',
          '>': 'ist größer als',
          '>=': 'ist gleich oder größer als',
          '<': 'ist kleiner als',
          '<=': 'ist gleich oder kleiner als'
        },
        groupOperators: {
          and: 'Alle diese',
          or: 'Beliebige von diesen',
          not: 'Nichts davon'
        },
        value: 'Wert',
        hasGroupsWithoutConditions: 'Alle Gruppen müssen mindestens über eine Bedingung verfügen.',
        hasMissingConditionInfo: 'Für mindestens eine Bedingung ist kein Feld, Operator oder Wert angegeben.'
      },
      actionMessages: {
        deleteRuleConfirmation: 'Sind Sie sicher, dass Sie diese Regel löschen möchten? Dieser Vorgang kann nicht rückgängig gemacht werden.',
        reorderSuccess: 'Sie haben die Reihenfolge der Regeln erfolgreich geändert.',
        reorderFailure: 'Beim Ändern der Reihenfolge der Regeln ist ein Problem aufgetreten.',
        cloneSuccess: 'Sie haben die ausgewählte Regel erfolgreich geklont.',
        cloneFailure: 'Beim Klonen der ausgewählten Regel ist ein Problem aufgetreten.',
        createSuccess: 'Sie haben erfolgreich eine neue Regel erstellt.',
        createFailure: 'Beim Erstellen der neuen Regel ist ein Problem aufgetreten.',
        deleteSuccess: 'Sie haben die ausgewählte Regel erfolgreich gelöscht.',
        deleteFailure: 'Beim Löschen der ausgewählten Regel ist ein Problem aufgetreten.',
        saveSuccess: 'Die Änderungen an der Regel wurden erfolgreich gespeichert.',
        saveFailure: 'Beim Speichern der Änderungen an der Regel ist ein Problem aufgetreten.',
        duplicateNameFailure: 'Eine Regel mit diesem Namen existiert bereits. Ändern Sie den Namen der Regel, damit er eindeutig ist.'
      },
      missingRequiredInfo: 'Es fehlen erforderliche Informationen für die Incident-Regel.'
    },
    notifications: {
      settings: 'Einstellungen für Antwort auf Benachrichtigung',
      emailServer: 'E-Mail-Server',
      socEmailAddresses: 'E-Mail-Adressen des SOC-Managers',
      noSocEmails: 'Es sind keine E-Mail-Adressen für den SOC-Manager konfiguriert.',
      emailAddressPlaceholder: 'Geben Sie eine E-Mail-Adresse ein, die hinzugefügt werden soll.',
      addEmail: 'Hinzufügen',
      notificationTypes: 'Benachrichtigungstypen',
      type: 'Typ',
      sendToAssignee: 'An Zuweisungsempfänger senden',
      sendToSOCManagers: 'An SOC-Manager senden',
      types: {
        'incident-created': 'Incident erstellt',
        'incident-state-changed': 'Incident aktualisiert'
      },
      hasUnsavedChanges: 'Es liegen nicht gespeicherte Änderungen vor. Klicken Sie zum Speichern auf „Anwenden“.',
      emailServerSettings: 'E-Mail-Server-Einstellungen',
      noManagePermissions: 'Sie verfügen nicht über ausreichende Berechtigungen, um diese Aktion durchführen zu können.',
      actionMessages: {
        fetchFailure: 'Beim Laden der Einstellungen für die Antwort ist ein Problem aufgetreten.',
        updateSuccess: 'Sie haben die Einstellungen für die Antwort erfolgreich aktualisiert.',
        updateFailure: 'Beim Aktualisieren der Einstellungen für die Antwort ist ein Problem aufgetreten.'
      }
    }
  },
  respond: {
    title: 'Reagieren',
    common: {
      yes: 'Ja',
      no: 'Nein',
      true: 'Ja',
      false: 'Nein'
    },
    none: 'Keine',
    select: 'Auswählen',
    close: 'Schließen',
    empty: '(leer)',
    filters: 'Filter',
    errorPage: {
      serviceDown: 'Antwortserver ist offline',
      serviceDownDescription: 'Der Antwortserver wird nicht ausgeführt oder es kann nicht darauf zugegriffen werden. Wenden Sie sich an den Administrator, um dieses Problem zu beheben.',
      fetchError: 'Ein Fehler ist aufgetreten. Der Antwortserver ist eventuell offline oder es kann nicht darauf zugegriffen werden.'
    },
    timeframeOptions: {
      LAST_5_MINUTES: 'Letzte 5 Minuten',
      LAST_10_MINUTES: 'Letzte 10 Minuten',
      LAST_15_MINUTES: 'Letzte 15 Minuten',
      LAST_30_MINUTES: 'Letzte 30 Minuten',
      LAST_HOUR: 'Letzte Stunde',
      LAST_3_HOURS: 'Letzte 3 Stunden',
      LAST_6_HOURS: 'Letzte 6 Stunden',
      LAST_TWELVE_HOURS: 'Letzte 12 Stunden',
      LAST_TWENTY_FOUR_HOURS: 'Letzte 24 Stunden',
      LAST_FORTY_EIGHT_HOURS: 'Die letzten 2 Tage',
      LAST_5_DAYS: 'Die letzten 5 Tage',
      LAST_7_DAYS: 'Die letzten 7 Tage',
      LAST_14_DAYS: 'Die letzten 14 Tage',
      LAST_30_DAYS: 'Die letzten 30 Tage',
      ALL_TIME: 'Alle Daten'
    },
    entities: {
      incidents: 'Incidents',
      remediationTasks: 'Aufgaben',
      alerts: 'Warnmeldungen',
      actionMessages: {
        updateSuccess: 'Änderung erfolgreich',
        updateFailure: 'Beim Aktualisieren des Felds für diesen Datensatz ist ein Problem aufgetreten.',
        createSuccess: 'Neuer Datensatz erfolgreich hinzugefügt',
        createFailure: 'Bei der Erstellung dieses Datensatzes ist ein Problem aufgetreten.',
        deleteSuccess: 'Sie haben diesen Datensatz erfolgreich gelöscht.',
        deleteFailure: 'Beim Löschen dieses Datensatzes ist ein Problem aufgetreten.',
        saveSuccess: 'Die Änderungen wurden erfolgreich gespeichert.',
        saveFailure: 'Beim Speichern dieses Datensatzes ist ein Problem aufgetreten.'
      },
      alert: 'Warnmeldung'
    },
    explorer: {
      noResults: 'Keine Ergebnisse gefunden. Erweitern Sie den Zeitbereich oder passen Sie vorhandene Filter an, um mehr Ergebnisse zu erhalten.',
      confirmation: {
        updateTitle: 'Aktualisierung bestätigen',
        deleteTitle: 'Löschen bestätigen',
        bulkUpdateConfrimation: 'Sie sind im Begriff, an mehreren Elementen die folgenden Änderungen vorzunehmen.',
        deleteConfirmation: 'Möchten Sie {{count}} Datensatz/Datensätze wirklich löschen? Dieser Vorgang kann nicht rückgängig gemacht werden.',
        field: 'Feld',
        value: 'Wert',
        recordCountAffected: 'Anzahl der Elemente'
      },
      filters: {
        timeRange: 'Zeitbereich',
        reset: 'Filter zurücksetzen',
        customDateRange: 'Benutzerdefinierter Datumsbereich',
        customStartDate: 'Startdatum',
        customEndDate: 'Enddatum',
        customDateErrorStartAfterEnd: 'Datum und Uhrzeit für den Start müssen vor Datum und Uhrzeit für das Ende liegen'
      },
      inspector: {
        overview: 'Überblick'
      },
      footer: '{{count}} von {{total}} Elementen wird/werden angezeigt'
    },
    remediationTasks: {
      loading: 'Aufgaben werden geladen',
      addNewTask: 'Neue Aufgabe hinzufügen',
      noTasks: 'Keine Aufgaben vorhanden für {{incidentId}}',
      openFor: 'Eröffnet',
      newTaskFor: 'Neue Aufgabe für',
      delete: 'Aufgabe löschen',
      noAccess: 'Sie sind nicht zum Anzeigen von Aufgaben berechtigt.',
      actions: {
        actionMessages: {
          deleteWarning: 'Eine aus NetWitness gelöschte Aufgabe wird nicht automatisch auch aus anderen Systemen gelöscht. Bitte beachten Sie, dass Sie dafür verantwortlich sind, ' +
          'die Aufgabe aus allen anderen zutreffenden Systemen zu löschen.'
        }
      },
      filters: {
        taskId: 'Aufgaben-ID',
        idFilterPlaceholder: 'z. B. REM-123',
        idFilterError: 'Die ID muss folgendes Format aufweisen: REM-###'
      },
      list: {
        priority: 'Priorität',
        select: 'Auswählen',
        id: 'ID',
        name: 'Name',
        createdDate: 'Erstellt',
        status: 'Status',
        assignee: 'Zuweisungsempfänger',
        noResultsMessage: 'Keine übereinstimmenden Aufgaben gefunden',
        incidentId: 'Incident-ID',
        targetQueue: 'Zielwarteschlange',
        remediationType: 'Typ',
        escalated: 'Eskaliert',
        lastUpdated: 'Letzte Aktualisierung',
        description: 'Beschreibung',
        createdBy: 'Erstellt von'
      },
      type: {
        QUARANTINE_HOST: 'Host in Quarantäne setzen',
        QUARANTINE_NETORK_DEVICE: 'Netzwerkgerät in Quarantäne setzen',
        BLOCK_IP_PORT: 'IP/Port blockieren',
        BLOCK_EXTERNAL_ACCESS_TO_DMZ: 'Externen Zugriff auf DMZ blockieren',
        BLOCK_VPN_ACCESS: 'VPN-Zugriff blockieren',
        REIMAGE_HOST: 'Neues Hostimage erstellen',
        UPDATE_FIREWALL_POLICY: 'Firewall-Policy aktualisieren',
        UPDATE_IDS_IPS_POLICY: 'IDS/IPS-Policy aktualisieren',
        UPDATE_WEB_PROXY_POLICY: 'Webproxy-Policy aktualisieren',
        UPDATE_ACCESS_POLICY: 'Zugriffs-Policy aktualisieren',
        UPDATE_VPN_POLICY: 'VPN-Richtlinie aktualisieren',
        CUSTOM: 'Benutzerdefiniert',
        MITIGATE_RISK: 'Risiko mindern',
        MITIGATE_COMPLIANCE_VIOLATION: 'Complianceverstöße mindern',
        MITIGATE_VULNERABILITY_THREAT: 'Schwachstelle/Bedrohung mindern',
        UPDATE_CORPORATE_BUSINESS_POLICY: 'Unternehmens-Policy aktualisieren',
        NOTIFY_BC_DR_TEAM: 'BC/DR-Team benachrichtigen',
        UPDATE_RULES: 'Regel(n) aktualisieren',
        UPDATE_FEEDS: 'Feed(s) aktualisieren'
      },
      targetQueue: {
        OPERATIONS: 'Vorgänge',
        GRC: 'GRC',
        CONTENT_IMPROVEMENT: 'Inhaltsverbesserung'
      },
      noDescription: 'Keine Beschreibung für diese Aufgabe vorhanden'
    },
    incidents: {
      incidentName: 'Incident-Name',
      actions: {
        addEntryLabel: 'Eintrag hinzuf.',
        confirmUpdateTitle: 'Aktualisierung bestätigen',
        changeAssignee: 'Zuweisungsempfänger ändern',
        changePriority: 'Priorität ändern',
        changeStatus: 'Status ändern',
        addJournalEntry: 'Journaleintrag hinzufügen',
        actionMessages: {
          deleteWarning: 'Warnung: Sie sind im Begriff, einen oder mehrere Incidents zu löschen, die eventuell Aufgaben enthalten oder eskaliert wurden. ' +
          'Ein aus NetWitness gelöschter Incident wird nicht automatisch auch aus anderen Systemen gelöscht. Bitte beachten Sie, dass Sie dafür verantwortlich sind, ' +
          'den Incident und seine Aufgaben aus allen anderen zutreffenden Systemen zu löschen.',
          addJournalEntrySuccess: 'Sie haben einen Journaleintrag zu Incident {{incidentId}} hinzugefügt.',
          addJournalEntryFailure: 'Beim Hinzufügen eines Journaleintrags zu Incident {{incidentId}} ist ein Problem aufgetreten.',
          incidentCreated: 'Sie haben den Incident {{incidentId}} erfolgreich aus den ausgewählten Warnmeldungen erstellt. Die Priorität des Incidents wurde standardmäßig auf NIEDRIG gesetzt.',
          incidentCreationFailed: 'Beim Erstellen eines Incidents aus den ausgewählten Warnmeldungen ist ein Problem aufgetreten.',
          createIncidentInstruction: 'Aus den ausgewählten {{alertCount}} Warnmeldungen wird ein Incident erstellt. Geben Sie einen Namen für den Incident an.',
          addAlertToIncidentSucceeded: 'Sie haben die ausgewählten Warnmeldungen erfolgreich zu {{incidentId}} hinzugefügt.',
          addAlertToIncidentFailed: 'Beim Hinzufügen der ausgewählten Warnmeldungen zu diesem Incident ist ein Problem aufgetreten.'
        },
        deselectAll: 'Alle Markierungen aufheben'
      },
      filters: {
        timeRange: 'Zeitbereich',
        incidentId: 'Incident-ID',
        idFilterPlaceholder: 'z. B. INC-123',
        idFilterError: 'Die ID muss folgendes Format aufweisen: INC-###',
        reset: 'Filter zurücksetzen',
        customDateRange: 'Benutzerdefinierter Datumsbereich',
        customStartDate: 'Startdatum',
        customEndDate: 'Enddatum',
        customDateErrorStartAfterEnd: 'Datum und Uhrzeit für den Start müssen vor Datum und Uhrzeit für das Ende liegen',
        showOnlyUnassigned: 'Nur nicht zugewiesene Incidents anzeigen'
      },
      selectionCount: '{{selectionCount}} ausgewählt',
      label: 'Incidents',
      list: {
        select: 'Auswählen',
        id: 'ID',
        name: 'Name',
        createdDate: 'Erstellt',
        status: 'Status',
        priority: 'Priorität',
        riskScore: 'Risikowert',
        assignee: 'Zuweisungsempfänger',
        alertCount: 'Warnmeldungen',
        sources: 'Quelle',
        noResultsMessage: 'Keine übereinstimmenden Incidents gefunden'
      },
      footer: '{{count}} von {{total}} Incidents werden angezeigt'
    },
    alerts: {
      createIncident: 'Incident erstellen',
      addToIncident: 'Einem Incident hinzufügen',
      incidentSearch: {
        searchInputLabel: 'Offene Incidents suchen',
        searchInputPlaceholder: 'Nach Incident-ID (z. B. INC-123) oder Incident-Name suchen',
        noResults: 'Keine offenen Incidents gefunden',
        noQuery: 'Verwenden Sie das obige Suchfeld, um anhand des Namens oder der ID nach offenen Incidents zu suchen. Der Suchbegriff muss mindestens drei (3) Zeichen enthalten.',
        error: 'Beim Suchen nach Incidents ist ein Problem aufgetreten.'
      },
      actions: {
        actionMessages: {
          deleteWarning: 'Warnung: Sie sind im Begriff, eine oder mehrere Warnmeldungen zu löschen, die eventuell Incidents zugewiesen sind. ' +
          'Beachten Sie, dass zugehörige Incidents entsprechend aktualisiert oder gelöscht werden.'
        }
      },
      list: {
        receivedTime: 'Erstellt',
        severity: 'Schweregrad',
        numEvents: 'Ereignisanzahl',
        id: 'ID',
        name: 'Name',
        status: 'Status',
        source: 'Quelle',
        incidentId: 'Incident-ID',
        partOfIncident: 'Zum Incident gehörig',
        type: 'Typ',
        hostSummary: 'Hostzusammenfassung',
        userSummary: 'Benutzerübersicht'
      },
      notAssociatedWithIncident: '(Keine)',
      originalAlert: 'Rohwarnmeldung',
      originalAlertLoading: 'Rohwarnmeldung wird geladen',
      originalAlertError: 'Beim Laden der Rohwarnmeldung ist ein Problem aufgetreten.',
      alertNames: 'Warnmeldungsnamen'
    },
    alert: {
      status: {
        GROUPED_IN_INCIDENT: 'Gruppiert in Incident',
        NORMALIZED: 'Normalisiert'
      },
      type: {
        Correlation: 'Korrelation',
        Log: 'Protokoll',
        Network: 'Netzwerk',
        'Instant IOC': 'IOC-Sofortwarnmeldung',
        'Web Threat Detection Incident': 'Web Threat Detection-Incident',
        'File Share': 'Dateifreigaben',
        'Manual Upload': 'Manuell hochladen',
        'On Demand': 'Nach Bedarf',
        Resubmit: 'Erneut übermitteln',
        Unknown: 'Unbekannt'
      },
      source: {
        ECAT: 'Endpunkt',
        'Event Stream Analysis': 'Event Stream Analysis',
        'Event Streaming Analytics': 'Event Stream Analysis',
        'Security Analytics Investigator': 'Security Analytics Investigator',
        'Web Threat Detection': 'Web Threat Detection',
        'Malware Analysis': 'Malware Analysis',
        'Reporting Engine': 'Reporting Engine',
        'NetWitness Investigate': 'NetWitness Investigate'
      },
      backToAlerts: 'Zurück zu Warnmeldungen'
    },
    incident: {
      created: 'Erstellt',
      status: 'Status',
      priority: 'Priorität',
      riskScore: 'Risikowert',
      assignee: 'Zuweisungsempfänger',
      alertCount: 'Indikator(en)',
      eventCount: 'Ereignis(se)',
      catalystCount: 'Katalysatoren',
      sealed: 'Versiegelt',
      sealsAt: 'Wird versiegelt um',
      sources: 'Quellen',
      categories: 'Kategorien',
      backToIncidents: 'Zurück zu Incidents',
      overview: 'Überblick',
      indicators: 'Indikatoren',
      indicatorsCutoff: '{{limit}} von {{expected}} Indikatoren werden angezeigt',
      events: 'Ereignisse',
      loadingEvents: 'Ereignisse werden geladen ...',
      view: {
        graph: 'Anzeigen: Graph',
        datasheet: 'Anzeigen: Datenblatt'
      },
      journalTasksRelated: 'Journal, Aufgaben und verwandte',
      search: {
        tab: 'Verwandt',
        title: 'Verwandte Indikatoren',
        subtext: 'Geben Sie unten einen Wert ein und klicken Sie auf „Suchen“, um für diesen Wert nach verwandten Indikatoren zu suchen.',
        partOfThisIncident: 'Zum Incident gehörig',
        types: {
          IP: 'IP',
          MAC_ADDRESS: 'MAC',
          HOST: 'Host',
          DOMAIN: 'Domain',
          FILE_NAME: 'Dateiname',
          FILE_HASH: 'Hash',
          USER: 'Benutzer',
          label: 'Suchen'
        },
        text: {
          label: 'Wert',
          placeholders: {
            IP: 'IP-Adresse eingeben',
            MAC_ADDRESS: 'MAC-Adresse eingeben',
            HOST: 'Hostnamen eingeben',
            DOMAIN: 'Domainnamen eingeben',
            FILE_NAME: 'Dateinamen eingeben',
            FILE_HASH: 'Datei-Hash eingeben',
            USER: 'Geben Sie einen Benutzernamen ein.'
          }
        },
        timeframe: {
          label: 'Wann'
        },
        devices: {
          source: 'Quelle',
          destination: 'Ziel',
          detector: 'Detektor',
          domain: 'Domain',
          label: 'Suchen in'
        },
        results: {
          title: 'Indikatoren für',
          openInNewWindow: 'In neuem Fenster öffnen'
        },
        actions: {
          search: 'Suchen',
          cancel: 'Abbrechen',
          addToIncident: 'Einem Incident hinzufügen',
          addingAlert: 'Warnmeldung zu einem Incident hinzufügen',
          unableToAddAlert: 'Warnmeldung kann nicht zum Incident hinzugefügt werden.',
          pleaseTryAgain: 'Wiederholen Sie den Vorgang.'
        }
      }
    },
    storyline: {
      loading: 'Incident-Abfolge wird geladen',
      error: 'Incident-Abfolge kann nicht geladen werden',
      catalystIndicator: 'Katalysatorindikator',
      relatedIndicator: 'Verwandter Indikator',
      source: 'Quelle',
      partOfIncident: 'Zum Incident gehörig',
      relatedBy: 'Verwandt mit Katalysator durch',
      event: 'Ereignis',
      events: 'Ereignisse'
    },
    details: {
      loading: 'Incident-Details werden geladen',
      error: 'Incident-Details können nicht geladen werden'
    },
    journal: {
      newEntry: 'Neuer Journaleintrag',
      title: 'Journal',
      close: 'Schließen',
      milestone: 'Meilenstein',
      loading: 'Journaleinträge werden geladen',
      noEntries: 'Keine Journaleinträge für {{incidentId}} vorhanden',
      delete: 'Eintrag lösch.',
      deleteConfirmation: 'Möchten Sie diesen Journaleintrag wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden.',
      noAccess: 'Sie sind nicht berechtigt, Journaleinträge anzuzeigen.'
    },
    milestones: {
      title: 'Meilensteine',
      RECONNAISSANCE: 'Aufklärung',
      DELIVERY: 'Lieferung',
      EXPLOITATION: 'Ausnutzung',
      INSTALLATION: 'Installation',
      COMMAND_AND_CONTROL: 'Befehl und Kontrolle',
      ACTION_ON_OBJECTIVE: 'Aktion für Ziel',
      CONTAINMENT: 'Eingrenzung',
      ERADICATION: 'Behebung',
      CLOSURE: 'Abschluss'
    },
    eventDetails: {
      title: 'Ereignisdetails',
      events: 'Ereignisse',
      in: 'in',
      indicators: 'Indikatoren',
      type: {
        'Instant IOC': 'IOC-Sofortwarnmeldung',
        'Log': 'Protokoll',
        'Network': 'Netzwerk',
        'Correlation': 'Korrelation',
        'Web Threat Detection': 'Web Threat Detection',
        'Web Threat Detection Incident': 'Web Threat Detection-Incident',
        'Unknown': 'Ereignis',
        'File Share': 'Dateifreigaben',
        'Manual Upload': 'Manuell hochladen',
        'On Demand': 'Nach Bedarf',
        Resubmit: 'Erneut übermitteln'
      },
      backToTable: 'Zurück zu Tabelle',
      labels: {
        timestamp: 'Zeitstempel',
        type: 'Typ',
        description: 'Beschreibung',
        source: 'Quelle',
        destination: 'Ziel',
        domain: 'Domain/Host',
        detector: 'Detektor',
        device: 'Gerät',
        ip_address: 'IP-Adresse',
        mac_address: 'MAC-Adresse',
        dns_hostname: 'Host',
        dns_domain: 'Domain',
        netbios_name: 'NetBIOS-Name',
        asset_type: 'Asset-Typ',
        business_unit: 'Geschäftsbereich',
        facility: 'Facility',
        criticality: 'Wichtigkeitsrating',
        compliance_rating: 'Compliancerating',
        malicious: 'Schädlich',
        site_categorization: 'Kategorisierung der Website',
        geolocation: 'GeoLocation',
        city: 'Ort',
        country: 'Land',
        longitude: 'Längengrad',
        latitude: 'Breitengrad',
        organization: 'Organisation',
        device_class: 'Geräteklasse',
        product_name: 'Produktname',
        port: 'Port',
        user: 'Benutzer',
        username: 'Benutzername',
        ad_username: 'Active Directory-Benutzername',
        ad_domain: 'Active Directory-Domain',
        email_address: 'E-Mail-Adresse',
        os: 'Betriebssystem',
        size: 'Größe',
        data: 'Daten',
        filename: 'Dateiname',
        hash: 'Hash',
        av_hit: 'AV Hit',
        extension: 'Erweiterung',
        mime_type: 'MIME-Typ',
        original_path: 'Ursprungspfad',
        av_aliases: 'AV-Aliasse',
        networkScore: 'Punktzahl für Netzwerke',
        communityScore: 'Community-Bewertung',
        staticScore: 'Statische Bewertung',
        sandboxScore: 'Sandbox-Bewertung',
        opswat_result: 'OPSWAT-Ergebnis',
        yara_result: 'YARA-Ergebnis',
        bit9_status: 'Bit9-Status',
        module_signature: 'Modulsignatur',
        related_links: 'Verwandte Links',
        url: 'URL',
        ecat_agent_id: 'NWE Agent-ID',
        ldap_ou: 'LDAP OU',
        last_scanned: 'Letzter Scan',
        enrichment: 'Erweiterung',
        enrichmentSections: {
          domain_registration: 'Domainregistrierung',
          command_control_risk: 'Befehl und Kontrolle',
          beaconing_behavior: 'Beaconing',
          domain_age: 'Domainalter',
          expiring_domain: 'Ablaufende Domain',
          rare_domain: 'Seltene Domain',
          no_referers: 'Referrer',
          rare_user_agent: 'Seltener User-Agent'
        },
        registrar_name: 'Domainregistrierungsstelle',
        registrant_organization: 'Registrierte Organisation',
        registrant_name: 'Registrierter Name',
        registrant_email: 'Registrierte E-Mail-Adresse',
        registrant_telephone: 'Registrierte Telefonnummer',
        registrant_street1: 'Registrierte Straße',
        registrant_postal_code: 'Registrierte Postleitzahl',
        registrant_city: 'Registrierter Ort',
        registrant_state: 'Registriertes Bundesland',
        registrant_country: 'Registriertes Land',
        whois_created_dateNetWitness: 'Registrierungsdatum',
        whois_updated_dateNetWitness: 'Aktualisierungsdatum',
        whois_expires_dateNetWitness: 'Ablaufdatum',
        whois_age_scoreNetWitness: 'Wert für Domainregistrierungsalter',
        whois_validity_scoreNetWitness: 'Wert für Ablaufende Domain',
        whois_estimated_domain_age_daysNetWitness: 'Domainregistrierungsalter (in Tagen)',
        whois_estimated_domain_validity_daysNetWitness: 'Zeit bis Ablauf (in Tagen)',
        command_control_aggregate: 'Command-and-Control-Risikowert',
        command_control_confidence: 'Wahrscheinlichkeit',
        weighted_c2_referer_score: 'Anteil des Werts für Seltene Domain (dieses Netzwerk)',
        weighted_c2_referer_ratio_score: 'Anteil des Werts für Ohne Domain-Referrer',
        weighted_c2_ua_ratio_score: 'Anteil des Werts für Seltener User-Agent',
        weighted_c2_whois_age_score: 'Anteil des Werts für Domainregistrierungsalter',
        weighted_c2_whois_validity_score: 'Anteil des Werts für Ablaufende Domain',
        smooth_score: 'Bewertung',
        beaconing_period: 'Punkt',
        newdomain_score: 'Wert für Domainalter (dieses Netzwerk)',
        newdomain_age: 'Domainalter (dieses Netzwerk)',
        referer_score: 'Wert für Seltenheit',
        referer_cardinality: 'Seltene Kardinalität',
        referer_num_events: 'Seltene Ereignisse',
        referer_ratio: 'Seltenheitsrate',
        referer_ratio_score: 'Wert für Seltenheitsrate',
        referer_cond_cardinality: 'Bedingte seltene Kardinalität',
        ua_num_events: 'Vorkommnisse in letzter Woche',
        ua_ratio: 'Prozentsatz der IP-Adressen mit seltenem User-Agent',
        ua_ratio_score: 'Wert für Seltene User-Agents',
        ua_cond_cardinality: 'IP-Adressen mit seltenem User-Agent'
      },
      periodValue: {
        hours: 'Stunde(n)',
        minutes: 'Minute(n)',
        seconds: 'Sekunde(n)'
      }
    },
    eventsTable: {
      time: 'Uhrzeit',
      type: 'Typ',
      sourceDomain: 'Quelldomain',
      destinationDomain: 'Zieldomain',
      sourceHost: 'Quellhost',
      destinationHost: 'Zielhost',
      sourceIP: 'Quell-IP',
      destinationIP: 'Ziel-IP',
      detectorIP: 'Detektor-IP',
      sourcePort: 'Quellport',
      destinationPort: 'Zielport',
      sourceMAC: 'Quell-MAC',
      destinationMAC: 'Ziel-MAC',
      sourceUser: 'Quellbenutzer',
      destinationUser: 'Zielbenutzer',
      fileName: 'Dateiname',
      fileHash: 'Datei-Hash',
      indicator: 'Indikator'
    },
    entity: {
      legend: {
        user: 'Benutzer',
        host: 'Host(s)',
        ip: 'IP(s)',
        domain: 'Domain(s)',
        mac_address: 'MAC(s)',
        file_name: 'Datei(en)',
        file_hash: 'Hash(es)',
        selection: {
          storyPoint: 'in {{count}} ausgewählten Indikatoren',
          event: 'in {{count}} ausgewählten Ereignissen'
        },
        selectionNotShown: 'Die ausgewählten Nodes konnten wegen Größenbeschränkungen nicht angezeigt werden.',
        hasExceededNodeLimit: 'Nur die ersten {{limit}} Nodes werden angezeigt.',
        showAll: 'Alle Daten anzeigen'
      }
    },
    enrichment: {
      uniformTimeIntervals: 'Die Zeitintervalle zwischen Kommunikationsereignissen sind sehr einheitlich.',
      newDomainToEnvironment: 'Die Domain ist in der Umgebung relativ neu.',
      rareDomainInEnvironment: 'Die Domain ist in dieser Umgebung selten.',
      newDomainRegistration: 'Die Domain ist basierend auf dem Registrierungsdatum relativ neu:',
      domainRegistrationExpires: 'Die Domainregistrierung läuft relativ bald ab:',
      rareUserAgent: 'Ein hoher Prozentanteil der Hostverbindungen zur Domain nutzt einen seltenen oder keinen User-Agent.',
      noReferers: 'Ein hoher Prozentanteil der Hostverbindungen zur Domain nutzt keine Referrer.',
      highNumberServersAccessed: 'Heute wurde auf eine abnormal hohe Anzahl an Servern zugegriffen.',
      highNumberNewServersAccessed: 'Heute wurde auf eine abnormal hohe Anzahl an neuen Servern zugegriffen.',
      highNumberNewDevicesAccessed: 'Diese Woche wurde auf eine abnormal hohe Anzahl an neuen Geräten zugegriffen.',
      highNumberFailedLogins: 'Heute sind bei einer abnormal hohen Anzahl an Servern Anmeldungen fehlgeschlagen.',
      passTheHash: 'Potentieller Pass-the-Hash-Angriff angezeigt durch ein neues Gerät gefolgt von einem neuen Server.',
      rareLogonType: 'Zugriff über einen Windows-Anmeldetyp, der bisher relativ selten verwendet wurde.',
      authFromRareDevice: 'Authentifizierung von einem seltenen Gerät aus.',
      authFromRareLocation: 'Zugriff von einem seltenen Standort aus.',
      authFromRareServiceProvider: 'Zugriff über einen seltenen Serviceanbieter.',
      authFromNewServiceProvider: 'Zugriff über einen neuen Serviceanbieter.',
      highNumberVPNFailedLogins: 'Hohe Anzahl fehlgeschlagener VPN-Anmeldungen.',
      daysAgo: 'Vor {{days}} Tag(en)',
      days: '{{days}} Tag(e)',
      domainIsWhitelisted: 'Domain auf weißer Liste.',
      domainIsNotWhitelisted: 'Domain nicht auf weißer Liste.'
    },
    sources: {
      'C2-Packet': 'Verhaltensanalysen zur Benutzerentität',
      'C2-Log': 'Verhaltensanalysen zur Benutzerentität',
      'UBA-WinAuth': 'Verhaltensanalysen zur Benutzerentität',
      UbaCisco: 'Verhaltensanalysen zur Benutzerentität',
      ESA: 'Event Stream-Analysen',
      'Event-Stream-Analysis': 'Event Stream-Analysen',
      RE: 'Reporting Engine',
      'Reporting-Engine': 'Reporting Engine',
      ModuleIOC: 'Endpunkt',
      ECAT: 'Endpunkt',
      generic: 'NetWitness'
    },
    status: {
      NEW: 'Neu',
      ASSIGNED: 'Zugewiesen',
      IN_PROGRESS: 'Läuft',
      REMEDIATION_REQUESTED: 'Aufgabe angefordert',
      REMEDIATION_COMPLETE: 'Aufgabe abgeschlossen',
      CLOSED: 'Geschlossen',
      CLOSED_FALSE_POSITIVE: 'Geschlossen – falsch positives Ergebnis',
      REMEDIATED: 'Korrigiert',
      RISK_ACCEPTED: 'Risiko akzeptiert',
      NOT_APPLICABLE: 'Nicht zutreffend'
    },
    priority: {
      LOW: 'Niedrig',
      MEDIUM: 'Mittel',
      HIGH: 'Hoch',
      CRITICAL: 'Kritisch'
    },
    assignee: {
      none: '(Nicht zugewiesen)'
    }
  },
  context: {
    noData: 'Kein entsprechender Kontext verfügbar',
    noResults: '(Keine Ergebnisse)',
    notConfigured: '(Nicht konfiguriert)',
    title: 'Kontext für',
    lastUpdated: 'Letzte Aktualisierung:',
    timeWindow: 'Zeitfenster: ',
    iiocScore: 'IIOC-Wert',
    IP: 'IP',
    USER: 'Benutzer',
    MAC_ADDRESS: 'Mac-Adresse',
    HOST: 'Host',
    FILE_NAME: 'Dateiname',
    FILE_HASH: 'Datei-Hash',
    DOMAIN: 'Domain',
    noValues: 'Kontextquellen ohne Werte: ',
    dsNotConfigured: 'Nicht konfigurierte Kontextquellen: ',
    marketingText: ' ist derzeit keine konfigurierte Datenquelle in Context Hub. Wenden Sie sich an Ihren Administrator, um diese Funktion zu aktivieren. Context Hub zentralisiert bei Bedarf Datenquellen aus Endpunkten, Warnmeldungen, Incidents, Listen und vielen anderen Quellen. Weitere Informationen finden Sie in der Hilfe.',
    lcMarketingText: 'Live Connect erfasst, analysiert und bewertet Intelligence-Daten zu Bedrohungen wie beispielsweise IP-Adressen, Domains und Datei-Hashes, die aus verschiedenen Quellen erfasst werden. Live Connect ist keine Standarddatenquelle in Context Hub. Sie müssen Sie manuell aktivieren. Weitere Informationen finden Sie in der Hilfe.',
    timeUnit: {
      allData: 'ALLE DATEN',
      HOUR: 'STUNDE',
      HOURS: 'STUNDEN',
      MINUTE: 'MINUTE',
      MINUTES: 'MINUTEN',
      DAY: 'TAG',
      DAYS: 'TAGE',
      MONTH: 'MONAT',
      MONTHS: 'MONATE',
      YEAR: 'JAHR',
      YEARS: 'JAHRE',
      WEEK: 'WOCHE',
      WEEKS: 'WOCHEN'
    },
    marketingDSType: {
      Users: 'Active Directory',
      Alerts: 'Reagieren (Warnmeldungen)',
      Incidents: 'Reagieren (Incidents)',
      Machines: 'Endpunkt (Rechner)',
      Modules: 'Endpunkt (Module)',
      IOC: 'Endpunkt (IOC)',
      Archer: 'Archer',
      LIST: 'Liste'
    },
    header: {
      title: {
        archer: 'Archer',
        users: 'Active Directory',
        alerts: 'Warnmeldungen',
        incidents: 'Incidents',
        lIST: 'Listen',
        endpoint: 'NetWitness Endpoint',
        liveConnectIp: 'Live Connect',
        liveConnectFile: 'Live Connect',
        liveConnectDomain: 'Live Connect'
      },
      archer: 'Archer',
      overview: 'Überblick',
      iioc: 'IIOC',
      users: 'Benutzer',
      categoryTags: 'Kategorietags',
      modules: 'Module',
      incidents: 'Incidents',
      alerts: 'Warnmeldungen',
      files: 'Dateien',
      lists: 'Listen',
      feeds: 'Feeds',
      endpoint: 'Endpunkt',
      liveConnect: 'Live Connect',
      unsafe: 'Sie sind nicht sicher',
      closeButton: {
        title: 'Schließen'
      },
      help: {
        title: 'Hilfe'
      }
    },
    toolbar: {
      investigate: 'Untersuchen',
      endpoint: 'NetWitness Endpoint',
      googleLookup: 'Google-Abfrage',
      virusTotal: 'VirusTotal-Abfrage',
      addToList: 'Zur Liste hinzufügen'
    },
    hostSummary: {
      title: 'Endpunkt',
      riskScore: 'Risikowert',
      modulesCount: 'Modulanzahl',
      iioc0: 'IIOC 0',
      iioc1: 'IIOC 1',
      lastUpdated: 'Letzte Aktualisierung',
      adminStatus: 'Administratorstatus',
      lastLogin: 'Letzte Anmeldung',
      macAddress: 'MAC-Adresse',
      operatingSystem: 'Betriebssystem',
      machineStatus: 'Computerstatus',
      ipAddress: 'IP-Adresse',
      endpoint: 'Gilt für Hosts mit installierten 4.x-Endpunkt-Agents. Installieren Sie den NetWitness Endpunkt-Thick-Client.'
    },
    addToList: {
      title: 'Zu Liste hinzufügen/Aus Liste entfernen',
      create: 'Neue Liste erstellen',
      metaValue: 'Metawert',
      newList: 'Neue Liste erstellen',
      tabAll: 'Alle',
      tabSelected: 'Ausgewählt',
      tabUnselected: 'Nicht ausgewählt',
      cancel: 'Abbrechen',
      save: 'Speichern',
      name: 'Listenname',
      listTitle: 'Liste',
      descriptionTitle: 'Beschreibung',
      filter: 'Ergebnisse filtern',
      listName: 'Listennamen eingeben',
      headerMessage: 'Klicken Sie auf „Speichern“, um die Liste(n) zu aktualisieren. Aktualisieren Sie die Seite, um die Aktualisierungen anzuzeigen.'
    },
    ADdata: {
      title: 'Benutzerinformationen',
      employeeID: 'Mitarbeiter-ID',
      department: 'Abteilung',
      location: 'Position',
      manager: 'Manager',
      groups: 'Gruppen',
      company: 'Unternehmen',
      email: 'E-Mail',
      phone: 'Telefonnummer',
      jobTitle: 'Position',
      lastLogon: 'Letzte Anmeldung',
      lastLogonTimeStamp: 'Zeitstempel letzte Anmeldung',
      adUserID: 'AD-Benutzer-ID',
      distinguishedName: 'Distinguished Name',
      displayName: 'Anzeigename'
    },
    archer: {
      title: 'Archer',
      criticalityRating: 'Wichtigkeitsrating',
      riskRating: 'Risikorating',
      deviceName: 'Gerätename',
      hostName: 'Hostname',
      deviceId: 'Geräte-ID',
      deviceType: 'Gerätetyp',
      deviceOwner: 'Device-Eigentümer',
      deviceOwnerTitle: 'Titel Device-Eigentümer',
      businessUnit: 'Geschäftsbereich',
      facility: 'Standort',
      ipAddress: 'Interne IP-Adresse'
    },
    modules: {
      title: 'Verdächtigste Module',
      iiocScore: 'IIOC-Wert',
      moduleName: 'Modulname',
      analyticsScore: 'Analysebewertung',
      machineCount: 'Rechneranzahl',
      signature: 'Signatur'
    },
    iiocs: {
      title: 'Rechner-IOC-Ebenen',
      lastExecuted: 'Zuletzt ausgeführt',
      description: 'Beschreibung',
      iOCLevel: 'IOC-Ebene',
      header: ''
    },
    incident: {
      title: 'Incidents',
      averageAlertRiskScore: 'Risikowert',
      _id: 'ID',
      name: 'Name',
      created: 'Erstellt',
      status: 'Status',
      assignee: 'ZUWEISUNGSEMPFÄNGER',
      alertCount: 'Warnmeldungen',
      priority: 'Priorität',
      header: ''
    },
    alerts: {
      title: 'Warnmeldungen',
      risk_score: 'Schweregrad',
      source: 'Quelle',
      name: 'Name',
      numEvents: 'Ereignisanzahl',
      severity: 'Schweregrad',
      created: 'Erstellt',
      id: 'Incident-ID',
      timestamp: 'Zeitstempel',
      header: ''
    },
    list: {
      title: 'Liste',
      createdByUser: 'Autor',
      createdTimeStamp: 'Erstellt',
      lastModifiedTimeStamp: 'Aktualisiert',
      dataSourceDescription: 'Beschreibung',
      dataSourceName: 'Name',
      data: 'Daten'
    },
    lc: {
      reviewStatus: 'Prüfstatus',
      status: 'Status',
      notReviewed: 'NICHT GEPRÜFT',
      noFeedback1: 'Es ist noch keine Feedback-Analyse vorhanden.',
      noFeedback2: ' Beteiligen Sie sich aktiv an der Live Connect Threat Community und geben Sie eine Risikobewertung ab.',
      blankField: '-',
      modifiedDate: 'Änderungsdatum',
      reviewer: 'Prüfer',
      riskConfirmation: 'Risikobestätigung',
      safe: 'Sie sind sicher',
      unsafe: 'Sie sind nicht sicher',
      unknown: 'Unbekannt',
      suspicious: 'Verdächtig',
      highRisk: 'Hohes Risiko',
      high: 'Hoch',
      med: 'Mittel',
      low: 'Niedrig',
      riskTags: 'Risikoindikatortags',
      commActivity: 'Community-Aktivität',
      firstSeen: 'Erstes Anzeigen',
      activitySS: 'Aktivitäts-Snapshot',
      communityTrend: 'Trend Community-Aktivität (letzte 30 Tage)',
      submitTrend: 'Trend Übermittlungsaktivität (letzte 30 Tage)',
      communityActivityDesc1: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}} %</span> von der Community angezeigt <span class="rsa-context-panel__liveconnect__entity">{{value}}</span>',
      communityActivityDesc2: 'Von den angezeigten <span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}} %</span> haben <span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}} %</span> der Community ein Feedback übermittelt',
      submittedActivityDesc1: 'Für die <span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}} %</span> an übermitteltem Feedback galt:',
      submittedActivityDesc2: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__high-risk">{{highrisk}} %</span> als „Hohes Risiko“ eingestuft',
      submittedActivityDesc3: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unsafe">{{unsafe}} %</span> als „Unsicher“ eingestuft',
      submittedActivityDesc4: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__suspicious">{{suspicious}} %</span> als „Verdächtig“ eingestuft',
      submittedActivityDesc5: '(nicht im Diagramm angezeigt)',
      submittedActivityDesc6: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__safe">{{safe}} %</span> als „Sicher“ eingestuft',
      submittedActivityDesc7: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unknown">{{unknown}} %</span> als „Unbekannt“ eingestuft',
      riskIndicators: 'Risikoindikatoren',
      identity: 'Identität',
      asn: 'Autonomous System Number (ASN)',
      prefix: 'Präfix',
      countryCode: 'Ländercode',
      countryName: 'Name des Landes',
      organization: 'Organisation',
      fileDate: 'Datum',
      fileName: 'DATEINAME',
      fileSize: 'DATEIGRÖSSE',
      md5: 'MD5',
      compileTime: 'KOMPILIERZEIT',
      sh1: 'SH1',
      mimeType: 'MIME-TYP',
      sh256: 'SH256',
      certificateInfo: 'Zertifikatinformationen',
      certIssuer: 'Aussteller des Zertifikats',
      certSubject: 'Betreff des Zertifikats',
      certSerial: 'Seriennummer des Zertifikats',
      certSigAlgo: 'Signaturalgorithmus',
      certThumbprint: 'Gegenzeichner des Zertifikats',
      certNotValidBefore: 'Zertifikat gültig ab',
      certNotValidAfter: 'Zertifikat gültig bis',
      whois: 'WHOIS',
      whoisCreatedDate: 'Erstellt',
      whoisUpdatedDate: 'Aktualisierungsdatum',
      whoisExpiredDate: 'Ablaufdatum',
      whoisRegType: 'Typ',
      whoisRegName: 'Name',
      whoisRegOrg: 'Organisation',
      whoisRegStreet: 'Straße',
      whoisRegCity: 'Ort',
      whoisRegState: 'Status',
      whoisPostalCode: 'Postleitzahl',
      whoisCountry: 'Land',
      whoisPhone: 'Telefon',
      whoisFax: 'Fax',
      whoisEmail: 'E-Mail',
      domain: 'Domain',
      ipAddress: 'IP-Adresse',
      errorMsg: 'Daten konnten nicht von Live Connect abgerufen werden: {{error}}',
      riskAssessment: 'Live Connect-Risikobewertung',
      riskReason: 'Gründe für Risiko',
      highRiskDesc: 'Indikator als hohes Risiko erachtet und erfordert gezielte Aufmerksamkeit',
      safeRiskDesc: 'Untersuchungen und Analysen zeigen Indikatoren als vertrauenswürdige Ressourcen an',
      unsafeRiskDesc: 'Untersuchungen und Analysen zeigen Ressource als nicht vertrauenswürdig an',
      unknownRiskDesc: 'Keine eindeutigen Ergebnisse aus verfügbaren Informationen, Untersuchungen und Analysen',
      suspiciousRiskDesc: 'Untersuchungen und Analysen zeigen potenziell bedrohliche Aktivität an',
      riskFeedback: 'Feedback zur Risikobewertung',
      relatedFiles: 'Verwandte Regeln ',
      risk: 'LC-RISIKORATING',
      importHashFunction: 'API-FUNKTION HASH IMPORTIEREN',
      compiledTime: 'KOMPILIERDATUM',
      relatedDomains: 'Verwandte Domains ',
      relatedIps: 'Verwandte IPs ',
      country: 'Land',
      registeredDate: 'Registrierungsdatum',
      expiredDate: 'Ablaufdatum',
      email: 'Registrierte E-Mail-Adresse',
      asnShort: 'ASN',
      confidenceLevel: 'Konfidenzniveau',
      select: 'Auswählen ...',
      feedbackSubmitted: 'Feedback wurde an Live Connect-Server übermittelt.',
      feedbackSubmissionFailed: 'Feedback konnte nicht an Live Connect-Server übermittel werden.',
      feedbackFormInvalid: 'Wählen Sie „Risikobestätigung“ und „Konfidenzniveau“ aus.',
      noTrendingCommunityActivity: 'Keine neue Community-Aktivität in den letzten 30 Tagen',
      noTrendingSubmissionActivity: 'Keine neuen Übermittlungen in den letzten 30 Tagen',
      skillLevel: 'Kompetenzebene des Analysten',
      skillLevelPrefix: 'Tier {{level}}',
      noRelatedData: 'Keine verwandten {{entity}} für diese Entität vorhanden.',
      ips: 'IP',
      files: 'Dateien',
      domains: 'Domains'
    },
    error: {
      error: 'Beim Versuch, die Daten abzurufen, ist ein unerwarteter Fehler aufgetreten.',
      noDataSource: 'Keine Datenquelle konfiguriert/aktiviert.',
      dataSourcesFailed: 'Daten können nicht aus den konfigurierten Datenquellen abgerufen werden.',
      dataSource: 'Beim Versuch, die Daten abzurufen, ist ein unerwarteter Fehler aufgetreten.',
      noData: 'Keine Kontextdaten verfügbar für diese Datenquelle.',
      listDuplicateName: 'Listenname ist bereits vorhanden.',
      listValidName: 'Geben Sie einen gültigen Listennamen ein (max. Länge ist 255 Zeichen).',
      'mongo.error': 'Ein unerwarteter Datenbankfehler ist aufgetreten.',
      'total.entries.exceed.max': 'Die Listengröße überschreitet den Grenzwert von 100.000.',
      'admin.error': 'Der Administratorservice ist nicht erreichbar. Überprüfen Sie die Serviceverbindung.',
      'datasource.disk.usage.high': 'Geringer Festplattenspeicher. Löschen Sie nicht mehr benötigte Daten, um Speicherplatz freizugeben.',
      'context.service.timeout': 'Der Context Hub-Service ist nicht erreichbar. Überprüfen Sie die Serviceverbindung.',
      'get.mongo.connect.failed': 'Die Datenbank ist nicht erreichbar. Versuchen Sie es später erneut.',
      'datasource.query.not.supported': 'Kontextdatensuche wird für diese Metadaten nicht unterstützt.',
      'transport.http.read.failed': 'Kontextdaten niciht verfügbar, da die Datenquelle nicht erreichbar ist.',
      'transport.ad.read.failed': 'Kontextdaten niciht verfügbar, da die Datenquelle nicht erreichbar ist.',
      'transport.init.failed': 'Zeitüberschreitung bei der Verbindung zur Datenquelle.',
      'transport.not.found': 'Kontextdaten niciht verfügbar, da die Datenquelle nicht erreichbar ist.',
      'transport.create.failed': 'Kontextdaten niciht verfügbar, da die Datenquelle nicht erreichbar ist.',
      'transport.refresh.failed': 'Kontextdaten niciht verfügbar, da die Datenquelle nicht erreichbar ist.',
      'transport.connect.failed': 'Kontextdaten niciht verfügbar, da die Datenquelle nicht erreichbar ist.',
      'live.connect.private.ip.unsupported': 'Live Connect unterstützt ausschließlich öffentliche IP-Adressen.',
      'transport.http.error': 'Kontextsuche für diese Datenquelle fehlgeschlagen, da ein Fehler zurückgegeben wurde.',
      'transport.validation.error': 'Das Datenformat wird für die Datenquelle nicht unterstützt.',
      'transport.http.auth.failed': 'Kontext aus dieser Datenquelle konnte nicht abgerufen werden, Autorisierung fehlgeschlagen.'
    },
    footer: {
      viewAll: 'Alle anzeigen',
      title: {
        incidents: 'Incident(s)',
        alerts: 'Warnmeldung(en)',
        lIST: 'Liste(n)',
        users: 'Benutzer',
        endpoint: 'Host',
        archer: 'Ressource'
      },
      resultCount: '(erste {{count}} Ergebnisse)'
    },
    tooltip: {
      contextHighlights: 'Kontexthighlights',
      viewOverview: 'Kontext anzeigen',
      actions: 'Aktionen',
      investigate: 'Zu Ermittlungen wechseln',
      addToList: 'Zu Liste hinzufügen/Aus Liste entfernen',
      virusTotal: 'VirusTotal-Abfrage',
      googleLookup: 'Google-Abfrage',
      ecat: 'Zu Endpunkt-Thick-Client wechseln',
      events: 'Zu Ereignissen wechseln',
      contextUnavailable: 'Derzeitig keine Kontextdaten verfügbar.',
      dataSourceNames: {
        Incidents: 'Incidents',
        Alerts: 'Warnmeldungen',
        LIST: 'Listen',
        Users: 'Benutzer',
        IOC: 'IOCs',
        Machines: 'Endpunkt',
        Modules: 'Module',
        'LiveConnect-Ip': 'LiveConnect',
        'LiveConnect-File': 'LiveConnect',
        'LiveConnect-Domain': 'LiveConnect'
      }
    }
  },
  preferences: {
    'investigate-events': {
      panelTitle: 'Ereigniseinstellungen',
      triggerTip: 'Ereigniseinstellungen ein-/ausblenden',
      defaultEventView: 'Standardmäßige Ansicht „Ereignisanalyse“',
      defaultLogFormat: 'Standardmäßiges Protokollformat',
      defaultPacketFormat: 'Standardmäßiges Paketformat',
      LOG: 'Download-Protokoll',
      CSV: 'CSV herunterladen',
      XML: 'XML herunterladen',
      JSON: 'JSON herunterladen',
      PCAP: 'PCAP herunterladen',
      PAYLOAD: 'Alle Nutzdaten herunterladen',
      PAYLOAD1: 'Anforderungsnutzdaten herunterladen',
      PAYLOAD2: 'Antwortnutzdaten herunterladen',
      FILE: 'Dateianalyse',
      TEXT: 'Textanalyse',
      PACKET: 'Paketanalyse',
      queryTimeFormat: 'Zeitformat für Abfrage',
      DB: 'Datenbankzeit',
      WALL: 'Uhrzeit',
      'DB-tooltip': 'Datenbankzeit, zu der Ereignisse gespeichert werden',
      'WALL-tooltip': 'Aktuelle Uhrzeit mit in den Benutzereinstellungen festgelegter Zeitzone',
      autoDownloadExtractedFiles: 'Extrahierte Dateien automatisch herunterladen'
    },
    'endpoint-preferences': {
      visibleColumns: 'Sichtbare Spalten',
      sortField: 'Sortierfeld',
      sortOrder: 'Sortierreihenfolge',
      filter: 'Filter'
    }
  },
  packager: {
    errorMessages: {
      invalidServer: 'Geben Sie einen gültigen Hostnamen oder eine gültige IP-Adresse ein.',
      invalidPort: 'Geben Sie eine gültige Portnummer ein.',
      invalidName: 'Geben Sie einen gültigen Namen ohne Sonderzeichen ein.',
      passwordEmptyMessage: 'Geben Sie das Zertifikatpasswort ein.',
      invalidPasswordString: 'Darf alphanumerische oder Sonderzeichen enthalten und muss mindestens drei (3) Zeichen lang sein.',
      NAME_EMPTY: 'Warnung: Kein Name für die Konfiguration angegeben.',
      SERVERS_EMPTY: 'Warnung: Keine Server gefunden.',
      EVENT_ID_INVALID: 'Warnung: Ereignis-ID ist ungültig.',
      CHANNEL_EMPTY: 'Warnung: Kanal ist leer.',
      FILTER_EMPTY: 'Warnung: Filter ist leer.',
      FILTER_INVALID: 'Warnung: Filter ist ungültig.',
      INVALID_HOST: 'Warnung: Host ist ungültig.',
      CONFIG_NAME_INVALID: 'Warnung: Name der Konfiguration ist ungültig.',
      INVALID_PROTOCOL: 'Warnung: Protokoll ist ungültig.',
      CHANNEL_NAME_INVALID: 'Warnung: Kanalname ist ungültig.',
      EMPTY_CHANNELS: 'Warnung: Kanalname ist leer.',
      CHANNEL_FILTER_INVALID: 'Warnung: Kanalfilter ist ungültig.'
    },
    packagerTitle: 'Packager',
    serviceName: 'Servicename*',
    server: 'Endpunktserver*',
    port: 'HTTPS-Port*',
    certificateValidation: 'Servervalidierung',
    certificatePassword: 'Zertifikatpasswort*',
    none: 'Keine',
    fullChain: 'Vollständige Kette',
    thumbprint: 'Fingerabdruck des Zertifikats',
    reset: 'Zurücksetzen',
    generateAgent: 'Agent erzeugen',
    generateLogConfig: 'Nur Protokollkonfiguration erzeugen',
    loadExistingLogConfig: 'Vorhandene Konfiguration wird geladen ...',
    description: 'Beschreibung',
    title: 'Packager',
    becon: 'Becon',
    displayName: 'Anzeigename*',
    upload: {
      success: 'Die Konfigurationsdatei wurde erfolgreich geladen.',
      failure: 'Die Konfigurationsdatei kann nicht hochgeladen werden.'
    },
    error: {
      generic: 'Beim Versuch, diese Daten abzurufen, ist ein unerwarteter Fehler aufgetreten.'
    },
    autoUninstall: 'Automatische Deinstallation',
    forceOverwrite: 'Überschreiben erzwingen',
    windowsLogCollectionCongfig: 'Windows-Protokollsammlungskonfiguration',
    enableWindowsLogCollection: 'Windows-Protokollsammlung aktivieren',
    configurationName: 'Konfigurationsname*',
    primaryLogDecoder: 'Primärer Log Decoder/Log Collector*',
    secondaryLogDecoder: 'Sekundärer Log Decoder/Log Collector*',
    protocol: 'Protokoll',
    channels: 'Kanalfilter',
    eventId: 'Ein-/Auszuschließende Ereignis-ID (?)',
    heartbeatLogs: 'Heartbeat-Protokolle senden',
    heartbeatFrequency: 'Heartbeat-Frequenz',
    testLog: 'Testprotokoll senden',
    placeholder: 'Auswahl treffen',
    searchPlaceholder: 'Filteroption eingeben',
    emptyName: 'Kein Name für die Konfiguration angegeben',
    channelFilter: 'Kanalfilter',
    specialCharacter: 'Der Name der Konfiguration enthält Sonderzeichen.',
    channel: {
      add: 'Neuen Kanal hinzufügen',
      name: 'KANALNAME*',
      filter: 'FILTER*',
      event: 'EREIGNIS-ID*',
      empty: ''
    }
  },
  investigateFiles: {
    title: 'Dateien',
    deleteTitle: 'Löschen bestätigen',
    button: {
      exportToCSV: 'In CSV-Datei exportieren',
      downloading: 'Wird heruntergeladen',
      save: 'Speichern',
      reset: 'Zurücksetzen',
      cancel: 'Abbrechen'
    },
    message: {
      noResultsMessage: 'Keine übereinstimmenden Dateien gefunden'
    },
    errorPage: {
      serviceDown: 'Endpunktserver ist offline',
      serviceDownDescription: 'Der Endpunktserver wird nicht ausgeführt oder es kann nicht darauf zugegriffen werden. Wenden Sie sich an den Administrator, um dieses Problem zu beheben.'
    },
    footer: '{{count}} von {{total}} {{label}}',
    filter: {
      filter: 'Filter',
      filters: 'Gespeicherte Filter',
      newFilter: 'Neuer Filter',
      windows: 'WINDOWS',
      mac: 'MAC',
      linux: 'LINUX',
      favouriteFilters: 'Beliebte Filter',
      addMore: 'Filter hinzufügen',
      invalidFilterInput: 'Ungültige Filtereingabe',
      invalidFilterInputLength: 'Filtereingabe ist länger als 256 Zeichen',
      invalidCharacters: 'Darf alphanumerische oder Sonderzeichen enthalten.',
      invalidCharsAlphabetOnly: 'Zahlen und Sonderzeichen sind nicht zulässig',
      invalidCharsAlphaNumericOnly: 'Sonderzeichen sind nicht zulässig',
      restrictionType: {
        moreThan: 'Größer als',
        lessThan: 'Kleiner als',
        between: 'Zwischen',
        equals: 'Ist gleich',
        contains: 'Enthält'
      },
      customFilters: {
        save: {
          description: 'Geben Sie einen Namen für die zu speichernde Suche an. Dieser Name wird in der Suchfeldliste angezeigt.',
          name: 'Name*',
          errorHeader: 'Suche kann nicht gespeichert werden',
          header: 'Suche speichern',
          errorMessage: 'Die Suche kann nicht gespeichert werden. ',
          emptyMessage: 'Das Feld für den Namen ist leer.',
          nameExistsMessage: 'Eine gespeicherte Suche mit demselben Namen ist bereits vorhanden.',
          success: 'Die Suchabfrage wurde erfolgreich gespeichert.',
          filterFieldEmptyMessage: 'Eines oder mehrere der neu hinzugefügten Filterfelder sind leer. Fügen Sie zum Speichern die Filter hinzu oder entfernen Sie die Felder.',
          invalidInput: 'Geben Sie einen gültigen Namen ein. (Nur \'-\' und \'_\' Sonderzeichen sind zulässig.)'
        },
        delete: {
          successMessage: 'Abfrage erfolgreich gelöscht.',
          confirmMessage: 'Möchten Sie die ausgewählte Abfrage wirklich löschen?'
        }
      }
    },
    fields: {
      panelTitle: 'Dateieinstellungen',
      triggerTip: 'Dateieinstellungen ein-/ausblenden',
      id: 'ID',
      companyName: 'Name des Unternehmens',
      checksumMd5: 'MD5',
      checksumSha1: 'SHA1',
      checksumSha256: 'SHA256',
      machineOsType: 'Betriebssystem',
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
        features: 'Signatur',
        signer: 'Signaturgeber'
      },
      owner: {
        userName: 'Eigentümer',
        groupName: 'Eigentümergruppe'
      },
      rpm: {
        packageName: 'Paket'
      },
      path: 'Pfad',
      entropy: 'Entropie',
      fileName: 'FileName',
      firstFileName: 'FileName',
      firstSeenTime: 'Zeit des ersten Auftretens',
      timeCreated: 'Erstellt',
      format: 'Format',
      sectionNames: 'Abschnittsnamen',
      importedLibraries: 'Importierte Bibliotheken',
      size: 'Größe'
    },
    sort: {
      fileNameDescending: 'Dateiname (absteigend)',
      fileNameAscending: 'Dateiname (aufsteigend)',
      sizeAscending: 'Größe (aufsteigend)',
      sizeDescending: 'Größe (absteigend)',
      formatAscending: 'Format (aufsteigend)',
      formatDescending: 'Format (absteigend)',
      signatureAscending: 'Signatur (aufsteigend)',
      signatureDescending: 'Signatur (absteigend)'
    }
  },
  investigateHosts: {
    title: 'Untersuchen',
    loading: 'Wird geladen ...',
    loadMore: 'Weitere laden',
    deleteTitle: 'Löschen bestätigen',
    noSnapshotMessage: 'Kein Scanverlauf gefunden.',
    common: {
      save: 'Speichern',
      enable: 'Aktivieren',
      saveSuccess: 'Erfolgreich gespeichert',
      emptyMessage: 'Keine entsprechenden Ergebnisse'
    },
    errorPage: {
      serviceDown: 'Endpunktserver ist offline',
      serviceDownDescription: 'Der Endpunktserver wird nicht ausgeführt oder es kann nicht darauf zugegriffen werden. Wenden Sie sich an den Administrator, um dieses Problem zu beheben.'
    },
    property: {
      file: {
        companyName: 'Name des Unternehmens',
        checksumMd5: 'MD5',
        checksumSha1: 'SHA1',
        checksumSha256: 'SHA256',
        machineOsType: 'Betriebssystem',
        timeCreated: 'Erstellt',
        timeModified: 'Geändert',
        timeAccessed: 'Mit Zugriff',
        createTime: 'Prozess erstellt',
        pid: 'PID',
        eprocess: 'EPROCESS',
        path: 'Vollständiger Pfad',
        sameDirectoryFileCounts: {
          nonExe: '# Nicht ausführbare Dateien',
          exe: '# Ausführbare Dateien',
          subFolder: '# Ordner',
          exeSameCompany: '# Ausführbare Dateien selbes Unternehmen'
        },
        elf: {
          classType: 'Klassentyp',
          data: 'Daten',
          entryPoint: 'Einstiegspunkt',
          features: 'Merkmale',
          type: 'Typ',
          sectionNames: 'Abschnittsnamen',
          importedLibraries: 'Importierte Bibliotheken'
        },
        pe: {
          timeStamp: 'Zeitstempel',
          imageSize: 'Bildgröße',
          numberOfExportedFunctions: 'Exportierte Funktionen',
          numberOfNamesExported: 'Exportierte Namen',
          numberOfExecuteWriteSections: 'Schreibbereiche ausführen',
          features: 'Merkmale',
          sectionNames: 'Abschnittsnamen',
          importedLibraries: 'Importierte Bibliotheken',
          resources: {
            originalFileName: 'Dateiname',
            company: 'Unternehmen',
            description: 'Beschreibung',
            version: 'Version'
          }
        },
        macho: {
          uuid: 'UUID',
          identifier: 'Kennung',
          minOsxVersion: 'OS X-Version',
          features: 'Merkmale',
          flags: 'Flags',
          numberOfLoadCommands: 'Geladene Befehle',
          version: 'Version',
          sectionNames: 'Abschnittsnamen',
          importedLibraries: 'Importierte Bibliotheken'
        },
        signature: {
          timeStamp: 'Zeitstempel',
          thumbprint: 'Fingerabdruck',
          features: 'Merkmale',
          signer: 'Signaturgeber'
        },
        process: {
          title: 'Vorgang',
          processName: 'Prozessname',
          eprocess: 'EPROCESS',
          integrityLevel: 'Integrität',
          parentPath: 'Übergeordneter Pfad',
          threadCount: 'Anzahl Threads',
          owner: 'Eigentümer',
          sessionId: 'Sitzungs-ID',
          createUtcTime: 'Erstellt',
          imageBase: 'Bilddatenbank',
          imageSize: 'Bildgröße'
        },
        entropy: 'Entropie',
        firstFileName: 'FileName',
        fileName: 'FileName',
        format: 'Format',
        sectionNames: 'Abschnittsnamen',
        importedLibraries: 'Importierte Bibliotheken',
        size: 'Größe',
        imageBase: 'Bilddatenbank',
        imageSize: 'Bildgröße',
        loaded: 'Geladen',
        fileProperties: {
          entropy: 'Entropie',
          size: 'Größe',
          format: 'Format'
        }
      }
    },
    process: {
      title: 'Prozesse',
      processName: 'Prozessname',
      properties: 'Prozesseigenschaften',
      pid: 'PID',
      parentId: 'PPID',
      owner: 'Eigentümer',
      hostCount: 'Hostanzahl',
      creationTime: 'Erstellungszeit',
      hashlookup: 'Hash-Suche',
      signature: 'Signatur',
      path: 'Pfad',
      launchArguments: 'Startargumente',
      message: {
        noResultsMessage: 'Keine Prozessinformationen gefunden'
      },
      dll: {
        dllName: 'DLL-Name',
        filePath: 'Dateipfad',
        title: 'Geladene Bibliotheken',
        message: {
          noResultsMessage: 'Keine Informationen zu geladenen Bibliotheken gefunden'
        },
        note: {
          windows: 'Hinweis: Zeigt Bibliotheken an, die nicht von Microsoft signiert sind',
          mac: 'Hinweis: Zeigt Bibliotheken an, die nicht von Apple signiert sind'
        }
      }
    },
    tabs: {
      overview: 'Überblick',
      process: 'Vorgang',
      autoruns: 'Automatische Ausführungen',
      files: 'Dateien',
      drivers: 'Treiber',
      systemInformation: 'Systeminformationen',
      services: 'Services',
      tasks: 'Aufgaben',
      hostFileEntries: 'Hostdateieinträge',
      mountedPaths: 'Gemountete Pfade',
      networkShares: 'Netzwerkfreigaben',
      bashHistories: 'Bash-Verlauf',
      libraries: 'Bibliotheken',
      explore: 'Durchsuchen',
      securityProducts: 'Sicherheitsprodukte',
      windowsPatches: 'Patches für Windows'
    },
    systemInformation: {
      ipAddress: 'IP-Adresse',
      dnsName: 'DNS-Name',
      fileSystem: 'Dateisystem',
      path: 'Pfad',
      remotePath: 'Remotepfad',
      options: 'Optionen',
      name: 'Name',
      description: 'Beschreibung',
      permissions: 'Berechtigungen',
      type: 'Typ',
      maxUses: 'Max. Benutzer',
      currentUses: 'Aktuelle Benutzer',
      userName: 'Benutzername',
      command: 'Befehl',
      commandNote: 'Hinweis: Aktuelle Befehle stehen oben',
      filterUser: 'Tippen, um Benutzer zu filtern',
      filterBy: 'Nach Benutzer filtern',
      patches: 'Patches',
      securityProducts: {
        type: 'Typ',
        instance: 'Instanz',
        displayName: 'Anzeigename',
        companyName: 'Name des Unternehmens',
        version: 'Version',
        features: 'Merkmale'
      }
    },
    hosts: {
      title: 'Hosts',
      search: 'Filter',
      button: {
        addMore: 'Filter hinzufügen',
        loadMore: 'Weitere laden',
        exportCSV: 'In CSV-Datei exportieren',
        export: 'In JSON-Datei exportieren',
        exportTooltip: 'Exportiert alle Scandatenkategorien für den Host.',
        downloading: 'Wird heruntergeladen',
        initiateScan: 'Scan starten',
        cancelScan: 'Scan stoppen',
        delete: 'Löschen',
        cancel: 'Abbrechen',
        save: 'Speichern',
        saveAs: 'Speichern unter ...',
        clear: 'Löschen',
        search: 'Suchen',
        ok: 'OK',
        moreActions: 'Mehr Aktionen',
        explore: 'Durchsuchen',
        gearIcon: 'Klicken Sie hier, um Spalten zu verwalten.',
        overview: 'Übersichtsbereich ein-/ausblenden',
        settings: 'Einstellungen',
        meta: 'Metadaten ein-/ausblenden',
        close: 'Hostdetails schließen',
        shrink: 'Weniger anzeigen',
        update: 'Aktualisieren',
        reset: 'Zurücksetzen'
      },
      autoruns: {
        services: {
          initd: 'INIT.D',
          systemd: 'SYSTEM.D'
        }
      },
      ranas: {
        ranas: 'Ausgeführt als',
        categories: {
          Process: 'Vorgang',
          Libraries: 'Bibliothek',
          Autorun: 'Automatische Ausführung',
          Service: 'Service',
          Task: 'Aufgabe',
          Driver: 'Treiber',
          Thread: 'Thread'
        }
      },
      explore: {
        input: {
          placeholder: 'Nach Dateiname, Pfad oder Hash suchen'
        },
        noResultsFound: 'Keine Ergebnisse gefunden.',
        fileName: 'Dateiname ',
        path: 'Pfad: ',
        hash: 'Hash: ',
        search: {
          minimumtext: {
            required: 'Geben Sie für Dateinamen oder Pfade mindestens drei (3) Zeichen ein. Geben Sie für Hashes die vollständige SHA-256-Hash-Zeichenfolge ein.'
          }
        }
      },
      footerLabel: {
        autoruns: {
          autoruns: 'Automatische Ausführungen',
          services: 'Services',
          tasks: 'Aufgaben'
        },
        files: 'Dateien',
        drivers: 'Treiber',
        libraries: 'Bibliotheken'
      },
      summary: {
        snapshotTime: 'Snapshot-Zeit',
        overview: {
          typeToFilterOptions: 'Filteroption eingeben',
          noSnapShots: 'Keine Snapshots verfügbar'
        },
        body: {
          ipAddresses: 'IP-Adressen ({{count}})',
          securityConfig: 'Sicherheitskonfiguration',
          loggedUsers: 'Angemeldete Benutzer ({{count}})',
          user: {
            administrator: 'Administrator',
            sessionId: 'Sitzungs-ID',
            sessionType: 'Sitzungstyp',
            groups: 'Gruppen',
            host: 'Host',
            deviceName: 'Gerätename'
          }
        },
        securityConfig: {
          arrangeBy: 'ANORDNEN NACH',
          alphabetical: 'Alphabetisch',
          status: 'Status'
        }
      },
      selected: 'ausgewählt ({{count}})',
      list: {
        noResultsMessage: 'Keine Ergebnisse gefunden.',
        errorOffline: 'Ein Fehler ist aufgetreten. Der Endpunktserver ist eventuell offline oder es kann nicht darauf zugegriffen werden.'
      },
      filters: {
        systemFilter: 'Diese Suche ist vom System vorgegeben und kann nicht bearbeitet werden.',
        since: 'seit',
        customDateRange: 'Benutzerdefinierter Datumsbereich',
        customStartDate: 'Startdatum',
        customEndDate: 'Enddatum',
        customDate: 'Kundenspezifisches Datum',
        operator: 'Operator',
        searchPlaceHolder: 'Filteroption eingeben',
        mutlipleValuesNote: 'Hinweis: Um nach mehreren Werten zu suchen, verwenden Sie Doppelstriche (||) als Trennzeichen.',
        invalidFilterInput: 'Ungültige Filtereingabe',
        invalidFilterInputLength: 'Filtereingabe ist länger als 256 Zeichen',
        invalidIP: 'Geben Sie eine gültige IP-Adresse ein.',
        invalidAgentID: 'Geben Sie eine gültige Agent-ID ein',
        invalidAgentVersion: 'Geben Sie eine gültige Agent-Version ein.',
        invalidMacAddress: 'Geben Sie eine gültige MAC-Adresse ein.',
        invalidOsDescription: 'Buchstaben, Zahlen und .,-,() sind zulässig',
        invalidCharacters: 'Darf alphanumerische oder Sonderzeichen enthalten.',
        invalidCharsAlphabetOnly: 'Zahlen und Sonderzeichen sind nicht zulässig',
        invalidCharsAlphaNumericOnly: 'Sonderzeichen sind nicht zulässig',
        inTimeRange: 'In',
        notInTimeRange: 'Nicht in',
        agentStatus: {
          lastSeenTime: 'Agent nicht gesehen seit'
        }
      },
      restrictionTypeOptions: {
        EQUALS: 'Ist gleich',
        CONTAINS: 'Enthält',
        GT: '>',
        LT: '<',
        GTE: '>=',
        LTE: '<=',
        NOT_EQ: '!=',
        LESS_THAN: 'Kleiner als',
        GREATER_THAN: 'Größer als',
        BETWEEN: 'Zwischen',
        LAST_5_MINUTES: 'Letzte 5 Minuten',
        LAST_10_MINUTES: 'Letzte 10 Minuten',
        LAST_15_MINUTES: 'Letzte 15 Minuten',
        LAST_30_MINUTES: 'Letzte 30 Minuten',
        LAST_HOUR: 'Letzte Stunde',
        LAST_3_HOURS: 'Letzte 3 Stunden',
        LAST_6_HOURS: 'Letzte 6 Stunden',
        LAST_TWELVE_HOURS: 'Letzte 12 Stunden',
        LAST_TWENTY_FOUR_HOURS: 'Letzte 24 Stunden',
        LAST_FORTY_EIGHT_HOURS: 'Die letzten 2 Tage',
        LAST_5_DAYS: 'Die letzten 5 Tage',
        LAST_7_DAYS: 'Die letzten 7 Tage',
        LAST_14_DAYS: 'Die letzten 14 Tage',
        LAST_30_DAYS: 'Die letzten 30 Tage',
        LAST_HOUR_AGO: 'Vor 1 Stunde',
        LAST_TWENTY_FOUR_HOURS_AGO: 'Vor 24 Stunden',
        LAST_5_DAYS_AGO: 'Vor 5 Tagen',
        ALL_TIME: 'Alle Daten'
      },
      footer: '{{count}} von {{total}} Hosts',
      column: {
        panelTitle: 'Hosteinstellungen',
        triggerTip: 'Hosteinstellungen ein-/ausblenden',
        id: 'Agent-ID',
        analysisData: {
          iocs: 'IOC-Warnmeldungen',
          machineRiskScore: 'Risikowert'
        },
        agentStatus: {
          scanStatus: 'Agent-Scanstatus',
          lastSeenTime: 'Agent zuletzt gesehen'
        },
        machine: {
          machineOsType: 'Betriebssystem',
          machineName: 'Hostname',
          id: 'Agent-ID',
          agentVersion: 'Agent-Version',
          scanStartTime: 'Zeit des letzten Scans',
          scanRequestTime: 'Scananforderungszeit',
          scanType: 'Scantyp',
          scanTrigger: 'Scanauslöser',
          securityConfigurations: 'Sicherheitskonfigurationen',
          hostFileEntries: {
            ip: 'Hostdatei-IP',
            hosts: 'Hosteinträge'
          },
          users: {
            name: 'Benutzername',
            sessionId: 'Sitzungs-ID des Benutzers',
            sessionType: 'Sitzungstyp des Benutzers',
            isAdministrator: 'Benutzer ist Administrator',
            groups: 'Benutzergruppen',
            domainUserQualifiedName: 'Vollständiger Name des Benutzers',
            domainUserId: 'Benutzer-ID der Benutzerdomain',
            domainUserOu: 'Benutzer-OU der Benutzerdomain',
            domainUserCanonicalOu: 'Kanonische Benutzer-OU der Benutzerdomain',
            host: 'Benutzerhost',
            deviceName: 'Name des Benutzergeräts'
          },
          errors: {
            time: 'Fehler – Zeit',
            fileID: 'Fehler – Datei-ID',
            line: 'Fehler – Zeile',
            number: 'Fehler – Zahl',
            value: 'Fehler – Wert',
            param1: 'Fehler – Param1',
            param2: 'Fehler – Param2',
            param3: 'Fehler – Param3',
            info: 'Fehler – Info',
            level: 'Fehler – Level',
            type: 'Fehler – Typ'
          },
          networkShares: {
            path: 'NetworkShare – Pfad',
            name: 'NetworkShare – Name',
            description: 'NetworkShare – Beschreibung',
            type: 'NetworkShare – Typ',
            permissions: 'NetworkShare – Berechtigungen',
            maxUses: 'NetworkShare – Max. Verwendungen',
            currentUses: 'NetworkShare – Aktuelle Verwendungen'
          },
          mountedPaths: {
            path: 'MountedPaths – Pfad',
            fileSystem: 'MountedPaths – Dateisystem',
            options: 'MountedPaths – Optionen',
            remotePath: 'MountedPaths – Remotepfad'
          },
          securityProducts: {
            type: 'SecurityProducts – Typ',
            instance: 'SecurityProducts – Instance',
            displayName: 'SecurityProducts – Anzeigename',
            companyName: 'SecurityProducts – Name des Unternehmens',
            version: 'SecurityProducts – Version',
            features: 'SecurityProducts – Funktionen'
          },
          networkInterfaces: {
            name: 'NIC-Name',
            macAddress: 'MAC-Adresse der NIC',
            networkId: 'NetworkInterface – Netzwerk-ID',
            ipv4: 'IPv4',
            ipv6: 'IPv6',
            gateway: 'NetworkInterface – Gateway',
            dns: 'NetworkInterface – DNS',
            promiscuous: 'Promiskuitive NIC'
          }
        },
        riskScore: {
          moduleScore: 'Modulbewertung',
          highestScoringModules: 'Am höchsten bewertetes Modul'
        },
        machineIdentity: {
          machineName: 'Hostname',
          group: 'Agent-Gruppe',
          agentMode: 'Agent-Modus',
          agent: {
            exeCompileTime: 'Agent – Kompilierzeit Benutzermodus',
            sysCompileTime: 'Agent – Kompilierzeit Treiber',
            packageTime: 'Agent – Paketzeit',
            installTime: 'Agent – Installationsdauer',
            serviceStartTime: 'Agent – Startzeit des Service',
            serviceProcessId: 'Agent – Prozess-ID des Service',
            serviceStatus: 'Agent – Servicestatus',
            driverStatus: 'Agent – Treiberstatus',
            blockingEnabled: 'Agent – Sperren aktiviert',
            blockingUpdateTime: 'Agent – Aktualisierungszeit der Sperre'
          },
          operatingSystem: {
            description: 'OS – Beschreibung',
            buildNumber: 'OS – Build-Nummer',
            servicePack: 'OS – Service Pack',
            directory: 'OS – Verzeichnis',
            kernelId: 'OS – Kernel-ID',
            kernelName: 'OS – Kernelname',
            kernelRelease: 'OS – Kernelversion',
            kernelVersion: 'OS – Kernelversion',
            distribution: 'OS – Distribution',
            domainComputerId: 'OS – Domaincomputer-ID',
            domainComputerOu: 'OS – Domaincomputer-OU',
            domainComputerCanonicalOu: 'OS – Kanonische Domaincomputer-OU',
            domainOrWorkgroup: 'OS – Domain oder Arbeitsgruppe',
            domainRole: 'OS – Domainrolle',
            lastBootTime: 'OS – Letzter Startvorgang'
          },
          hardware: {
            processorArchitecture: 'Hardware – Prozessorarchitektur',
            processorArchitectureBits: 'Hardware – Prozessorarchitekturbits',
            processorCount: 'Hardware – Prozessoranzahl',
            processorName: 'Hardware – Prozessorname',
            totalPhysicalMemory: 'Hardware – Physikalischer Speicher insgesamt',
            chassisType: 'Hardware – Gehäusetyp',
            manufacturer: 'Hardware – Hersteller',
            model: 'Hardware – Modell',
            serial: 'Hardware – Seriennummer',
            bios: 'Hardware – BIOS'
          },
          locale: {
            defaultLanguage: 'Gebietsschema – Standardsprache',
            isoCountryCode: 'Gebietsschema – Ländercode',
            timeZone: 'Gebietsschema – Zeitzone'
          },
          knownFolder: {
            appData: 'Ordner – Anwendungsdaten',
            commonAdminTools: 'Ordner – Häufig verwendete Administratorwerkzeuge',
            commonAppData: 'Ordner – Häufig verwendete Anwendungsdaten',
            commonDestop: 'Ordner – Häufig verwendeter Desktop',
            commonDocuments: 'Ordner – Häufig verwendete Dokumente',
            commonProgramFiles: 'Ordner – Häufig verwendete Programmdateien',
            commonProgramFilesX86: 'Ordner – Häufig verwendete Programmdateien (x86)',
            commonPrograms: 'Ordner – Häufig verwendete Programme',
            commonStartMenu: 'Ordner – Häufig verwendetes Startmenü',
            commonStartup: 'Ordner – Häufig verwendeter Systemstart',
            desktop: 'Ordner – Desktop',
            localAppData: 'Ordner – Lokale Anwendungsdaten',
            myDocuments: 'Ordner – Eigene Dokumente',
            programFiles: 'Ordner – Programmdateien',
            programFilesX86: 'Ordner – Programmdateien (x86)',
            programs: 'Ordner – Programme',
            startMenu: 'Ordner – Startmenü',
            startup: 'Ordner – Systemstart',
            system: 'Ordner – System',
            systemX86: 'Ordner – System (x86)',
            windows: 'Ordner – Windows'
          }
        },
        markedForDeletion: 'Für Löschung markiert'
      },

      properties: {
        title: 'Hosteigenschaften',
        filter: 'Tippen, um die Liste zu filtern',
        checkbox: 'Eigenschaften nur mit Werten anzeigen',
        machine: {
          securityConfigurations: 'Sicherheitskonfigurationen',
          hostFileEntries: {
            title: 'Hostdateieinträge',
            ip: 'Hostdatei-IP',
            hosts: 'Hosteinträge'
          },
          users: {
            title: 'Benutzer',
            name: 'Name',
            sessionId: 'Sitzungs-ID',
            sessionType: 'Sitzungstyp',
            isAdministrator: 'Ist Administrator',
            administrator: 'Ist Administrator',
            groups: 'Gruppen',
            domainUserQualifiedName: 'Vollständiger Name',
            domainUserId: 'Domainbenutzer-ID',
            domainUserOu: 'Domainbenutzer-OU',
            domainUserCanonicalOu: 'Kanonische Domainbenutzer-ID',
            host: 'Host',
            deviceName: 'Gerätename'
          },
          networkInterfaces: {
            title: 'Netzwerkschnittstellen',
            name: 'Name',
            macAddress: 'MAC-Adresse',
            networkId: 'Netzwerk-ID',
            ipv4: 'IPv4',
            ipv6: 'IPv6',
            gateway: 'Gateway',
            dns: 'DNS',
            promiscuous: 'Promiskuitiv'
          }
        },
        machineIdentity: {
          agent: {
            agentId: 'Agent-ID',
            agentMode: 'Agent-Modus',
            agentVersion: 'Agent-Version',
            title: 'Agent',
            exeCompileTime: 'Kompilierzeit Benutzermodus',
            sysCompileTime: 'Kompilierzeit Treiber',
            packageTime: 'Paketzeit',
            installTime: 'Installationsdauer',
            serviceStartTime: 'Startzeit des Service',
            serviceProcessId: 'Prozess-ID des Service',
            serviceStatus: 'Servicestatus',
            driverStatus: 'Treiberstatus',
            blockingEnabled: 'Sperren aktiviert',
            blockingUpdateTime: 'Aktualisierungszeit der Sperre'
          },
          operatingSystem: {
            title: 'Betriebssystem',
            description: 'Beschreibung',
            buildNumber: 'Build-Nummer',
            servicePack: 'Service Pack',
            directory: 'Verzeichnis',
            kernelId: 'Kernel-ID',
            kernelName: 'Kernelname',
            kernelRelease: 'Kernelversion',
            kernelVersion: 'Kernelversion',
            distribution: 'Distribution',
            domainComputerId: 'Domaincomputer-ID',
            domainComputerOu: 'Domaincomputer-OU',
            domainComputerCanonicalOu: 'Kanonische Domaincomputer-OU',
            domainOrWorkgroup: 'Domain oder Arbeitsgruppe',
            domainRole: 'Domainrolle',
            lastBootTime: 'Letzter Startvorgang'
          },
          hardware: {
            title: 'Hardware',
            processorArchitecture: 'Prozessorarchitektur',
            processorArchitectureBits: 'Prozessorarchitekturbits',
            processorCount: 'Prozessoranzahl',
            processorName: 'Prozessorname',
            totalPhysicalMemory: 'Physikalischer Speicher insgesamt',
            chassisType: 'Gehäusetyp',
            manufacturer: 'Hersteller',
            model: 'Modell',
            serial: 'Seriennummer',
            bios: 'BIOS'
          },
          locale: {
            title: 'Gebietsschema',
            defaultLanguage: 'Standardsprache',
            isoCountryCode: 'Ländercode',
            timeZone: 'Zeitzone'
          }
        }
      },
      propertyPanelTitles: {
        autoruns: {
          autorun: 'Eigenschaften der automatischen Ausführung',
          services: 'Serviceeigenschaften',
          tasks: 'Aufgabeneigenschaften'
        },
        files: 'Dateieigenschaften',
        drivers: 'Treibereigenschaften',
        libraries: 'Bibliothekseigenschaften'
      },
      medium: {
        network: 'Netzwerk',
        log: 'Protokoll',
        correlation: 'Korrelation'
      },
      empty: {
        title: 'Keine Ereignisse gefunden',
        description: 'Es wurden keine Datensätze gefunden, die den Filterkriterien entsprechen.'
      },
      error: {
        title: 'Daten können nicht geladen werden.',
        description: 'Beim Versuch, die Datensätze abzurufen, ist ein unerwarteter Fehler aufgetreten.'
      },
      meta: {
        title: 'Meta',
        clickToOpen: 'Zum Öffnen klicken'
      },
      events: {
        title: 'Ereignisse',
        error: 'Beim Ausführen dieser Anfrage ist ein unerwarteter Fehler aufgetreten.'
      },
      services: {
        loading: 'Liste der verfügbaren Services wird geladen',
        empty: {
          title: 'Services können nicht gefunden werden.',
          description: 'Es wurden keine Brokers, Concentrators oder sonstigen Services gefunden. Ursache hierfür kann ein Problem mit der Konfiguration oder der Verbindung sein.'
        },
        error: {
          title: 'Services können nicht geladen werden.',
          description: 'Beim Laden der Liste mit Brokers, Concentrators und sonstigen zu untersuchenden Services ist ein unerwarteter Fehler aufgetreten. Ursache hierfür kann ein Problem mit der Konfiguration oder der Verbindung sein.'
        }
      },
      customQuery: {
        title: 'Geben Sie eine Anfrage ein.'
      },
      customFilter: {
        save: {
          description: 'Geben Sie einen Namen für die Suche an. Dieser Name wird in der Suchliste angezeigt.',
          name: 'Name*',
          errorHeader: 'Suche kann nicht gespeichert werden',
          header: 'Suche speichern',
          errorMessage: 'Die Suche kann nicht gespeichert werden. ',
          emptyMessage: 'Das Feld für den Namen ist leer.',
          nameExistsMessage: 'Eine gespeicherte Suche mit demselben Namen ist bereits vorhanden.',
          success: 'Die Suchabfrage wurde erfolgreich gespeichert.',
          filterFieldEmptyMessage: 'Eines oder mehrere der neu hinzugefügten Filterfelder sind leer. Fügen Sie zum Speichern die Filter hinzu oder entfernen Sie die Felder.',
          invalidInput: 'Nur die Sonderzeichen \'-\' und \'_\' sind zulässig.'
        },
        update: {
          success: 'Die Suchabfrage wurde erfolgreich aktualisiert.'
        }
      },
      initiateScan: {
        modal: {
          title: 'Scan starten für {{count}} Host(s)',
          modalTitle: 'Scan starten für {{name}}',
          description: 'Wählen Sie den Scantyp für die ausgewählten Hosts aus.',
          error1: '*Wählen Sie mindestens einen Host aus.',
          error2: 'Um den Scan zu starten, sind maximal 100 Hosts zulässig.',
          infoMessage: 'Einige der ausgewählten Hosts werden bereits gescannt. Für sie wird daher kein erneuter Scan gestartet.',
          ecatAgentMessage: 'Einige der ausgewählten Hosts sind 4.4-Agenten. Für sie wird diese Funktion nicht unterstützt.',
          quickScan: {
            label: 'Schnellscan (Standard)',
            description: 'Führt einen schnellen Scan aller ausführbaren Module durch, die im Speicher geladen sind. Dieser Scan dauert etwa 10 Minuten.'
          }
        },
        success: 'Scan erfolgreich initialisiert',
        error: 'Initialisieren des Scans fehlgeschlagen'
      },
      cancelScan: {
        modal: {
          title: 'Scan beenden für {{count}} Host(s)',
          description: 'Möchten Sie das Scannen für die ausgewählten Hosts wirklich beenden?',
          error1: '*Wählen Sie mindestens einen Host aus.'
        },
        success: 'Beenden des Scans erfolgreich initialisiert',
        error: 'Beenden des Scans fehlgeschlagen'
      },
      deleteHosts: {
        modal: {
          title: '{{count}} Host(s) löschen',
          message: 'Löschen Sie den Host, wenn die Daten des Hostscans nicht mehr benötigt werden oder der Agent deinstalliert wird. ' +
          'Alle zum Host gehörenden Scandaten werden gelöscht. Möchten Sie fortfahren? '
        },
        success: 'Ein oder mehrere Hosts erfolgreich gelöscht',
        error: 'Löschen der Hosts fehlgeschlagen'
      },
      moreActions: {
        openIn: 'Zu Endpoint wechseln',
        openInErrorMessage: 'Wählen Sie mindestens einen Host aus.',
        notAnEcatAgent: 'Wählen Sie nur die 4.4-Agenten aus.',
        cancelScan: 'Scan stoppen'
      }
    },
    savedQueries: {
      headerContent: 'Wählen Sie eine gespeicherte Abfrage aus der Liste aus, um sie auszuführen. Sie können den Namen der gespeicherten Abfrage auch bearbeiten, indem Sie auf das Bleistiftsymbol neben dem Namen klicken. Wenn Sie auf das Sternsymbol klicken, wird die Abfrage als Standard festgelegt.',
      deleteBtn: 'Auswahl löschen',
      runBtn: 'Ausgewählte ausführen',
      yesBtn: 'Ja',
      noBtn: 'Nein',
      delete: {
        successMessage: 'Abfrage erfolgreich gelöscht.',
        confirmMessage: 'Möchten Sie die ausgewählte Abfrage wirklich löschen?'
      },
      edit: {
        successMessage: 'Abfragename erfolgreich aktualisiert',
        errorMessage: 'Aktualisieren des Abfragenamens fehlgeschlagen',
        nameExistsMessage: 'Abfragename bereits vorhanden'
      }
    },
    files: {
      footer: '{{count}} von {{total}} {{label}}',
      filter: {
        filters: 'Gespeicherte Filter',
        newFilter: 'Neuer Filter',
        windows: 'WINDOWS',
        mac: 'MAC',
        linux: 'LINUX',
        favouriteFilters: 'Beliebte Filter',
        restrictionType: {
          moreThan: 'Größer als',
          lessThan: 'Kleiner als',
          between: 'Zwischen',
          equals: 'Ist gleich',
          contains: 'Enthält'
        },
        save: 'Speichern',
        reset: 'Zurücksetzen',
        customFilters: {
          save: {
            description: 'Geben Sie einen Namen für die Suche an. Dieser Name wird in der Suchliste angezeigt.',
            name: 'Name*',
            errorHeader: 'Suche kann nicht gespeichert werden',
            header: 'Suche speichern',
            errorMessage: 'Die Suche kann nicht gespeichert werden. ',
            emptyMessage: 'Das Feld für den Namen ist leer.',
            nameExistsMessage: 'Eine gespeicherte Suche mit demselben Namen ist bereits vorhanden.',
            success: 'Die Suchabfrage wurde erfolgreich gespeichert.',
            filterFieldEmptyMessage: 'Filterfelder sind leer',
            invalidInput: 'Nur die Sonderzeichen \'-\' und \'_\' sind zulässig.'
          }
        },
        button: {
          cancel: 'Abbrechen',
          save: 'Speichern'
        }
      },
      fields: {
        id: 'ID',
        firstSeenTime: 'Zeit des ersten Auftretens',
        companyName: 'Name des Unternehmens',
        checksumMd5: 'MD5',
        checksumSha1: 'SHA1',
        checksumSha256: 'SHA256',
        machineOsType: 'Betriebssystem',
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
          features: 'Signatur',
          signer: 'Signaturgeber'
        },
        owner: {
          userName: 'Eigentümer',
          groupName: 'Eigentümergruppe'
        },
        rpm: {
          packageName: 'Paket'
        },
        path: 'Pfad',
        entropy: 'Entropie',
        fileName: 'FileName',
        firstFileName: 'FileName',
        timeCreated: 'Erstellt',
        format: 'Format',
        sectionNames: 'Abschnittsnamen',
        importedLibraries: 'Importierte Bibliotheken',
        size: 'Größe'
      }
    },
    pivotToInvestigate: {
      title: 'Service auswählen',
      buttonText: 'Navigieren',
      buttonText2: 'Ereignisanalyse',
      iconTitle: 'Zu Navigation wechseln oder Ereignisanalyse'
    }
  },
  hostsScanConfigure: {
    title: 'Scanplanung',
    save: 'Speichern',
    enable: 'Aktivieren',
    saveSuccess: 'Erfolgreich gespeichert',
    startDate: 'Startdatum',
    recurrenceInterval: {
      title: 'Wiederholungsintervall',
      options: {
        daily: 'Täglich',
        weekly: 'Wöchentlich',
        monthly: 'Monatlich'
      },
      every: 'Alle',
      on: 'Am',
      intervalText: {
        DAYS: 'Tag(e)',
        WEEKS: 'Woche(n)',
        MONTHS: 'Monat(e)'
      },
      week: {
        monday: 'n',
        tuesday: 'T',
        wednesday: 'W',
        thursday: 'T',
        friday: 'D',
        saturday: 'S',
        sunday: 'S'
      }
    },
    startTime: 'Startzeit',
    cpuThrottling: {
      title: 'CPU-Drosselung für Agent',
      cpuMax: 'Maximalleistung CPU (%)',
      vmMax: 'Maximalleistung virtuelle Maschine (%) '
    },
    error: {
      generic: 'Beim Versuch, diese Daten abzurufen, ist ein unerwarteter Fehler aufgetreten.'
    }
  }
};
});
