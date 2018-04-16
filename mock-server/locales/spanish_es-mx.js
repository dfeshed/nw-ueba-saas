define('sa/locales/es-mx/translations', ['exports'], function (exports) {
  'use strict';

  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports.default = {
  appTitle: 'NetWitness Platform',
  pageTitle: '{{section}}: NetWitness Platform',
  empty: '',
  languages: {
    en: 'Inglés',
    'en-us': 'Inglés',
    ja: 'Japonés'
  },
  passwordPolicy: {
    passwordPolicyRequestError: 'Se produjo un problema al recuperar su política de contraseña.',
    passwordPolicyMinChars: 'Debe tener por lo menos {{passwordPolicyMinChars}} caracteres',
    passwordPolicyMinNumericChars: 'Debe contener por lo menos {{passwordPolicyMinNumericChars}} número(s) (del 0 al 9)',
    passwordPolicyMinUpperChars: 'Debe tener por lo menos {{passwordPolicyMinUpperChars}} carácter(es) en mayúscula',
    passwordPolicyMinLowerChars: 'Debe tener por lo menos {{passwordPolicyMinLowerChars}} carácter(es) en minúscula',
    passwordPolicyMinNonLatinChars: 'Debe contener por lo menos {{passwordPolicyMinNonLatinChars}} carácter(es) alfabético(s) Unicode que no estén en mayúscula ni en minúscula',
    passwordPolicyMinSpecialChars: 'Debe contener por lo menos {{passwordPolicyMinSpecialChars}} carácter(es) no alfanumérico(s): (~!@#$%^&*_-+=`|(){}[]:;"\'<>,.?/)',
    passwordPolicyCannotIncludeId: 'La contraseña no puede contener su nombre de usuario'
  },
  forms: {
    cancel: 'Cancelar',
    submit: 'Enviar',
    reset: 'Restablecer',
    apply: 'Aplicar',
    ok: 'Aceptar',
    delete: 'Eliminar',
    save: 'Guardar',
    yes: 'Sí',
    no: 'No'
  },
  tables: {
    noResults: 'Sin resultados',
    columnChooser: {
      filterPlaceHolder: 'Tipo para filtrar la lista'
    }
  },
  login: {
    username: 'Nombre de usuario',
    password: 'Contraseña',
    login: 'Iniciar sesión',
    loggingIn: 'Iniciando sesión',
    logout: 'Cerrar sesión',
    oldPassword: 'Contraseña anterior',
    newPassword: 'Nueva contraseña',
    confirmPassword: 'Confirmar contraseña',
    passwordMismatch: 'La confirmación de la contraseña no coincide.',
    passwordNoChange: 'La nueva contraseña no puede coincidir con la contraseña anterior.',
    passwordChangeFailed: 'Se produjo un problema al guardar el cambio de la contraseña. Vuelva a intentarlo.',
    lostPasswordLink: '¿Perdió la contraseña?',
    genericError: 'Error de autenticación. Vuelva a intentarlo.',
    communicationError: 'No se pudo acceder al servidor. Póngase en contacto con el administrador del sistema.',
    userLocked: 'La cuenta de usuario está bloqueada.',
    userDisabled: 'La cuenta de usuario está deshabilitada',
    userExpired: 'La cuenta de usuario venció',
    changePasswordLink: 'Cambiar mi contraseña',
    changePasswordSoon: 'Tenga en cuenta que su contraseña del servidor de RSA NetWitness vencerá en {{daysRemaining}} día(s). Lo invitamos a cambiar la contraseña antes de que venza. Para cambiar la contraseña, haga clic en el botón Preferencias de la parte superior derecha de la ventana de la aplicación.',
    changePasswordToday: 'Tenga en cuenta que su contraseña del servidor de RSA NetWitness vencerá hoy. Lo invitamos a cambiar la contraseña antes de que venza. Para cambiar la contraseña, haga clic en el botón Preferencias de la parte superior derecha de la ventana de la aplicación.',
    lostPassword: {
      title: 'Recuperación de contraseña perdida',
      description: 'Envíe su nombre de usuario.'
    },
    thankYou: {
      title: 'Gracias.',
      description: 'Se envió un restablecimiento de la contraseña a la cuenta de correo electrónico registrada del usuario.',
      back: 'Volver al inicio de sesión'
    },
    eula: {
      title: 'Acuerdo de licencia para el usuario final',
      agree: 'Aceptar'
    },
    forcePassword: {
      warning: 'Deberá crear una contraseña nueva para poder iniciar sesión.',
      changePassword: 'Cambiar contraseña'
    }
  },
  userPreferences: {
    preferences: 'Preferencias de usuario',
    personalize: 'Personalizar su experiencia',
    signOut: 'Cerrar sesión',
    version: 'Versión',
    username: 'Nombre de usuario',
    email: 'Correo electrónico',
    language: 'Idioma',
    timeZone: 'Zona horaria',
    dateFormatError: 'Se produjo un error al intentar guardar su selección de Formato de fecha. Vuelva a intentarlo. Si este problema persiste, póngase en contacto con el administrador del sistema.',
    landingPageError: 'Se produjo un error al intentar guardar su selección de Página principal predeterminada. Vuelva a intentarlo. Si este problema persiste, póngase en contacto con el administrador del sistema.',
    defaultInvestigatePageError: 'Se produjo un error al intentar guardar su selección de Vista Investigate predeterminada. Vuelva a intentarlo. Si este problema persiste, póngase en contacto con el administrador del sistema.',
    timeFormatError: 'Se produjo un error al intentar guardar su selección de Formato de hora. Vuelva a intentarlo. Si este problema persiste, póngase en contacto con el administrador del sistema.',
    timezoneError: 'Se produjo un error al intentar guardar su selección de Zona horaria. Vuelva a intentarlo. Si este problema persiste, póngase en contacto con el administrador del sistema.',
    dateFormat: {
      label: 'Formato de fecha',
      dayFirst: 'DD/MM/AAAA',
      monthFirst: 'MM/DD/AAAA',
      yearFirst: 'AAAA/MM/DD'
    },
    timeFormat: {
      label: 'Formato de hora',
      twelveHour: '12 h',
      twentyFourHour: '24 h'
    },
    theme: {
      title: 'Tema',
      dark: 'Oscuro',
      light: 'Ligera',
      error: 'Se produjo un error al intentar guardar su selección de Tema. Vuelva a intentarlo. Si este problema persiste, póngase en contacto con el administrador del sistema.'
    },
    defaultLandingPage: {
      label: 'Página principal predeterminada',
      monitor: 'Monitor',
      investigate: 'Investigar',
      investigateClassic: 'Investigar',
      dashboard: 'Monitor',
      live: 'Configurar',
      respond: 'Respond',
      admin: 'Admin'
    },
    defaultInvestigatePage: {
      label: 'Vista Investigate predeterminada',
      events: 'Eventos',
      eventAnalysis: 'Análisis de eventos',
      malware: 'Malware Analysis',
      navigate: 'Navegar',
      hosts: 'Hosts',
      files: 'Archivos'
    }
  },
  queryBuilder: {
    noMatches: 'No se encontró ninguna coincidencia',
    enterValue: 'Introducir un único valor',
    insertFilter: 'Insertar nuevo filtro',
    query: 'Consultar con filtros',
    open: 'Abrir en una pestaña nueva',
    delete: 'Eliminar los filtros seleccionados',
    deleteFilter: 'Eliminar este filtro',
    edit: 'Editar este filtro',
    placeholder: 'Introducir una clave, un operador y un valor de metadatos (opcional)',
    querySelected: 'Consultar con filtros seleccionados',
    querySelectedNewTab: 'Consultar con filtros seleccionados en una pestaña nueva',
    expensive: 'La ejecución de esta operación podría tardar más tiempo.',
    notEditable: 'Los filtros de consultas complejas no son editables.',
    validationMessages: {
      time: 'Debe introducir una fecha válida.',
      text: 'Se deben agregar comillas (") a las cadenas',
      ipv4: 'Debe introducir una dirección IPv4.',
      ipv6: 'Debe introducir una dirección IPv6.',
      uint8: 'Debe introducir un entero de 8 bits.',
      uint16: 'Debe introducir un entero de 16 bits.',
      uint32: 'Debe introducir un entero de 32 bits.',
      float32: 'Debe introducir datos de tipo flotante de 32 bits.'
    }
  },
  ipConnections: {
    ipCount: '({{count}} IP)',
    clickToCopy: 'Haga clic para copiar la dirección IP',
    sourceIp: 'Dirección IP de origen',
    destinationIp: 'Dirección IP de destino'
  },
  list: {
    all: '(Todo)',
    items: 'elementos',
    packets: 'paquetes',
    packet: 'paquete',
    of: 'de',
    sessions: 'sesiones'
  },
  updateLabel: {
    'one': 'actualizar',
    'other': 'actualizaciones'
  },
  recon: {
    extractWarning: '<span>Salió antes de que los archivos descargados se pudieran adjuntar a la bandeja del navegador. La descarga estará disponible <a href="{{url}}" target="_blank">aquí</a>.</span>',
    extractedFileReady: 'El archivo se extrajo. Vaya a la línea de espera de trabajos para descargarlo.',
    titleBar: {
      titles: {
        endpoint: 'Detalles del evento de Endpoint',
        network: 'Detalles del evento de red',
        log: 'Detalles del evento de registro'
      },
      views: {
        text: 'Análisis de texto',
        packet: 'Análisis de paquetes',
        file: 'Análisis de archivos',
        web: 'Web',
        mail: 'Correo electrónico'
      }
    },
    meta: {
      scroller: {
        of: 'de',
        results: 'resultados'
      }
    },
    textView: {
      compressToggleLabel: 'Mostrar cargas útiles comprimidas',
      compressToggleTitle: 'Mostrar cargas útiles HTTP como comprimidas o no',
      downloadCsv: 'Descargar CSV',
      downloadEndpointEvent: 'Descargar Endpoint',
      pivotToEndpoint: 'Cambiar a cliente grueso de Endpoint',
      pivotToEndpointTitle: 'Se aplica a los hosts con agentes de Endpoint 4.x instalados. Instale el cliente grueso de NetWitness Endpoint.',
      downloadJson: 'Descargar JSON',
      downloadLog: 'Descargar registro',
      downloadXml: 'Descargar XML',
      headerShowing: 'Mostrando',
      isDownloading: 'Descargando…',
      maxPacketsReached: 'Se generaron <span class="darker">{{maxPacketCount}} (máx.)</span> de <span class="darker">{{packetTotal}}</span> paquetes',
      maxPacketsReachedTooltip: 'Se alcanzó el límite de {{maxPacketCount}} paquetes para generar un único evento; no se generarán paquetes adicionales para este evento. El umbral de paquetes garantiza la mejor experiencia de generación.',
      rawEndpointHeader: 'Endpoint crudo',
      rawLogHeader: 'Registro crudo',
      renderingMore: 'Mostrando más…',
      renderRemaining: 'Generando {{remainingPercent}} % restante…',
      showRemaining: 'Mostrar {{remainingPercent}} % restante'
    },
    packetView: {
      noHexData: 'No se generaron datos hexadecimales durante la reconstrucción de contenido.',
      isDownloading: 'Descargando…',
      defaultDownloadPCAP: 'Descargar PCAP',
      downloadPCAP: 'Descargar PCAP',
      downloadPayload1: 'Descargar carga útil de la solicitud',
      downloadPayload2: 'Descargar carga útil de la respuesta',
      downloadPayload: 'Descargar todas las cargas útiles',
      payloadToggleLabel: 'Mostrar solo cargas útiles',
      payloadToggleTitle: 'Quita los encabezados y los pies de página de los paquetes de la presentación',
      stylizeBytesLabel: 'Sombrear bytes',
      stylizeBytesTitle: 'Habilite esta opción como ayuda para distinguir patrones dentro de los datos',
      commonFilePatternLabel: 'Patrones de archivo comunes',
      commonFilePatternTitle: 'Habilite esta opción para destacar patrones de firmas de archivos comunes',
      headerMeta: 'Metadatos de encabezado',
      headerAttribute: 'Atributo de encabezado',
      headerSignature: 'Bytes de interés',
      headerDisplayLabel: '{{label}} = {{displayValue}}',
      renderingMore: 'Mostrando más…'
    },
    reconPager: {
      packetPagnationPageFirst: 'Primero',
      packetPagnationPagePrevious: 'Anterior',
      packetPagnationPageNext: 'Siguiente',
      packetPagnationPageLast: 'Último',
      packetsPerPageText: 'Paquetes por página'
    },
    fileView: {
      downloadFile: 'Descargar archivo',
      downloadFiles: 'Descargar archivos ({{fileCount}})',
      isDownloading: 'Descargando…',
      downloadWarning: 'Advertencia: Los archivos incluyen el contenido sin asegurar crudo original. Tenga precaución al abrir o descargar los archivos; pueden contener datos maliciosos.'
    },
    files: {
      fileName: 'Nombre de archivo',
      extension: 'Extensión',
      mimeType: 'Tipo MIME',
      fileSize: 'Tamaño del archivo',
      hashes: 'Hashes',
      noFiles: 'No hay archivos disponibles para este evento.',
      linkFile: 'Este archivo está en otra sesión.<br>Haga clic en el enlace del archivo para ver la sesión relacionada en una pestaña nueva.'
    },
    error: {
      generic: 'Se produjo un error inesperado al intentar recuperar estos datos.',
      missingRecon: 'Este evento (ID = {{id}}) no se guardó o se eliminó del almacenamiento. No hay contenido para mostrar.',
      noTextContentData: 'no se generaron datos de texto durante la reconstrucción de contenido. Esto podría significar que los datos de eventos estaban dañados o que no eran válidos. Compruebe las otras vistas de reconstrucción.',
      noRawDataEndpoint: 'no se generaron datos de texto durante la reconstrucción de contenido. Esto podría significar que los datos de los eventos estaban dañados o no eran válidos, o que un administrador deshabilitó la transmisión de eventos de terminal crudos en la configuración del servidor de Endpoint. Compruebe las otras vistas de reconstrucción.',
      permissionError: 'Permisos insuficientes para los datos solicitados. Si cree que debe tener acceso, solicite a su administrador que le proporcione los permisos necesarios.'
    },
    fatalError: {
      115: 'La sesión no está disponible para su visualización.',
      124: 'ID de sesión no válido: {{eventId}}',
      11: 'El ID de la sesión es demasiado grande y no se puede manejar: {{eventId}}',
      permissions: 'No tiene los permisos requeridos para ver este contenido.'
    },
    toggles: {
      header: 'Mostrar/ocultar encabezado',
      request: 'Mostrar/ocultar solicitud',
      response: 'Mostrar/ocultar respuesta',
      topBottom: 'Vista de arriba abajo',
      sideBySide: 'Vista de lado a lado',
      meta: 'Mostrar/ocultar metadatos',
      expand: 'Expandir vista',
      shrink: 'Contraer vista',
      close: 'Cerrar reconstrucción'
    },
    eventHeader: {
      nwService: 'Servicio de NW',
      sessionId: 'ID de sesión',
      type: 'Tipo',
      source: 'IP:PUERTO de origen',
      destination: 'IP:PUERTO de destino',
      service: 'Servicio',
      firstPacketTime: 'Hora del primer paquete',
      lastPacketTime: 'Hora del último paquete',
      packetSize: 'Tamaño de paquetes calculado',
      payloadSize: 'Tamaño de cargas útiles calculado',
      packetCount: 'Conteo de paquetes calculado',
      packetSizeTooltip: 'El tamaño de paquetes calculado en el encabezado de resumen puede ser distinto al tamaño de paquetes en el panel de detalles de metadatos porque, en ocasiones, los datos de metadatos se escriben antes de que se complete el análisis de eventos y pueden incluir duplicados de paquetes.',
      payloadSizeTooltip: 'El tamaño de cargas útiles calculado en el encabezado de resumen puede ser distinto al tamaño de cargas útiles en el panel de detalles de metadatos porque, en ocasiones, los datos de metadatos se escriben antes de que se complete el análisis de eventos y pueden incluir duplicados de paquetes.',
      packetCountTooltip: 'El conteo de paquetes calculado en el encabezado de resumen puede ser distinto al conteo de paquetes en el panel de detalles de metadatos porque, en ocasiones, los datos de metadatos se escriben antes de que se complete el análisis de eventos y puede incluir duplicados de paquetes.',
      deviceIp: 'IP del dispositivo',
      deviceType: 'Tipo de dispositivo',
      deviceClass: 'Clase de dispositivo',
      eventCategory: 'Categoría de eventos',
      nweCategory: 'Categoría de NWE',
      collectionTime: 'Hora de recopilación',
      eventTime: 'Hora del evento',
      nweEventTime: 'Hora del evento',
      nweMachineName: 'Nombre de la máquina',
      nweMachineIp: 'IP de máquina',
      nweMachineUsername: 'Nombre de usuario de máquina',
      nweMachineIiocScore: 'Puntaje de IIOC de máquina',
      nweEventSourceFilename: 'Nombre de archivo de origen de eventos',
      nweEventSourcePath: 'Ruta de origen de eventos',
      nweEventDestinationFilename: 'Nombre de archivo de destino de eventos',
      nweEventDestinationPath: 'Ruta de destino de eventos',
      nweFileFilename: 'Nombre de archivo',
      nweFileIiocScore: 'Puntaje de IIOC de archivo',
      nweProcessFilename: 'Nombre de archivo del proceso',
      nweProcessParentFilename: 'Nombre de archivo primario',
      nweProcessPath: 'Ruta del proceso',
      nweDllFilename: 'Nombre de archivo DLL',
      nweDllPath: 'Ruta de DLL',
      nweDllProcessFilename: 'Nombre de archivo del proceso',
      nweAutorunFilename: 'Nombre de archivo de la ejecución automática',
      nweAutorunPath: 'Ruta de la ejecución automática',
      nweServiceDisplayName: 'Nombre para mostrar del servicio',
      nweServiceFilename: 'Nombre de archivo del servicio',
      nweServicePath: 'Ruta del servicio',
      nweTaskName: 'Nombre de la tarea',
      nweTaskPath: 'Ruta de la tarea',
      nweNetworkFilename: 'Nombre de archivo de la red',
      nweNetworkPath: 'Ruta de red',
      nweNetworkProcessFilename: 'Nombre de archivo del proceso de red',
      nweNetworkProcessPath: 'Ruta del proceso de red',
      nweNetworkRemoteAddress: 'Dirección remota de la red'
    },
    contextmenu: {
      copy: 'Copiar',
      externalLinks: 'Búsqueda externa',
      livelookup: 'Búsqueda en Live',
      endpointIoc: 'Búsqueda del cliente grueso de Endpoint',
      applyDrill: 'Aplicar desglose en pestaña nueva',
      applyNEDrill: 'Aplicar desglose !EQUALS en pestaña nueva',
      refocus: 'Volver a centrar la investigación en una pestaña nueva',
      hostslookup: 'Búsqueda de hosts',
      external: {
        google: 'Google',
        sansiphistory: 'Historial de IP SANS',
        centralops: 'CentralOps Whois para direcciones IP y nombres de host',
        robtexipsearch: 'Búsqueda de dirección IP en Robtex',
        ipvoid: 'IPVoid',
        urlvoid: 'URLVoid',
        threatexpert: 'Búsqueda en ThreatExpert'
      }
    }
  },
  memsize: {
    B: 'bytes',
    KB: 'KB',
    MB: 'MB',
    GB: 'GB',
    TB: 'TB'
  },
  midnight: 'Medianoche',
  noon: 'Mediodía',
  investigate: {
    controls: {
      toggle: 'Mostrar/ocultar el panel de eventos',
      togglePreferences: 'Alternar entre preferencias de Investigate'
    },
    title: 'Investigar',
    loading: 'Cargando',
    loadMore: 'Cargar más',
    tryAgain: 'Volver a intentarlo',
    service: 'Servicio',
    timeRange: 'Rango de tiempo',
    filter: 'Filtrar',
    size: {
      bytes: 'bytes',
      KB: 'KB',
      MB: 'MB',
      GB: 'GB',
      TB: 'TB'
    },
    medium: {
      endpoint: 'Terminal',
      network: 'Red',
      log: 'Registro',
      correlation: 'Correlación',
      undefined: 'Desconocido'
    },
    empty: {
      title: 'No se encontró ningún evento.',
      description: 'Sus criterios de filtro no coincidieron con ningún registro.'
    },
    error: {
      title: 'No se pueden cargar datos.',
      description: 'Se produjo un error inesperado cuando se intentó buscar los registros de datos.'
    },
    meta: {
      title: 'Meta',
      clickToOpen: 'Haga clic para abrir'
    },
    events: {
      title: 'Eventos',
      columnGroups: {
        custom: 'Grupos de columnas personalizados',
        customTitle: 'Administrar grupos de columnas personalizados en la vista Eventos',
        default: 'Grupos de columnas predeterminados',
        searchPlaceholder: 'Tipo para filtrar el grupo de columnas'
      },
      error: 'Se produjo un error inesperado al ejecutar esta consulta.',
      shrink: 'Reducir el panel de eventos',
      expand: 'Expandir el panel de eventos',
      close: 'Cerrar el panel de eventos',
      scrollMessage: 'Desplazarse hacia abajo para ver el evento seleccionado resaltado en azul',
      eventTips: {
        noResults: 'Aún no hay resultados. Seleccione un servicio y un rango de tiempo, y envíe una consulta',
        head: {
          header: 'EJEMPLOS DE FILTROS DE CONSULTA',
          text: {
            one: 'Buscar eventos HTTP salientes con un agente de usuario de alguna versión de Mozilla',
            two: 'Buscar eventos de Windows de inicios de sesión fallidos',
            three: 'Buscar eventos de terminal con tareas cuyos nombres de archivo terminan en exe'
          }
        },
        section: {
          mouse: {
            header: 'INTERACCIONES CON EL MOUSE',
            textOne: 'Haga clic antes o después de los filtros o entre estos para insertar otro filtro.',
            textTwo: 'Haga clic en un filtro y haga clic con el botón secundario para mostrar el menú Acción.',
            textThree: 'Haga doble clic en un filtro para abrirlo para su edición.',
            textFour: 'Haga clic en varios filtros y presione <span class="highlight">Eliminar</span> para quitar los filtros seleccionados.',
            textFive: 'Haga clic en el botón <span class="highlight">Atrás</span> del navegador para volver al estado anterior.'
          },
          keyboard: {
            header: 'INTERACCIONES CON EL TECLADO',
            textOne: 'Comience a escribir un nombre o una descripción de clave de metadatos en el generador de consultas.',
            textTwo: 'Use las flechas hacia <span class="highlight">arriba</span> y hacia <span class="highlight">abajo</span> en los menús desplegables y presione <span class="highlight">Entrar</span> para seleccionar.',
            textThree: 'Presione <span class="highlight">Entrar</span> o haga clic en <span class="highlight">Eventos de consulta</span> para ejecutar una consulta.',
            textFour: 'Presione la flecha hacia la <span class="highlight">izquierda</span> o la <span class="highlight">derecha</span> para recorrer la consulta con el fin de agregar más filtros, o presione <span class="highlight">Entrar</span> para editar los existentes.',
            textFive: 'Presione <span class="highlight">Maýus + flecha hacia la izquierda</span> o <span class="highlight">flecha hacia la derecha</span> para seleccionar varios filtros con el fin de eliminarlos con el uso de la <span class="highlight">Tecla de retroceso</span> o de <span class="highlight">Eliminar</span.'
          }
        }
      },
      logs: {
        wait: 'Cargando el registro…',
        rejected: 'No hay datos del registro.'
      }
    },
    generic: {
      loading: 'Cargando datos…'
    },
    services: {
      loading: 'Cargando servicios',
      noData: 'El servicio seleccionado no tiene datos',
      coreServiceNotUpdated: 'Análisis de eventos requiere que todos los servicios principales sean NetWitness 11.1. La conexión de versiones de servicios anteriores al servidor de NetWitness 11.1 da lugar a funcionalidad limitada (consulte “Investigate en modo mixto” en la Guía de actualización de hosts físicos).',
      empty: {
        title: 'No se pueden encontrar servicios.',
        description: 'No se detectaron Brokers, Concentrators u otros servicios. Esto se puede deber a un problema de configuración o conectividad.'
      },
      error: {
        label: 'Servicios no disponibles',
        description: 'Se produjo un error inesperado al cargar la lista de Brokers, Concentrators y otros servicios para investigar. Esto se puede deber a un problema de configuración o conectividad.'
      }
    },
    summary: {
      loading: 'Cargando resumen'
    },
    customQuery: {
      title: 'Ingrese una consulta.'
    }
  },
  configure: {
    title: 'Configurar',
    liveContent: 'Live Content',
    esaRules: 'Reglas de ESA',
    respondNotifications: 'Notificaciones de Respond',
    incidentRulesTitle: 'Reglas de incidentes',
    subscriptions: 'Suscripciones',
    customFeeds: 'Feeds personalizados',
    incidentRules: {
      noManagePermissions: 'No tiene permisos para realizar ediciones en las reglas de incidentes',
      confirm: '¿Está seguro de que desea hacer esto?',
      assignee: {
        none: '(Sin asignar)'
      },
      priority: {
        LOW: 'Baja',
        MEDIUM: 'Media',
        HIGH: 'Alta',
        CRITICAL: 'Crítica'
      },
      action: 'Acción',
      actionMessage: 'Elegir la acción que se lleva a cabo si la regla coincide con una alerta',
      error: 'Se produjo un problema al cargar las reglas de incidentes',
      noResults: 'No se encontró ninguna regla de incidentes',
      createRule: 'Crear regla',
      deleteRule: 'Eliminar',
      cloneRule: 'Clonar',
      select: 'Seleccionar',
      order: 'Orden',
      enabled: 'Habilitado',
      name: 'Nombre',
      namePlaceholder: 'Proporcionar un nombre único para la regla',
      ruleNameRequired: 'Debe proporcionar un nombre de regla',
      description: 'Descripción',
      descriptionPlaceholder: 'Proporcionar una descripción de la regla',
      lastMatched: 'Última coincidencia',
      alertsMatchedCount: 'Alertas con coincidencia',
      incidentsCreatedCount: 'Incidentes',
      matchConditions: 'Condiciones de coincidencia',
      queryMode: 'Modo de consulta',
      queryModes: {
        RULE_BUILDER: 'Generador de reglas',
        ADVANCED: 'Avanzada'
      },
      queryBuilderQuery: 'Generador de consultas',
      advancedQuery: 'Avanzada',
      advancedQueryRequired: 'La consulta avanzada no puede estar vacía',
      groupingOptions: 'Opciones de agrupación',
      groupBy: 'Agrupar por',
      groupByPlaceholder: 'Elegir un campo agrupar por (obligatorio)',
      groupByError: 'Se requiere un mínimo de un campo agrupar por y se permite un máximo de dos',
      timeWindow: 'Ventana de tiempo',
      incidentOptions: 'Opciones de incidente',
      incidentTitle: 'Título',
      incidentTitleRequired: 'Debe proporcionar un título para los incidentes creados a partir de esta regla',
      incidentTitlePlaceholder: 'Introducir un título para el incidente que creó esta regla',
      incidentTitleHelp: 'La plantilla de título se usa para crear el título del incidente. Por ejemplo, si la regla tiene el nombre Rule-01 y el campo groupBy es Gravedad, el valor de groupBy es 50 y la plantilla es ${ruleName} para ${groupByValue1}, se creará un incidente con el nombre Rule-01 para 50.',
      incidentSummary: 'Resumen',
      incidentSummaryPlaceholder: 'Introducir un resumen para el incidente que creó esta regla',
      incidentCategories: 'Categorías',
      incidentCategoriesPlaceholder: 'Elegir una categoría (opcional)',
      incidentAssignee: 'Usuario asignado',
      incidentAssigneePlaceholder: 'Elegir un usuario asignado (opcional)',
      incidentPriority: 'Prioridad',
      incidentPriorityInstruction: 'Use lo siguiente para configurar la prioridad del incidente',
      incidentPriorityAverage: 'Promedio de puntaje de riesgo en todas las alertas',
      incidentPriorityHighestScore: 'Puntaje de riesgo más alto disponible en todas las alertas',
      incidentPriorityAlertCount: 'Cantidad de alertas en la ventana de tiempo',
      priorityScoreError: 'Los rangos de puntaje de prioridad no son válidos',
      confirmQueryChange: 'Confirmar cambio en consulta',
      confirmAdvancedQueryMessage: 'El cambio del modo Generador de consultas al modo Avanzado restablecerá los criterios de coincidencia.',
      confirmQueryBuilderMessage: 'El cambio del modo Avanzado al modo Generador de consultas restablecerá los criterios de coincidencia.',
      groupAction: 'Agrupar en un incidente',
      suppressAction: 'Suprimir la alerta',
      timeUnits: {
        DAY: 'Días',
        HOUR: 'Horas',
        MINUTE: 'Minutos'
      },
      ruleBuilder: {
        addConditionGroup: 'Agregar grupo',
        removeConditionGroup: 'Eliminar grupo',
        addCondition: 'Agregar condición',
        field: 'Campo',
        operator: 'Operador',
        operators: {
          '=': 'es igual a',
          '!=': 'no es igual a',
          'begins': 'comienza con',
          'ends': 'finaliza con',
          'contains': 'contiene',
          'regex': 'coincide con regex',
          'in': 'en',
          'nin': 'no en',
          '>': 'es mayor que',
          '>=': 'es igual o mayor que',
          '<': 'es menor que',
          '<=': 'es igual o menor que'
        },
        groupOperators: {
          and: 'Todas estas',
          or: 'Cualquiera de estas',
          not: 'Ninguna de estas'
        },
        value: 'Valor',
        hasGroupsWithoutConditions: 'Todos los grupos deben tener por lo menos una condición',
        hasMissingConditionInfo: 'Falta un campo, un operador o un valor por lo menos en una condición'
      },
      actionMessages: {
        deleteRuleConfirmation: '¿Confirma que desea eliminar esta regla? Una vez que se aplique, esta eliminación no se puede revertir.',
        reorderSuccess: 'Cambió correctamente el orden de las reglas',
        reorderFailure: 'Se produjo un problema al cambiar el orden de las reglas',
        cloneSuccess: 'Clonó correctamente la regla seleccionada',
        cloneFailure: 'Se produjo un problema al clonar la regla seleccionada',
        createSuccess: 'Creó correctamente una regla nueva',
        createFailure: 'Se produjo un problema al crear la regla nueva',
        deleteSuccess: 'Eliminó correctamente la regla seleccionada',
        deleteFailure: 'Se produjo un problema al eliminar la regla seleccionada',
        saveSuccess: 'Los cambios en la regla se guardaron correctamente',
        saveFailure: 'Se produjo un problema al guardar los cambios en la regla',
        duplicateNameFailure: 'Ya hay otra regla con el mismo nombre. Modifique el nombre de la regla de modo que sea único.'
      },
      missingRequiredInfo: 'Falta información requerida en la regla de incidentes'
    },
    notifications: {
      settings: 'Configuración de notificaciones de Respond',
      emailServer: 'Servidor de correo electrónico',
      socEmailAddresses: 'Direcciones de correo electrónico del administrador del SOC',
      noSocEmails: 'No hay correos electrónicos del administrador del SOC configurados',
      emailAddressPlaceholder: 'Introducir una dirección de correo electrónico para agregar',
      addEmail: 'Agregar',
      notificationTypes: 'Tipos de notificaciones',
      type: 'Tipo',
      sendToAssignee: 'Enviar a usuario asignado',
      sendToSOCManagers: 'Enviar a administradores del SOC',
      types: {
        'incident-created': 'Se creó el incidente',
        'incident-state-changed': 'Se actualizó el incidente'
      },
      hasUnsavedChanges: 'Tiene cambios no guardados. Haga clic en Aplicar para guardar.',
      emailServerSettings: 'Configuración de servidor de correo electrónico',
      noManagePermissions: 'No tiene permisos para realizar ediciones en las notificaciones de Respond',
      actionMessages: {
        fetchFailure: 'Se produjo un problema al cargar la configuración de notificaciones de Respond',
        updateSuccess: 'Actualizó correctamente la configuración de notificaciones de Respond',
        updateFailure: 'Se produjo un problema al actualizar la configuración de notificaciones de Respond'
      }
    }
  },
  respond: {
    title: 'Respond',
    common: {
      yes: 'Sí',
      no: 'No',
      true: 'Sí',
      false: 'No'
    },
    none: 'Ninguno',
    select: 'Seleccionar',
    close: 'Cerrar',
    empty: '(vacío)',
    filters: 'Filtros',
    errorPage: {
      serviceDown: 'El servidor de Respond está offline',
      serviceDownDescription: 'El servidor de Respond no está en ejecución o está inaccesible. Consulte al administrador para resolver este problema.',
      fetchError: 'Se produjo un error. El servidor de Respond puede estar offline o inaccesible.'
    },
    timeframeOptions: {
      LAST_5_MINUTES: 'Últimos 5 minutos',
      LAST_10_MINUTES: 'Últimos 10 minutos',
      LAST_15_MINUTES: 'Últimos 15 minutos',
      LAST_30_MINUTES: 'Últimos 30 minutos',
      LAST_HOUR: 'Última hora',
      LAST_3_HOURS: 'Últimas 3 horas',
      LAST_6_HOURS: 'Últimas 6 horas',
      LAST_TWELVE_HOURS: 'Últimas 12 horas',
      LAST_TWENTY_FOUR_HOURS: 'Últimas 24 horas',
      LAST_FORTY_EIGHT_HOURS: 'Últimos 2 días',
      LAST_5_DAYS: 'Últimos 5 días',
      LAST_7_DAYS: 'Últimos 7 días',
      LAST_14_DAYS: 'Últimos 14 días',
      LAST_30_DAYS: 'Últimos 30 días',
      ALL_TIME: 'Todos los datos'
    },
    entities: {
      incidents: 'Incidentes',
      remediationTasks: 'Tareas',
      alerts: 'Alertas',
      actionMessages: {
        updateSuccess: 'Su cambio se realizó correctamente',
        updateFailure: 'Se produjo un problema al actualizar el campo para este registro',
        createSuccess: 'Agregó correctamente un registro nuevo',
        createFailure: 'Se produjo un problema al crear este registro',
        deleteSuccess: 'Eliminó correctamente este registro',
        deleteFailure: 'Se produjo un problema al eliminar este registro',
        saveSuccess: 'Sus cambios se guardaron correctamente',
        saveFailure: 'Se produjo un problema al guardar este registro'
      },
      alert: 'Alerta'
    },
    explorer: {
      noResults: 'No se encontraron resultados. Intente ampliar su rango de tiempo o ajustar los filtros existentes para incluir más resultados.',
      confirmation: {
        updateTitle: 'Confirmar actualización',
        deleteTitle: 'Confirmar eliminación',
        bulkUpdateConfrimation: 'Está a punto de realizar los siguientes cambios en más de un elemento',
        deleteConfirmation: '¿Está seguro de que desea eliminar {{count}} registro(s)? Una vez que se aplique, esta eliminación no se puede revertir.',
        field: 'Campo',
        value: 'Valor',
        recordCountAffected: 'Cantidad de elementos'
      },
      filters: {
        timeRange: 'Rango de tiempo',
        reset: 'Restablecer filtros',
        customDateRange: 'Rango de fechas personalizado',
        customStartDate: 'Fecha de inicio',
        customEndDate: 'Fecha de finalización',
        customDateErrorStartAfterEnd: 'La fecha y la hora de inicio no pueden ser iguales o posteriores a la fecha de finalización'
      },
      inspector: {
        overview: 'Descripción general'
      },
      footer: 'Mostrando {{count}} de {{total}} elementos'
    },
    remediationTasks: {
      loading: 'Cargando tareas',
      addNewTask: 'Agregar tarea nueva',
      noTasks: 'No hay tareas para {{incidentId}}',
      openFor: 'Abierta',
      newTaskFor: 'Tarea nueva para',
      delete: 'Eliminar tarea',
      noAccess: 'No tiene permisos para ver tareas',
      actions: {
        actionMessages: {
          deleteWarning: 'La eliminación de una tarea de NetWitness no la elimina de otros sistemas. Tenga en cuenta que será su responsabilidad ' +
          'eliminar la tarea de cualquier otro sistema que corresponda.'
        }
      },
      filters: {
        taskId: 'ID de tarea',
        idFilterPlaceholder: 'por ejemplo, REM-123',
        idFilterError: 'El ID debe coincidir con el formato: REM-###'
      },
      list: {
        priority: 'Prioridad',
        select: 'Seleccionar',
        id: 'ID',
        name: 'Nombre',
        createdDate: 'Creado',
        status: 'Estado',
        assignee: 'Usuario asignado',
        noResultsMessage: 'No se encontraron tareas coincidentes',
        incidentId: 'ID del incidente',
        targetQueue: 'Línea de espera de destino',
        remediationType: 'Tipo',
        escalated: 'Elevado',
        lastUpdated: 'Última actualización',
        description: 'Descripción',
        createdBy: 'Creado por'
      },
      type: {
        QUARANTINE_HOST: 'Poner host en cuarentena',
        QUARANTINE_NETORK_DEVICE: 'Poner dispositivo de red en cuarentena',
        BLOCK_IP_PORT: 'Bloquear IP/puerto',
        BLOCK_EXTERNAL_ACCESS_TO_DMZ: 'Bloquear acceso externo a DMZ',
        BLOCK_VPN_ACCESS: 'Bloquear acceso a VPN',
        REIMAGE_HOST: 'Volver a crear la imagen del host',
        UPDATE_FIREWALL_POLICY: 'Actualizar política de firewall',
        UPDATE_IDS_IPS_POLICY: 'Actualizar política de IDS/IPS',
        UPDATE_WEB_PROXY_POLICY: 'Actualizar política de proxy web',
        UPDATE_ACCESS_POLICY: 'Actualizar política de acceso',
        UPDATE_VPN_POLICY: 'Actualizar política de VPN',
        CUSTOM: 'Personalizado',
        MITIGATE_RISK: 'Moderar riesgo',
        MITIGATE_COMPLIANCE_VIOLATION: 'Moderar infracción de cumplimiento de normas',
        MITIGATE_VULNERABILITY_THREAT: 'Moderar vulnerabilidad/amenaza',
        UPDATE_CORPORATE_BUSINESS_POLICY: 'Actualizar política corporativa/de negocios',
        NOTIFY_BC_DR_TEAM: 'Notificar a equipo de BC/DR',
        UPDATE_RULES: 'Actualizar reglas',
        UPDATE_FEEDS: 'Actualizar feeds'
      },
      targetQueue: {
        OPERATIONS: 'Operaciones',
        GRC: 'GRC',
        CONTENT_IMPROVEMENT: 'Mejora de contenido'
      },
      noDescription: 'No hay ninguna descripción para esta tarea'
    },
    incidents: {
      incidentName: 'Nombre del incidente',
      actions: {
        addEntryLabel: 'Agregar entrada',
        confirmUpdateTitle: 'Confirmar actualización',
        changeAssignee: 'Cambiar usuario asignado',
        changePriority: 'Cambiar prioridad',
        changeStatus: 'Cambiar estado',
        addJournalEntry: 'Agregar entrada del registro',
        actionMessages: {
          deleteWarning: 'Advertencia: Está a punto de eliminar uno o más incidentes que pueden tener tareas y que se pueden haber elevado. ' +
          'La eliminación de un incidente de NetWitness no lo elimina de otros sistemas. Tenga en cuenta que será su responsabilidad ' +
          'eliminar el incidente y sus tareas de cualquier otro sistema que corresponda.',
          addJournalEntrySuccess: 'Agregó una entrada del registro al incidente {{incidentId}}',
          addJournalEntryFailure: 'Se produjo un problema al agregar una entrada del registro al incidente {{incidentId}}',
          incidentCreated: 'Creó correctamente el incidente {{incidentId}} a partir de las alertas seleccionadas. La prioridad del incidente se configuró de manera predeterminada en BAJA.',
          incidentCreationFailed: 'Se produjo un problema al crear un incidente a partir de las alertas seleccionadas',
          createIncidentInstruction: 'Se creará un incidente a partir de las {{alertCount}} alerta(s) seleccionada(s). Proporcione un nombre para el incidente.',
          addAlertToIncidentSucceeded: 'Agregó correctamente las alertas seleccionadas a {{incidentId}}.',
          addAlertToIncidentFailed: 'Se produjo un problema al agregar las alertas seleccionadas a este incidente'
        },
        deselectAll: 'Deseleccionar todo'
      },
      filters: {
        timeRange: 'Rango de tiempo',
        incidentId: 'ID del incidente',
        idFilterPlaceholder: 'por ejemplo, INC-123',
        idFilterError: 'El ID debe coincidir con el formato: INC-###',
        reset: 'Restablecer filtros',
        customDateRange: 'Rango de fechas personalizado',
        customStartDate: 'Fecha de inicio',
        customEndDate: 'Fecha de finalización',
        customDateErrorStartAfterEnd: 'La fecha y la hora de inicio no pueden ser iguales o posteriores a la fecha de finalización',
        showOnlyUnassigned: 'Mostrar solo los incidentes sin asignar'
      },
      selectionCount: '{{selectionCount}} seleccionado(s)',
      label: 'Incidentes',
      list: {
        select: 'Seleccionar',
        id: 'ID',
        name: 'Nombre',
        createdDate: 'Creado',
        status: 'Estado',
        priority: 'Prioridad',
        riskScore: 'Puntaje de riesgo',
        assignee: 'Usuario asignado',
        alertCount: 'Alertas',
        sources: 'Origen',
        noResultsMessage: 'No se encontraron incidentes coincidentes'
      },
      footer: 'Mostrando {{count}} de {{total}} incidentes'
    },
    alerts: {
      createIncident: 'Crear incidente',
      addToIncident: 'Agregar a incidente',
      incidentSearch: {
        searchInputLabel: 'Buscar incidentes abiertos',
        searchInputPlaceholder: 'Buscar por ID de incidente (por ejemplo, INC-123) o nombre de incidente',
        noResults: 'No se encontraron incidentes abiertos',
        noQuery: 'Use el cuadro de búsqueda anterior para buscar incidentes abiertos por nombre o ID. La búsqueda debe tener al menos (3) caracteres.',
        error: 'Se produjo un problema al buscar incidentes'
      },
      actions: {
        actionMessages: {
          deleteWarning: 'Advertencia: Está a punto de eliminar una o más alertas que pueden estar asociadas con incidentes. ' +
          'Tenga en cuenta que cualquier incidente asociado se actualizará o se eliminará según corresponda.'
        }
      },
      list: {
        receivedTime: 'Creado',
        severity: 'Gravedad',
        numEvents: 'Cantidad de eventos',
        id: 'ID',
        name: 'Nombre',
        status: 'Estado',
        source: 'Origen',
        incidentId: 'ID del incidente',
        partOfIncident: 'Parte de incidente',
        type: 'Tipo',
        hostSummary: 'Resumen de host',
        userSummary: 'Resumen del usuario'
      },
      notAssociatedWithIncident: '(Ninguno)',
      originalAlert: 'Alerta cruda',
      originalAlertLoading: 'Cargando alerta cruda',
      originalAlertError: 'Se produjo un problema al cargar la alerta cruda',
      alertNames: 'Nombres de alerta'
    },
    alert: {
      status: {
        GROUPED_IN_INCIDENT: 'Agrupadas en incidente',
        NORMALIZED: 'Normalizado'
      },
      type: {
        Correlation: 'Correlación',
        Log: 'Registro',
        Network: 'Red',
        'Instant IOC': 'IOC instantáneo',
        'Web Threat Detection Incident': 'Incidente de Web Threat Detection',
        'File Share': 'Recurso compartido de archivos',
        'Manual Upload': 'Carga manual',
        'On Demand': 'Según demanda',
        Resubmit: 'Volver a enviar',
        Unknown: 'Desconocido'
      },
      source: {
        ECAT: 'Terminal',
        'Event Stream Analysis': 'Event Stream Analysis',
        'Event Streaming Analytics': 'Event Stream Analysis',
        'Security Analytics Investigator': 'Security Analytics Investigator',
        'Web Threat Detection': 'Web Threat Detection',
        'Malware Analysis': 'Malware Analysis',
        'Reporting Engine': 'Reporting Engine',
        'NetWitness Investigate': 'NetWitness Investigate'
      },
      backToAlerts: 'Volver a alertas'
    },
    incident: {
      created: 'Creado',
      status: 'Estado',
      priority: 'Prioridad',
      riskScore: 'Puntaje de riesgo',
      assignee: 'Usuario asignado',
      alertCount: 'Indicadores',
      eventCount: 'Evento(s)',
      catalystCount: 'Catalizadores',
      sealed: 'Sellado',
      sealsAt: 'Se sella a las',
      sources: 'Orígenes',
      categories: 'Categorías',
      backToIncidents: 'Volver a incidentes',
      overview: 'Descripción general',
      indicators: 'Indicadores',
      indicatorsCutoff: 'Mostrando {{limit}} de {{expected}} indicadores',
      events: 'Eventos',
      loadingEvents: 'Cargando eventos…',
      view: {
        graph: 'Ver: Gráfico',
        datasheet: 'Ver: Hoja de datos'
      },
      journalTasksRelated: 'Registro, tareas y relacionados',
      search: {
        tab: 'Relacionado',
        title: 'Indicadores relacionados',
        subtext: 'Ingrese un valor a continuación y haga clic en el botón Buscar para buscar otros indicadores relacionados con ese valor.',
        partOfThisIncident: 'Parte de este incidente',
        types: {
          IP: 'IP',
          MAC_ADDRESS: 'MAC',
          HOST: 'Host',
          DOMAIN: 'Dominio',
          FILE_NAME: 'Nombre de archivo',
          FILE_HASH: 'Hash',
          USER: 'Usuario',
          label: 'Buscar'
        },
        text: {
          label: 'Valor',
          placeholders: {
            IP: 'Ingrese una dirección IP',
            MAC_ADDRESS: 'Ingrese una dirección MAC',
            HOST: 'Introducir un hostname',
            DOMAIN: 'Ingrese un nombre de dominio',
            FILE_NAME: 'Ingrese un nombre de archivo',
            FILE_HASH: 'Ingrese un hash de archivo',
            USER: 'Ingrese un nombre de usuario'
          }
        },
        timeframe: {
          label: 'Cuándo'
        },
        devices: {
          source: 'Origen',
          destination: 'Destino',
          detector: 'Detector',
          domain: 'Dominio',
          label: 'Buscar en'
        },
        results: {
          title: 'Indicadores para',
          openInNewWindow: 'Abrir en una nueva ventana'
        },
        actions: {
          search: 'Buscar',
          cancel: 'Cancelar',
          addToIncident: 'Agregar a incidente',
          addingAlert: 'Agregando alerta a incidente',
          unableToAddAlert: 'No se puede agregar la alerta al incidente.',
          pleaseTryAgain: 'Vuelva a intentarlo.'
        }
      }
    },
    storyline: {
      loading: 'Cargando el argumento del incidente',
      error: 'No se puede cargar el argumento del incidente',
      catalystIndicator: 'Indicador de catalizador',
      relatedIndicator: 'Indicador relacionado',
      source: 'Origen',
      partOfIncident: 'Parte de incidente',
      relatedBy: 'Relacionado con catalizador por',
      event: 'evento',
      events: 'eventos'
    },
    details: {
      loading: 'Cargando detalles del incidente',
      error: 'No se pueden cargar detalles del incidente'
    },
    journal: {
      newEntry: 'Nueva entrada de diario',
      title: 'Registro',
      close: 'Cerrar',
      milestone: 'Punto de control',
      loading: 'Cargando entradas del registro',
      noEntries: 'No hay entradas del registro para {{incidentId}}',
      delete: 'Eliminar entrada',
      deleteConfirmation: '¿Está seguro de que desea eliminar esta entrada del registro? No se puede revertir esta acción.',
      noAccess: 'No tiene permisos para ver entradas del registro'
    },
    milestones: {
      title: 'Puntos de control',
      RECONNAISSANCE: 'Reconocimiento',
      DELIVERY: 'Distribución',
      EXPLOITATION: 'Explotación',
      INSTALLATION: 'Instalación',
      COMMAND_AND_CONTROL: 'Comando y control',
      ACTION_ON_OBJECTIVE: 'Acción en objetivo',
      CONTAINMENT: 'Contención',
      ERADICATION: 'Erradicación',
      CLOSURE: 'Cierre'
    },
    eventDetails: {
      title: 'Detalles de eventos',
      events: 'eventos',
      in: 'en',
      indicators: 'indicadores',
      type: {
        'Instant IOC': 'IOC instantáneo',
        'Log': 'Registro',
        'Network': 'Red',
        'Correlation': 'Correlación',
        'Web Threat Detection': 'Web Threat Detection',
        'Web Threat Detection Incident': 'Incidente de Web Threat Detection',
        'Unknown': 'Evento',
        'File Share': 'Recurso compartido de archivos',
        'Manual Upload': 'Carga manual',
        'On Demand': 'Según demanda',
        Resubmit: 'Volver a enviar'
      },
      backToTable: 'Volver a tabla',
      labels: {
        timestamp: 'Registro de fecha y hora',
        type: 'Tipo',
        description: 'Descripción',
        source: 'Origen',
        destination: 'Destino',
        domain: 'Dominio/host',
        detector: 'Detector',
        device: 'Dispositivo',
        ip_address: 'Dirección IP',
        mac_address: 'Dirección MAC',
        dns_hostname: 'Host',
        dns_domain: 'Dominio',
        netbios_name: 'Nombre NetBIOS',
        asset_type: 'Tipo de recurso',
        business_unit: 'Unidad de negocios',
        facility: 'Funcionalidad',
        criticality: 'Criticidad',
        compliance_rating: 'Compliance_rating',
        malicious: 'Malicioso',
        site_categorization: 'Categorización del sitio',
        geolocation: 'Ubicación geográfica',
        city: 'Ciudad',
        country: 'País',
        longitude: 'Longitud',
        latitude: 'Latitud',
        organization: 'Organización',
        device_class: 'Clase de dispositivo',
        product_name: 'Nombre del producto',
        port: 'Puerto',
        user: 'Usuario',
        username: 'Nombre de usuario',
        ad_username: 'Nombre de usuario de Active Directory',
        ad_domain: 'Dominio de Active Directory',
        email_address: 'Dirección de correo electrónico',
        os: 'Sistema operativo',
        size: 'Tamaño',
        data: 'Datos',
        filename: 'Nombre de archivo',
        hash: 'Hash',
        av_hit: 'Acierto de antivirus',
        extension: 'Extensión',
        mime_type: 'Tipo MIME',
        original_path: 'Ruta original',
        av_aliases: 'Alias de antivirus',
        networkScore: 'Puntaje de red',
        communityScore: 'Puntaje de la comunidad',
        staticScore: 'Puntaje estático',
        sandboxScore: 'Puntaje de Sandbox',
        opswat_result: 'Resultado de OPSWAT',
        yara_result: 'Resultado de YARA',
        bit9_status: 'Estado de Bit9',
        module_signature: 'Firma de módulo',
        related_links: 'Vínculos relacionados',
        url: 'URL',
        ecat_agent_id: 'ID de agente de NWE',
        ldap_ou: 'OU de LDAP',
        last_scanned: 'Último escaneo',
        enrichment: 'Enriquecimiento',
        enrichmentSections: {
          domain_registration: 'Registro de dominio',
          command_control_risk: 'Comando y control',
          beaconing_behavior: 'Beacon',
          domain_age: 'Antigüedad del dominio',
          expiring_domain: 'Dominio por vencer',
          rare_domain: 'Dominio poco común',
          no_referers: 'Orígenes de referencia',
          rare_user_agent: 'Agente de usuario poco común'
        },
        registrar_name: 'Registrador del dominio',
        registrant_organization: 'Organización del inscrito',
        registrant_name: 'Nombre del inscrito',
        registrant_email: 'Correo electrónico del inscrito',
        registrant_telephone: 'Teléfono del inscrito',
        registrant_street1: 'Dirección de calle del inscrito',
        registrant_postal_code: 'Código postal del inscrito',
        registrant_city: 'Ciudad del inscrito',
        registrant_state: 'Estado del inscrito',
        registrant_country: 'País del inscrito',
        whois_created_dateNetWitness: 'Fecha de registro',
        whois_updated_dateNetWitness: 'Fecha de actualización',
        whois_expires_dateNetWitness: 'Fecha de vencimiento',
        whois_age_scoreNetWitness: 'Puntaje de antigüedad de registro de dominio',
        whois_validity_scoreNetWitness: 'Puntaje de dominio por vencer',
        whois_estimated_domain_age_daysNetWitness: 'Antigüedad de registro de dominio (en días)',
        whois_estimated_domain_validity_daysNetWitness: 'Tiempo hasta el vencimiento (en días)',
        command_control_aggregate: 'Puntaje de riesgo de comando y control',
        command_control_confidence: 'Confianza',
        weighted_c2_referer_score: 'Contribución de puntaje de dominio poco común (esta red)',
        weighted_c2_referer_ratio_score: 'Contribución de puntaje sin remitente de dominio',
        weighted_c2_ua_ratio_score: 'Contribución de puntaje de agente de usuario poco común',
        weighted_c2_whois_age_score: 'Contribución de puntaje de antigüedad de registro de dominio',
        weighted_c2_whois_validity_score: 'Contribución de puntaje de dominio por vencer',
        smooth_score: 'Puntaje',
        beaconing_period: 'Período',
        newdomain_score: 'Puntaje de antigüedad del dominio (esta red)',
        newdomain_age: 'Antigüedad del dominio (esta red)',
        referer_score: 'Puntaje poco común',
        referer_cardinality: 'Cardinalidad poco común',
        referer_num_events: 'Eventos poco comunes',
        referer_ratio: 'Relación poco común',
        referer_ratio_score: 'Puntaje de relación poco común',
        referer_cond_cardinality: 'Cardinalidad condicional poco común',
        ua_num_events: 'Apariciones durante la semana pasada',
        ua_ratio: 'Porcentaje de direcciones IP con agente de usuario poco común',
        ua_ratio_score: 'Puntaje de agente de usuario poco común',
        ua_cond_cardinality: 'Direcciones IP con agente de usuario poco común'
      },
      periodValue: {
        hours: 'hora(s)',
        minutes: 'minuto(s)',
        seconds: 'segundo(s)'
      }
    },
    eventsTable: {
      time: 'Hora',
      type: 'Tipo',
      sourceDomain: 'Dominio de origen',
      destinationDomain: 'Dominio de destino',
      sourceHost: 'Host de origen',
      destinationHost: 'Host de destino',
      sourceIP: 'Dirección IP de origen',
      destinationIP: 'Dirección IP de destino',
      detectorIP: 'IP de detector',
      sourcePort: 'Puerto de origen',
      destinationPort: 'Puerto de destino',
      sourceMAC: 'MAC de origen',
      destinationMAC: 'MAC de destino',
      sourceUser: 'Usuario de origen',
      destinationUser: 'Usuario de destino',
      fileName: 'Nombre de archivo',
      fileHash: 'Hash de archivo',
      indicator: 'Indicador'
    },
    entity: {
      legend: {
        user: 'usuario(s)',
        host: 'host(s)',
        ip: 'IP',
        domain: 'dominios',
        mac_address: 'MAC',
        file_name: 'archivo(s)',
        file_hash: 'hashes',
        selection: {
          storyPoint: 'en {{count}} indicador(es) seleccionado(s)',
          event: 'en {{count}} evento(s) seleccionado(s)'
        },
        selectionNotShown: 'Los nodos seleccionados no se pudieron mostrar debido a límites de tamaño.',
        hasExceededNodeLimit: 'Mostrando solo los primeros {{limit}} nodos.',
        showAll: 'Mostrar todos los datos'
      }
    },
    enrichment: {
      uniformTimeIntervals: 'Los intervalos de tiempo entre los eventos de comunicación son muy uniformes.',
      newDomainToEnvironment: 'El dominio es relativamente nuevo para el ambiente.',
      rareDomainInEnvironment: 'El dominio es poco común en este ambiente.',
      newDomainRegistration: 'El dominio es relativamente nuevo en función de la fecha de registro:',
      domainRegistrationExpires: 'El registro del dominio vencerá relativamente pronto:',
      rareUserAgent: 'Un alto porcentaje de hosts que se conectan al dominio están usando un agente poco común o no de usuario.',
      noReferers: 'Un alto porcentaje de hosts que se conectan al dominio no utilizan orígenes de referencia.',
      highNumberServersAccessed: 'Cantidad anormalmente alta de servidores a los que accedió hoy.',
      highNumberNewServersAccessed: 'Cantidad anormalmente alta de servidores nuevos a los que accedió hoy.',
      highNumberNewDevicesAccessed: 'Esta semana accedió una cantidad anormalmente alta de dispositivos nuevos.',
      highNumberFailedLogins: 'Hoy hubo una cantidad anormalmente alta de servidores con inicios de sesión fallidos.',
      passTheHash: 'Ataque “pass the hash” potencial que indicó un dispositivo nuevo seguido de un servidor nuevo.',
      rareLogonType: 'Se accedió mediante un tipo de inicio de sesión de Windows que se usó rara vez en el pasado.',
      authFromRareDevice: 'Se autenticó desde un dispositivo poco común.',
      authFromRareLocation: 'Se accedió desde una ubicación poco común.',
      authFromRareServiceProvider: 'Se accedió mediante un proveedor de servicios poco común.',
      authFromNewServiceProvider: 'Se accedió mediante un proveedor de servicios nuevo.',
      highNumberVPNFailedLogins: 'Gran cantidad de errores al iniciar sesión en VPN.',
      daysAgo: 'Hace {{days}} día(s)',
      days: '{{days}} día(s)',
      domainIsWhitelisted: 'El dominio está en la lista blanca.',
      domainIsNotWhitelisted: 'El dominio no está en la lista blanca.'
    },
    sources: {
      'C2-Packet': 'Analítica del comportamiento de entidad de usuario',
      'C2-Log': 'Analítica del comportamiento de entidad de usuario',
      'UBA-WinAuth': 'Analítica del comportamiento de entidad de usuario',
      UbaCisco: 'Analítica del comportamiento de entidad de usuario',
      ESA: 'Event Stream Analytics',
      'Event-Stream-Analysis': 'Event Stream Analytics',
      RE: 'Reporting Engine',
      'Reporting-Engine': 'Reporting Engine',
      ModuleIOC: 'Terminal',
      ECAT: 'Terminal',
      generic: 'NetWitness'
    },
    status: {
      NEW: 'Nuevo',
      ASSIGNED: 'Asignado',
      IN_PROGRESS: 'En curso',
      REMEDIATION_REQUESTED: 'Tarea solicitada',
      REMEDIATION_COMPLETE: 'Tarea completa',
      CLOSED: 'Cerrado',
      CLOSED_FALSE_POSITIVE: 'Cerrado: falso positivo',
      REMEDIATED: 'Corregido',
      RISK_ACCEPTED: 'Riesgo aceptado',
      NOT_APPLICABLE: 'No aplicable'
    },
    priority: {
      LOW: 'Baja',
      MEDIUM: 'Media',
      HIGH: 'Alta',
      CRITICAL: 'Crítica'
    },
    assignee: {
      none: '(Sin asignar)'
    }
  },
  context: {
    noData: 'No hay ningún contexto coincidente disponible',
    noResults: '(Sin resultados)',
    notConfigured: '(Sin configuración)',
    title: 'Contexto para',
    lastUpdated: 'Última actualización:',
    timeWindow: 'Ventana de tiempo: ',
    iiocScore: 'Puntaje de IIOC',
    IP: 'IP',
    USER: 'Usuario',
    MAC_ADDRESS: 'Dirección MAC',
    HOST: 'Host',
    FILE_NAME: 'Nombre de archivo',
    FILE_HASH: 'Hash de archivo',
    DOMAIN: 'Dominio',
    noValues: 'Orígenes de contexto sin valores: ',
    dsNotConfigured: 'Orígenes de contexto no configurados: ',
    marketingText: ' no es un origen de datos configurado actualmente en Context Hub. Póngase en contacto con el administrador para habilitar esta función. Context Hub centraliza los orígenes de datos de terminal, alertas, incidentes, listas y muchos más orígenes según demanda. Para obtener más información, haga clic en Ayuda.',
    lcMarketingText: 'Live Connect recopila, analiza y evalúa datos de inteligencia de amenazas, como direcciones IP, dominios y hashes de archivo, recopilados de diversos orígenes. Live Connect no es un origen de datos predeterminado en Context Hub. Debe habilitarlo manualmente. Para obtener más información, haga clic en Ayuda.',
    timeUnit: {
      allData: 'TODOS LOS DATOS',
      HOUR: 'HORA',
      HOURS: 'HORAS',
      MINUTE: 'MINUTO',
      MINUTES: 'MINUTOS',
      DAY: 'DÍA',
      DAYS: 'DÍAS',
      MONTH: 'MES',
      MONTHS: 'MESES',
      YEAR: 'AÑO',
      YEARS: 'AÑOS',
      WEEK: 'SEMANA',
      WEEKS: 'SEMANAS'
    },
    marketingDSType: {
      Users: 'Active Directory',
      Alerts: 'Respond (alertas)',
      Incidents: 'Respond (incidentes)',
      Machines: 'Endpoint (máquinas)',
      Modules: 'Endpoint (módulos)',
      IOC: 'Endpoint (IOC)',
      Archer: 'Archer',
      LIST: 'Lista'
    },
    header: {
      title: {
        archer: 'Archer',
        users: 'Active Directory',
        alerts: 'Alertas',
        incidents: 'Incidentes',
        lIST: 'Listas',
        endpoint: 'NetWitness Endpoint',
        liveConnectIp: 'Live Connect',
        liveConnectFile: 'Live Connect',
        liveConnectDomain: 'Live Connect'
      },
      archer: 'Archer',
      overview: 'descripción general',
      iioc: 'IIOC',
      users: 'Usuarios',
      categoryTags: 'etiquetas de categoría',
      modules: 'Módulos',
      incidents: 'Incidentes',
      alerts: 'Alertas',
      files: 'Archivos',
      lists: 'Listas',
      feeds: 'Feeds',
      endpoint: 'Terminal',
      liveConnect: 'Live Connect',
      unsafe: 'Inseguro',
      closeButton: {
        title: 'Cerrar'
      },
      help: {
        title: 'Ayuda'
      }
    },
    toolbar: {
      investigate: 'Investigar',
      endpoint: 'NetWitness Endpoint',
      googleLookup: 'Búsqueda de Google',
      virusTotal: 'Búsqueda de VirusTotal',
      addToList: 'Agregar a lista'
    },
    hostSummary: {
      title: 'Terminal',
      riskScore: 'Puntaje de riesgo',
      modulesCount: 'Cantidad de módulos',
      iioc0: 'Iioc 0',
      iioc1: 'Iioc 1',
      lastUpdated: 'Última actualización',
      adminStatus: 'Estado administrativo',
      lastLogin: 'Último inicio de sesión',
      macAddress: 'Dirección MAC',
      operatingSystem: 'Sistema operativo',
      machineStatus: 'Estado de la máquina',
      ipAddress: 'IPAddress',
      endpoint: 'Se aplica a los hosts con agentes de Endpoint 4.X instalados. Instale el cliente grueso de NetWitness Endpoint.'
    },
    addToList: {
      title: 'Agregar/eliminar de la lista',
      create: 'Crear lista nueva',
      metaValue: 'Valor de metadatos',
      newList: 'Crear lista nueva',
      tabAll: 'Todo',
      tabSelected: 'Seleccionado',
      tabUnselected: 'No seleccionado',
      cancel: 'Cancelar',
      save: 'Guardar',
      name: 'Nombre de lista',
      listTitle: 'Lista',
      descriptionTitle: 'Descripción',
      filter: 'Filtrar resultados',
      listName: 'Ingresar nombre de lista',
      headerMessage: 'Haga clic en Guardar para actualizar las listas. Actualice la página para ver las actualizaciones.'
    },
    ADdata: {
      title: 'Información del usuario',
      employeeID: 'ID de empleado',
      department: 'departamento',
      location: 'ubicación',
      manager: 'administrador',
      groups: 'grupos',
      company: 'empresa',
      email: 'Correo electrónico',
      phone: 'teléfono',
      jobTitle: 'cargo',
      lastLogon: 'último inicio de sesión',
      lastLogonTimeStamp: 'último registro de fecha y hora de inicio de sesión',
      adUserID: 'ID de usuario de AD',
      distinguishedName: 'Nombre distinguido',
      displayName: 'Nombre para mostrar'
    },
    archer: {
      title: 'Archer',
      criticalityRating: 'Clasificación de criticidad',
      riskRating: 'Clasificación de riesgo',
      deviceName: 'Nombre del dispositivo',
      hostName: 'Nombre del host',
      deviceId: 'ID de dispositivo',
      deviceType: 'Tipo de dispositivo',
      deviceOwner: 'Propietario de dispositivos',
      deviceOwnerTitle: 'Cargo del propietario del dispositivo',
      businessUnit: 'Unidad de negocios',
      facility: 'Funcionalidad',
      ipAddress: 'Dirección IP interna'
    },
    modules: {
      title: 'Principales módulos sospechosos',
      iiocScore: 'Puntaje de IIOC',
      moduleName: 'Nombre de módulo',
      analyticsScore: 'Puntaje de analítica',
      machineCount: 'Conteo de máquinas',
      signature: 'Firma'
    },
    iiocs: {
      title: 'Niveles de IOC de la máquina',
      lastExecuted: 'LastExecuted',
      description: 'Descripción',
      iOCLevel: 'Nivel de IOC',
      header: ''
    },
    incident: {
      title: 'Incidentes',
      averageAlertRiskScore: 'Puntaje de riesgo',
      _id: 'ID',
      name: 'Nombre',
      created: 'Creado',
      status: 'Estado',
      assignee: 'USUARIO ASIGNADO',
      alertCount: 'Alertas',
      priority: 'Prioridad',
      header: ''
    },
    alerts: {
      title: 'Alertas',
      risk_score: 'Gravedad',
      source: 'Origen',
      name: 'Nombre',
      numEvents: 'Cantidad de eventos',
      severity: 'Gravedad',
      created: 'Creado',
      id: 'ID del incidente',
      timestamp: 'registro de fecha y hora',
      header: ''
    },
    list: {
      title: 'Lista',
      createdByUser: 'Autor',
      createdTimeStamp: 'Creado',
      lastModifiedTimeStamp: 'Actualizado',
      dataSourceDescription: 'Descripción',
      dataSourceName: 'Nombre',
      data: 'Datos'
    },
    lc: {
      reviewStatus: 'Estado de revisión',
      status: 'Estado',
      notReviewed: 'SIN REVISAR',
      noFeedback1: 'Aún no hay un análisis de comentarios',
      noFeedback2: ' - Sea un miembro activo de Live Connect Threat Community y proporcione su evaluación del riesgo.',
      blankField: '-',
      modifiedDate: 'Fecha de modificación',
      reviewer: 'Revisor',
      riskConfirmation: 'Confirmación de riesgo',
      safe: 'Bóveda',
      unsafe: 'Inseguro',
      unknown: 'Desconocido',
      suspicious: 'Sospechoso',
      highRisk: 'Alto riesgo',
      high: 'Alta',
      med: 'Media',
      low: 'Baja',
      riskTags: 'Etiquetas de indicador de riesgo',
      commActivity: 'Actividad de la comunidad',
      firstSeen: 'Visto por primera vez',
      activitySS: 'Instantánea de actividad',
      communityTrend: 'Actividad de la comunidad de tendencias (últimos 30 días)',
      submitTrend: 'Actividad de envío de tendencias (últimos 30 días)',
      communityActivityDesc1: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}} %</span> de la comunidad visto <span class="rsa-context-panel__liveconnect__entity">{{value}}</span>',
      communityActivityDesc2: 'Del <span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}} %</span> visto, <span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}} %</span> de los comentarios enviados de la comunidad',
      submittedActivityDesc1: 'Del <span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}} %</span> de los comentarios enviados:',
      submittedActivityDesc2: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__high-risk">{{highrisk}} %</span> marcado como de alto riesgo',
      submittedActivityDesc3: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unsafe">{{unsafe}} %</span> marcado como inseguro',
      submittedActivityDesc4: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__suspicious">{{suspicious}} %</span> marcado como sospechoso',
      submittedActivityDesc5: '(No se muestra en el gráfico)',
      submittedActivityDesc6: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__safe">{{safe}} %</span> marcado como seguro',
      submittedActivityDesc7: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unknown">{{unknown}} %</span> marcado como desconocido',
      riskIndicators: 'Indicadores de riesgo',
      identity: 'Identidad',
      asn: 'Número de sistema autónomo (ASN)',
      prefix: 'Prefijo',
      countryCode: 'Código del país',
      countryName: 'Nombre de país',
      organization: 'Organización',
      fileDate: 'Fecha',
      fileName: 'NOMBRE DE ARCHIVO',
      fileSize: 'TAMAÑO DEL ARCHIVO',
      md5: 'MD5',
      compileTime: 'HORA DE COMPILACIÓN',
      sh1: 'SH1',
      mimeType: 'TIPO MIME',
      sh256: 'SH256',
      certificateInfo: 'Información del certificado',
      certIssuer: 'Emisor del certificado',
      certSubject: 'Asunto del certificado',
      certSerial: 'Número de serie del certificado',
      certSigAlgo: 'Algoritmo de firma',
      certThumbprint: 'Contrafirma del certificado',
      certNotValidBefore: 'Certificado no válido antes de',
      certNotValidAfter: 'Certificado no válido después de',
      whois: 'WHOIS',
      whoisCreatedDate: 'Fecha de creación',
      whoisUpdatedDate: 'Fecha de actualización',
      whoisExpiredDate: 'Fecha de vencimiento',
      whoisRegType: 'Tipo',
      whoisRegName: 'Nombre',
      whoisRegOrg: 'Organización',
      whoisRegStreet: 'Calle',
      whoisRegCity: 'ciudad',
      whoisRegState: 'Estado',
      whoisPostalCode: 'Código postal',
      whoisCountry: 'País',
      whoisPhone: 'Teléfono',
      whoisFax: 'Fax',
      whoisEmail: 'Correo electrónico',
      domain: 'Dominio',
      ipAddress: 'Dirección IP',
      errorMsg: 'No se pudieron obtener datos desde Live Connect: {{error}}',
      riskAssessment: 'Evaluación del riesgo de Live Connect',
      riskReason: 'Motivos del riesgo',
      highRiskDesc: 'Indicador considerado como de alto riesgo que requiere atención especial',
      safeRiskDesc: 'La investigación y el análisis muestran que los indicadores son recursos de confianza',
      unsafeRiskDesc: 'La investigación y el análisis muestran que el recurso no es de confianza',
      unknownRiskDesc: 'Resultados no concluyentes de la información, la investigación y el análisis disponibles',
      suspiciousRiskDesc: 'La investigación y el análisis indican actividad potencialmente amenazante',
      riskFeedback: 'Comentarios sobre la evaluación del riesgo',
      relatedFiles: 'Archivos relacionados ',
      risk: 'CLASIFICACIÓN DE RIESGO DE LC',
      importHashFunction: 'HASH DE IMPORTACIÓN DE FUNCIÓN DE API',
      compiledTime: 'FECHA DE COMPILACIÓN',
      relatedDomains: 'Dominios relacionados ',
      relatedIps: 'IP relacionadas ',
      country: 'País',
      registeredDate: 'Fecha de registro',
      expiredDate: 'Fecha de vencimiento',
      email: 'Correo electrónico del inscrito',
      asnShort: 'ASN',
      confidenceLevel: 'Nivel de confianza',
      select: 'Seleccionar...',
      feedbackSubmitted: 'Se enviaron comentarios al servidor de Live Connect.',
      feedbackSubmissionFailed: 'No se pudieron enviar comentarios al servidor de Live Connect.',
      feedbackFormInvalid: 'Seleccione la \'Confirmación de riesgo\' y el \'Nivel de confianza\'.',
      noTrendingCommunityActivity: 'No hay actividad de comunidad nueva en los últimos 30 días',
      noTrendingSubmissionActivity: 'No hay envíos nuevos en los últimos 30 días',
      skillLevel: 'Nivel de habilidad del analista',
      skillLevelPrefix: 'Nivel {{level}}',
      noRelatedData: 'No hay ninguna {{entity}} relacionada para esta entidad.',
      ips: 'IP',
      files: 'Archivos',
      domains: 'Dominios'
    },
    error: {
      error: 'Se produjo un error inesperado al intentar obtener los datos.',
      noDataSource: 'No hay ningún origen de datos configurado/habilitado.',
      dataSourcesFailed: 'No se pueden obtener datos desde los orígenes de datos configurados.',
      dataSource: 'Se produjo un error inesperado al intentar obtener los datos.',
      noData: 'No hay datos de contexto disponibles para este origen de datos.',
      listDuplicateName: 'El nombre de lista ya existe.',
      listValidName: 'Ingrese un nombre de lista válido (la longitud máxima es 255 caracteres).',
      'mongo.error': 'Se produjo un error inesperado en la base de datos.',
      'total.entries.exceed.max': 'El tamaño de la lista supera el límite de 100,000.',
      'admin.error': 'El servicio de administración no está accesible. Compruebe su conectividad al servicio.',
      'datasource.disk.usage.high': 'Poco espacio en disco. Elimine los datos no deseados para liberar espacio.',
      'context.service.timeout': 'El servicio Context Hub no está accesible. Compruebe su conectividad al servicio.',
      'get.mongo.connect.failed': 'La base de datos no está accesible. Reinténtelo después de un momento.',
      'datasource.query.not.supported': 'La búsqueda de datos de contexto no se admite para estos metadatos.',
      'transport.http.read.failed': 'Los datos de contexto no están disponibles porque el origen de datos no está accesible.',
      'transport.ad.read.failed': 'Los datos de contexto no están disponibles porque el origen de datos no está accesible.',
      'transport.init.failed': 'Se agotó el tiempo de espera de la conexión al origen de datos.',
      'transport.not.found': 'Los datos de contexto no están disponibles porque el origen de datos no está accesible.',
      'transport.create.failed': 'Los datos de contexto no están disponibles porque el origen de datos no está accesible.',
      'transport.refresh.failed': 'Los datos de contexto no están disponibles porque el origen de datos no está accesible.',
      'transport.connect.failed': 'Los datos de contexto no están disponibles porque el origen de datos no está accesible.',
      'live.connect.private.ip.unsupported': 'Live Connect solo admite direcciones IP públicas.',
      'transport.http.error': 'La búsqueda de contexto falló para este origen de datos porque devolvió un error.',
      'transport.validation.error': 'El formato de datos no es compatible con el origen de datos.',
      'transport.http.auth.failed': 'No se pudo buscar contexto en este origen de datos; la autorización falló.'
    },
    footer: {
      viewAll: 'VER todo',
      title: {
        incidents: 'Incidente(s)',
        alerts: 'Alerta(s)',
        lIST: 'Lista(s)',
        users: 'Usuario(s)',
        endpoint: 'Host',
        archer: 'Recurso'
      },
      resultCount: '(Primeros {{count}} resultados)'
    },
    tooltip: {
      contextHighlights: 'Puntos destacados de contexto',
      viewOverview: 'Ver contexto',
      actions: 'Acciones',
      investigate: 'Cambiar a Investigate',
      addToList: 'Agregar/eliminar de la lista',
      virusTotal: 'Búsqueda de VirusTotal',
      googleLookup: 'Búsqueda de Google',
      ecat: 'Cambiar a cliente grueso de Endpoint',
      events: 'Cambiar a Events',
      contextUnavailable: 'No hay datos de contexto disponibles en este momento.',
      dataSourceNames: {
        Incidents: 'Incidentes',
        Alerts: 'Alertas',
        LIST: 'Listas',
        Users: 'Usuarios',
        IOC: 'IOC',
        Machines: 'Terminal',
        Modules: 'Módulos',
        'LiveConnect-Ip': 'LiveConnect',
        'LiveConnect-File': 'LiveConnect',
        'LiveConnect-Domain': 'LiveConnect'
      }
    }
  },
  preferences: {
    'investigate-events': {
      panelTitle: 'Preferencias de eventos',
      triggerTip: 'Abrir/ocultar preferencias de eventos',
      defaultEventView: 'Vista Análisis de eventos predeterminada',
      defaultLogFormat: 'Formato de registro predeterminado',
      defaultPacketFormat: 'Formato de paquete predeterminado',
      LOG: 'Descargar registro',
      CSV: 'Descargar CSV',
      XML: 'Descargar XML',
      JSON: 'Descargar JSON',
      PCAP: 'Descargar PCAP',
      PAYLOAD: 'Descargar todas las cargas útiles',
      PAYLOAD1: 'Descargar carga útil de la solicitud',
      PAYLOAD2: 'Descargar carga útil de la respuesta',
      FILE: 'Análisis de archivos',
      TEXT: 'Análisis de texto',
      PACKET: 'Análisis de paquetes',
      queryTimeFormat: 'Formato de hora para la consulta',
      DB: 'Hora de la base de datos',
      WALL: 'Hora del reloj',
      'DB-tooltip': 'Hora de la base de datos donde se almacenan los eventos',
      'WALL-tooltip': 'Hora actual con la zona horaria configurada en las preferencias del usuario',
      autoDownloadExtractedFiles: 'Descargar archivos extraídos automáticamente'
    },
    'endpoint-preferences': {
      visibleColumns: 'Columnas visibles',
      sortField: 'Campo de clasificación',
      sortOrder: 'Orden de clasificación',
      filter: 'Filtrar'
    }
  },
  packager: {
    errorMessages: {
      invalidServer: 'Introduzca una dirección IP o un hostname válidos',
      invalidPort: 'Introduzca un número de puerto válido',
      invalidName: 'Introduzca un nombre válido sin caracteres especiales',
      passwordEmptyMessage: 'Introduzca una contraseña del certificado',
      invalidPasswordString: 'Puede contener caracteres alfanuméricos o especiales y un mínimo de 3 caracteres.',
      NAME_EMPTY: 'Advertencia: El nombre de la configuración está vacío.',
      SERVERS_EMPTY: 'Advertencia: No se encontraron servidores.',
      EVENT_ID_INVALID: 'Advertencia: El ID de evento no es válido.',
      CHANNEL_EMPTY: 'Advertencia: El canal está vacío.',
      FILTER_EMPTY: 'Advertencia: El filtro está vacío.',
      FILTER_INVALID: 'Advertencia: El filtro no es válido.',
      INVALID_HOST: 'Advertencia: El host no es válido.',
      CONFIG_NAME_INVALID: 'Advertencia: El nombre de la configuración no es válido.',
      INVALID_PROTOCOL: 'Advertencia: El protocolo no es válido.',
      CHANNEL_NAME_INVALID: 'Advertencia: El nombre del canal no es válido.',
      EMPTY_CHANNELS: 'Advertencia: El nombre del canal está vacío.',
      CHANNEL_FILTER_INVALID: 'Advertencia: El filtro del canal no es válido.'
    },
    packagerTitle: 'Empaquetador',
    serviceName: 'Nombre de servicio*',
    server: 'Servidor de Endpoint*',
    port: 'Puerto HTTPS*',
    certificateValidation: 'Validación del servidor',
    certificatePassword: 'Contraseña del certificado*',
    none: 'Ninguno',
    fullChain: 'Cadena completa',
    thumbprint: 'Huella digital del certificado',
    reset: 'Restablecer',
    generateAgent: 'Generar agente',
    generateLogConfig: 'Generar solo configuración del registro',
    loadExistingLogConfig: 'Cargar configuración existente…',
    description: 'Descripción',
    title: 'Empaquetador',
    becon: 'Beacon',
    displayName: 'Nombre para mostrar*',
    upload: {
      success: 'El archivo de configuración se cargó correctamente.',
      failure: 'El archivo de configuración no se puede cargar.'
    },
    error: {
      generic: 'Se produjo un error inesperado al intentar recuperar estos datos.'
    },
    autoUninstall: 'Desinstalar automáticamente',
    forceOverwrite: 'Forzar sobrescritura',
    windowsLogCollectionCongfig: 'Configuración de la recopilación de registros de Windows',
    enableWindowsLogCollection: 'Habilitar la recopilación de registros de Windows',
    configurationName: 'Nombre de configuración*',
    primaryLogDecoder: 'Log Decoder/Log Collector primario*',
    secondaryLogDecoder: 'Log Decoder/Log Collector secundario',
    protocol: 'Protocolo',
    channels: 'Filtros del canal',
    eventId: 'ID de evento para incluir/excluir (?)',
    heartbeatLogs: 'Enviar registros de latido',
    heartbeatFrequency: 'Frecuencia de latido',
    testLog: 'Enviar registro de prueba',
    placeholder: 'Realizar una selección',
    searchPlaceholder: 'Introducir la opción del filtro',
    emptyName: 'El nombre de la configuración está vacío',
    channelFilter: 'Filtros del canal',
    specialCharacter: 'El nombre de la configuración contiene un carácter especial.',
    channel: {
      add: 'Agregar un canal nuevo',
      name: 'NOMBRE DEL CANAL *',
      filter: 'FILTRO *',
      event: 'ID DE EVENTO *',
      empty: ''
    }
  },
  investigateFiles: {
    title: 'Archivos',
    deleteTitle: 'Confirmar eliminación',
    button: {
      exportToCSV: 'Exportar a CSV',
      downloading: 'Descargando',
      save: 'Guardar',
      reset: 'Restablecer',
      cancel: 'Cancelar'
    },
    message: {
      noResultsMessage: 'No se encontraron archivos coincidentes'
    },
    errorPage: {
      serviceDown: 'El servidor de Endpoint está offline',
      serviceDownDescription: 'El servidor de Endpoint no está en ejecución o está inaccesible. Consulte al administrador para resolver este problema.'
    },
    footer: '{{count}} de {{total}} {{label}}',
    filter: {
      filter: 'Filtrar',
      filters: 'Filtros guardados',
      newFilter: 'Filtro nuevo',
      windows: 'WINDOWS',
      mac: 'MAC',
      linux: 'LINUX',
      favouriteFilters: 'Filtros favoritos',
      addMore: 'Agregar filtro',
      invalidFilterInput: 'Entrada del filtro no válida',
      invalidFilterInputLength: 'La entrada del filtro tiene más de 256 caracteres',
      invalidCharacters: 'Puede contener caracteres alfanuméricos o especiales.',
      invalidCharsAlphabetOnly: 'Los números y los caracteres especiales no se permiten',
      invalidCharsAlphaNumericOnly: 'Los caracteres especiales no se permiten',
      restrictionType: {
        moreThan: 'Mayor que',
        lessThan: 'Menor que',
        between: 'Entre',
        equals: 'Es igual a',
        contains: 'Contiene'
      },
      customFilters: {
        save: {
          description: 'Proporcione un nombre para la búsqueda que se guardará. Este nombre aparecerá en la lista del cuadro de búsqueda.',
          name: 'Nombre*',
          errorHeader: 'No se puede guardar la búsqueda',
          header: 'Guardar búsqueda',
          errorMessage: 'La búsqueda no se puede guardar. ',
          emptyMessage: 'El campo Nombre está vacío.',
          nameExistsMessage: 'Una búsqueda guardada con el mismo nombre.',
          success: 'La consulta de búsqueda se guardó correctamente.',
          filterFieldEmptyMessage: 'Uno o más de los campos de filtro recientemente agregados están vacíos. Agregue los filtros o quite los campos para guardar.',
          invalidInput: 'Introduzca un nombre válido (solo se permiten los caracteres especiales \“-\” y \“_\”).'
        },
        delete: {
          successMessage: 'La consulta se eliminó correctamente.',
          confirmMessage: '¿Está seguro de que desea eliminar la consulta seleccionada?'
        }
      }
    },
    fields: {
      panelTitle: 'Preferencias de archivos',
      triggerTip: 'Abrir/ocultar preferencias de archivos',
      id: 'ID',
      companyName: 'Nombre de la empresa',
      checksumMd5: 'MD5',
      checksumSha1: 'SHA1',
      checksumSha256: 'SHA256',
      machineOsType: 'Sistema operativo',
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
        features: 'Firma',
        signer: 'Firmante'
      },
      owner: {
        userName: 'Propietario',
        groupName: 'Grupo de propietarios'
      },
      rpm: {
        packageName: 'Paquete'
      },
      path: 'Ruta',
      entropy: 'Entropía',
      fileName: 'Nombre de archivo',
      firstFileName: 'Nombre de archivo',
      firstSeenTime: 'Hora en que se vio por primera vez',
      timeCreated: 'Creado',
      format: 'Formato',
      sectionNames: 'Nombres de sección',
      importedLibraries: 'Bibliotecas importadas',
      size: 'Tamaño'
    },
    sort: {
      fileNameDescending: 'Nombre de archivo (descendente)',
      fileNameAscending: 'Nombre de archivo (ascendente)',
      sizeAscending: 'Tamaño (ascendente)',
      sizeDescending: 'Tamaño (descendente)',
      formatAscending: 'Formato (ascendente)',
      formatDescending: 'Formato (descendente)',
      signatureAscending: 'Firma (ascendente)',
      signatureDescending: 'Firma (descendente)'
    }
  },
  investigateHosts: {
    title: 'Investigar',
    loading: 'Cargando',
    loadMore: 'Cargar más',
    deleteTitle: 'Confirmar eliminación',
    noSnapshotMessage: 'No se encontró ningún historial de escaneo.',
    common: {
      save: 'Guardar',
      enable: 'Habilitar',
      saveSuccess: 'Se guardó correctamente',
      emptyMessage: 'Sin resultados coincidentes'
    },
    errorPage: {
      serviceDown: 'El servidor de Endpoint está offline',
      serviceDownDescription: 'El servidor de Endpoint no está en ejecución o está inaccesible. Consulte al administrador para resolver este problema.'
    },
    property: {
      file: {
        companyName: 'Nombre de la empresa',
        checksumMd5: 'MD5',
        checksumSha1: 'SHA1',
        checksumSha256: 'SHA256',
        machineOsType: 'Sistema operativo',
        timeCreated: 'Creado',
        timeModified: 'Modificado',
        timeAccessed: 'Con acceso',
        createTime: 'Proceso creado',
        pid: 'PID',
        eprocess: 'EPROCESS',
        path: 'Ruta completa',
        sameDirectoryFileCounts: {
          nonExe: 'N.º de archivos no ejecutables',
          exe: 'N.º de archivos ejecutables',
          subFolder: 'N.º de carpeta',
          exeSameCompany: 'N.º de archivos ejecutables de la misma empresa'
        },
        elf: {
          classType: 'Tipo de clase',
          data: 'Datos',
          entryPoint: 'Punto de entrada',
          features: 'Funciones',
          type: 'Tipo',
          sectionNames: 'Nombres de sección',
          importedLibraries: 'Bibliotecas importadas'
        },
        pe: {
          timeStamp: 'Registro de fecha y hora',
          imageSize: 'Tamaño de la imagen',
          numberOfExportedFunctions: 'Funciones exportadas',
          numberOfNamesExported: 'Nombres exportados',
          numberOfExecuteWriteSections: 'Secciones de escritura de ejecución',
          features: 'Funciones',
          sectionNames: 'Nombres de sección',
          importedLibraries: 'Bibliotecas importadas',
          resources: {
            originalFileName: 'Nombre de archivo',
            company: 'Empresa',
            description: 'Descripción',
            version: 'Versión'
          }
        },
        macho: {
          uuid: 'UUID',
          identifier: 'Identificador',
          minOsxVersion: 'Versión de OSX',
          features: 'Funciones',
          flags: 'Marcas',
          numberOfLoadCommands: 'Comandos cargados',
          version: 'Versión',
          sectionNames: 'Nombres de sección',
          importedLibraries: 'Bibliotecas importadas'
        },
        signature: {
          timeStamp: 'Registro de fecha y hora',
          thumbprint: 'Huella',
          features: 'Funciones',
          signer: 'Firmante'
        },
        process: {
          title: 'Proceso',
          processName: 'Nombre de proceso',
          eprocess: 'EPROCESS',
          integrityLevel: 'Integridad',
          parentPath: 'Ruta principal',
          threadCount: 'Conteo de hilos de ejecución',
          owner: 'Propietario',
          sessionId: 'ID de sesión',
          createUtcTime: 'Creado',
          imageBase: 'Base de imagen',
          imageSize: 'Tamaño de la imagen'
        },
        entropy: 'Entropía',
        firstFileName: 'Nombre de archivo',
        fileName: 'Nombre de archivo',
        format: 'Formato',
        sectionNames: 'Nombres de sección',
        importedLibraries: 'Bibliotecas importadas',
        size: 'Tamaño',
        imageBase: 'Base de imagen',
        imageSize: 'Tamaño de la imagen',
        loaded: 'Cargado',
        fileProperties: {
          entropy: 'Entropía',
          size: 'Tamaño',
          format: 'Formato'
        }
      }
    },
    process: {
      title: 'Procesos',
      processName: 'Nombre de proceso',
      properties: 'Propiedades de proceso',
      pid: 'PID',
      parentId: 'PPID',
      owner: 'Propietario',
      hostCount: 'Conteo de hosts',
      creationTime: 'Hora de creación',
      hashlookup: 'Búsqueda de hash',
      signature: 'Firma',
      path: 'Ruta',
      launchArguments: 'Argumentos de lanzamiento',
      message: {
        noResultsMessage: 'No se encontró información del proceso'
      },
      dll: {
        dllName: 'Nombre de DLL',
        filePath: 'Ruta del archivo',
        title: 'Bibliotecas cargadas',
        message: {
          noResultsMessage: 'No se encontró información de la biblioteca cargada'
        },
        note: {
          windows: 'Nota: Muestra las bibliotecas que no firma Microsoft',
          mac: 'Nota: Muestra las bibliotecas que no firma Apple.'
        }
      }
    },
    tabs: {
      overview: 'Descripción general',
      process: 'Proceso',
      autoruns: 'Ejecuciones automáticas',
      files: 'Archivos',
      drivers: 'Motivadores',
      systemInformation: 'Información del sistema',
      services: 'Servicios',
      tasks: 'Tareas',
      hostFileEntries: 'Entradas del archivo host',
      mountedPaths: 'Rutas montadas',
      networkShares: 'Recursos compartidos de red',
      bashHistories: 'Historial de Bash',
      libraries: 'Bibliotecas',
      explore: 'Explorar',
      securityProducts: 'Productos de seguridad',
      windowsPatches: 'Parches de Windows'
    },
    systemInformation: {
      ipAddress: 'Dirección IP',
      dnsName: 'Nombre de DNS',
      fileSystem: 'Sistema de archivos',
      path: 'Ruta',
      remotePath: 'Ruta remota',
      options: 'Opciones',
      name: 'Nombre',
      description: 'Descripción',
      permissions: 'Permisos',
      type: 'Tipo',
      maxUses: 'Máx. de usuarios',
      currentUses: 'Usuarios actuales',
      userName: 'Nombre de usuario',
      command: 'Comando',
      commandNote: 'Nota: Los comandos más recientes están en la parte superior',
      filterUser: 'Tipo para filtrar el usuario',
      filterBy: 'Filtrar por usuario',
      patches: 'Parches',
      securityProducts: {
        type: 'Tipo',
        instance: 'Instancia',
        displayName: 'Nombre para mostrar',
        companyName: 'Nombre de la empresa',
        version: 'Versión',
        features: 'Funciones'
      }
    },
    hosts: {
      title: 'Hosts',
      search: 'Filtrar',
      button: {
        addMore: 'Agregar filtro',
        loadMore: 'Cargar más',
        exportCSV: 'Exportar a CSV',
        export: 'Exportar a JSON',
        exportTooltip: 'Exporta todas las categorías de datos de escaneo para el host.',
        downloading: 'Descargando',
        initiateScan: 'Iniciar escaneo',
        cancelScan: 'Detener exploración',
        delete: 'Eliminar',
        cancel: 'Cancelar',
        save: 'Guardar',
        saveAs: 'Guardar como...',
        clear: 'Borrar',
        search: 'Buscar',
        ok: 'Aceptar',
        moreActions: 'Más acciones',
        explore: 'Explorar',
        gearIcon: 'Haga clic aquí para administrar las columnas',
        overview: 'Mostrar/ocultar el panel Descripción general',
        settings: 'Ajustes de configuración',
        meta: 'Mostrar/ocultar metadatos',
        close: 'Cerrar detalles de host',
        shrink: 'Contraer vista',
        update: 'Actualizar',
        reset: 'Restablecer'
      },
      autoruns: {
        services: {
          initd: 'INIT.D',
          systemd: 'SYSTEM.D'
        }
      },
      ranas: {
        ranas: 'Ejecutar como',
        categories: {
          Process: 'Proceso',
          Libraries: 'Biblioteca',
          Autorun: 'Ejecución automática',
          Service: 'Servicio',
          Task: 'Tarea',
          Driver: 'Controlador',
          Thread: 'Hilo de ejecución'
        }
      },
      explore: {
        input: {
          placeholder: 'Buscar por nombre de archivo, ruta o hash'
        },
        noResultsFound: 'No se encontraron resultados.',
        fileName: 'Nombre del archivo: ',
        path: 'Ruta: ',
        hash: 'Hash: ',
        search: {
          minimumtext: {
            required: 'Para el nombre de archivo o la ruta, introduzca un mínimo de 3 caracteres. Para hash, introduzca la cadena de hash SHA-256 completa'
          }
        }
      },
      footerLabel: {
        autoruns: {
          autoruns: 'ejecuciones automáticas',
          services: 'servicios',
          tasks: 'tareas'
        },
        files: 'archivos',
        drivers: 'controladores',
        libraries: 'bibliotecas'
      },
      summary: {
        snapshotTime: 'Hora de instantánea',
        overview: {
          typeToFilterOptions: 'Introducir la opción del filtro',
          noSnapShots: 'No hay instantáneas disponibles'
        },
        body: {
          ipAddresses: 'Direcciones IP ({{count}})',
          securityConfig: 'Configuración de seguridad',
          loggedUsers: 'Usuario que iniciaron sesión ({{count}})',
          user: {
            administrator: 'Administrador',
            sessionId: 'ID de sesión',
            sessionType: 'Tipo de sesión',
            groups: 'Grupos',
            host: 'Host',
            deviceName: 'Nombre del dispositivo'
          }
        },
        securityConfig: {
          arrangeBy: 'ORGANIZAR POR',
          alphabetical: 'Alfabético',
          status: 'Estado'
        }
      },
      selected: 'elementos seleccionados ({{count}})',
      list: {
        noResultsMessage: 'No se encontraron resultados.',
        errorOffline: 'Se produjo un error. El servidor de Endpoint puede estar offline o inaccesible.'
      },
      filters: {
        systemFilter: 'Esta búsqueda la define el sistema y no se puede editar.',
        since: 'Desde',
        customDateRange: 'Rango de fechas personalizado',
        customStartDate: 'Fecha de inicio',
        customEndDate: 'Fecha de finalización',
        customDate: 'Fecha personalizada',
        operator: 'Operador',
        searchPlaceHolder: 'Introducir la opción del filtro',
        mutlipleValuesNote: 'Nota: Para buscar valores múltiples, use || como separador',
        invalidFilterInput: 'Entrada del filtro no válida',
        invalidFilterInputLength: 'La entrada del filtro tiene más de 256 caracteres',
        invalidIP: 'Ingrese una dirección IP válida',
        invalidAgentID: 'Introduzca un ID de agente válido',
        invalidAgentVersion: 'Introduzca una versión de agente válida',
        invalidMacAddress: 'Introduzca una dirección MAC válida',
        invalidOsDescription: 'Se permiten caracteres alfabéticos, números y ., -, ()',
        invalidCharacters: 'Puede contener caracteres alfanuméricos o especiales.',
        invalidCharsAlphabetOnly: 'Los números y los caracteres especiales no se permiten',
        invalidCharsAlphaNumericOnly: 'Los caracteres especiales no se permiten',
        inTimeRange: 'En',
        notInTimeRange: 'No en',
        agentStatus: {
          lastSeenTime: 'Agente no visto desde'
        }
      },
      restrictionTypeOptions: {
        EQUALS: 'Es igual a',
        CONTAINS: 'Contiene',
        GT: '>',
        LT: '<',
        GTE: '>=',
        LTE: '<=',
        NOT_EQ: '!=',
        LESS_THAN: 'Menor que',
        GREATER_THAN: 'Mayor que',
        BETWEEN: 'Entre',
        LAST_5_MINUTES: 'Últimos 5 minutos',
        LAST_10_MINUTES: 'Últimos 10 minutos',
        LAST_15_MINUTES: 'Últimos 15 minutos',
        LAST_30_MINUTES: 'Últimos 30 minutos',
        LAST_HOUR: 'Última hora',
        LAST_3_HOURS: 'Últimas 3 horas',
        LAST_6_HOURS: 'Últimas 6 horas',
        LAST_TWELVE_HOURS: 'Últimas 12 horas',
        LAST_TWENTY_FOUR_HOURS: 'Últimas 24 horas',
        LAST_FORTY_EIGHT_HOURS: 'Últimos 2 días',
        LAST_5_DAYS: 'Últimos 5 días',
        LAST_7_DAYS: 'Últimos 7 días',
        LAST_14_DAYS: 'Últimos 14 días',
        LAST_30_DAYS: 'Últimos 30 días',
        LAST_HOUR_AGO: 'Hace 1 hora',
        LAST_TWENTY_FOUR_HOURS_AGO: 'Hace 24 horas',
        LAST_5_DAYS_AGO: 'Hace 5 días',
        ALL_TIME: 'Todos los datos'
      },
      footer: '{{count}} de {{total}} hosts',
      column: {
        panelTitle: 'Preferencias de hosts',
        triggerTip: 'Abrir/ocultar preferencias de hosts',
        id: 'ID de agente',
        analysisData: {
          iocs: 'Alertas de IOC',
          machineRiskScore: 'Puntaje de riesgo'
        },
        agentStatus: {
          scanStatus: 'Estado de escaneo de agente',
          lastSeenTime: 'Agente visto por última vez'
        },
        machine: {
          machineOsType: 'Sistema operativo',
          machineName: 'Nombre del host',
          id: 'ID de agente',
          agentVersion: 'Versión de agente',
          scanStartTime: 'Hora de último escaneo',
          scanRequestTime: 'Hora de solicitud de escaneo',
          scanType: 'Tipo de escaneo',
          scanTrigger: 'Desencadenante del escaneo',
          securityConfigurations: 'Configuraciones de seguridad',
          hostFileEntries: {
            ip: 'IP de archivo host',
            hosts: 'Entradas de host'
          },
          users: {
            name: 'Nombre de usuario',
            sessionId: 'ID de sesión de usuario',
            sessionType: 'Tipo de sesión de usuario',
            isAdministrator: 'El usuario es administrador',
            groups: 'Grupos de usuarios',
            domainUserQualifiedName: 'Nombre calificado del usuario',
            domainUserId: 'ID de usuario de dominio de usuario',
            domainUserOu: 'OU de usuario de dominio de usuario',
            domainUserCanonicalOu: 'OU canónica de usuario de dominio de usuario',
            host: 'Host de usuario',
            deviceName: 'Nombre de dispositivo de usuario'
          },
          errors: {
            time: 'Error: hora',
            fileID: 'Error: ID de archivo',
            line: 'Error: línea',
            number: 'Error: número',
            value: 'Error: valor',
            param1: 'Error: parámetro1',
            param2: 'Error: parámetro2',
            param3: 'Error: parámetro3',
            info: 'Error: información',
            level: 'Error: nivel',
            type: 'Error: tipo'
          },
          networkShares: {
            path: 'Recurso compartido de red: ruta',
            name: 'Recurso compartido de red: nombre',
            description: 'Recurso compartido de red: descripción',
            type: 'Recurso compartido de red: tipo',
            permissions: 'Recurso compartido de red: permisos',
            maxUses: 'Recurso compartido de red: máximo de usos',
            currentUses: 'Recurso compartido de red: usos actuales'
          },
          mountedPaths: {
            path: 'Rutas montadas: ruta',
            fileSystem: 'Rutas montadas: sistema de archivos',
            options: 'Rutas montadas: opciones',
            remotePath: 'Rutas montadas: ruta remota'
          },
          securityProducts: {
            type: 'Productos de seguridad: tipo',
            instance: 'Productos de seguridad: instancia',
            displayName: 'Productos de seguridad: nombre para mostrar',
            companyName: 'Productos de seguridad: nombre de la empresa',
            version: 'Productos de seguridad: versión',
            features: 'Productos de seguridad: funciones'
          },
          networkInterfaces: {
            name: 'Nombre de NIC',
            macAddress: 'Dirección MAC de NIC',
            networkId: 'Interfaz de red: ID de red',
            ipv4: 'IPv4',
            ipv6: 'IPv6',
            gateway: 'Interfaz de red: puerta de enlace',
            dns: 'Interfaz de red: DNS',
            promiscuous: 'NIC promiscua'
          }
        },
        riskScore: {
          moduleScore: 'Puntaje del módulo',
          highestScoringModules: 'Módulo con puntaje más alto'
        },
        machineIdentity: {
          machineName: 'Nombre del host',
          group: 'Grupo de agentes',
          agentMode: 'Modo de agente',
          agent: {
            exeCompileTime: 'Agente: hora de compilación de modo de usuario',
            sysCompileTime: 'Agente: hora de compilación de controlador',
            packageTime: 'Agente: hora del paquete',
            installTime: 'Agente: hora de instalación',
            serviceStartTime: 'Agente: hora de inicio del servicio',
            serviceProcessId: 'Agente: ID de proceso del servicio',
            serviceStatus: 'Agente: estado del servicio',
            driverStatus: 'Agente: estado del controlador',
            blockingEnabled: 'Agente: bloqueo habilitado',
            blockingUpdateTime: 'Agente: hora de actualización del bloqueo'
          },
          operatingSystem: {
            description: 'SO: descripción',
            buildNumber: 'SO: número de compilación',
            servicePack: 'SO: Service Pack',
            directory: 'SO: directorio',
            kernelId: 'SO: ID de kernel',
            kernelName: 'SO: nombre de kernel',
            kernelRelease: 'SO: versión del kernel',
            kernelVersion: 'SO: versión del kernel',
            distribution: 'SO: distribución',
            domainComputerId: 'SO: ID de computadora del dominio',
            domainComputerOu: 'SO: OU de computadora del dominio',
            domainComputerCanonicalOu: 'SO: OU canónica de computadora del dominio',
            domainOrWorkgroup: 'SO: dominio o grupo de trabajo',
            domainRole: 'SO: función del dominio',
            lastBootTime: 'SO: hora de último arranque'
          },
          hardware: {
            processorArchitecture: 'Hardware: arquitectura del procesador',
            processorArchitectureBits: 'Hardware: bits de arquitectura del procesador',
            processorCount: 'Hardware: conteo de procesadores',
            processorName: 'Hardware: nombre del procesador',
            totalPhysicalMemory: 'Hardware: memoria física total',
            chassisType: 'Hardware: tipo de chasis',
            manufacturer: 'Hardware: fabricante',
            model: 'Hardware: modelo',
            serial: 'Hardware: serie',
            bios: 'Hardware: BIOS'
          },
          locale: {
            defaultLanguage: 'Configuración regional: idioma predeterminado',
            isoCountryCode: 'Configuración regional: código de país',
            timeZone: 'Configuración regional: zona horaria'
          },
          knownFolder: {
            appData: 'Carpeta: datos de aplicación',
            commonAdminTools: 'Carpeta: herramientas de administración comunes',
            commonAppData: 'Carpeta: datos de aplicaciones comunes',
            commonDestop: 'Carpeta: escritorio común',
            commonDocuments: 'Carpeta: documentos comunes',
            commonProgramFiles: 'Carpeta: archivos de programa comunes',
            commonProgramFilesX86: 'Carpeta: archivos de programa comunes (x86)',
            commonPrograms: 'Carpeta: programas comunes',
            commonStartMenu: 'Carpeta: menú Inicio común',
            commonStartup: 'Carpeta: arranque común',
            desktop: 'Carpeta: escritorio',
            localAppData: 'Carpeta: datos de aplicaciones locales',
            myDocuments: 'Carpeta: Mis documentos',
            programFiles: 'Carpeta: Archivos de programa',
            programFilesX86: 'Carpeta: Archivos de programa (x86)',
            programs: 'Carpeta: programas',
            startMenu: 'Carpeta: menú Inicio',
            startup: 'Carpeta: arranque',
            system: 'Carpeta: sistema',
            systemX86: 'Carpeta: sistema (x86)',
            windows: 'Carpeta: Windows'
          }
        },
        markedForDeletion: 'Marcado para eliminación'
      },

      properties: {
        title: 'Propiedades del host',
        filter: 'Tipo para filtrar la lista',
        checkbox: 'Mostrar propiedades solo con valores',
        machine: {
          securityConfigurations: 'Configuraciones de seguridad',
          hostFileEntries: {
            title: 'Entradas del archivo host',
            ip: 'IP de archivo host',
            hosts: 'Entradas de host'
          },
          users: {
            title: 'Usuario',
            name: 'Nombre',
            sessionId: 'ID de sesión',
            sessionType: 'Tipo de sesión',
            isAdministrator: 'Es administrador',
            administrator: 'Es administrador',
            groups: 'Grupos',
            domainUserQualifiedName: 'Nombre calificado',
            domainUserId: 'ID de usuario de dominio',
            domainUserOu: 'OU de usuario de dominio',
            domainUserCanonicalOu: 'OU canónica de usuario de dominio',
            host: 'Host',
            deviceName: 'Nombre de dispositivo'
          },
          networkInterfaces: {
            title: 'Interfaces de red',
            name: 'Nombre',
            macAddress: 'Dirección MAC',
            networkId: 'ID de red',
            ipv4: 'IPv4',
            ipv6: 'IPv6',
            gateway: 'Gateway',
            dns: 'DNS',
            promiscuous: 'Promiscuo'
          }
        },
        machineIdentity: {
          agent: {
            agentId: 'ID de agente',
            agentMode: 'Modo de agente',
            agentVersion: 'Versión de agente',
            title: 'Agente',
            exeCompileTime: 'Hora de compilación de modo de usuario',
            sysCompileTime: 'Hora de compilación de controlador',
            packageTime: 'Hora del paquete',
            installTime: 'Hora de instalación',
            serviceStartTime: 'Hora de inicio del servicio',
            serviceProcessId: 'ID de proceso del servicio',
            serviceStatus: 'Estado del servicio',
            driverStatus: 'Estado de controlador',
            blockingEnabled: 'Bloqueo habilitado',
            blockingUpdateTime: 'Hora de actualización del bloqueo'
          },
          operatingSystem: {
            title: 'Sistema operativo',
            description: 'Descripción',
            buildNumber: 'Número de compilación',
            servicePack: 'Service Pack',
            directory: 'Directorio',
            kernelId: 'ID de kernel',
            kernelName: 'Nombre de kernel',
            kernelRelease: 'Versión del kernel',
            kernelVersion: 'Versión del kernel',
            distribution: 'Distribución',
            domainComputerId: 'ID de computadora del dominio',
            domainComputerOu: 'OU de computadora del dominio',
            domainComputerCanonicalOu: 'OU canónica de computadora del dominio',
            domainOrWorkgroup: 'Dominio o grupo de trabajo',
            domainRole: 'Función del dominio',
            lastBootTime: 'Hora de último arranque'
          },
          hardware: {
            title: 'Hardware',
            processorArchitecture: 'Arquitectura del procesador',
            processorArchitectureBits: 'Bits de arquitectura del procesador',
            processorCount: 'Conteo de procesadores',
            processorName: 'Nombre del procesador',
            totalPhysicalMemory: 'Memoria física total',
            chassisType: 'Tipo de chasis',
            manufacturer: 'Fabricante',
            model: 'Modelo',
            serial: 'Serie',
            bios: 'BIOS'
          },
          locale: {
            title: 'Configuración regional',
            defaultLanguage: 'Idioma predeterminado',
            isoCountryCode: 'Código del país',
            timeZone: 'Zona horaria'
          }
        }
      },
      propertyPanelTitles: {
        autoruns: {
          autorun: 'Propiedades de ejecución automática',
          services: 'Propiedades de servicio',
          tasks: 'Propiedades de tarea'
        },
        files: 'Propiedades de archivo',
        drivers: 'Propiedades de controlador',
        libraries: 'Propiedades de biblioteca'
      },
      medium: {
        network: 'Red',
        log: 'Registro',
        correlation: 'Correlación'
      },
      empty: {
        title: 'No se encontró ningún evento.',
        description: 'Sus criterios de filtro no coincidieron con ningún registro.'
      },
      error: {
        title: 'No se pueden cargar datos.',
        description: 'Se produjo un error inesperado cuando se intentó buscar los registros de datos.'
      },
      meta: {
        title: 'Meta',
        clickToOpen: 'Haga clic para abrir'
      },
      events: {
        title: 'Eventos',
        error: 'Se produjo un error inesperado al ejecutar esta consulta.'
      },
      services: {
        loading: 'Cargando lista de servicios disponibles',
        empty: {
          title: 'No se pueden encontrar servicios.',
          description: 'No se detectaron Brokers, Concentrators u otros servicios. Esto se puede deber a un problema de configuración o conectividad.'
        },
        error: {
          title: 'No se pueden cargar servicios.',
          description: 'Se produjo un error inesperado al cargar la lista de Brokers, Concentrators y otros servicios para investigar. Esto se puede deber a un problema de configuración o conectividad.'
        }
      },
      customQuery: {
        title: 'Ingrese una consulta.'
      },
      customFilter: {
        save: {
          description: 'Proporcione un nombre para la búsqueda. Este nombre aparecerá en la lista de búsqueda.',
          name: 'Nombre*',
          errorHeader: 'No se puede guardar la búsqueda',
          header: 'Guardar búsqueda',
          errorMessage: 'La búsqueda no se puede guardar. ',
          emptyMessage: 'El campo Nombre está vacío.',
          nameExistsMessage: 'Una búsqueda guardada con el mismo nombre.',
          success: 'La consulta de búsqueda se guardó correctamente.',
          filterFieldEmptyMessage: 'Uno o más de los campos de filtro recientemente agregados están vacíos. Agregue los filtros o quite los campos para guardar.',
          invalidInput: 'Solo se permiten los caracteres especiales \“-\” y \“_\”.'
        },
        update: {
          success: 'La consulta de búsqueda se actualizó correctamente.'
        }
      },
      initiateScan: {
        modal: {
          title: 'Iniciar escaneo para {{count}} host(s)',
          modalTitle: 'Iniciar escaneo para {{name}}',
          description: 'Seleccione el tipo de escaneo para los hosts seleccionados.',
          error1: '*Seleccione al menos un host',
          error2: 'Se permite un máximo de 100 hosts para iniciar el escaneo',
          infoMessage: 'Algunos de los hosts seleccionados ya se están escaneando, por lo que no se iniciará un escaneo nuevo para ellos.',
          ecatAgentMessage: 'Algunos de los hosts seleccionados son agentes 4.4. Esta función no se admite para ellos.',
          quickScan: {
            label: 'Escaneo rápido (predeterminado)',
            description: 'Ejecuta un escaneo rápido de todos los módulos ejecutables cargados en la memoria. Esto tarda aproximadamente 10 minutos.'
          }
        },
        success: 'El escaneo se inició correctamente',
        error: 'El inicio del escaneo falló'
      },
      cancelScan: {
        modal: {
          title: 'Detener escaneo para {{count}} host(s)',
          description: '¿Está seguro de que desea detener el escaneo para los hosts seleccionados?',
          error1: '*Seleccione al menos un host'
        },
        success: 'La detención del escaneo se inició correctamente',
        error: 'El inicio de la detención del escaneo falló'
      },
      deleteHosts: {
        modal: {
          title: 'Eliminar {{count}} host(s)',
          message: 'Elimine el host si sus datos de escaneo ya no se requieren o si el agente se desinstaló. ' +
          'Se eliminarán todos los datos de escaneo asociados con el host. ¿Desea continuar? '
        },
        success: 'Los hosts se eliminaron correctamente',
        error: 'La eliminación de hosts falló'
      },
      moreActions: {
        openIn: 'Cambiar a Endpoint',
        openInErrorMessage: 'Seleccione al menos un host',
        notAnEcatAgent: 'Seleccione solo agentes 4.4',
        cancelScan: 'Detener exploración'
      }
    },
    savedQueries: {
      headerContent: 'Seleccione una consulta guardada en la lista para ejecutarla. También puede editar el nombre de la consulta guardada si hace clic en el ícono de lápiz junto al nombre y configurarla como valor predeterminado si hace clic en el ícono de estrella.',
      deleteBtn: 'Eliminar selección',
      runBtn: 'Ejecutar selección',
      yesBtn: 'Sí',
      noBtn: 'No',
      delete: {
        successMessage: 'La consulta se eliminó correctamente.',
        confirmMessage: '¿Está seguro de que desea eliminar la consulta seleccionada?'
      },
      edit: {
        successMessage: 'El nombre de la consulta se actualizó correctamente',
        errorMessage: 'La actualización del nombre de la consulta falló',
        nameExistsMessage: 'El nombre de la consulta ya existe'
      }
    },
    files: {
      footer: '{{count}} de {{total}} {{label}}',
      filter: {
        filters: 'Filtros guardados',
        newFilter: 'Filtro nuevo',
        windows: 'WINDOWS',
        mac: 'MAC',
        linux: 'LINUX',
        favouriteFilters: 'Filtros favoritos',
        restrictionType: {
          moreThan: 'Mayor que',
          lessThan: 'Menor que',
          between: 'Entre',
          equals: 'Es igual a',
          contains: 'Contiene'
        },
        save: 'Guardar',
        reset: 'Restablecer',
        customFilters: {
          save: {
            description: 'Proporcione un nombre para la búsqueda. Este nombre aparecerá en la lista de búsqueda.',
            name: 'Nombre*',
            errorHeader: 'No se puede guardar la búsqueda',
            header: 'Guardar búsqueda',
            errorMessage: 'La búsqueda no se puede guardar. ',
            emptyMessage: 'El campo Nombre está vacío.',
            nameExistsMessage: 'Una búsqueda guardada con el mismo nombre.',
            success: 'La consulta de búsqueda se guardó correctamente.',
            filterFieldEmptyMessage: 'Los campos del filtro están vacíos',
            invalidInput: 'Solo se permiten los caracteres especiales \“-\” y \“_\”.'
          }
        },
        button: {
          cancel: 'Cancelar',
          save: 'Guardar'
        }
      },
      fields: {
        id: 'ID',
        firstSeenTime: 'Hora en que se vio por primera vez',
        companyName: 'Nombre de la empresa',
        checksumMd5: 'MD5',
        checksumSha1: 'SHA1',
        checksumSha256: 'SHA256',
        machineOsType: 'Sistema operativo',
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
          features: 'Firma',
          signer: 'Firmante'
        },
        owner: {
          userName: 'Propietario',
          groupName: 'Grupo de propietarios'
        },
        rpm: {
          packageName: 'Paquete'
        },
        path: 'Ruta',
        entropy: 'Entropía',
        fileName: 'Nombre de archivo',
        firstFileName: 'Nombre de archivo',
        timeCreated: 'Creado',
        format: 'Formato',
        sectionNames: 'Nombres de sección',
        importedLibraries: 'Bibliotecas importadas',
        size: 'Tamaño'
      }
    },
    pivotToInvestigate: {
      title: 'Seleccionar servicio',
      buttonText: 'Navegar',
      buttonText2: 'Análisis de eventos',
      iconTitle: 'Cambiar a Navegar o Análisis de eventos'
    }
  },
  hostsScanConfigure: {
    title: 'Programa de escaneo',
    save: 'Guardar',
    enable: 'Habilitar',
    saveSuccess: 'Se guardó correctamente',
    startDate: 'Fecha de inicio',
    recurrenceInterval: {
      title: 'Intervalo de recurrencia',
      options: {
        daily: 'Diariamente',
        weekly: 'Semanalmente',
        monthly: 'Mensualmente'
      },
      every: 'Cada',
      on: 'El',
      intervalText: {
        DAYS: 'día(s)',
        WEEKS: 'semana(s)',
        MONTHS: 'meses'
      },
      week: {
        monday: 'M',
        tuesday: 'T',
        wednesday: 'W',
        thursday: 'T',
        friday: 'F',
        saturday: 'S',
        sunday: 'S'
      }
    },
    startTime: 'Hora de inicio',
    cpuThrottling: {
      title: 'Regulación de CPU de agente',
      cpuMax: 'Máximo de CPU (%)',
      vmMax: 'Máximo de máquinas virtuales (%) '
    },
    error: {
      generic: 'Se produjo un error inesperado al intentar recuperar estos datos.'
    }
  }
};
});
