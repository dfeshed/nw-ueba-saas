import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
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

  @computed('allSelectedEventIds', 'limit')
  selectedEventIds(allSelectedEventIds, limit) {
    if (!allSelectedEventIds) {
      return [];
    } else {
      const allSelectedEventIdsArray = Object.values(allSelectedEventIds);
      if (!Number.isNaN(limit) && limit > 0) {
        return allSelectedEventIdsArray.slice(0, limit);
      }
      return allSelectedEventIdsArray;
    }
  },

  @computed('allSelectedEventIds', 'limit')
  isSelectedEventsLimitCrossed(allSelectedEventIds, limit) {
    if (!allSelectedEventIds) {
      return [];
    } else {
      const allSelectedEventIdsArray = Object.values(allSelectedEventIds);
      return allSelectedEventIdsArray && allSelectedEventIdsArray.length > limit;
    }
  },

  @computed('selectedEventIds')
  modalClassName(selectedEventIds) {
    if (selectedEventIds != null && selectedEventIds.length > 0) {
      return 'standard add-to-incident-modal investigate-panel';
    }
    return 'standard add-to-incident-modal respond-panel';
  },

  init() {
    this._super(arguments);
    this.get('eventBus').on('rsa-application-click', (target = '') => {
      if (target.classList && !(target.classList.contains('rsa-icon-arrow-down-12-filled') ||
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
