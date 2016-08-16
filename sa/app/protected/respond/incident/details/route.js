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
    this.set('layoutService.journalPanel', 'hidden');
    this.set('layoutService.main', 'panelC');
    this.set('layoutService.panelA', 'quarter');
    this.set('layoutService.panelB', 'quarter');
    this.set('layoutService.panelC', 'half');
  },

  deactivate() {
    this.set('layoutService.journalPanel', 'hidden');
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'quarter');
    this.set('layoutService.panelB', 'main');
    this.set('layoutService.panelC', 'hidden');
  },

  model() {
    return {};
  }

});
