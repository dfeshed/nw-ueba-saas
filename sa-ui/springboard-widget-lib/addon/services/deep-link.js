import Service, { inject as service } from '@ember/service';
import { get } from '@ember/object';

// Add mapping here more new deep link urls
const DEEP_LINK_URLS = {
  HOST_LIST: '/investigate/hosts',
  HOST_DETAILS: '/investigate/hosts/{0}/OVERVIEW?sid={1}',
  FILE_LIST: '/investigate/files',
  FILE_DETAILS: '/investigate/files/{0}?checksum={0}&sid={1}&tabName=OVERVIEW'
};


export default class DeepLinkService extends Service {
  @service i18n;

  /**
   * Transitions to the given URL
   * @param openInNewTab
   * @param url
   * @private
   */
  _open(openInNewTab, url) {
    if (!openInNewTab) {
      window.open(`${window.location.origin}${url}`, '_self');
    } else {
      window.open(`${window.location.origin}${url}`, '_blank');
    }
  }

  /**
   * Returns the formatted URL, Params order matters
   * @param location
   * @param item
   * @param params
   * @returns {*}
   * @private
   */
  _getURL(location, item, params) {
    const url = DEEP_LINK_URLS[location];
    const data = params.map((param) => get(item, param));
    return this.i18n.formatMessage(url, data);
  }

  /**
   * Public API for transition to new page. It take deepLink as object, which has the following property
   * {
   *   location: {@string} // Where to navigate [ 'HOST_LIST, HOST_DETAILS, FILE_LIST, FILE_DETAILS],
   *   openInNewTab: {@boolean} // To indicate open the page in new tab, default to false
   *   params: {@params} // query or path params in the url. Orders matters. Params field must there in response
   * }
   * Params order matters because params will be replaced in the url based on the positions
   * ex: /investigate/{0}/{1} and params ['a', 'b'] then 0 will be replace with 'a' and 1 will be replaced wit 'b'
   *
   * item is context information, if click is coming from the table row then item contains information about row data
   * @param deepLink
   * @param item
   */
  transition(deepLink, item) {
    const { location, params, openInNewTab } = deepLink;
    switch (location) {
      case 'HOST_LIST': {
        this._open(openInNewTab, DEEP_LINK_URLS[location]);
        break;
      }
      case 'HOST_DETAILS': {
        this._open(openInNewTab, this._getURL(location, item, params));
        break;
      }
      case 'FILE_DETAILS': {
        this._open(openInNewTab, this._getURL(location, item, params));
        break;
      }
    }
  }
}
