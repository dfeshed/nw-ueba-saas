
const googleSearchUrl = 'https://www.google.com/search?hl=en&q=';
const VirusTotalSearchUrl = 'https://www.virustotal.com/latest-scan/';

/**
 * Opens multiple new tabs and loads the google or virustotal search based on the actions for given list.
 * @param {*} action
 * @param {*} selectedList
 * @public
 */
export function externalLookup(action, selectedList) {
  const url = action.type === 'google' ? googleSearchUrl : VirusTotalSearchUrl;
  selectedList.map((selectedItem) => {
    let fileName;
    switch (action.name) {
      case 'fileName':
        if (selectedItem.name) {
          fileName = selectedItem.name;
        } else {
          fileName = { selectedItem };
        }
        _lookup(url, fileName);
        break;
      case 'md5':
        _lookup(url, selectedItem.checksumMd5);
        break;
      case 'sha1':
        _lookup(url, selectedItem.checksumSha1);
        break;
      case 'sha256':
        _lookup(url, selectedItem.checksumSha256);
        break;
    }
  });
  return true;
}
/**
 * Opens a new tab and loads the given resource url in the browser
 * @param {*} url
 * @param {*} query
 * @private
 */
function _lookup(url, query) {
  window.open(`${url}${query}`, '_blank');
}