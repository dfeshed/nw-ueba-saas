import Ember from 'ember';
import config from './config/environment';

const { Router: EmberRouter } = Ember;

const Router = EmberRouter.extend({
  location: config.locationType
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
    });

    this.route('form', function() {
      this.route('buttons');
      this.route('selects');
      this.route('radios');
      this.route('checkboxes');
      this.route('textareas');
      this.route('inputs');
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
      this.route('ipConnections');
      this.route('tooltip');
    });

    this.route('nav', function() {
      this.route('linkList');
    });
  });

  this.route('demo', function() {
    this.route('content', function() {
      this.route('ipConnections');
    });
  });
});

export default Router;
