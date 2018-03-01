import $ from 'jquery';
import Route from '@ember/routing/route';

export default Route.extend({
  classNames: ['test123'],

  activate() {
    $('body').addClass('respond-engine-entry');
  }
});
