import computed from 'ember-computed';
import Route from 'ember-route';
import service from 'ember-service/inject';

import Context from 'investigate-events/actions/context';
import InvestigateState from 'investigate-events/state/index';
import Meta from 'investigate-events/actions/meta';
import Nav from 'investigate-events/actions/nav';
import Results from 'investigate-events/actions/results';

export default Route.extend(Context, InvestigateState, Meta, Nav, Results, {
  i18n: service(),

  state: computed(function() {
    return InvestigateState;
  }),

  model() {
    return this.get('state');
  },

  title() {
    return this.get('i18n').t('pageTitle', {
      section: this.get('i18n').t('investigate.title')
    });
  }
});
