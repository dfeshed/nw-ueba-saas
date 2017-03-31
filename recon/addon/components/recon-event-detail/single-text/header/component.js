import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import layout from './template';

export default Component.extend({
  classNameBindings: ['isSticky', 'side', 'isSticky:rsa-packet__header'],
  classNames: ['request-response-header'],
  layout,

  isLog: false,
  isSticky: false,
  side: null,

  /**
   * Determine the direction, request or response, for the arrow
   * @param side Request or response
   * @returns {string} right or left
   * @public
   */
  @computed('side')
  arrowDirection(side) {
    return side === 'request' ? 'right' : 'left';
  }
});
