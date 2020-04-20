import Ember from 'ember';

const {
  Route
} = Ember;

export default Route.extend({
  activate() {
    const body = document.querySelector('body');
    if (!body.classList.contains('admin-source-management-engine-entry')) {
      body.classList.add('admin-source-management-engine-entry');
    }
  }
});
