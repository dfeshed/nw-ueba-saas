import Ember from 'ember';
import layout from '../templates/components/rsa-application-action-bar';

const {
  Component,
  inject: {
    service
  }
} = Ember;


export default Component.extend({
  layout,

  eventBus: service('event-bus'),

  layoutService: service('layout'),

  classNames: ['rsa-application-action-bar'],

  actions: {
    toggleIncidentQueue() {
      this.get('layoutService').toggleIncidentQueue();
    },

    toggleJournal() {
      this.get('layoutService').toggleJournal();
    }
  }

});
