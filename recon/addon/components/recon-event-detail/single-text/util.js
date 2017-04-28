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
  if (metaToHighlight) {
    const metaStringRegex = new RegExp(String(metaToHighlight), 'gi');
    const foundMatch = text.match(metaStringRegex);
    if (foundMatch) {
      text = text.replace(
        metaStringRegex,
        `<span class='highlighted-meta'>${foundMatch[0]}</span>`
      );
    }
  }

  return htmlSafe(text);
};
