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
// document.querySelectorAll('.some-element').item(0)
// findAll('.some-element').shift()

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
// await focus('.some-element');

// $('.some-element').blur()
// {
//    await focus('.some-element');
//    this.element.querySelector('.some-element').blur();
// }

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

// element css properties
// this.$('.some-element').css('cssProperty')
// find('.some-element').style['cssProperty'];

// computed css properties
// this.$('.some-element').css('cssProperty')
// window.getComputedStyle(find('.some-element')).getPropertyValue('flex-grow')git stt

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
// TRIGGER EVENTS ( using ember test helpers)
//
// const e = window.$.Event('keyup');
// e.keyCode = 27
// this.$('.rsa-application-modal').trigger(e);
// await triggerKeyEvent(find('.rsa-application-modal'), 'keyup', 27);

// this.$().find('.rsa-application-overlay').click();
// await click(find('.rsa-application-overlay'));

//
// UTILITY
//

// $.isFunction(foo)
// typeof foo === 'function'

//
// ATTRIBUTES
//

// this.$('.some-element').prop('some-attribute')
// document.querySelector('.some-element').getAttribute('some-attribute')

// this.$('.some-element').attr('some-attribute)
// document.querySelector('.some-element').getAttribute('some-attribute')
