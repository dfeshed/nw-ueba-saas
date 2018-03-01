import Component from '@ember/component';
import { run } from '@ember/runloop';
import { observer } from '@ember/object';

import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { defaultMetaGroup } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { META_PANEL_SIZES } from 'investigate-events/constants/panelSizes';

const stateToComputed = (state) => ({
  group: defaultMetaGroup(state),
  size: state.investigate.data.metaPanelSize,
  query: state.investigate.queryNode,
  metaKeyStates: state.investigate.eventResults.metaKeyStates
});

const MetaViewComponent = Component.extend({
  tagName: 'article',
  classNames: 'rsa-investigate-meta',
  classNameBindings: ['_sizeClass'],

  metaPanelSizes: META_PANEL_SIZES,

  /**
   * Duration (in millisec) of delay between opening of component & revealing
   * its DOM content.
   * @public
   */
  unhideDelay: 250,

  /**
   * Private size tracker.
   * @private
   */
  _size: 'default',

  /**
   * Converts `size` to CSS class equivalent.
   * @private
   */
  @computed('size')
  _sizeClass: (size) => `meta-size-${size}`,

  /**
   * Reacts to the size specified for this component. Sizes are either
   * minimized ('min'), maximized ('max') or default ('default').
   * @private
   */
  _sizeWatcher: observer('size', (sender) => {
    const prevSize = sender.get('_size');
    const currentSize = sender.get('size');
    const changed = prevSize !== currentSize;
    if (changed) {
      if (currentSize === 'min') {
        sender._didClose();
      } else if (prevSize === 'min') {
        sender._didOpen();
      }
      sender.set('_size', currentSize);
    }
  }),

  willDestroy() {
    this._cancelUnhideTimer();
    this._super(...arguments);
  },

  /**
   * Invoked after `size` changes to `min`. Responsible for hiding DOM content.
   * Cancels any pending timer to unhide content.
   * @private
   */
  _didClose() {
    this._cancelUnhideTimer();
    this.set('hideDom', true);
  },

  /**
   * Invoked after `size` changes from `min` to something else. Responsible for
   * un-hiding DOM content after a delay, which gives the resize animation time
   * to render smoothly and improves perceived performance. If a timer to
   * un-hide the DOM is already in progress, let it continue and exit.
   * @private
   */
  _didOpen() {
    if (!this._unhideTimer) {
      this._unhideTimer = run.later(() => {
        this.set('hideDom', false);
      }, this.get('unhideDelay'));
    }
  },

  /**
   * Cancels any pending timer for unhiding the DOM.
   * @private
   */
  _cancelUnhideTimer() {
    if (this._unhideTimer) {
      run.cancel(this._unhideTimer);
      this._unhideTimer = null;
    }
  }
});

export default connect(stateToComputed)(MetaViewComponent);
