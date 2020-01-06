import fetch from 'component-lib/utils/fetch';
import { get } from '@ember/object';
import computed from 'ember-computed-decorators';
import Controller from '@ember/controller';
import { inject as service } from '@ember/service';

export const DEFAULT_THEME = 'dark';
export const DEFAULT_LOCALE = { id: 'en_US', key: 'en-us', label: 'english' };
export const DEFAULT_LOCALES = [DEFAULT_LOCALE];

export default Controller.extend({
  moment: service(),
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

  // The Admin nav tab is the same setup as the Configure nav tab above...
  @computed('currentPath')
  isAdminRoute(path) {
    return path.indexOf('protected.admin.') === 0;
  },

  _updateBodyClass(themeName) {
    // remove class having theme on it
    const className = [...document.body.classList].find((name) => {
      return /.*-theme/.test(name);
    });
    document.body.classList.remove(className);

    // add new theme
    document.body.classList.add(`${themeName}-theme`);
  },

  // add/set the theme into a cookie for use by integrated apps (e.g., UEBA)
  _updateThemeCookie(themeName) {
    document.cookie = `nw-ui-theme=${themeName};Path=/;`;
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

  _addDynamicLocale(key, langCode, fileName) {
    if (!fileName) {
      this.set('i18n.locale', key);
      get(this, 'moment').changeLocale(langCode);
    } else {
      const scriptUrl = `/locales/${fileName}`;
      fetch(scriptUrl).then((fetched) => fetched.text()).then((body) => {
        this._appendLocaleScript(body);
        this.set('i18n.locale', key);
        get(this, 'moment').changeLocale(langCode);
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

    this.themeName = () => {
      return DEFAULT_THEME;
    };

    this.localeSelection = () => {
      return DEFAULT_LOCALE;
    };

  },

  actions: {
    controllerLogout() {
      this.send('logout');
    },

    controllerClearFatalErrorQueue() {
      this.send('clearFatalErrorQueue');
    }
  }
});
