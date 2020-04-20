import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { htmlSafe } from '@ember/string';
import { initializeAlerts } from 'respond-shared/actions/creators/create-incident-creators';

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return htmlSafe(`top: ${elRect.height - 2}px`);
  } else {
    return null;
  }
};

export default Component.extend({
  layout,

  redux: service(),

  eventBus: service(),

  classNames: ['incident-toolbar'],

  classNameBindings: ['class', 'isExpanded:expanded:collapsed'],

  activeModalId: null,

  isExpanded: false,

  offsetsStyle: null,

  selectedEventIds: computed('allSelectedEventIds', 'limit', function() {
    if (this.allSelectedEventIds) {
      const allSelectedEventIdsArray = Object.values(this.allSelectedEventIds);
      if (!Number.isNaN(this.limit) && this.limit > 0) {
        return allSelectedEventIdsArray.slice(0, this.limit);
      }
      return allSelectedEventIdsArray;
    }
  }),

  isSelectedEventsLimitCrossed: computed('allSelectedEventIds', 'limit', function() {
    if (!this.allSelectedEventIds) {
      return [];
    } else {
      const allSelectedEventIdsArray = Object.values(this.allSelectedEventIds);
      return allSelectedEventIdsArray && allSelectedEventIdsArray.length > this.limit;
    }
  }),

  modalClassName: computed('selectedEventIds', function() {
    if (this.selectedEventIds != null && this.selectedEventIds.length > 0) {
      return 'standard add-to-incident-modal investigate-panel';
    }
    return 'standard add-to-incident-modal respond-panel';
  }),

  init() {
    this._super(arguments);
    this.get('eventBus').on('rsa-application-click', (target = '') => {
      if (target.classList && !(target.classList.contains('rsa-icon-arrow-down-12') ||
        target.classList.contains('rsa-form-button')) &&
        (!this.isDestroyed || !this.isDestroying)) {
        if (this.get('isExpanded')) {
          this.toggleProperty('isExpanded');
        }
      }
    });
    const redux = this.get('redux');
    redux.dispatch(initializeAlerts());
  },

  actions: {
    showModal(modalId) {
      this.set('activeModalId', modalId);
      next(() => {
        this.get('eventBus').trigger(`rsa-application-modal-open-${modalId}`);
      });
    },
    closeModal(modalId) {
      this.get('eventBus').trigger(`rsa-application-modal-close-${modalId}`);
      this.set('activeModalId', null);
    },
    createIncident() {
      this.send('showModal', 'create-incident');
    },
    addToIncident() {
      this.send('showModal', 'add-to-incident');
    },
    clickOutside() {
      if (this.get('isExpanded')) {
        this.toggleProperty('isExpanded');
      }
    },
    toggleExpand() {
      this.set('offsetsStyle', menuOffsetsStyle(this.get('element')));
      this.toggleProperty('isExpanded');
    }

  }
});
