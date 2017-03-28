import Mixin from 'ember-metal/mixin';
import service from 'ember-service/inject';
import $ from 'jquery';

export default Mixin.create({
  eventBus: service(),
  didDrag: false,
  startDragPosition: null,
  spanClass: null,

  // Get the handle object of the selected text
  getSelected() {
    if (window.getSelection) {
      return window.getSelection();
    }
    return '';
  },

  mouseUp() {
    if (this.get('startDragPosition') && this.get('didDrag')) {
      const selection = this.getSelected();
      // get the range of the highlighted selection. This range object includes
      // the start and end offsets of the selection.
      const range = selection.getRangeAt(0);
      // Create a span tag around the highlighted selection. This span tag is used for
      // tethering.
      if (range.startOffset !== range.endOffset) {
        const newNode = document.createElement('span');
        const index = this.get('index'); // index is appended at the end of each span class
        const spanClass = `span${index}`;
        newNode.setAttribute('class', spanClass);
        range.surroundContents(newNode);
        const getClass = this.$(`.${spanClass}`);
        const height = getClass.height();
        const width = getClass.width();
        this.get('eventBus').trigger(`rsa-content-tethered-panel-display-span${index}`,
          height, width, spanClass);
        this.setProperties({ didDrag: false, startDragPosition: null, spanClass });
      }
    }
  },

  mouseMove() {
    this.set('didDrag', true);
  },

  mouseDown(e) {
    this.set('startDragPosition', { left: e.pageX, top: e.pageY });
    const { index, spanClass, eventBus } = this.getProperties('index', 'spanClass', 'eventBus');
    eventBus.trigger(`rsa-content-tethered-panel-hide-span${index}`);
    // Delete the span tag that was introduced by mouseUp() without affecting the content
    $(`.text-container > .${spanClass}`).contents().unwrap();
  }
});