import Ember from 'ember';
import Transition from './actions/transition';
import Nav from './actions/nav';
import Services from './actions/services';
import Events from './actions/events';
import EventCount from './actions/event-count';
import InvestigateState from './state/index';

const {
  computed,
  Route
} = Ember;

export default Route.extend(
  Transition, Nav, Services, Events, EventCount,
  {
    model() {
      return this.get('state');
    },

    state: computed(function() {
      return InvestigateState;
    })
  }
);
