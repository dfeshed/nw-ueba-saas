import Component from 'ember-component';
import SelectionTooltip from './selection-tooltip-mixin';
import layout from './template';
import { alias } from 'ember-computed-decorators';

export default Component.extend(SelectionTooltip, {
  classNames: ['rsa-packet', 'rsa-text-entry'],
  classNameBindings: ['packet.side'],
  layout,

  encodedDecodedStr: null,
  index: null,
  isLog: false,
  packet: null,
  tooltipHeading: null,

  // Tooltip has two views depending upon being in IF/ELSE conditional
  // The IF conditional shows the final encoded/decoded text that has the closeButton X
  @alias('isActionClicked') hasCloseButton: null,

  /**
   * @description Function used to encode/decode the original string selected by the user
   * @param {string} - Signifies which operation encode or decode to be done
   * @private
   */
  _encodedDecoded(operation) {
    const originalString = this.get('originalString');
    const tooltipHeading = (operation === 'decode') ? 'Decoded Text' : 'Encoded Text';
    let encodedDecodedStr;
    try {
      if (operation === 'decode') {
        encodedDecodedStr = decodeURIComponent(escape(window.atob(originalString)));
      } else {
        encodedDecodedStr = window.btoa(unescape(encodeURIComponent(originalString)));
      }
    } catch (err) {
      encodedDecodedStr = 'The format of the string is not valid.';
    }
    this.setProperties({ isActionClicked: true, tooltipHeading, encodedDecodedStr });
  },

  actions: {
    decodeText() {
      this._encodedDecoded('decode');
    },
    encodeText() {
      this._encodedDecoded('encode');
    }
  }
});
