import Component from 'ember-component';
import computed, { alias } from 'ember-computed-decorators';
import { later, scheduleOnce } from 'ember-runloop';

import SelectionTooltip from './selection-tooltip-mixin';
import { retrieveTranslatedData, prepareLinesForDisplay } from './util';
import layout from './template';

const HIDE_PACKETS_LINE_COUNT = 250;
const SHOW_TRUNCATED_AMOUNT = 100;
const CHUNK_SIZE = 250;
const TIME_BETWEEN_CHUNKS = 250;

export default Component.extend(SelectionTooltip, {
  classNames: ['rsa-text-entry'],
  classNameBindings: ['packet.side'],
  layout,

  encDecStrBase64: null,
  encDecStrUrl: null,
  index: null,
  isLog: null,
  metaToHighlight: null,
  packet: null,
  renderedAll: false,
  renderingRemainingLines: false,
  tooltipHeading: null,

  // Tooltip has two views depending upon being in IF/ELSE conditional
  // The IF conditional shows the final encoded/decoded text that has the closeButton X
  @alias('isActionClicked') hasCloseButton: null,

  @computed('shouldBeTruncated', 'renderedAll')
  displayShowAllButton: (shouldBeTruncated, renderedAll) => shouldBeTruncated && !renderedAll,

  /*
   * Up front determination if the packet data should be truncated
   * based on the size of the array and whether or not the content
   * of the text has highlighted stuff. If content is highlighted
   * then have to make it show up.
   */
  @computed('packet.text')
  shouldBeTruncated(textEntries = []) {
    return textEntries.length > HIDE_PACKETS_LINE_COUNT;
  },

  /*
   * Builds message indicating how many lines are left to show
   * or are in the process of rendering
   */
  @computed('packet.text', 'renderingRemainingLines')
  remainingLineCountMessage(textEntries = [], renderingRemainingLines) {
    const remainingLineCount = textEntries.length - SHOW_TRUNCATED_AMOUNT;
    let msg = '';
    if (renderingRemainingLines) {
      msg = `Rendering ${remainingLineCount} more lines...`;
    } else {
      msg = `Show Remaining ${remainingLineCount} Lines`;
    }
    return msg;
  },

  /*
   * Determines the text entries to display, truncated or not, and then
   * formats them for display.
   */
  @computed('packet.text', 'shouldBeTruncated', 'metaToHighlight.value')
  initialTextEntriesToDisplay(textEntries = [], shouldBeTruncated, metaToHighlight) {
    let textEntriesReturn = textEntries;
    if (shouldBeTruncated) {
      textEntriesReturn = textEntriesReturn.slice(0, SHOW_TRUNCATED_AMOUNT);
    }

    return prepareLinesForDisplay(textEntriesReturn, metaToHighlight);
  },

  // when we first render, need to highlight meta,
  // but if the meta to highlight then changes we need to
  // redo all the checks
  didReceiveAttrs() {
    this._super(...arguments);
    scheduleOnce('afterRender', this, this._checkForRenderRemainingLines);
  },

  // checks to see if presence of meta to highlight
  // means rest of text should be shown
  _checkForRenderRemainingLines() {
    const metaToHighlight = this.get('metaToHighlight.value');

    // If meta to highlight is there, and
    // the content is truncated...
    if (metaToHighlight && this.get('shouldBeTruncated') && !this.get('renderedAll')) {
      const remainingLines = this.get('packet.text').slice(SHOW_TRUNCATED_AMOUNT);
      const metaStringRegex = new RegExp(String(metaToHighlight), 'gi');
      const foundMatch = remainingLines.join('<br>').match(metaStringRegex);
      // ...and the hidden content has the meta to highlight in it
      // then need to render that content
      if (foundMatch) {
        this._renderRemainingLines();
      }
    }
  },

  _handleEncodeDecode(type, label) {
    const string = this.get('originalString');
    const { encDecStrBase64, encDecStrUrl } = retrieveTranslatedData(type, string);
    this.setProperties({
      isActionClicked: true,
      tooltipHeading: `${label} Text`,
      encDecStrBase64,
      encDecStrUrl
    });
  },

  _renderRemainingLines() {
    // Update rendering button to show status message
    this.set('renderingRemainingLines', true);

    // Build array of text chunks to render
    const remainingLines = this.get('packet.text').slice(SHOW_TRUNCATED_AMOUNT);
    const mth = this.get('metaToHighlight.value');
    let i = 0;
    while (remainingLines.length > 0) {
      const chunk = remainingLines.splice(0, CHUNK_SIZE);
      // Schedule those chunks for rendering
      later(() => {
        // NOTE: this needs to be done with $ as opposed to any
        // sort of Ember-y thing. Any use of sub-components would
        // render additional unwanted DOM (and be needless code).
        // Any manipulation of text to display attached to a computed
        // will re-render the text each time. So have to brute
        // force this in.
        const text = prepareLinesForDisplay(chunk, mth);
        this.$('.text-container').append(`<br>${text}`);
      }, i++ * TIME_BETWEEN_CHUNKS);
    }

    // And now when all is done, set flag indicating all have been rendered.
    later(() => {
      this.set('renderedAll', true);
    }, (i - 1) * TIME_BETWEEN_CHUNKS);
  },

  actions: {
    decodeText() {
      this._handleEncodeDecode('decode', 'Decoded');
    },

    encodeText() {
      this._handleEncodeDecode('encode', 'Encoded');
    },

    showRemainingLines() {
      this._renderRemainingLines();
    }
  }
});