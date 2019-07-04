import EmberContextMenuService from 'ember-context-menu/services/context-menu';

// to store element's event handlers
// [ {element, 'event': {eventType, handler, options}}, {}, {}, ]
const eventHandlerStorageArray = [];

// function to use to add to eventHandlerMap
const addToEventHandlerStorageArray = function(element, eventType, handler, options = null) {
  const temp = {
    element,
    event: {
      eventType,
      handler,
      options
    }
  };

  if (eventHandlerStorageArray.indexOf(temp) < 0) {
    eventHandlerStorageArray.push(temp);
  }
};

// function to use to remove from eventHandlerMap
const removeFromEventHandlerStorageArray = function(element, eventType, handler, options = null) {
  const temp = {
    element,
    event: {
      eventType,
      handler,
      options
    }
  };

  const i = eventHandlerStorageArray.indexOf(temp);
  if (i > -1) {
    eventHandlerStorageArray.splice(i);
  }
};

export default EmberContextMenuService.extend({

  removeDeactivateHandler() {
    const deactivate = this.get('deactivate');
    const eventHandlerOption = { once: true };

    if (deactivate) {
      document.body.removeEventListener('contextmenu', deactivate, eventHandlerOption);
      removeFromEventHandlerStorageArray(document.body, 'contextmenu', deactivate, eventHandlerOption);
    }

    // Clearing the previously set data, which was causing problems in multiple instance
    this.set('items', null);
    this.set('event', null);
    this.set('selection', null);
    this.set('details', null);
    this._super(...arguments);
  },

  addDeactivateHandler() {
    this._super(...arguments);
    const deactivate = this.get('deactivate');
    const eventHandler = (event) => {
      if (event.target.closest('.content-context-menu')) {
        // if context-menu target, do not call deactivate
        return;
      }
      deactivate();
    };
    const eventHandlerOption = { once: true };
    document.body.addEventListener('contextmenu', eventHandler, eventHandlerOption);
    addToEventHandlerStorageArray(document.body, 'contextmenu', eventHandler, eventHandlerOption);
  },

  /**
   * returns [ {element, 'event': {eventType, handler, options}}, {}, {}, ]
   */
  getEventHandlerStorageArray() {
    return eventHandlerStorageArray;
  }
});
