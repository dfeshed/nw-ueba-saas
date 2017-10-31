import { connect } from 'ember-redux';
import Button from 'component-lib/components/rsa-form-button';
import computed from 'ember-computed-decorators';
import run from 'ember-runloop';
import $ from 'jquery';

import {
  setActiveFilter
} from 'investigate-hosts/actions/data-creators/filter';

const stateToComputed = ({ endpoint }) => ({
  activeFilter: endpoint.filter.activeFilter
});


const dispatchToActions = {
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
          this.sendAction('setActiveButton', null); // remove active class from all the button
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
