import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  classNames: ['rsa-panel-message'],
  classNameBindings: ['messageType', 'messagePosition', 'testCss'],
  /**
   * Message type gives the developer the option to hand in there on CSS style
   * @public
   */
  messageType: undefined,
  /**
   * This css value is here for QA/QE test
   * @public
   */
  testCss: undefined,
  /**
   * Place the title and message in the center of the view,
   * if the message position is set to center.
   * Opitons are center, left, and right.
   * Default is center.
   * @public
   */
  messagePosition: 'center',
  /**
   * The title of the message - this is not required.
   * @public
   */
  title: undefined,
  /**
   * The message to display under the title
   * @public
   */
  message: undefined
});
