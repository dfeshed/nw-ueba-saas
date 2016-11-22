export default {
  languages: {
    en: 'English',
    ja: 'Japanese'
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
    language: 'Language',
    timeZone: 'Time Zone',
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
      investigateClassic: 'Investigate Classic',
      dashboard: 'Dashboard',
      live: 'Live',
      respond: 'Respond',
      admin: 'Admin'
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
  },
  previousMonth: 'Previous Month',
  nextMonth: 'Next Month',
  months() {
    return [
      'January',
      'February',
      'March',
      'April',
      'May',
      'June',
      'July',
      'August',
      'September',
      'October',
      'November',
      'December'
    ];
  },
  monthsShort() {
    return [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec'
    ];
  },
  weekdays() {
    return [
      'Sunday',
      'Monday',
      'Tuesday',
      'Wednesday',
      'Thursday',
      'Friday',
      'Saturday'
    ];
  },
  weekdaysShort() {
    return [
      'Sun',
      'Mon',
      'Tue',
      'Wed',
      'Thu',
      'Fri',
      'Sat'
    ];
  },
  weekdaysMin() {
    return [
      'Sun',
      'Mon',
      'Tue',
      'Wed',
      'Thu',
      'Fri',
      'Sat'
    ];
  },
  midnight: 'Midnight',
  noon: 'Noon'
};
