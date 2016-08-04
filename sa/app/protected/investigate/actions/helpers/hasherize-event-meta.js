// Takes a Netwitness Core event object with a `metas` array, and applies each meta value as a key-value pair
// on the event object (while leaving the original `metas` intact.
// @example `{.., metas: [ [a, b], [c, d], .. ]} => {.., metas: [..], a: b, c: d, ..}
// If any duplicate keys are found in `metas`, only the last key value will be applied.
export default function(evt) {
  if (evt) {
    let { metas } = evt;
    if (!metas) {
      return;
    }

    // This function will be executed thousands of times, with high frequency, so its need to be performant.
    // Therefore we forego using closures & `[].forEach()` and instead use a `for` loop.
    let len = (metas && metas.length) || 0;
    let i;
    for (i = 0; i < len; i++) {
      let meta = metas[i];
      evt[meta[0]] = meta[1];
    }
  }
}
