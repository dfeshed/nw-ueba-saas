import Controller from '@ember/controller';
import { later } from '@ember/runloop';

export default Controller.extend({

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
    handleOnLoad() {
      later(this, () => {
        const iframe = document.getElementById('ueba-iframe').contentWindow;
        const locationReference = iframe.location.href;
        // Hackery: Check if the the loaded page title has 404 Not Found or 502 Bad Gateway, and if so, set hasUEBAError
        // to true. Also check the location reference and set hasUEBAError to true if it is pointing to the ember internal
        // error page
        const iframeTitle = iframe.document.querySelector('title').textContent.toLowerCase();
        if (iframeTitle.indexOf('404 not found') >= 0 || iframeTitle.indexOf('502 bad gateway') >= 0 || locationReference.indexOf('internal-error') >= 0) {
          this.set('hasUEBAError', true);
        }
      }, 750); // Even more hackery: we need to delay the evaluation of the content so that the page has time to load
    }
  }
});
