import Ember from 'ember';

const {
  Route,
  inject: {
    service
  }
 } = Ember;

export default Route.extend({

  layoutService: service('layout'),

  model() {
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'quarter');
    this.set('layoutService.panelB', 'main');
    this.set('layoutService.panelC', 'quarter');
    this.set('layoutService.panelD', 'main');

    return {
      'title': 'Application Layout Manager',
      'subtitle': 'Flexible layout and workflow manager.',
      'description': 'The Application Layout Manager is used with the Layout Service to manage transitions between routes and/or user interactions. The Service can be called from Routes, Components, or controllers to update the layout.',
      'testFilter': 'rsa-application-layout-manager',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-application-layout-manager.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_application-layout-manager.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-application-layout-manager.hbs'
    };
  },

  actions: {
    expandA() {
      this.set('layoutService.main', 'panelA');
      this.set('layoutService.panelA', 'main');
      this.set('layoutService.panelB', 'quarter');
    },

    expandB() {
      this.set('layoutService.main', 'panelB');
      this.set('layoutService.panelA', 'quarter');
      this.set('layoutService.panelB', 'main');
    },

    expandC() {
      this.set('layoutService.main', 'panelC');
      this.set('layoutService.panelC', 'main');
      this.set('layoutService.panelD', 'quarter');
    },

    expandDFullWidth() {
      this.get('layoutService').toggleFullWidthPanel('panelD');
    }

  }
});
