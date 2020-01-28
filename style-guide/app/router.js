import EmberRouter from '@ember/routing/router';
import config from './config/environment';

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
    this.route('gauge');

    this.route('app', function() {
      this.route('header');
      this.route('content');
      this.route('footer');
      this.route('modal');
      this.route('action-bar');
      this.route('layout-manager');
      this.route('fatal-error');
      this.route('standard-errors');
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
      this.route('donut');
    });

    this.route('content', function() {
      this.route('sectionHeader');
      this.route('accordion');
      this.route('definition');
      this.route('badgeScore');
      this.route('datetime');
      this.route('label');
      this.route('memorySize');
      this.route('tetheredPanels');
      this.route('contextMenu');
      this.route('pageLayout');
      this.route('riskScore');
    });

    this.route('form', function() {
      this.route('buttons');
      this.route('button-with-confirmation');
      this.route('selects');
      this.route('radios');
      this.route('checkboxes');
      this.route('textareas');
      this.route('textareas-oneway');
      this.route('inputs');
      this.route('inputs-oneway');
      this.route('sliders');
      this.route('datetime');
      this.route('date-time-range');
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
