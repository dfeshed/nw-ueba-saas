import Ember from 'ember';
import Transition from './actions/transition';
import Nav from './actions/nav';
import Services from './actions/services';
import Results from './actions/results';
import Events from './actions/events';
import EventCount from './actions/event-count';
import EventTimeline from './actions/event-timeline';
import InvestigateState from './state/index';
import Meta from './actions/meta';
import Recon from './actions/recon';
import Context from './actions/context';

const {
  computed,
  Route
} = Ember;

export default Route.extend(
  Transition, Nav, Services, Results, Events, EventCount, EventTimeline, Meta, Recon, Context,
  {
    model() {
      return this.get('state');
    },

    state: computed(function() {
      return InvestigateState;
    })
  }
);
