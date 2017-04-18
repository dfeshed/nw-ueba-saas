import Component from 'ember-component';
import layout from './template';
import computed, { alias } from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';
import * as InteractionActions from 'recon/actions/interaction-creators';

const dispatchToActions = (dispatch) => ({
  highlightMeta: (metaToHighlight) => dispatch(InteractionActions.highlightMeta(metaToHighlight))
});

const MetaContentItem = Component.extend({
  layout,
  tagName: 'hbox',
  classNames: ['recon-meta-content-item'],
  classNameBindings: ['isHovering', 'isSelected'],
  isHovering: false,

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
  mouseEnter() {
    if (this.get('isTextView')) {
      this.toggleProperty('isHovering');
    }
  },
  mouseLeave() {
    if (this.get('isTextView')) {
      this.toggleProperty('isHovering');
    }
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
