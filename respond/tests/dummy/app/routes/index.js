import Route from '@ember/routing/route';

export default Route.extend({
  classNames: ['test123'],

  activate() {
    const body = document.querySelector('body');
    body.classList.contains('respond-engine-entry') || body.classList.add('respond-engine-entry');
  }
});
