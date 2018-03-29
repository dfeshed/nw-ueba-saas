import $ from 'jquery';
import fetch from 'fetch';
import { get } from '@ember/object';
import computed from 'ember-computed-decorators';
import Controller from '@ember/controller';
import { inject as service } from '@ember/service';
import { getLocale, getTheme } from 'sa/reducers/global/preferences/selectors';
import RSVP from 'rsvp';

const { Promise } = RSVP;

const cssVariablesSupported = window.CSS &&
    window.CSS.supports && window.CSS.supports('--a', 0);

export default Controller.extend({
  redux: service(),
  flashMessages: service(),
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

  _fetch(url) {
    return fetch(url);
  },

  _fetchScript(url) {
    return new Promise((resolve, reject) => {
      return this._fetch(url).then((response) => {
        if (response.ok) {
          resolve(response.text());
        } else {
          throw new Error('invalid http response');
        }
      }).catch((error) => {
        reject(error);
      });
    });
  },

  _appendLocaleScript(body) {
    const sourceId = 'dynamicLocale';
    const dynamicScript = document.getElementById(sourceId);
    if (dynamicScript) {
      document.body.removeChild(dynamicScript);
    }
    const script = document.createElement('script');
    script.id = sourceId;
    script.type = 'text/javascript';
    script.innerHTML = body;
    document.body.appendChild(script);
  },

  _addDynamicLocale(id, fileName) {
    if (!fileName) {
      this.set('i18n.locale', id);
    } else {
      const scriptUrl = `/locales/${fileName}.js`;
      this._fetchScript(scriptUrl).then((body) => {
        this._appendLocaleScript(body);
        this.set('i18n.locale', id);
      }).catch(() => {
        const translationService = get(this, 'i18n');
        const flashMessages = get(this, 'flashMessages');
        const errorMessage = translationService.t('userPreferences.locale.fetchError');
        flashMessages.error(errorMessage);
      });
    }
  },

  init() {
    this._super(...arguments);

    const redux = this.get('redux');

    this.themeName = () => {
      const state = redux.getState();
      const theme = getTheme(state);
      return theme && theme.toLowerCase();
    };

    this.localeSelection = () => {
      const state = redux.getState();
      return getLocale(state);
    };

    let activeTheme, activeLocaleId;
    redux.subscribe(() => {
      const themeName = this.themeName();
      if (themeName !== activeTheme) {
        activeTheme = themeName;
        this._updateBodyClass(themeName);
        this._fetchStylesheet(themeName);
      }

      const locale = this.localeSelection();
      const { id, fileName } = locale;
      if (id !== activeLocaleId) {
        activeLocaleId = id;
        this._addDynamicLocale(id, fileName);
      }
    });
  }
});
