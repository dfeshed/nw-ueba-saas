import Component from '@ember/component';
import { later } from '@ember/runloop';
import { Promise } from 'rsvp';
import getOwner from 'ember-owner/get';

const retry = function(isFinished, timeout) {
  let attempts = 0;
  return new Promise((resolve, reject) => {
    function loop() {
      attempts = attempts + 1;
      later(next, attempts * timeout);
    }

    function next() {
      Promise.resolve(isFinished()).then((finished) => {
        if (finished) {
          reject();
        } else if (attempts > 10) {
          resolve();
        } else {
          loop();
        }
      });
    }

    loop();
  });
};

export default Component.extend({
  actions: {
    // Because of nginx configuration, if the ueba feature is not deployed or is down, we get an internal-error
    // page returned in the iframe. We also have no way of knowing whether the customer even owns/licenses the UEBA
    // product. Product Management has indicated that we should show a marketing image in the event that UEBA is offline,
    // operating under the assumption that UEBA being offline means they don't own it. This is a hack where we check the
    // iframe's location.href to see if it's being redirected to the internal-error page. If so, we set the hasUEBAError
    // to true, and in the template show the marketing image.

    // It's also possible that we receive an nginx error page such as a "404 Not Found" or "502 Bad Gateway". There's a
    // check for the title of the iframe'd content to set hasUEBAError if the title of the page includes either of these
    // values.
    iframeLoad() {
      const configuration = getOwner(this).resolveRegistration('config:environment');
      const uebaTimeout = configuration.APP.uebaTimeout || 100;

      const searchComplete = () => {
        const uebaIframe = document.getElementById('ueba-iframe');
        const iframe = uebaIframe && uebaIframe.contentWindow;
        const locationReference = iframe && iframe.location.href || '';
        const title = iframe && iframe.document.querySelector('title');
        const iframeTitle = title && title.textContent.toLowerCase();
        const notFound = iframeTitle && iframeTitle.indexOf('404 not found') >= 0;
        const badGateway = iframeTitle && iframeTitle.indexOf('502 bad gateway') >= 0;
        const internalError = locationReference.indexOf('internal-error') >= 0;
        if (notFound || badGateway || internalError) {
          return true;
        }
      };

      const setProperty = (propertyName) => {
        if (!this.get('isDestroying') && !this.get('isDestroyed')) {
          this.set(propertyName, true);
        }
      };

      return retry(searchComplete, uebaTimeout).then(() => {
        setProperty('iframeVisible');
      }, () => {
        setProperty('hasUEBAError');
      });
    }
  }
});
