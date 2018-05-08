import Ember from 'ember';

const {
  $,
  Route
} = Ember;

export default Route.extend({
  activate() {
    $('body').addClass('admin-source-management-engine-entry');
  }
});
