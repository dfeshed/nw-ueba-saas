import { htmlSafe } from 'ember-string';

/**
 * @description Function used to do Base64 encode/decode the original string
 * @param {string} - Signifies which operation encode or decode to be done
 * @private
 */
const _encodedDecodedBase64 = function(operation, string) {
  let encDecStrBase64;
  try {
    if (operation === 'decode') {
      encDecStrBase64 = decodeURIComponent(escape(window.atob(string)));
    } else {
      encDecStrBase64 = window.btoa(unescape(encodeURIComponent(string)));
    }
  } catch (err) {
    encDecStrBase64 = 'The format of the string is not valid.';
  }
  return encDecStrBase64;
};

/**
 * @description Function used to URL encode/decode the original string
 * @param {string} - Signifies which operation encode or decode to be done
 * @private
 */
const _encodedDecodedUrl = function(operation, string) {
  let encDecStrUrl;
  try {
    if (operation === 'decode') {
      encDecStrUrl = decodeURIComponent(string);
    } else {
      encDecStrUrl = encodeURIComponent(string);
    }
  } catch (err) {
    encDecStrUrl = 'The format of the string is not valid.';
  }
  return encDecStrUrl;
};

export const retrieveTranslatedData = function(operation, string) {
  return {
    encDecStrUrl: _encodedDecodedUrl(operation, string),
    encDecStrBase64: _encodedDecodedBase64(operation, string)
  };
};

export const prepareTextForDisplay = function(text, metaToHighlight) {
  const safeText = _generateHTMLSafeText(text);
  let markedText;

  if (metaToHighlight && _isString(metaToHighlight)) {
    // Escape RegEx special characters and convert < and > to HTML safe strings
    const pattern = metaToHighlight
      .replace(/[-[\]{}()*+?.,\\^$|#]/g, '\\$&')
      .replace(/\</g, '&lt;')
      .replace(/\>/g, '&gt;');
    const regex = new RegExp(pattern, 'gi'); // case insensitive
    if (safeText.match(regex)) {
      // Use the special replacement pattern "$&" to replace matches so that
      // case changes are applied to the corresponding match
      markedText = safeText.replace(regex, '<span class="highlighted-meta">$&</span>');
    }
  }

  return htmlSafe(markedText || safeText);
};

const _isString = (s) => Object.prototype.toString.call(s) === '[object String]';

/*
 * Processes text content and normalizes it for use in
 * the browser. Results in an html-ified string.
 */
const _generateHTMLSafeText = (text) => {
  return text
    .replace(/\</g, '&lt;')
    .replace(/\>/g, '&gt;')
    .replace(/(?:\r\n|\r|\n)/g, '<br>')
    .replace(/\t/g, '&nbsp;&nbsp;')
    .replace(/[\x00-\x1F]/g, '.')
    // https://bedfordjira.na.rsa.net/browse/ASOC-35522
    // Replacing this specific character, because if we do not
    // Chrome (and just Chrome) will crash, ¯\_(ツ)_/¯
    .replace(new RegExp(String.fromCharCode(1836), 'g'), '.');
};
