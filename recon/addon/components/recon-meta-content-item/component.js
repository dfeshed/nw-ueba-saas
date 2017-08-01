import Component from 'ember-component';
import layout from './template';
import computed, { alias } from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { highlightMeta } from 'recon/actions/interaction-creators';

import metaToLimit from './limited-meta';

const dispatchToActions = {
  highlightMeta
};

const MetaContentItem = Component.extend({
  layout,
  tagName: 'hbox',
  classNames: ['recon-meta-content-item'],
  classNameBindings: ['isHovering', 'isSelected'],
  isHovering: false,

  // Passed in, boolean, whether or not the event has payload to view
  eventHasPayload: undefined,
  // Passed in, boolean for if text view or not
  isTextView: undefined,
  // Passed in, null or object with {name, value}
  metaToHighlight: undefined,

  @alias('item.0') name: null,
  @alias('item.1') value: null,
  /**
   * Determines if the meta should be highlighted and selected
   * @param {boolean} isTextView If text view or not, so we can deselect on other views
   * @param {object} metaToHighlight The meta to highlighted, passed down in, grabbed from redux
   * @param {string|*} name The name of the meta key
   * @param {string|*} value The value for the meta
   * @returns {boolean} If selected or not
   * @private
   */
  @computed('isTextView', 'metaToHighlight', 'name', 'value')
  isSelected(isTextView, metaToHighlight, name, value) {
    if (metaToHighlight && isTextView) {
      return name === metaToHighlight.name && String(metaToHighlight.value) === String(value);
    }

    return false;
  },

  /*
   * Only show meta highlight binoculars if:
   * 1) User is hovering or has already selected the item
   * 2) And the user is on text view
   * 3) And the event actually has payload to highlight
   * 4) And the meta is one of the metas that are highlightable
   */
  @computed('isHovering', 'isSelected', 'isTextView', 'eventHasPayload')
  shouldShowBinoculars(isHovering, isSelected, isTextView, eventHasPayload) {
    return (isHovering || isSelected) && isTextView && eventHasPayload && metaToLimit.includes(this.get('name'));
  },

  @computed('isSelected', 'isTextView', 'eventHasPayload')
  shouldShowHighlightScroller(isSelected, isTextView, eventHasPayload) {
    return isSelected && isTextView && eventHasPayload && metaToLimit.includes(this.get('name'));
  },

  mouseEnter() {
    this.set('isHovering', true);
  },

  mouseLeave() {
    this.set('isHovering', false);
  },

  actions: {
    /**
     * Select/deselect meta when binoculars are clicked
     * @private
     */
    clickBinoculars() {
      const name = this.get('name');
      const value = this.get('value');
      let newlySelected = { name, value };
      const previouslySelected = this.get('metaToHighlight');
      // If we already had selected some meta, and it is the same one we just clicked, we need to deselect
      if (previouslySelected && previouslySelected.name === name) {
        newlySelected = null;
      }
      this.send('highlightMeta', newlySelected);
    }
  }

});

export default connect(null, dispatchToActions)(MetaContentItem);
