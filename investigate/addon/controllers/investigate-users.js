import Controller from '@ember/controller';

export default Controller.extend({

  actions: {
    // Because of nginx configuration, if the ueba feature is not deployed or is down, we get an internal-error
    // page returned in the iframe. We also have no way of knowing whether the customer even owns/licenses the UEBA
    // product. Product Management has indicated that we should show a marketing image in the event that UEBA is offline,
    // operating under the assumption that UEBA being offline means they don't own it. This is a hack where we check the
    // iframe's location.href to see if it's being redirected to the internal-error page. If so, we set the hasUEBAError
    // to true, and in the template show the marketing image.
    handleOnLoad() {
      const locationReference = document.getElementById('ueba-iframe').contentWindow.location.href;
      if (locationReference.indexOf('internal-error') >= 0) {
        this.set('hasUEBAError', true);
      }
    }
  }
});
