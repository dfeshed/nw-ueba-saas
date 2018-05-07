/**
 * Creates a serialized representation of an array suitable for use in a URL query string.
 * @public
 */
function serializeQueryParams(qp = []) {
  const keys = Object.keys(qp);
  const values = Object.values(qp);
  return keys.map((d, i) => `${d}=${values[i]}`).join('&');
  // Once we drop IE11 we should be able to use Object.entries
  // return Object.entries(qp).map((d) => `${d[0]}=${d[1]}`).join('&');
}

export {
  serializeQueryParams
};