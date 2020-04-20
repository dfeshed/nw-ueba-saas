import Route from '@ember/routing/route';

export default Route.extend({
  classNames: ['test123'],

  activate() {
    document.body.classList.add('engine-entry');
  }
});
