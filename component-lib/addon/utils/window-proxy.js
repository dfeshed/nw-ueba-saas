const windowProxy = {
  openInCurrentTab: (url) => {
    window.open(url, '_self');
  },
  openInNewTab: (url) => {
    window.open(url);
  },
  currentUri: () => {
    return window.location.href;
  }
};

export default windowProxy;