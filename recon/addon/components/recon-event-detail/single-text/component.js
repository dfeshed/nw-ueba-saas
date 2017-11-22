import Component from 'ember-component';
import computed, { alias } from 'ember-computed-decorators';
import { later, scheduleOnce, next } from 'ember-runloop';

import SelectionTooltip from './selection-tooltip-mixin';
import { retrieveTranslatedData } from './util';
import layout from './template';
import copyToClipboard from 'component-lib/utils/copy-to-clipboard';

const HIDE_CONTENT_CHARACTER_COUNT = 3000;
const SHOW_TRUNCATED_AMOUNT = 2000;
const CHUNK_SIZE = 6000;
let TIME_BETWEEN_CHUNKS = 1250;

// gimp IE along since it renders text slowly
const IS_IE = !!window.document.documentMode;
if (IS_IE) {
  TIME_BETWEEN_CHUNKS = 2500;
}

const SUPPORTS_COPY_PASTE = document.queryCommandSupported && document.queryCommandSupported('copy');

export default Component.extend(SelectionTooltip, {
  classNameBindings: ['packet.side', 'isSticky::rsa-text-entry'],
  layout,

  encDecStrBase64: null,
  encDecStrUrl: null,
  isLog: false,
  isEndpoint: false,
  isSticky: false,
  metaToHighlight: null,
  packet: null,
  percentRendered: null, // updated as incremental rendering is taking place
  portionsToRender: [],
  renderedAll: false,
  renderingRemainingText: false,
  stickyRenderedPercent: null,
  tooltipHeading: null,
  supportsCopyPaste: SUPPORTS_COPY_PASTE,

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
   * Calculated once, is the superset of text portions for incremental rendering
   */
  @computed('packet.text', 'shouldBeTruncated')
  textPortions(text, shouldBeTruncated) {
    if (!shouldBeTruncated) {
      return [text];
    }

    const initialTextPortion = text.substr(0, SHOW_TRUNCATED_AMOUNT);
    this.set('portionsToRender', [initialTextPortion]);

    const portions = [initialTextPortion];
    let remainingText = text.substr(SHOW_TRUNCATED_AMOUNT);
    while (remainingText.length > 0) {
      // get next chunk
      const chunk = remainingText.slice(0, CHUNK_SIZE);
      // remove that chunk from the string
      remainingText = remainingText.slice(CHUNK_SIZE);

      portions.push(chunk);
    }

    return portions;
  },

  @computed('portionsToRender', 'textPortions', 'renderingRemainingText')
  renderedPortions(portionsToRender, textPortions, renderingRemainingText) {
    if (!renderingRemainingText && textPortions.length > 0) {
      // If not rendering remaining text, just render the first portion
      return [textPortions[0]];
    } else if (renderingRemainingText) {
      // if rendering remaining text, render those portions
      // that are ready for rendering
      return portionsToRender;
    } else {
      // no text portions, render nothing
      return [];
    }
  },

  didReceiveAttrs() {
    this._super(...arguments);
    // when we first render, need to highlight meta,
    // but if the meta to highlight then changes we need to
    // redo all the checks
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
    const firstPacketId = this.get('packet.firstPacketId');
    const textPortions = this.get('textPortions');

    // start with 1 as first chunk already rendered
    let i = 1;
    const numPortions = textPortions.length;
    for (; i < numPortions; i++) {
      // subtract 1 out of i as we want first chunk to render immediately
      const whenToRender = (i - 1) * TIME_BETWEEN_CHUNKS;
      later(this, this._renderChunk, textPortions[i], whenToRender);
    }

    // And now when all is done, send/set flag indicating all have been rendered.
    // account for last i++ which ended the loop and for the fact that i starts at 1
    const whenToSignalDone = (i - 2) * TIME_BETWEEN_CHUNKS;
    later(() => {
      this.set('renderedAll', true);
      this.sendAction('showMoreFinished', firstPacketId);
    }, whenToSignalDone);
  },

  _renderChunk(chunk) {
    this.get('portionsToRender').addObject(chunk);

    // save/send notifications about the amount
    // of text that has been rendered so far
    next(this, function() {
      const packetText = this.get('packet.text');
      const firstPacketId = this.get('packet.firstPacketId');

      let percentRendered = Math.ceil((this.$('.text-container').text().length / packetText.length) * 100);
      // this calculation isn't exact, doesn't need to be, so don't show 100+
      percentRendered = (percentRendered > 99) ? 99 : percentRendered;
      this.set('percentRendered', percentRendered);
      this.sendAction('updatePercentRendered', { id: firstPacketId, percentRendered });
    });
  },

  actions: {
    copyText() {
      // When the user selects the "Copy Selected Text" option, we need to
      // reselect the original text because the mouseup handler removes the
      // browser's selection.
      try {
        return copyToClipboard(this.get('originalString'));
      } finally {
        this.set('userInComponent', false);
        this.unTether();
      }
    },

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
