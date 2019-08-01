const securitybanner = {
  total: 1,
  message: 'Settings Loaded',
  object: null,
  success: true,
  data: [{
    securityBannerTitle: 'Terms and Conditions <img src=a onerror=alert(\'loginBanner\')>',
    securityBannerText: 'banner text example <img src=a onerror=alert(\'loginBanner\')>',
    securityBannerEnabled: true,
    contextPath: '',
    serverTitlePrefix: '[trick</title><script type=\'javascript\'>runMyFunction();</script><title>:)]',
    dirty: false
  }]
};

const securitybannerdisabled = {
  total: 1,
  message: 'Settings Loaded',
  object: null,
  success: true,
  data: [{
    securityBannerEnabled: false
  }]
};

export { securitybanner };
export { securitybannerdisabled };
