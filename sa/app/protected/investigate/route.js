import Ember from 'ember';
import Transition from './actions/transition';
import Nav from './actions/nav';
import Services from './actions/services';
import Events from './actions/events';
import EventCount from './actions/event-count';
import EventTimeline from './actions/event-timeline';
import InvestigateState from './state/index';
import Meta from './actions/meta';

const {
  computed,
  Route
} = Ember;

export default Route.extend(
  Transition, Nav, Services, Events, EventCount, EventTimeline, Meta,
  {
    model() {
      return this.get('state');
    },

    state: computed(function() {
      return InvestigateState;
    })
  }
);
