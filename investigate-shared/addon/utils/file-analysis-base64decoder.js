const _stringReplacer = (encoded) => {
  const clz32 = Math.clz32 || ((x) => (31 - Math.log(x >>> 0) / Math.LN2 | 0));

  let codePoint = encoded.charCodeAt(0) << 24;
  const leadingOnes = clz32(~codePoint);
  const encodedStringLen = encoded.length;
  let endPos = 0;
  let result = '';
  if (leadingOnes < 5 && encodedStringLen >= leadingOnes) {
    codePoint = (codePoint << leadingOnes) >>> (24 + leadingOnes);
    for (endPos = 1; endPos < leadingOnes; ++endPos) {
      codePoint = (codePoint << 6) | (encoded.charCodeAt(endPos) & 0x3f);
    }

    if (codePoint <= 0xFFFF) { // for BMP 0 code point
      result += String.fromCharCode(codePoint);
    } else if (codePoint <= 0x10FFFF) { // for remaining BMPs
      codePoint -= 0x10000;
      result += String.fromCharCode(
        (codePoint >> 10) + 0xD800, // highSurrogate
        (codePoint & 0x3ff) + 0xDC00 // lowSurrogate
      );
    } else {
      endPos = 0; // for beyond unicode range
    }
  }
  for (endPos; endPos < encodedStringLen; ++endPos) {
    result += '\ufffd'; // replacement character
  }
  return result;
};

export const base64ToUnicode = (inputString) => {
  if (inputString.substring(0, 3) === '\xEF\xBB\xBF') {
    inputString = inputString.substring(3); // removing UTF-8 BOM
  }
  return atob(inputString).replace(/[\xc0-\xff][\x80-\xbf]*/g, _stringReplacer);
};