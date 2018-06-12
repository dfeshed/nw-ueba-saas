import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import layout from './template';
import { connect } from 'ember-redux';

const stateToComputed = ({ recon: { text } }) => ({
  isItemTooLarge: text.itemTooLarge
});

const SingleTextHeaderComponent = Component.extend({
  classNameBindings: ['isSticky', 'side'],
  classNames: ['recon-request-response-header'],
  layout,

  isLog: false,
  isEndpoint: false,
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
  },

  @computed('displayedPercent', 'renderedAll')
  showPercentMessage(displayedPercent, allContentIsRendered) {
    return !allContentIsRendered && displayedPercent !== 100;
  },

  @computed('displayedPercent')
  percentText(displayedPercent) {
    return (displayedPercent === 0) ? '< 1' : displayedPercent;
  },

  @alias('isItemTooLarge')
  showTruncatedMessage: null

});

export default connect(stateToComputed)(SingleTextHeaderComponent);
