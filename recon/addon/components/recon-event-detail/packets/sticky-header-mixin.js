import Ember from 'ember';

const {
  Mixin,
  run: {
    join,
    debounce
  }
} = Ember;

export default Mixin.create({

  indexAtTop: 0,

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

      // Process each of the headers from top of the page on down
      // to see if any of them need to be the stuck/top header.
      // A header gets stuck if it is in view and the header
      // before it has scrolled out of view
      let previousHeaderPosition = -1;
      $headers.each((i) => {
        if (i > 0) {
          previousHeaderPosition = $headers.eq(i - 1).position().top;
        }
        const currentHeaderPosition = $headers.eq(i).position().top;

        // if the previous header is above the top of the view
        // and the current header is below the top of the view
        // then current header needs sticking!
        if (previousHeaderPosition < 0 && currentHeaderPosition > 0) {

          // only set if changing, ember checks if things change,
          // but why bother letting ember bother if we know up front
          if (indexAtTop !== i) {
            // Gotta run.join this to ensure it gets notified immediately,
            // header sticking needs to be faaaast
            join(() => {
              this.set('indexAtTop', i);
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
