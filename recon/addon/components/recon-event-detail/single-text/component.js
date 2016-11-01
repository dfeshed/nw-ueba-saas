import Ember from 'ember';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

import layout from './template';
import { not, readOnly } from 'ember-computed-decorators';
const { Component, on, run } = Ember;

const stateToComputed = ({ recon: { visuals } }) => ({
  isRequestShown: visuals.isRequestShown,
  isResponseShown: visuals.isResponseShown
});

const SingleTextComponent = Component.extend({
  layout,
  tagName: 'section',
  classNames: 'rsa-packet',
  classNameBindings: ['packet.side'],
  packet: null,
  index: null,
  viewportEntered: false,
  @readOnly @not('viewportEntered') viewportExited: null,

  /**
   * Determine the direction, request or response, for the arrow
   * @param side Request or response
   * @returns {string} right or left
   * @public
   */
  @computed('packet.side')
  arrowDirection(side) {
    return side === 'request' ? 'right' : 'left';
  },

  @computed('packet.side', 'isRequestShown', 'isResponseShown')
  shouldShowPacket(side, isRequestShown, isResponseShown) {
    return (side === 'request' && isRequestShown) || (side === 'response' && isResponseShown);
  },

  /**
   * Observe the component's this.element intersecting with the root element
   * @private
   */
  setupIntersectionObserver: on('didInsertElement', function() {
    const options = {
      rootMargin: '2000px 0px 2000px 0px',
      threshold: 0
    };

    const observer = new IntersectionObserver(([entry]) => {
      run(() => {
        // If intersectionRatio <= 0 it is hidden
        this.set('viewportEntered', entry.intersectionRatio > 0);
      });
    }, options);

    observer.observe(this.element);

    this.set('observer', observer);
  }),
  willDestroyElement() {
    this.get('observer').disconnect();
  },
  actions: {
    expandPacket() {
      this.toggleProperty('packetIsExpanded');
    }
  }
});

export default connect(stateToComputed)(SingleTextComponent);