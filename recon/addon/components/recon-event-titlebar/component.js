import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';
import { TYPES } from '../../utils/reconstruction-types';

const { Component } = Ember;

export default Component.extend({
  layout,
  tagName: 'hbox',
  classNameBindings: [':recon-event-titlebar'],

  // INPUTS
  index: undefined,
  reconstructionType: undefined,
  total: undefined,
  showHeaderData: null,
  showMetaDetails: null,

  // Actions
  closeRecon: null,
  expandRecon: null,
  shrinkRecon: null,
  toggleHeaderData: null,
  toggleMetaDetails: null,
  updateReconstructionView: null,
  // END INPUTS

  isExpanded: false,

  @computed('reconstructionType')
  reconViewsConfig({ code }) {
    return TYPES.map((c) => {
      return { ...c, selected: c.code === code };
    });
  },

  /**
   * Dynamically builds the recon type selection prompt.
   * Adds 1 to index as it is 0 based.
   *
   * @type {string} The title to display
   * @public
   */
  @computed('reconstructionType', 'index', 'total')
  displayTitle: ({ label }, index, total) => {
    if (index !== undefined) {
      label = `${label} (${index + 1} of ${total})`;
    }
    return label;
  },

  @computed('isExpanded')
  arrowDirection: (isExpanded) => (isExpanded) ? 'right' : 'left',

  actions: {
    toggleExpanded() {
      const isExpanded = this.get('isExpanded');
      if (isExpanded) {
        this.sendAction('shrinkRecon');
        // when shrinking recon, need to make sure to hide meta
        this.sendAction('toggleMetaDetails', true);
      } else {
        this.sendAction('expandRecon');
      }
      this.set('isExpanded', !isExpanded);
    },

    toggleMetaDetails() {
      // need to expand recon to have meta open
      if (!this.get('isExpanded')) {
        this.send('toggleExpanded');
      }
      this.sendAction('toggleMetaDetails');
    },

    findNewReconstructionView([code]) {
      // codes are int, comes in as string from form-select
      const newView = TYPES.findBy('code', parseInt(code, 10));
      if (newView) {
        this.sendAction('updateReconstructionView', newView);
      }
    }
  }

});
