import Ember from 'ember';
import config from './config/environment';

const { Router: EmberRouter } = Ember;

const Router = EmberRouter.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.route('design', function() {
    this.route('typography');
    this.route('colors');
    this.route('grid');
    this.route('layers');
    this.route('opacity');
    this.route('whitespace');
  });

  this.route('demos', function() {
    this.route('logo');
    this.route('icons');
    this.route('loader');
    this.route('routableLogin');

    this.route('app', function() {
      this.route('header');
      this.route('content');
      this.route('footer');
      this.route('modal');
      this.route('action-bar');
      this.route('layout-manager');
      this.route('fatal-error');
      this.route('contextual-help');
      this.route('flash-messages');
      this.route('panels');
      this.route('permissions');
      this.route('page-titles');
      this.route('panel-message');
    });

    this.route('chart', function() {
      this.route('chart');
      this.route('xAxis');
      this.route('yAxis');
      this.route('lineSeries');
      this.route('areaSeries');
      this.route('grids');
    });

    this.route('content', function() {
      this.route('sectionHeader');
      this.route('accordion');
      this.route('card');
      this.route('definition');
      this.route('badgeScore');
      this.route('badgeIcon');
      this.route('datetime');
      this.route('label');
      this.route('memorySize');
      this.route('ipConnections');
      this.route('tetheredPanels');
      this.route('contextMenu');
    });

    this.route('form', function() {
      this.route('buttons');
      this.route('selects');
      this.route('radios');
      this.route('checkboxes');
      this.route('textareas');
      this.route('inputs');
      this.route('sliders');
      this.route('datetime');
      this.route('errors');
      this.route('switch');
    });

    this.route('nav', function() {
      this.route('linkList');
      this.route('tab');
      this.route('linkToWin');
    });

    this.route('table');
  });

  this.route('visual-evolution');
});

export default Router;
