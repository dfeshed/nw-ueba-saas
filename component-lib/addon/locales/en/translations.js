export default {
  languages: {
    en: 'English',
    ja: 'Japanese'
  },
  themes: {
    light: 'Light Theme',
    dark: 'Dark Theme'
  },
  forms: {
    cancel: 'Cancel',
    submit: 'Submit',
    reset: 'Reset',
    apply: 'Apply'
  },
  login: {
    username: 'Username',
    password: 'Password',
    login: 'Login',
    logout: 'Logout',
    lostPasswordLink: 'Lost Password?',
    genericError: 'Error: Please try again',
    unAuthorized: 'Invalid credentials',
    badCredentials: 'Invalid credentials',
    userLocked: 'User account is locked',
    userDisabled: 'User account is disabled',
    userExpired: 'User account has expired',
    authServerNotFound: 'There was an error while authenticating your credentials.',
    lostPassword: {
      title: 'Lost Password Recovery',
      description: 'Please submit your username.'
    },
    thankYou: {
      title: 'Thank You!',
      description: 'A password reset has been sent to the registered user\'s email account.',
      back: 'Return to Login'
    }
  },
  userPreferences: {
    preferences: 'Preferences',
    preferencesFor: 'Preferences for {{user}}',
    username: 'Username',
    email: 'Email',
    newPassword: 'New Password',
    confirmPassword: 'Confirm Password',
    passwordMismatchError: 'Password and confirmation do not match',
    language: 'Language',
    timeZone: 'Time Zone',
    friendlyName: 'Friendly Username',
    dateFormat: {
      label: 'Date Format',
      dayFirst: 'DD/MM/YYYY',
      monthFirst: 'MM/DD/YYYY',
      yearFirst: 'YYYY/MM/DD'
    },
    timeFormat: {
      label: 'Time Format',
      twelveHour: '12hr',
      twentyFourHour: '24hr'
    },
    defaultLandingPage: {
      label: 'Default Landing Page',
      monitor: 'Monitor',
      investigate: 'Investigate',
      respond: 'Respond',
      admin: 'Admin'
    },
    theme: {
      label: 'Theme',
      light: 'Light',
      dark: 'Dark'
    },
    spacing: {
      label: 'Spacing',
      tight: 'Tight',
      loose: 'Loose'
    },
    notifications: 'Enable Notifications',
    contextMenus: 'Enable Context Menus'
  },
  ipConnections: {
    ipCount: '({{count}} IPs)',
    clickToCopy: 'Click to copy IP address'
  },
  list: {
    all: '(All)',
    items: 'items',
    packets: 'packets',
    packet: 'packet',
    of: 'of',
    sessions: 'sessions'
  },
  updateLabel: {
    'one': 'update',
    'other': 'updates'
  },
  recon: {
    files: {
      fileName: 'File Name',
      extension: 'Extension',
      mimeType: 'MIME Type',
      fileSize: 'File Size',
      hashes: 'Hashes',
      noFiles: 'There are no files available for this event.'
    },
    error: {
      generic: 'An unexpected error has occurred attempting to retrieve this data.',
      missingRecon: 'This event (id = {{id}}) was not saved or has been rolled out of storage. No content to display.'
    }
  },
  memsize: {
    B: 'bytes',
    KB: 'KB',
    MB: 'MB',
    GB: 'GB',
    TB: 'TB'
  }
};
