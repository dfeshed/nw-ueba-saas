import $ from 'jquery';
import computed from 'ember-computed-decorators';
import Controller from 'ember-controller';
import service from 'ember-service/inject';

const cssVariablesSupported = window.CSS &&
    window.CSS.supports && window.CSS.supports('--a', 0);

export default Controller.extend({
  redux: service(),

  fatalErrors: service(),

  session: service(),

  accessControl: service(),

  @computed('session.isAuthenticated', 'currentPath')
  authenticatedAndPageFound(isAuthenticated, path) {

    if (!isAuthenticated || path === 'not-found' || path === 'internal-error') {
      return false;
    } else {
      return true;
    }
  },

  // The Configure nav tab currently points to a series of Classic pages and is not a {{link-to}}, which means
  // that it will not appear active when the user is in the Configure engine in Ember. For now we'll check if the
  // path points to the configure engine so that we can ensure the tab is active on those pages. When the Classic
  // pages have been emberified, we can remove this check and replace with the standard link-to.
  @computed('currentPath')
  isConfigureRoute(path) {
    return path.indexOf('protected.configure.') === 0;
  },

  _updateBodyClass(themeName) {
    $('body').removeClass((index, bodyClasses) => {
      const names = bodyClasses || '';
      const classNames = names.split(' ');
      return classNames.find((name) => {
        const match = name.match(/.*-theme/);
        return match && match[0];
      });
    });
    $('body').addClass(`${themeName}-theme`);
  },

  _generateFileName(themeName) {
    const stylesheets = $('link[rel=stylesheet]').filter(function(i, style) {
      return style.href.indexOf('assets/sa-') > -1;
    });
    if (stylesheets && stylesheets[0] && stylesheets[0].href) {
      const [ stylesheet ] = stylesheets;
      const { href } = stylesheet;
      const pattern = new RegExp('/assets/sa-(.*)\.css');
      const fingerprint = pattern.exec(href);
      if (fingerprint && fingerprint[1]) {
        return `/assets/${themeName}-${fingerprint[1]}.css`;
      }
    }
    return `/assets/${themeName}.css`;
  },

  _fetchStylesheet(themeName) {
    if (!cssVariablesSupported) {
      const themeUrl = this._generateFileName(themeName);
      const themeLink = document.createElement('link');
      themeLink.href = themeUrl;
      themeLink.rel = 'stylesheet';
      themeLink.type = 'text/css';
      document.body.appendChild(themeLink);
    }
  },

  init() {
    this._super(...arguments);

    const redux = this.get('redux');

    this.themeName = () => {
      const state = redux.getState();
      const { global } = state;
      return global && global.preferences && global.preferences.theme && global.preferences.theme.toLowerCase();
    };

    let activeTheme;
    redux.subscribe(() => {
      const themeName = this.themeName();
      if (themeName !== activeTheme) {
        activeTheme = themeName;
        this._updateBodyClass(themeName);
        this._fetchStylesheet(themeName);
      }
    });
  }
});
