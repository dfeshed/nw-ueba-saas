import Ember from 'ember';
import * as DataActions from 'respond/actions/data-creators';

const {
  run,
  Route,
  inject: {
    service
  }
} = Ember;

export default Route.extend({
  redux: service(),

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('respond.title') });
  },

  model({ incident_id }) {
    // @workaround We want to fire data actions when model changes. That won't work in Safari & Firefox if you are
    // transitioning from another route (e.g., `incidents`); only works if you are coming directly to this route from
    // a url/bookmark. As a workaround, use `run.next` to let the route transition finish before firing redux actions.
    run.next(() => {
      this.get('redux').dispatch(DataActions.initializeIncident(incident_id));
    });
    return {
      incidentId: incident_id
    };
  }
});
