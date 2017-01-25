import Ember from 'ember';
import Transition from 'investigate/actions/transition';
import Nav from 'investigate/actions/nav';
import Services from 'investigate/actions/services';
import Results from 'investigate/actions/results';
import Events from 'investigate/actions/events';
import EventCount from 'investigate/actions/event-count';
import EventTimeline from 'investigate/actions/event-timeline';
import InvestigateState from 'investigate/state/index';
import Meta from 'investigate/actions/meta';
import Recon from 'investigate/actions/recon';
import Context from 'investigate/actions/context';

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
