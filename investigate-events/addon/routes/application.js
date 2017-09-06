import computed from 'ember-computed';
import Route from 'ember-route';
import service from 'ember-service/inject';

import Context from 'investigate-events/actions/context';
import Events from 'investigate-events/actions/events';
import EventCount from 'investigate-events/actions/event-count';
import EventTimeline from 'investigate-events/actions/event-timeline';
import InvestigateState from 'investigate-events/state/index';
import Meta from 'investigate-events/actions/meta';
import Nav from 'investigate-events/actions/nav';
import Recon from 'investigate-events/actions/recon';
import Results from 'investigate-events/actions/results';
import Services from 'investigate-events/actions/services';
import Transition from 'investigate-events/actions/transition';


export default Route.extend(Context, Events, EventCount, EventTimeline, InvestigateState, Meta, Nav, Recon, Results, Services, Transition, {
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
