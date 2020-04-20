const windowProxy = {
  openInCurrentTab: (url) => {
    window.open(url, '_self');
  },
  openInNewTab: (url) => {
    window.open(url);
  },
  currentUri: () => {
    return urlUtil.getWindowLocationHRef();
  }
};

/**
 * Decoupling this tightly bound windowProxy function to return
 * the current url while retaining the ability to pass your own url
 * by overriding this function.
 * Check out buil-url-test if you want to inject your own url in a test.
 */
const urlUtil = (function() {
  function getWindowLocationHRef() {
    return window.location.href;
  }
  return {
    getWindowLocationHRef
  };
})();

export {
  windowProxy,
  urlUtil
};