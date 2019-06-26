// Not easy replacements

export const offset = (elem) => {
  const rect = elem.getBoundingClientRect();
  const win = elem.ownerDocument.defaultView;
  return {
    top: rect.top + win.pageYOffset,
    left: rect.left + win.pageXOffset
  };
};
/**
 * Tests input to see if it's an empty object.
 * @param {object} obj
 */
export const isEmptyObject = (obj) => {
  for (const name in obj) {
    return false;
  }
  return true;
};

export const isNumeric = (x) => {
  return !Array.isArray(x) && (x - parseFloat(x) + 1) >= 0;
};

/**
 * jQuery .height()
 * @param {*} elem
 */
export const getHeight = (elem) => {
  const style = getComputedStyle(elem);
  const paddingY = getPaddingY(elem);
  const borderY = getBorderY(elem);
  let height;

  // .offsetHeight includes padding and border
  // jQuery .height() does not
  if (elem.offsetHeight !== undefined) {
    height = elem.offsetHeight - paddingY - borderY;

  } else { // CSS border box
    if (elem.getClientRects().length > 0) {
      height = elem.getBoundingClientRect().height;
    }
  }

  // if style.height has px value (not 'auto'), use it
  // more precise than .offsetHeight which is rounded
  const parseFloatHeight = parseFloat(style.getPropertyValue('height'));
  if (!isNaN(parseFloatHeight)) {
    height = parseFloatHeight;
  }
  return height;
};

/**
 * jQuery .width()
 * @param {*} elem
 */
export const getWidth = (elem) => {
  const style = getComputedStyle(elem);
  const paddingX = getPaddingX(elem);
  const borderX = getBorderX(elem);
  let width;

  // .offsetWidth includes padding and border
  // jQuery .width() does not
  if (elem.offsetWidth !== undefined) {
    width = elem.offsetWidth - paddingX - borderX;

  } else { // CSS border box
    if (elem.getClientRects().length > 0) {
      width = elem.getBoundingClientRect().width;
    }
  }

  // if style.width has px value (not 'auto'), use it
  // more precise than .offsetWidth which is rounded
  const parseFloatWidth = parseFloat(style.getPropertyValue('width'));
  if (!isNaN(parseFloatWidth)) {
    width = parseFloatWidth;
  }
  return width;
};

export const getPaddingX = (elem) => {
  const style = getComputedStyle(elem);
  const paddingLeft = style.getPropertyValue('paddingLeft') ? +(style.getPropertyValue('paddingLeft').split('px')[0]) : 0;
  const paddingRight = style.getPropertyValue('paddingRight') ? +(style.getPropertyValue('paddingRight').split('px')[0]) : 0;
  return paddingLeft + paddingRight;
};

export const getPaddingY = (elem) => {
  const style = getComputedStyle(elem);
  const paddingTop = style.getPropertyValue('paddingTop') ? +(style.getPropertyValue('paddingTop').split('px')[0]) : 0;
  const paddingBottom = style.getPropertyValue('paddingBottom') ? +(style.getPropertyValue('paddingBottom').split('px')[0]) : 0;
  return paddingTop + paddingBottom;
};

export const getBorderX = (elem) => {
  const style = getComputedStyle(elem);
  const borderLeft = style.getPropertyValue('borderLeft') ? +(style.getPropertyValue('borderLeft').split('px')[0]) : 0;
  const borderRight = style.getPropertyValue('borderRight') ? +(style.getPropertyValue('borderRight').split('px')[0]) : 0;
  return borderLeft + borderRight;
};

export const getBorderY = (elem) => {
  const style = getComputedStyle(elem);
  const borderTop = style.getPropertyValue('borderTop') ? +(style.getPropertyValue('borderTop').split('px')[0]) : 0;
  const borderBottom = style.getPropertyValue('borderBottom') ? +(style.getPropertyValue('borderBottom').split('px')[0]) : 0;
  return borderTop + borderBottom;
};

/**
 * jQuery (':visible')
 * @param {*} elements NodeList
 */
export const visible = (elements) => {
  const found = [];
  for (const el of elements) {
    if (el.offsetWidth > 0 && el.offsetHeight > 0) {
      found.push(el);
    }
  }
  return found;
};

/**
 * jQuery .text()
 * get the combined text contents of each element in the set of matched elements including their descendants
 * @param {*} elem
 */
export const text = (elem) => {
  let node;
  let ret = '';
  let i = 0;

  if (!elem.nodeType) {
    // If no nodeType, this is expected to be an array
    while ((node = elem[ i++ ])) {
      ret += text(node);
    }
  } else if (elem.nodeType === 1 || elem.nodeType === 9 || elem.nodeType === 11) {
    return elem.textContent;

  } else if (elem.nodeType === 3 || elem.nodeType === 4) {
    return elem.nodeValue;
  }
  return ret;
};

/**
 * returns Element created from htmlString
 * @param {*} htmlString
 */
export const htmlStringToElement = (htmlString) => {
  if (htmlString) {
    const template = document.createElement('template');
    template.innerHTML = htmlString.trim();
    return template.content.firstChild;
  }
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

// $('.some-class')
// to get many elements
// .getElementsByClassName('some-class')
// to get one element
// .getElementsByClassName('some-class').item(0)

// $('.some-element').length
// document.querySelectorAll('.some-element').length

// $('.some-element').closest()
// document.querySelector('.some-element').closest()

// $('.some-elements').first()
// document.querySelectorAll('.some-element').item(0)
// findAll('.some-element').shift()

// $(':visible')
// visible(NodeList) - see above

//
// MANIPULATING ELEMENTS
//

// $('.some-element').remove()
// document.querySelector('.some-element').remove()

//
// EFFECTS
//

// $('.some-element').focus()
// document.querySelector('.some-element').focus()
// await focus('.some-element')

// $('.some-element').blur()
// {
//    await focus('.some-element')
//    this.element.querySelector('.some-element').blur()
// }

//
// STYLE
//

// $('.some-element').hide()
// document.querySelector('.some-element').style.display = 'none'

// $('.some-element').hasClass('foo-class')
// document.querySelector('.some-element').classList.contains('foo-class')

// $('.some-element').addClass('foo-class')
// document.querySelector('.some-element').classList.add('foo-class')

// $('.some-element').removeClass('foo-class')
// document.querySelector('.some-element').classList.remove('foo-class')

// element css properties
// this.$('.some-element').css('cssProperty')
// find('.some-element').style['cssProperty']

// computed css properties
// this.$('.some-element').css('cssProperty')
// window.getComputedStyle(find('.some-element')).getPropertyValue('flex-grow')

// this.$('.some-element').css('cssProperty', 'value')
// document.querySelector('.some-element').style.cssProperty = value

//
// DIMENSIONS
//

// $(window).width()
// window.innerWidth

// $(window).height()
// window.innerHeight

// $('.some-element').height()
// getHeight(Element) - see above

// $('.some-element').width()
// getWidth(Element) - see above

//
// EVENT HANDLERS
//

// $('.some-elenent').on('mouseleave', this._someHandlerFunction)
// document.querySelectorAll('.some-element').addEventListener('mouseleave', this._someHandlerFunction)

// .off() removes all event handlers
// with .removeEventListener(), need to identify specific event handler to remove
// https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/removeEventListener
// $('.some-element').off('mouseleave', this._someHandlerFunction)
// document.querySelectorAll('.some-element').removeEventListener('mouseleave', this._someHandlerFunction)

//
// TRIGGER EVENTS (using ember test helpers)
//
// const e = window.$.Event('keyup')
// e.keyCode = 27
// this.$('.rsa-application-modal').trigger(e)
// await triggerKeyEvent(find('.rsa-application-modal'), 'keyup', 27)

// this.$('.some-element').trigger('some-event')
// await triggerEvent(find('.some-element'), 'some-event')

// this.$().find('.rsa-application-overlay').click()
// await click(find('.rsa-application-overlay'))

//
// UTILITY
//

// $.isFunction(foo)
// typeof foo === 'function'

// $.is('.some-class')
// Element.classList.contains('some-class')

// $('.some-element').text()
// text(some-element) - see above

// $('.some-element').append(htmlString)
// Element.append(htmlStringToElement(htmlString)) - see above

//
// ATTRIBUTES
//

// this.$('.some-element').prop('some-attribute')
// document.querySelector('.some-element').getAttribute('some-attribute')

// this.$('.some-element').attr('some-attribute)
// document.querySelector('.some-element').getAttribute('some-attribute')

// this.$().removeAttr('some-attribute')
// this.element.removeAttribute('some-attribute')

// this.$('.some-element').attr('data-*-attribute')
// use data attributes https://developer.mozilla.org/en-US/docs/Learn/HTML/Howto/Use_data_attributes
// $el.attr('data-entity-id')
// el.dataset[entityId]
