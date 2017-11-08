import Component from 'ember-component';
import layout from './template';
import computed, { alias, bool } from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { highlightMeta } from 'recon/actions/interaction-creators';

const stateToComputed = ({ recon: { data: { endpointId, startTime, endTime } } }) => ({
  endpointId,
  startTime,
  endTime
});

const dispatchToActions = {
  highlightMeta
};

const MetaContentItem = Component.extend({
  layout,
  tagName: 'hbox',
  classNames: ['recon-meta-content-item'],
  classNameBindings: ['isHovering', 'isSelected'],
  isHovering: false,

  /**
   * Whether or not the event has payload to view.
   * @type {boolean}
   * @public
   */
  eventHasPayload: undefined,

  /**
   * If text view or not.
   * @type {boolean}
   * @public
   */
  isTextView: undefined,

  /**
   * null or object with {name, value}
   * @type {object}
   * @public
   */
  metaToHighlight: undefined,

  /**
   * Array of meta objects with the number of occurances within the text.
   * @type {object[]}
   * @public
   */
  metaHighlightCount: undefined,

  /**
   * The meta name
   * @type {string}
   * @public
   */
  @alias('item.0')
  name: null,

  /**
   * The meta value
   * @type {*}
   * @public
   */
  @alias('item.1')
  value: null,

  /**
   * Determines if the meta should be highlighted and selected
   * @param {boolean} isTextView If text view or not, so we can deselect on other views
   * @param {object} metaToHighlight The meta to highlighted, passed down in, grabbed from redux
   * @param {string} name The name of the meta key
   * @param {*} value The value for the meta
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

  /**
   * The number of times the meta value exists in the text.
   * @param {string} name The meta name.
   * @param {object[]} metaHighlightCount List of all meta and their counts
   * contained within the text.
   * @return {number}
   * @public
   */
  @computed('name', 'value', 'metaHighlightCount')
  totalCount(name, value, metaHighlightCount) {
    let count = 0;
    if (metaHighlightCount) {
      const meta = metaHighlightCount.find(
        (meta) => meta.name === name && meta.value === value
      );
      count = (meta) ? meta.count : 0;
    }
    return count;
  },

  /**
   * Is there meta to highlight.
   * @type {boolean}
   * @public
   */
  @bool('totalCount')
  hasMetaToHighlight: false,

  /*
   * Only show meta highlight binoculars if:
   * 1) User is hovering or has already selected the item
   * 2) The event actually has payload to highlight
   * 3) The meta exists in the text
   */
  @computed('isHovering', 'isSelected', 'eventHasPayload', 'hasMetaToHighlight')
  shouldShowBinoculars(isHovering, isSelected, eventHasPayload, hasMetaToHighlight) {
    return (isHovering || isSelected) && eventHasPayload && hasMetaToHighlight;
  },

  @computed('isSelected', 'eventHasPayload', 'hasMetaToHighlight')
  shouldShowHighlightScroller(isSelected, eventHasPayload, hasMetaToHighlight) {
    return isSelected && eventHasPayload && hasMetaToHighlight;
  },

  mouseEnter() {
    this.set('isHovering', true);
  },

  mouseLeave() {
    this.set('isHovering', false);
  },

  @computed('name', 'value', 'endpointId', 'startTime', 'endTime')
  contextSelection: (metaName, metaValue, endpointId, startTime, endTime) => ({ metaName, metaValue, endpointId, startTime, endTime }),

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

export default connect(stateToComputed, dispatchToActions)(MetaContentItem);
