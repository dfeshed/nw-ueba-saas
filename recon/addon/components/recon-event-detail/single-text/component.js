import Component from 'ember-component';
import computed, { alias } from 'ember-computed-decorators';
import { later, scheduleOnce } from 'ember-runloop';

import SelectionTooltip from './selection-tooltip-mixin';
import { retrieveTranslatedData, prepareTextForDisplay } from './util';
import layout from './template';

const HIDE_CONTENT_CHARACTER_COUNT = 3000;
const SHOW_TRUNCATED_AMOUNT = 2000;
const CHUNK_SIZE = 10000;
const TIME_BETWEEN_CHUNKS = 750;

export default Component.extend(SelectionTooltip, {
  classNameBindings: ['packet.side', 'isSticky::rsa-text-entry'],
  layout,

  encDecStrBase64: null,
  encDecStrUrl: null,
  isLog: null,
  isSticky: false,
  metaToHighlight: null,
  packet: null,
  percentRendered: null, // updated as incremental rendering is taking place
  renderedAll: false,
  renderingRemainingText: false,
  stickyRenderedPercent: null,
  tooltipHeading: null,

  // Tooltip has two views depending upon being in IF/ELSE conditional
  // The IF conditional shows the final encoded/decoded text that has the closeButton X
  @alias('isActionClicked')
  hasCloseButton: null,

  /*
   * Up front determination if the packet data should be truncated
   * based on the size of the array and whether this is a stuck
   * version of the component.
   */
  @computed('isSticky', 'packet.text')
  shouldBeTruncated(isSticky, text) {
    return !isSticky && text.length > HIDE_CONTENT_CHARACTER_COUNT;
  },

  @computed('shouldBeTruncated', 'renderedAll')
  displayShowAllButton: (shouldBeTruncated, renderedAll) => shouldBeTruncated && !renderedAll,

  @computed('packet.text', 'percentRendered', 'isSticky', 'stickyRenderedPercent')
  displayedPercent(text, percentRendered, isSticky, stickyRenderedPercent) {
    // if this component is being used in a sticky
    // then just return stickyRenderedPercent which is passed up
    // from the actual component rendering the text
    if (isSticky && stickyRenderedPercent) {
      return stickyRenderedPercent;
    }

    // if incremental rendering is going on, it
    // updates percentRendered as it chunks in
    // content so just use that
    if (percentRendered) {
      return percentRendered;
    }

    // Otherwise this is just initial render of component
    if (text.length < HIDE_CONTENT_CHARACTER_COUNT) {
      return 100;
    }
    const displayedPercent = Math.floor((SHOW_TRUNCATED_AMOUNT / text.length) * 100);
    return displayedPercent;
  },

  /*
   * Builds message indicating how much text is left to show
   * or are in the process of rendering
   */
  @computed('displayedPercent', 'renderingRemainingText', 'percentRendered')
  remainingTextMessage(displayedPercent, renderingRemainingText, percentRendered) {
    let msg = '';

    // if rendering under way, use that percentage, otherwise use initial displayedPercent value
    // which doesn't update after initial rendering
    const percentToUse = percentRendered || displayedPercent;

    const percentLabel = {
      remainingPercent: (percentToUse === 0) ? '99+' : 100 - percentToUse
    };

    if (renderingRemainingText) {
      msg = this.get('i18n').t('recon.textView.renderRemaining', percentLabel);
    } else {
      msg = this.get('i18n').t('recon.textView.showRemaining', percentLabel);
    }
    return msg;
  },

  /*
   * Determines the text entries to display, truncated or not, and then
   * formats them for display.
   */
  @computed('packet.text', 'shouldBeTruncated', 'metaToHighlight.value')
  initialTextToDisplay(text, shouldBeTruncated, metaToHighlight) {
    let textEntriesReturn = text;
    if (shouldBeTruncated) {
      textEntriesReturn = text.substr(0, SHOW_TRUNCATED_AMOUNT);
    }

    return prepareTextForDisplay(textEntriesReturn, metaToHighlight);
  },

  // when we first render, need to highlight meta,
  // but if the meta to highlight then changes we need to
  // redo all the checks
  didReceiveAttrs() {
    this._super(...arguments);
    if (!this.get('isSticky')) {
      scheduleOnce('afterRender', this, this._checkForRenderRemainingText);
    }
  },

  // checks to see if presence of meta to highlight
  // means rest of text should be shown
  _checkForRenderRemainingText() {
    const metaToHighlight = this.get('metaToHighlight.value');

    // If meta to highlight is there, and
    // the content is truncated...
    if (metaToHighlight && this.get('shouldBeTruncated') && !this.get('renderedAll')) {
      const remainingText = this.get('packet.text').substring(SHOW_TRUNCATED_AMOUNT);
      const metaStringRegex = new RegExp(String(metaToHighlight), 'gi');
      const foundMatch = remainingText.match(metaStringRegex);
      // ...and the hidden content has the meta to highlight in it
      // then need to render that content
      if (foundMatch) {
        this._renderRemainingText();
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

  _renderRemainingText() {
    // Update rendering button to show status message
    this.set('renderingRemainingText', true);

    // Build array of text chunks to render
    const {
      'packet.text': packetText,
      'packet.firstPacketId': firstPacketId,
      'metaToHighlight.value': mth
    } = this.getProperties('packet.text', 'packet.firstPacketId', 'metaToHighlight.value');

    let remainingText = packetText.substr(SHOW_TRUNCATED_AMOUNT);
    let i = 0;
    while (remainingText.length > 0) {
      // get next chunk
      const chunk = remainingText.substr(0, CHUNK_SIZE);
      // remove that chunk from the string
      remainingText = remainingText.replace(chunk, '');
      // Schedule those chunks for rendering
      later(() => {
        // NOTE: this needs to be done with $ as opposed to any
        // sort of Ember-y thing. Any use of sub-components would
        // render additional unwanted DOM (and be needless code).
        // Any manipulation of text to display attached to a computed
        // will re-render the text each time. So have to brute
        // force this in.
        const text = prepareTextForDisplay(chunk, mth);

        // append the next batch of text and remove the
        // text node that creates an extra space between
        // this batch and the previous
        const $contents = this.$('.text-container').contents();
        this.$('.text-container').append(text.string);
        $contents.get($contents.length - 1).remove();
        const percentRendered = Math.ceil((this.$('.text-container').text().length / packetText.length) * 100);

        // save/send notifications about the amount of text that has
        // been rendered so far
        this.set('percentRendered', percentRendered);
        this.sendAction('updatePercentRendered', { id: firstPacketId, percentRendered });
      }, i++ * TIME_BETWEEN_CHUNKS);
    }

    // And now when all is done, send/set flag indicating all have been rendered.
    later(() => {
      this.set('renderedAll', true);
      this.sendAction('showMoreFinished', firstPacketId);
    }, (i - 1) * TIME_BETWEEN_CHUNKS);
  },

  actions: {
    decodeText() {
      this._handleEncodeDecode('decode', 'Decoded');
    },

    encodeText() {
      this._handleEncodeDecode('encode', 'Encoded');
    },

    showRemainingText() {
      this._renderRemainingText();
    }
  }
});
