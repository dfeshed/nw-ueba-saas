import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  classNames: ['error-page'],
  /**
   * Back is a route-action passed in from the application route that just calls history.back()
   * @public
   */
  back: undefined,
  /**
   * The subtitle to display under the title
   * @public
   */
  subtitle: undefined,
  /**
   * The title of the error, e.g. "404".
   * @public
   */
  title: undefined,
  actions: {
    back() {
      this.back();
    }
  }
});
