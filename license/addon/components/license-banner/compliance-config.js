export default {
  'LICENSE_SERVER_DOWN': {
    level: 'error',
    messageKey: 'license.banner.serverDown',
    url: '/admin/services',
    urlTextKey: 'license.banner.servicesPage'
  },
  'UNLICENSED': {
    level: 'error',
    messageKey: 'license.banner.unlicensed',
    url: '/admin/system#licensing',
    urlTextKey: 'license.banner.licensePage'
  },
  'EXPIRED': {
    level: 'error',
    messageKey: 'license.banner.expired',
    url: '/admin/system#licensing',
    urlTextKey: 'license.banner.licensePage'
  },
  'USAGE_LIMIT_EXCEEDED_GRACE': {
    level: 'error',
    messageKey: 'license.banner.usage-exceeded',
    url: '/admin/system#licensing',
    urlTextKey: 'license.banner.licensePage'
  },
  'USAGE_LIMIT_EXCEEDED_BREACH': {
    level: 'error',
    messageKey: 'license.banner.usage-exceeded',
    url: '/admin/system#licensing',
    urlTextKey: 'license.banner.licensePage'
  },
  'NEARING_EXPIRY': {
    level: 'warning',
    messageKey: 'license.banner.near-expiry',
    url: '/admin/system#licensing',
    urlTextKey: 'license.banner.licensePage'
  },
  'USAGE_LIMIT_NEARING': {
    level: 'warning',
    messageKey: 'license.banner.near-usage-limit',
    url: '/admin/system#licensing',
    urlTextKey: 'license.banner.licensePage'
  },
  'OKAY': {
    level: null,
    messageKey: null,
    url: null,
    urlTextKey: null
  }
};
