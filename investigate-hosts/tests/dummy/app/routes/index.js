import Route from '@ember/routing/route';

export default Route.extend({
  classNames: ['test123'],

  activate() {
    document.querySelector('body').addClass('engine-entry');
  }
});
