import computed from 'ember-computed';
import Route from 'ember-route';

import Context from 'investigate/actions/context';
import Events from 'investigate/actions/events';
import EventCount from 'investigate/actions/event-count';
import EventTimeline from 'investigate/actions/event-timeline';
import InvestigateState from 'investigate/state/index';
import Meta from 'investigate/actions/meta';
import Nav from 'investigate/actions/nav';
import Recon from 'investigate/actions/recon';
import Results from 'investigate/actions/results';
import Services from 'investigate/actions/services';
import Transition from 'investigate/actions/transition';

export default Route.extend(
  Transition, Nav, Services, Results, Events, EventCount, EventTimeline, Meta, Recon, Context,
  {
    title() {
      return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('investigate.title') });
    },

    model() {
      return this.get('state');
    },

    state: computed(function() {
      return InvestigateState;
    })
  }
);
