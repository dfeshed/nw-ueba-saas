// Not easy replacements

export const offset = (elem) => {
  const rect = elem.getBoundingClientRect();
  const win = elem.ownerDocument.defaultView;
  return {
    top: rect.top + win.pageYOffset,
    left: rect.left + win.pageXOffset
  };
};

export const isNumeric = (x) => {
  return !Array.isArray(x) && (x - parseFloat(x) + 1) >= 0;
};

// Easy replacement docs

//
// SELECTING ELEMENTS
//

// $('.some-element')
// Where this selector is expected to return ONE element
// document.querySelector('.some-element')

// $('.some-element')
// Where this selector is expected to return MANY elements
// document.querySelectorAll('.some-element')

// $('.some-element').length
// document.querySelectorAll('.some-element').length

// $('.some-element').closest();
// document.querySelector('.some-element').closest()

// $('.some-elements').first()
// document.querySelectorAll('.some-elements')[0]

//
// MANIPULATING ELEMENTS
//

// $('.some-element').remove()
// document.querySelector('.some-element').remove()

//
// EFFECTS
//

// $('.some-element').focus()
// document.querySelector('.some-element').focus();

//
// STYLE
//

// $('.some-element').hide();
// document.querySelector('.some-element').style.display = 'none'

// $('.some-element').hasClass('foo-class')
// document.querySelector('.some-element').classList.contains('foo-class')

// $('.some-element').addClass('foo-class')
// document.querySelector('.some-element').classList.add('foo-class');

// $('.some-element').removeClass('foo-class')
// document.querySelector('.some-element').classList.remove('foo-class');

//
// DIMENSIONS
//

// $(window).width()
// window.innerWidth

// $(window).height()
// window.innerHeight

// $('.some-element').height()
// document.querySelector('.some-element').offsetHeight

// $('.some-element').width()
// document.querySelector('.some-element').offsetWidth

//
// EVENT HANDLERS
//

// $('.some-elenent').on('mouseleave', this._someHandlerFunction)
// document.querySelectorAll('.some-element').addEventListener('mouseleave', this._someHandlerFunction)

// $('.some-element').off('mouseleave', this._someHandlerFunction);
// document.querySelectorAll('.some-element').removeEventListener('mouseleave', this._someHandlerFunction);

//
// UTILITY
//

// $.isFunction(foo)
// typeof foo === 'function'


