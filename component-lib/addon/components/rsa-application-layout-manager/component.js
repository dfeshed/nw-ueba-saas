import { alias } from 'ember-computed';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import layout from './template';

export default Component.extend({

  eventBus: service(),

  layoutService: service('layout'),

  layout,

  tagName: 'hbox',

  classNames: ['rsa-application-layout-manager'],

  classNameBindings: [
    'layoutService.journalPanelClass',
    'layoutService.contextPanelClass',
    'layoutService.panelAClass',
    'layoutService.panelBClass',
    'layoutService.panelCClass',
    'layoutService.panelDClass',
    'layoutService.panelEClass'],

  journalPanel: alias('layoutService.journalPanel'),
  contextPanel: alias('layoutService.contextPanel'),

  panelA: alias('layoutService.panelA'),
  panelB: alias('layoutService.panelB'),
  panelC: alias('layoutService.panelC'),
  panelD: alias('layoutService.panelD'),
  panelE: alias('layoutService.panelE'),

  main: alias('layoutService.main')

});
