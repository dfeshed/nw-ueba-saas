/**
 * @description utility file that holds helper functions
 * @public
 */

/**
 * @function parsePostData
 * @param query {string} eg key1=value1&key2=value2 or key1=val!1&key2=val@123
 * @return result {object} {key1: "value1", key2: "value2"} or {key1: "val!1", key2: "val@123"}
 * @public
 */
export function parsePostData(query) {
  let result = {};
  query.split('&').forEach(function(part) {
    let item = part.split('=');
    result[item[0]] = decodeURIComponent(item[1]);
  });
  return result;
}

export default {
  parsePostData
};
