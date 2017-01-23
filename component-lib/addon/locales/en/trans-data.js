export default {
  languages: {
    en: 'English',
    'en-us': 'English',
    ja: 'Japanese'
  },
  forms: {
    cancel: 'Cancel',
    submit: 'Submit',
    reset: 'Reset',
    apply: 'Apply',
    ok: 'OK',
    delete: 'Delete',
    save: 'Save'
  },
  tables: {
    noResults: 'No Results'
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
    preferences: 'User Preferences',
    personalize: 'Personalize your experience',
    signOut: 'Sign Out',
    version: 'Version 11.0.0',
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
      dashboard: 'Monitor',
      live: 'Configure',
      respond: 'Respond',
      admin: 'Admin'
    }
  },
  ipConnections: {
    ipCount: '({{count}} IPs)',
    clickToCopy: 'Click to copy IP address',
    sourceIp: 'Source IP',
    destinationIp: 'Destination IP'
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
    error: {
      generic: 'An unexpected error has occurred attempting to retrieve this data.',
      missingRecon: 'This event (id = {{id}}) was not saved or has been rolled out of storage. No content to display.'
    },
    files: {
      fileName: 'File Name',
      extension: 'Extension',
      mimeType: 'MIME Type',
      fileSize: 'File Size',
      hashes: 'Hashes',
      noFiles: 'There are no files available for this event.',
      linkFile: 'This file is in another session.<br>Click the file link to view the related session in a new tab.'
    },
    toggles: {
      header: 'Show/Hide Header',
      request: 'Show/Hide Request',
      response: 'Show/Hide Response',
      topBottom: 'Top/Bottom View',
      sideBySide: 'Side by Side View',
      meta: 'Show/Hide Meta',
      settings: 'Settings',
      expand: 'Expand/Contract View',
      close: 'Close Reconstruction'
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
  noon: 'Noon',
  investigate: {
    title: 'Investigate',
    loading: 'Loading',
    loadMore: 'Load More',
    tryAgain: 'Try Again',
    service: 'Service',
    timeRange: 'Time Range',
    filter: 'Filter',
    size: {
      bytes: 'bytes',
      KB: 'KB',
      MB: 'MB',
      GB: 'GB',
      TB: 'TB'
    },
    medium: {
      network: 'Network',
      log: 'Log',
      correlation: 'Correlation'
    },
    empty: {
      title: 'No events found.',
      description: 'Your filter criteria did not match any records.'
    },
    error: {
      title: 'Unable to load data.',
      description: 'An unexpected error occurred when attempting to fetch the data records.'
    },
    meta: {
      title: 'Meta',
      clickToOpen: 'Click to open'
    },
    events: {
      title: 'Events',
      error: 'An unexpected error occurred when executing this query.'
    },
    services: {
      loading: 'Loading list of available services',
      empty: {
        title: 'Unable to find services.',
        description: 'No Brokers, Concentrators, or other services were detected. This may be due to a configuration or connectivity issue.'
      },
      error: {
        title: 'Unable to load services.',
        description: 'Unexpected error loading the list of Brokers, Concentrators, and other services to investigate. This may be due to a configuration or connectivity issue.'
      }
    },
    customQuery: {
      title: 'Enter a query.'
    }
  }
};
