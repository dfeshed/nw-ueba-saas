import Route from 'ember-route';
import service from 'ember-service/inject';

import Context from 'investigate-events/actions/context';

export default Route.extend(Context, {
  i18n: service(),

  title() {
    return this.get('i18n').t('pageTitle', {
      section: this.get('i18n').t('investigate.title')
    });
  }
});
