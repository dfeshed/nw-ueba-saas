import Component from 'ember-component';
import computed, { and, alias } from 'ember-computed-decorators';
import { htmlSafe } from 'ember-string';
import { later } from 'ember-runloop';

import SelectionTooltip from './selection-tooltip-mixin';
import layout from './template';

const HIDE_PACKETS_LINE_COUNT = 250;
const SHOW_TRUNCATED_AMOUNT = 100;

export default Component.extend(SelectionTooltip, {
  classNames: ['rsa-text-entry'],
  classNameBindings: ['packet.side'],
  layout,

  encDecStrBase64: null,
  encDecStrUrl: null,
  index: null,
  isLog: null,
  packet: null,
  remainingLinesHidden: false,
  renderingRemainingLines: false,
  tooltipHeading: null,

  /*
   * Up front determination if the packet data should be truncated
   * based on the size of the array and whether or not the content
   * of the text has highlighted stuff. If content is highlighted
   * then have to make it show up.
   */
  @computed('packet.text', 'packet.containsHighlightedText')
  shouldBeTruncated(textEntries = [], forceOpen = false) {
    // If the packet has highlighted text, then need to force
    // it to be open no matter how big it is so the user
    // can see the highlighted text when scrolling
    if (forceOpen) {
      return false;
    }

    const shouldTruncate = textEntries.length > HIDE_PACKETS_LINE_COUNT;
    if (shouldTruncate) {
      this.set('remainingLinesHidden', true);
    }
    return shouldTruncate;
  },

  /*
   * Used to determine if a UI block that allows the user
   * to show remaining lines should be shown.
   *
   * If the content should have been truncated, and they haven't
   * since been shown, then the block should be shown.
   */
  @and('shouldBeTruncated', 'remainingLinesHidden')
  areRemainingLinesToShow: true,

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
  @computed('packet.text', 'areRemainingLinesToShow')
  textEntriesToDisplay(textEntries = [], areRemainingLinesToShow) {
    let textEntriesReturn = textEntries;
    if (areRemainingLinesToShow) {
      textEntriesReturn = textEntriesReturn.slice(0, SHOW_TRUNCATED_AMOUNT);
    }
    return htmlSafe(textEntriesReturn.join('<br>'));
  },

  // Tooltip has two views depending upon being in IF/ELSE conditional
  // The IF conditional shows the final encoded/decoded text that has the closeButton X
  @alias('isActionClicked') hasCloseButton: null,

  /**
   * @description Function used to do Base64 encode/decode the original string
   * @param {string} - Signifies which operation encode or decode to be done
   * @private
   */
  _encodedDecodedBase64(operation) {
    const originalString = this.get('originalString');
    let encDecStrBase64;
    try {
      if (operation === 'decode') {
        encDecStrBase64 = decodeURIComponent(escape(window.atob(originalString)));
      } else {
        encDecStrBase64 = window.btoa(unescape(encodeURIComponent(originalString)));
      }
    } catch (err) {
      encDecStrBase64 = 'The format of the string is not valid.';
    }
    this.set('encDecStrBase64', encDecStrBase64);
  },

  /**
   * @description Function used to URL encode/decode the original string
   * @param {string} - Signifies which operation encode or decode to be done
   * @private
   */
  _encodedDecodedUrl(operation) {
    const originalString = this.get('originalString');
    let encDecStrUrl;
    try {
      if (operation === 'decode') {
        encDecStrUrl = decodeURIComponent(originalString);
      } else {
        encDecStrUrl = encodeURIComponent(originalString);
      }
    } catch (err) {
      encDecStrUrl = 'The format of the string is not valid.';
    }
    this.set('encDecStrUrl', encDecStrUrl);
  },

  actions: {
    decodeText() {
      this._encodedDecodedBase64('decode');
      this._encodedDecodedUrl('decode');
      this.setProperties({ isActionClicked: true, tooltipHeading: 'Decoded Text' });
    },
    encodeText() {
      this._encodedDecodedBase64('encode');
      this._encodedDecodedUrl('encode');
      this.setProperties({ isActionClicked: true, tooltipHeading: 'Encoded Text' });
    },
    showRemainingLines() {

      // Want to get the rendering message in place
      // before rendering the remaining lines, so update
      // flag to indicate rendering is taking place,
      // then wait until next run loop to make
      // rendering happen
      this.set('renderingRemainingLines', true);
      later(() => {
        this.set('remainingLinesHidden', false);
        this.set('renderingRemainingLines', false);
      }, 50);
    }
  }
});