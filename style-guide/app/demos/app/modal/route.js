import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Application Modal',
      'subtitle': 'General use modal component.',
      'description': 'A modal can be injected anywhere without concern for the parent element/component. Simply wrap the trigger and content in the rsa-applicaton-modal component. Assign the .modal-trigger to whatever you want to click on to activate the modal, and .modal-content to whatever you want to appear in the modal. All styles are dependent on the content in the modal, including the absence or presence of any whitespace. The class .modal-close can be added to any element in the modal content to close on click. Clicking outside the modal content, as well as hitting the ESC key will close the modal. For external programmatic usage, other components can inject the event-bus service to interact with the modal. To respond to the modal opening or closing, subscribe to the event rsa-application-modal-did-open and handle accordingly based on a single boolean argument that will be passed with the event. To programmatically close all open modals, trigger rsa-application-modal-close-all. To open or close a specific modal, each modal responds to custom events based on its eventId property. For example, the first modal below will respond to the events rsa-application-modal-open-example and rsa-application-modal-close-example. These events are the supported API for interaction. There should be no need to manually update isOpen on the modal.',
      'testFilter': 'rsa-application-modal',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-application-modal.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_application-modal.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-application-modal.hbs'
    };
  }

});
