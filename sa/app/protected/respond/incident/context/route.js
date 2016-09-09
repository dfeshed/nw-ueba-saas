import Ember from 'ember';

const {
  Route,
  inject: {
    service
  }
} = Ember;

export default Route.extend({
  layoutService: service('layout'),

  activate() {
    this.set('journalPanel', 'hidden');
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'hidden');
    this.set('layoutService.panelB', 'half');
    this.set('layoutService.contextPanel', 'half');
  },

  deactivate() {
    this.set('layoutService.journalPanel', 'hidden');
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'quarter');
    this.set('layoutService.panelB', 'main');
    this.set('layoutService.contextPanel', 'hidden');
  },

  model() {
    return {};
  }

});
