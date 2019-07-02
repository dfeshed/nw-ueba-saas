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

export const isWindow = (obj) => {
  return obj != null && obj === obj.window;
};

/**
 * jQuery .outerHeight()
 */
export const getOuterHeight = (elem, includeMargin = false) => {
  if (isWindow(elem)) {
    return elem.innerHeight;
  }

  // if Document node
  if (elem.nodeType === 9) {
    const doc = elem.documentElement;

    return Math.max(elem.body.scrollHeight, doc.scrollHeight,
      elem.body.offsetHeight, doc.offsetHeight, doc.clientHeight);
  }

  const paddingY = getPaddingY(elem);
  const borderY = getBorderY(elem);
  const marginY = getMarginY(elem);

  if (includeMargin) {
    return elem.getBoundingClientRect().height + paddingY + borderY + marginY;
  } else {
    return elem.getBoundingClientRect().height + paddingY + borderY;
  }
};

/**
 * jQuery .height()
 * @param {*} elem
 */
export const getHeight = (elem) => {
  if (elem) {
    const style = getComputedStyle(elem);
    const paddingY = getPaddingY(elem);
    const borderY = getBorderY(elem);
    let height;

    if (elem.getClientRects().length > 0) {
      height = elem.getBoundingClientRect().height;
    } else {
      // .offsetHeight includes padding and border
      // jQuery .height() does not
      if (elem.offsetHeight !== undefined) {
        height = elem.offsetHeight - paddingY - borderY;
      }
    }

    // if style.height has px value (not 'auto'), use it
    // more precise than .offsetHeight which is rounded
    const parseFloatHeight = parseFloat(style.getPropertyValue('height'));
    if (!isNaN(parseFloatHeight)) {
      height = parseFloatHeight;
    }
    return height;
  }
};

/**
 * jQuery .innerWidth()
 * @param {*} elem
 */
export const getInnerWidth = (elem) => {
  const paddingX = getPaddingX(elem);
  let width;

  if (isWindow(elem)) {
    return elem.document.documentElement.clientWidth;
  }

  // if Document node
  if (elem.nodeType === 9) {
    const doc = elem.documentElement;

    return Math.max(elem.body.scrollHeight, doc.scrollHeight,
      elem.body.offsetHeight, doc.offsetHeight, doc.clientHeight);
  }

  if (elem.getClientRects().length > 0) {
    width = elem.getBoundingClientRect().width + paddingX;
  } else {
    if (elem.clientWidth !== undefined) {
      width = elem.clientWidth;
    }
  }

  return width;
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

  if (elem.getClientRects().length > 0) {
    width = elem.getBoundingClientRect().width;
  } else {
    // .offsetWidth includes padding and border
    // jQuery .width() does not
    if (elem.offsetWidth !== undefined) {
      width = elem.offsetWidth - paddingX - borderX;
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
  const paddingLeft = style.getPropertyValue('paddingLeft') ? parseFloat(style.getPropertyValue('paddingLeft')) : 0;
  const paddingRight = style.getPropertyValue('paddingRight') ? parseFloat(style.getPropertyValue('paddingRight')) : 0;
  return paddingLeft + paddingRight;
};

export const getPaddingY = (elem) => {
  const style = getComputedStyle(elem);
  const paddingTop = style.getPropertyValue('paddingTop') ? parseFloat(style.getPropertyValue('paddingTop')) : 0;
  const paddingBottom = style.getPropertyValue('paddingBottom') ? parseFloat(style.getPropertyValue('paddingBottom')) : 0;
  return paddingTop + paddingBottom;
};

export const getBorderX = (elem) => {
  const style = getComputedStyle(elem);
  const borderLeft = style.getPropertyValue('borderLeft') ? parseFloat(style.getPropertyValue('borderLeft')) : 0;
  const borderRight = style.getPropertyValue('borderRight') ? parseFloat(style.getPropertyValue('borderRight')) : 0;
  return borderLeft + borderRight;
};

export const getBorderY = (elem) => {
  const style = getComputedStyle(elem);
  const borderTop = style.getPropertyValue('borderTop') ? parseFloat(style.getPropertyValue('borderTop')) : 0;
  const borderBottom = style.getPropertyValue('borderBottom') ? parseFloat(style.getPropertyValue('borderBottom')) : 0;
  return borderTop + borderBottom;
};

export const getMarginY = (elem) => {
  const style = getComputedStyle(elem);
  const marginTop = style.getPropertyValue('marginTop') ? parseFloat(style.getPropertyValue('marginTop')) : 0;
  const marginBottom = style.getPropertyValue('marginBottom') ? parseFloat(style.getPropertyValue('marginBottom')) : 0;
  return marginTop + marginBottom;
};

/**
 * jQuery .find(selector)
 * returns array of elements matching selector
 * @param {NodeList | HTMLCollection | Array} elements
 * @param {string} selector
 */
export const findBySelector = (elements, selector) => {
  if (elements && elements.length > 0 && selector && typeof selector === 'string') {
    const result = [];
    elements.forEach((el) => {
      if (el) {
        const found = el.querySelectorAll(selector);
        result.push(Array.from(found));
      }
    });
    return _flattenDeep(result);

  } else {
    return [];
  }
};

/**
 * jQuery .find(element)
 * returns array of elements matching elementToFind
 * @param {*} elements
 * @param {*} elementToFind
 */
export const findElement = (elements, elementToFind) => {
  if (elements && elements.length > 0 && elementToFind) {
    let result = [];
    elements.forEach((el) => {
      if (el) {
        const temp = el.querySelectorAll('*');
        result = result.concat(Array.from(temp));
      }
    });
    return result.filter((el) => {
      return el.nodeType === 1 && el === elementToFind;
    });

  } else {
    return [];
  }
};

const _flattenDeep = (arr1) => {
  return arr1.reduce((acc, val) => Array.isArray(val) ?
    acc.concat(_flattenDeep(val)) : acc.concat(val), []);
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
 * jQuery (':not')
 * returns an array
 * @param {*} elements NodeList
 * @param {*} selector
 */
export const filterElements = (elements, selector) => {
  if (elements && elements.length > 0) {
    const toArray = Array.from(elements);
    let toRemove = [];

    // find elements to remove
    toArray.forEach((el) => {
      const test = el.querySelectorAll(selector);
      toRemove.push(Array.from(test));
    });
    toRemove = _flattenDeep(toRemove);

    // return array of elements filtered by selector
    return toArray.filter((el) => {
      return toRemove.indexOf(el) < 0;
    });
  }
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
    while ((node = elem[i++])) {
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

/**
 *  jquery ajax throws error for non 2** responses. but fetch doesnt.
 *  fetch only throws error for issues like parsing error or connectivity etc but not for non 200 erros.
 *  this method mimics that behavior.
* returns response or throws error based on http response code
* @param {*} response
*/
export const isHttpRequestSuccess = (response) => {
  if (response.status == 200) {
    return response;
  } else {
    const error = new Error(response.statusText);
    error.response = response;
    throw error;

  }
};

/**
 * Removes the wrapping DOM of the elements for the given selector. This is the
 * counterpoint to `wrap()`.
 * @param {string} selector The selector for the element(s) you want to unwrap
 */
export const unwrap = (selector) => {
  const elements = document.querySelectorAll(selector);
  elements.forEach((el) => {
    const { parentNode } = el;
    if (parentNode !== document.body) {
      if (el.hasChildNodes()) {
        // childNodes is a nodeList that contains all the Text and DOM nodes
        // within the parent. We create a document fragment, append those nodes
        // into the fragment, then insert the fragment and remove the old parent.
        // This helps reduce the browser work if we were to do this one node at
        // a time (like in a forEach loop).
        const { childNodes } = el;
        const fragment = document.createDocumentFragment();
        fragment.append(...childNodes);
        parentNode.insertBefore(fragment, el);
        parentNode.removeChild(el);
      }
    }
    // const elParentNode = el.parentNode;
    // if (elParentNode !== document.body) {
    //   elParentNode.parentNode.insertBefore(el, elParentNode);
    //   elParentNode.parentNode.removeChild(elParentNode);
    // }
  });
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

// $('> .some-class')
// document.querySelectorAll(parentElementSelector > '.some-class')

// $('.some-element').length
// document.querySelectorAll('.some-element').length

// $('.some-element').closest()
// document.querySelector('.some-element').closest()

// $('.some-elements').first()
// document.querySelectorAll('.some-element').item(0)
// findAll('.some-element').shift()

// $('.some.element').parent()
// document.querySelector('.some-element').parentElement
// document.querySelector('.some-element').parentNode

// $('.some.element').find('some-selector')
// findBySelector(elements, 'some-selector') - see above

// $('.some.element').find(some-element)
// findElement(elements, some-element) - see above

// $(':visible')
// visible(NodeList) - see above

// $(':not')
// filterElements(NodeList) - see above

// $(':first')
// $().filter(':first')
// document.querySelector('some-selector')

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

// $('.some-element').scrollTop(y)
// document.querySelector('.some-element').scroll(0, y)

// $('.some-element').scrollLeft(x)
// document.querySelector('.some-element').scroll(x, 0)

// $('.some-element').animate({ scrollTop }, 0)
// document.querySelector('.some-element').scroll({ top: scrollTop })
// document.querySelector('.some-element').scroll({ top: scrollTop, behavior: 'smooth' })

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
// await triggerEvent(find('.some-element'), 'some-event', { some-event-options })

// this.$().find('.rsa-application-overlay').click()
// await click(find('.rsa-application-overlay'))

// $('.some-element').contextmenu()
// await triggerEvent(find('.some-element'), 'contextmenu')
// await triggerEvent(find('.some-element'), 'contextmenu', { event-options })

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

// $('.some-element').val(some-value)
// document.querySelector('.some-element').setAttribute('value', some-value)

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
