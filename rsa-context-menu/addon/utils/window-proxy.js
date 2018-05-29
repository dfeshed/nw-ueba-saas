const windowProxy = {
  openInCurrentTab: (url) => {
    window.open(url, '_self');
  },
  openInNewTab: (url) => {
    window.open(url);
  }
};

export default windowProxy;