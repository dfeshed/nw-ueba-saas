import Ember from 'ember';
import Mixin from 'ember-metal/mixin';
import service from 'ember-service/inject';
import $ from 'jquery';
import { sendTetherEvent } from 'component-lib/utils/tooltip-trigger';

const { run } = Ember;

export default Mixin.create({
  eventBus: service(),
  didDrag: false,
  startDragPosition: null,
  spanClass: null,
  spanEl: null,

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
      const range = selection.getRangeAt(0).cloneRange();
      // Create a span tag around the highlighted selection. This span tag is used for
      // tethering.
      if (range.startOffset !== range.endOffset) {
        const newNode = document.createElement('span');
        const index = this.get('index'); // index is appended at the end of each span class
        const spanClass = `span${index}`;
        newNode.setAttribute('class', spanClass);
        range.surroundContents(newNode);
        // To persist the browser highlighting after adding span tag on the content
        selection.removeAllRanges();
        selection.addRange(range);

        const spanEl = this.$(`.${spanClass}`).get(0); // get the raw DOM element used for tethering

        sendTetherEvent(
          spanEl,
          spanClass,
          this.get('eventBus'),
          'display'
        );
        this.setProperties({ didDrag: false, startDragPosition: null, spanEl, spanClass });
      }
    }
  },

  mouseMove() {
    this.set('didDrag', true);
  },

  mouseDown(e) {
    this.ensureOnlyOneTether();
    this.set('startDragPosition', { left: e.pageX, top: e.pageY });
    // Make sure span element is present before we do un-tether
    if (this.get('spanEl')) {
      const { spanEl, spanClass, eventBus } = this.getProperties('spanEl', 'spanClass', 'eventBus');
      sendTetherEvent(spanEl, spanClass, eventBus, 'hide');

      // Delete the span tag that was introduced by mouseUp() without affecting the content
      $(`.text-container > .${spanClass}`).contents().unwrap();
    }
  },

  // Make sure there is no tether DOM element active on the page from the previous states.
  // ember-tether attaches the tooltip panel that pops up on selection at the root of the
  // page before the closed body element. Find it and remove from the DOM
  ensureOnlyOneTether() {
    const childEl = $('.ember-tether').get(0);
    if (childEl) {
      const parentEl = childEl.parentElement; // parentEl is the body element
      parentEl.removeChild(childEl);
    }
  },

  // Throttling the scroll handler, so that checkTether is never called frequently than the
  // spacing period
  checkTether() {
    if (this.get('spanEl') && $('.ember-tether').length) {
      this.ensureOnlyOneTether();
      const spanClass = this.get('spanClass');
      $(`.text-container > .${spanClass}`).contents().unwrap();
    }
  },

  didInsertElement() {
  // Also hide the tooltip on scroll
    $('.recon-event-detail-text .scroll-box').scroll(() => {
      run.throttle(this, this.checkTether, 500);
    });
  },

  willDestroyElement() {
    $('.recon-event-detail-text .scroll-box').off('scroll');
  }
});