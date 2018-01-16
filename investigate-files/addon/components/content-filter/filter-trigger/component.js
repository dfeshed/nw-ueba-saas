import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import run from 'ember-runloop';
import $ from 'jquery';

import Button from 'component-lib/components/rsa-form-button';
import {
  removeFilter,
  setActiveFilter
} from 'investigate-files/actions/data-creators';

const stateToComputed = ({ files }) => ({
  activeFilter: files.filter.activeFilter
});

const dispatchToActions = {
  removeFilter,
  setActiveFilter
};

/**
 * Trigger for content filter
 * @public
 */
const TriggerButton = Button.extend({

  /**
   * Unique panel ID
   * @public
   * @property
   */
  panelId: null,

  /**
   * Flag to indicate show or hide close button
   * @public
   * @property
   */
  showRemoveButton: false,

  /**
   * Label for the panel
   * @public
   * @property
   */
  filterLabel: '',

  @computed('activeFilter', 'panelId')
  isActive: (activeFilter, panelId) => {
    return activeFilter === panelId;
  },

  click() {
    if (!this.get('isDisabled')) {
      this.send('setActiveFilter', this.get('panelId'));
    }
  },

  willDestroyElement() {
    this._super(...arguments);
    this.get('eventBus').off('rsa-application-click', this, 'onApplicationClick');
  },

  onApplicationClick(target) {
    if (!$(target).closest('.rsa-form-button').length > 0) {
      run.next(() => {
        if (!this.get('isDestroyed') && !this.get('isDestroying')) {
          this.send('setActiveFilter', null); // remove active class from all the button
        }
      });
    }
  },

  didInsertElement() {
    this._super(...arguments);
    this.get('eventBus').on('rsa-application-click', this, 'onApplicationClick');
  }
});

export default connect(stateToComputed, dispatchToActions)(TriggerButton);
