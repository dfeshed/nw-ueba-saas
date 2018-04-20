import Service, { inject as service } from '@ember/service';
import { get, computed, observer } from '@ember/object';

export default Service.extend({
  i18n: service(),
  router: service(),
  title: computed('_title', function() {
    // Services are only materialized if you actually attempt to retrieve them
    get(this, 'i18n');
    return get(this, '_title');
  }),
  onLocaleChange: observer('i18n.locale', function() {
    const router = get(this, 'router');
    router._router.send('collectTitleTokens', []);
  })
});
