import Ember from 'ember';

const {
  $,
  Route
} = Ember;

export default Route.extend({
  classNames: ['test123'],

  activate() {
    $('body').addClass('dark-theme');
    $('body').addClass('respond-engine-entry');
  }
});
