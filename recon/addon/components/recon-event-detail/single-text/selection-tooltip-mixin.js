import Mixin from 'ember-metal/mixin';
import service from 'ember-service/inject';
import { next, cancel, throttle } from 'ember-runloop';
import $ from 'jquery';
import { sendTetherEvent } from 'component-lib/utils/tooltip-trigger';

const ELEMENT_NODE = 1;
const TEXT_NODE = 3;

/**
 * Get the `className` of the parent node. We check what type of node is
 * passed in because different types of nodes have different paths to their
 * parent's className.
 * @param {object} node A DOM node
 * @return {string} The className of the node
 * @private
 */
const _getContainerClassName = (node) => {
  const type = node.nodeType;
  let className;
  if (type === ELEMENT_NODE) {
    className = node.className;
  } else if (type === TEXT_NODE) {
    className = node.parentNode.className;
  }
  return className;
};

export default Mixin.create({
  didDrag: false,
  eventBus: service(),
  // OPTIONAL The component using the SelectionTooltip mixin, can use this isActionClicked
  // to toggle if/else conditionals of the tooltip panel
  isActionClicked: false,
  originalString: null,
  startDragPosition: null,
  spanClass: null,
  spanEl: null,
  displayEvent: null,
  hideEvent: null,
  userInComponent: false,

  // Get the handle object of the selected text
  getSelected() {
    if (window.getSelection) {
      return window.getSelection();
    }
    return '';
  },

  /*
   * mouseEnter/mouseLeave handle delayed rendering of the tether component.
   * This delayed rendering and eventual un-rendering isn't perfect. It will,
   * for instance, leave the tether panel in place if someone has the tooltip
   * open and clicks outside the component to close it.
   */
  mouseEnter() {
    this.set('userInComponent', true);
  },

  mouseLeave() {
    // going to the tooltip component is considered a mouseLeave
    // of THIS component, but we do not want to remove the thether,
    // obviously, if the tooltip is open
    if (!this.get('spanEl')) {
      this.set('userInComponent', false);
    }
  },

  mouseUp() {
    if (this.get('startDragPosition') && this.get('didDrag')) {
      const selection = this.getSelected();
      // model contains the highlighted text, this text is used to do encoding/decoding operations
      const originalString = selection.toString();
      // get the range of the highlighted selection. This range object includes
      // the start and end offsets of the selection.
      const range = selection.getRangeAt(0).cloneRange();
      const startContainerClassName = _getContainerClassName(range.startContainer);
      const endContainerClassName = _getContainerClassName(range.endContainer);

      // Create a span tag around the highlighted selection. This span tag is used for
      // tethering.
      if (startContainerClassName === 'text-container' && endContainerClassName === 'text-container' && !range.collapsed) {
        const newNode = document.createElement('span');
        const index = this.elementId; // index is appended at the end of each span class
        const spanClass = `span${index}`;
        newNode.setAttribute('class', spanClass);
        range.surroundContents(newNode);
        // Remove the browser highlighting after adding span tag on the content
        // since we are highlighting span tag on our own
        selection.removeAllRanges();

        const spanEl = this.$(`.${spanClass}`).get(0); // get the raw DOM element used for tethering

        if (this.get('hideEvent')) {
          cancel(this.get('hideEvent'));
          this.set('hideEvent', null);
        }
        const displayEvent = next(() => {
          sendTetherEvent(
            spanEl,
            spanClass,
            this.get('eventBus'),
            'display'
          );
        });
        this.set('displayEvent', displayEvent);

        this.setProperties({ didDrag: false, startDragPosition: null, spanEl, spanClass, originalString });
      }
    }
  },

  mouseMove() {
    this.set('didDrag', true);
  },

  mouseDown(e) {
    this.set('startDragPosition', { left: e.pageX, top: e.pageY });
    this.unTether();
  },

  // Make sure there is no tether DOM element active on the page from the previous states.
  // ember-tether attaches the tooltip panel that pops up on selection at the root of the
  // page before the closed body element. Find it and remove from the DOM
  // This ensures we don't have two tooltips at the same time
  ensureOnlyOneTether() {
    const childEl = $('.ember-tether').get(0);
    if (childEl) {
      const parentEl = childEl.parentNode; // parentNode is the body element
      parentEl.removeChild(childEl);
    }
  },

  // unTether does the teardown/cleanup of the tooltip
  unTether() {
    this.ensureOnlyOneTether();
    if (this.get('spanEl')) {
      const { spanEl, spanClass, eventBus } = this.getProperties('spanEl', 'spanClass', 'eventBus');

      if (this.get('displayEvent')) {
        cancel(this.get('displayEvent'));
        this.set('displayEvent', null);
      }
      const hideEvent = next(() => {
        sendTetherEvent(
          spanEl,
          spanClass,
          eventBus,
          'hide'
        );
      });
      this.set('hideEvent', hideEvent);

      // Delete the span tag that was introduced by mouseUp() without affecting the content
      $(`.text-container > .${spanClass}`).contents().unwrap();
      this.setProperties({ isActionClicked: false, spanEl: null });
    }
  },

  // Hide the tooltip on scroll. Throttling the scroll handler, so that unTether
  // is never called frequently than the spacing period
  _handleScroll() {
    throttle(this, this.unTether, 500);
  },

  // For the click events outside the recon component; close the recon tooltip if it is open
  _handleWindowClick(e) {
    // using offsets to detect clicks outside recon component, click events within the boundaries
    // of recon are ignored
    const $reconContainer = $('.recon-event-content');
    const xstart = $reconContainer.offset().left;
    const xend = xstart + $reconContainer.width();
    const ystart = $reconContainer.offset().top;
    const yend = ystart + $reconContainer.height();

    // Consider the cases where the tooltip falls outside the recon boundaries and flag the clicks
    // within that as inside clicks
    const $targetParent = $(e.target.parentElement);
    let isClickInsideTooltip = false;
    if (!$targetParent.length || $targetParent.attr('class') === 'reconTooltip') {
      isClickInsideTooltip = true;
    }
    // Get x and y coordinates of the click event
    const xx = e.clientX;
    const yy = e.clientY;

    if (!((xx >= xstart && xx <= xend) && (yy >= ystart && yy <= yend)) && !isClickInsideTooltip) {
      // The click is outside the recon bondaries, so cleanup the tooltip if it is open
      this.unTether();
    }
  },

  didInsertElement() {
    // if is sticky, no content, so no tooltips
    if (!this.get('isSticky')) {
      const scrollFunct = this._handleScroll.bind(this);
      const windowClickFunct = this._handleWindowClick.bind(this);
      $('.recon-event-detail-text .scroll-box').scroll(scrollFunct);
      $(window).click(windowClickFunct);
      this.setProperties({ scrollFunct, windowClickFunct });
    }
  },

  willDestroyElement() {
    const { scrollFunct, windowClickFunct } = this.getProperties('scrollFunct', 'windowClickFunct');
    // if one exists they both exist
    if (scrollFunct) {
      $('.recon-event-detail-text .scroll-box').off('scroll', scrollFunct);
      $(window).off('click', windowClickFunct);
    }
  }
});