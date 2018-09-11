/**
 * builds a final url for a stream request based
 * @public
 * @param baseUrl {string}
 * @param postfix {string}
 * @param requiredUrl {string} the URL that needs postfixing
*/

export const buildBaseUrl = (baseUrl = '', postfix, requiredUrl) => {
  if (postfix && requiredUrl && baseUrl.includes(requiredUrl)) {
    return `${baseUrl}/${postfix}`;
  }
  return baseUrl;
};