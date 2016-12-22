export default {
  languages: {
    en: 'ja_English',
    'en-us': 'ja_English',
    ja: 'ja_Japanese'
  },
  forms: {
    cancel: 'ja_Cancel',
    submit: 'ja_Submit',
    reset: 'ja_Reset',
    apply: 'ja_Apply',
    ok: 'ja_OK',
    delete: 'ja_Delete'
  },
  login: {
    username: 'ja_Username',
    password: 'ja_Password',
    login: 'ja_Login',
    logout: 'ja_Logout',
    lostPasswordLink: 'ja_Lost Password?',
    genericError: 'ja_Error: Please try again',
    unAuthorized: 'ja_Invalid credentials',
    badCredentials: 'ja_Invalid credentials',
    userLocked: 'ja_User account is locked',
    userDisabled: 'ja_User account is disabled',
    userExpired: 'ja_User account has expired',
    authServerNotFound: 'ja_There was an error while authenticating your credentials.',
    lostPassword: {
      title: 'ja_Lost Password Recovery',
      description: 'ja_Please submit your username.'
    },
    thankYou: {
      title: 'ja_Thank You!',
      description: 'ja_A password reset has been sent to the registered user\'s email account.',
      back: 'ja_Return to Login'
    }
  },
  userPreferences: {
    preferences: 'ja_Preferences',
    preferencesFor: 'ja_Preferences for {{user}}',
    username: 'ja_Username',
    email: 'ja_Email',
    language: 'ja_Language',
    timeZone: 'ja_Time Zone',
    dateFormat: {
      label: 'ja_Date Format',
      dayFirst: 'ja_DD/MM/YYYY',
      monthFirst: 'ja_MM/DD/YYYY',
      yearFirst: 'ja_YYYY/MM/DD'
    },
    timeFormat: {
      label: 'ja_Time Format',
      twelveHour: 'ja_12hr',
      twentyFourHour: 'ja_24hr'
    },
    defaultLandingPage: {
      label: 'ja_Default Landing Page',
      monitor: 'ja_Monitor',
      investigate: 'ja_Investigate',
      investigateClassic: 'ja_Investigate Classic',
      dashboard: 'ja_Monitor',
      live: 'ja_Configure',
      respond: 'ja_Respond',
      admin: 'ja_Admin'
    }
  },
  ipConnections: {
    ipCount: '(ja_{{count}} IPs)',
    clickToCopy: 'ja_Click to copy IP address',
    sourceIp: 'ja_Source IP',
    destinationIp: 'ja_Destination IP'
  },
  updateLabel: {
    'one': 'ja_update',
    'other': 'ja_updates'
  },
  recon: {
    files: {
      fileName: 'ja_File Name',
      extension: 'ja_Extension',
      mimeType: 'ja_MIME Type',
      fileSize: 'ja_File Size',
      hashes: 'ja_Hashes',
      noFiles: 'ja_There are no files available for this event.',
      linkFile: 'ja_This file is in another session.<br>Click the file link to view the related session in a new tab.'
    },
    error: {
      generic: 'ja_An unexpected error has occurred attempting to retrieve this data.',
      missingRecon: 'ja_This event (id = {{id}}) was not saved or has been rolled out of storage. No content to display.'
    },
    toggles: {
      header: 'ja_Show/Hide Header',
      request: 'ja_Show/Hide Request',
      response: 'ja_Show/Hide Response',
      topBottom: 'ja_Top/Bottom View',
      sideBySide: 'ja_Side by Side View',
      meta: 'ja_Show/Hide Meta',
      settings: 'ja_Settings',
      expand: 'ja_Expand/Contract View',
      close: 'ja_Close Reconstruction'
    }
  },
  memsize: {
    B: 'ja_bytes',
    KB: 'ja_KB',
    MB: 'ja_MB',
    GB: 'ja_GB',
    TB: 'ja_TB'
  }
};
