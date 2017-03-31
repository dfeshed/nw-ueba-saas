import Ember from 'ember';
import computed from 'ember-computed-decorators';

const {
  Mixin,
  run: {
    join,
    debounce
  }
} = Ember;

export default Mixin.create({
  // Internal mixin state, height of sticky, used for sliding
  // sticky out when new sticky hits the bottom of the stuck iteam
  heightOfCurrentSticky: 0,

  // Interal mixin state, which item in the list of stickables
  // is currently stuck
  indexAtTop: 0,

  // REQUIRED The key of the data, when paired with the index,
  // the result is the data used in the sticky component
  stickyContentKey: null,

  // OPTIONAL If stickySelector isn't the actual header
  // this selector can be used to find the header inside
  // items found using stickySelector
  stickyHeaderSelector: null,

  // REQUIRED The selector for all of the items to be stuck in a list
  // of items. This could be the headers themselves if that makes sense,
  // or it could be the parent of the header. If it is the parent of the header
  // then stickyHeaderSelector must be provided.
  stickySelector: null,

  @computed('indexAtTop')
  stickyContent(index) {
    // no scrolling has occurred so no content to stick
    if (index === 0) {
      return;
    }

    const content = this.get(this.get('stickyContentKey'));

    // no content either, so no header to render
    if (content.length === 0) {
      return;
    }

    return content[index - 1];
  },

  // didRender is called each time another page of packets is returned
  // from an API call and rendered to the page (keep in mind they stream
  // in from the API in batches).
  // Need to reset the $headers, as each time didRender is called
  // there will be more of them, one for each packet
  didRender() {
    this._super(...arguments);
    const stickySelector = this.get('stickySelector');
    this.set('$headers', this.$(stickySelector));

    // Other factors can result in packets being removed from the page
    // like requests/responses being toggled.
    // If headers are eliminated, need to recalc the scroll sticky,
    // debounce as scroll can be finicky.
    debounce(() => {
      this._scrolled();
    }, 100);
  },

  // Just one time, on insert of element, need to register scroll bindings
  didInsertElement() {
    this._super(...arguments);
    this.$('.scroll-box').scroll(() => {
      this._scrolled();

      // Keeping this in case we need it later, seems to be ok right now
      //
      // When scrolling fast (like with the scrollbar vs wheel)
      // occasionally the code can't keep up, ¯\_(ツ)_/¯
      // need last debounced 'scrolled' call to catch those cases
      // debounce(() => {
      //   this._scrolled();
      // }, 200);
    });
  },

  _scrolled() {
    const $headers = this.get('$headers');

    if ($headers && $headers.length > 0) {

      // fast eject, common use case, is just at the top
      // nothing to do here
      if ($headers.eq(0).position().top === 0) {
        this.set('indexAtTop', 0);
        return;
      }

      const indexAtTop = this.get('indexAtTop');
      const heightOfCurrentSticky = this.get('heightOfCurrentSticky');

      // Process each of the headers from top of the page on down
      // to see if any of them need to be the stuck/top header.
      // A header gets stuck if it is in view and the header
      // before it has scrolled out of view
      let previousHeaderPosition = -1;
      $headers.each((i) => {
        const $currentHeader = $headers.eq(i);
        if (i > 0) {
          previousHeaderPosition = $headers.eq(i - 1).position().top;
        }
        const currentHeaderPosition = $currentHeader.position().top;

        // if the previous header is above the top of the view
        // and the current header is below the top of the view
        // then current header needs sticking!
        if (previousHeaderPosition - heightOfCurrentSticky < 0 && currentHeaderPosition - heightOfCurrentSticky > 0) {

          // only set if changing, ember checks if things change,
          // but why bother letting ember bother if we know up front
          if (indexAtTop !== i) {

            // Determine height of header
            // Header could be entire element provided, or could
            // optionally be element nested inside
            let newStickyHeaderHeight = 0;
            const stickyHeaderSelector = this.get('stickyHeaderSelector');
            if (stickyHeaderSelector) {
              newStickyHeaderHeight = $currentHeader.find(stickyHeaderSelector).outerHeight();
            } else {
              newStickyHeaderHeight = $currentHeader.outerHeight();
            }

            // Gotta run.join this to ensure it gets notified immediately,
            // header sticking needs to be faaaast
            join(() => {
              this.set('indexAtTop', i);
              this.set('heightOfCurrentSticky', newStickyHeaderHeight);
            });
          }

          // the previous header is out of view and the current header is
          // in view, which means no more headers will be stickyable as
          // they will all have a header above them that is in view, so
          // now we can exit out of each loop, nothing else to do
          return false;
        }
      });
    }
  }
});
