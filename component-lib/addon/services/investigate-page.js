import { isNone } from '@ember/utils';
import Service, { inject as service } from '@ember/service';
import { alias } from 'ember-computed';

// Service which persists the user selected investigate page in the userPreferences
export default Service.extend({
  request: service(),
  accessControl: service(),
  selected: null,
  flashMessages: service(),
  i18n: service(),
  hasInvestigateAccess: alias('accessControl.hasInvestigateAccess'),
  options: [
    { key: '/navigate', label: 'navigate', route: '/investigation', isClassic: true },
    { key: '/events', label: 'events', route: '/investigation/events', isClassic: true },
    { key: '/eventanalysis', label: 'eventAnalysis', route: 'protected.investigate.investigate-events' },
    { key: '/hosts', label: 'hosts', route: 'protected.investigate.investigate-hosts' },
    { key: '/files', label: 'files', route: 'protected.investigate.investigate-files' },
    { key: '/malware', label: 'malware', route: '/investigation/malware', isClassic: true }
  ],

  persist(value) {
    this.get('request').promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        data: {
          defaultInvestigatePage: value
        }
      }
    }).catch(() => {
      this.get('flashMessages').error(this.get('i18n').t('userPreferences.defaultInvestigatePageError'));
    });
  },

  /**
   * Sets and persists the default investigate page the user selects in the preferences menu
   * @param value The selected default investigate page
   * @public
   */
  setDefaultInvestigatePage(value) {
    if (value && value.key) {
      if (!isNone(this.get('selected'))) {
        this.persist(value.key);
      }
      this.set('selected', value);
    } else {
      if (!isNone(this.get('selected'))) {
        this.persist(value);
      }
      this.set('selected', this.get('options').findBy('key', value));
    }
  }

});
