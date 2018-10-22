import { findAll, find } from '@ember/test-helpers';
import { selectors } from './selectors';

// nodeType of 3 === text node
const textWithoutChildren = (parentElement) => {
  return [].reduce.call(parentElement.childNodes, (a, b) => {
    return a + (b.nodeType === 3 ? b.textContent && b.textContent.trim() : '');
  }, '');
};

export const assertDetailColumns = (assert, { total, children }) => {
  assert.equal(findAll(selectors.column).length, total);
  assert.equal(findAll(`${selectors.column}:nth-of-type(1)`).length, total);
  assert.equal(findAll(`${selectors.column}:nth-of-type(2)`).length, 0);
  assert.equal(find(`${selectors.column}:nth-of-type(1)`).children.length, children);
};

const getContextElement = (column, row) => {
  const context = find(`${selectors.column}:nth-of-type(${column})`);
  const contextElement = document.getElementById(context.getAttribute('id'));
  const child = contextElement.children[row - 1];
  return document.getElementById(child.getAttribute('id'));
};

export const assertDetailRow = (assert, { column, row, label, value, nestedColumns }) => {
  const element = getContextElement(column, row);
  assert.equal(element.querySelectorAll(selectors.key).length, 1);
  assert.equal(element.querySelector(selectors.key).textContent.trim(), label);
  assert.equal(element.querySelectorAll(selectors.value).length, 1);
  assert.equal(element.querySelector(selectors.value).textContent.trim(), value);
  assert.equal(element.querySelectorAll(selectors.column).length, nestedColumns || 0);
};

export const assertDetailRowParent = (assert, { column, row, label, value }) => {
  const element = getContextElement(column, row);
  assert.equal(element.querySelector(selectors.key).textContent.trim(), label);
  const parent = element.querySelector(selectors.value);
  assert.equal(textWithoutChildren(parent), value);
  return element;
};

export const assertDetailRowChild = (assert, { parentElement, label, value, subRowIndex, metaKey }) => {
  const index = subRowIndex || 1;
  const child = parentElement.querySelector(`${selectors.column}:nth-of-type(1)`);
  const childElement = document.getElementById(child.getAttribute('id'));
  const theChild = childElement.children[index - 1];
  assert.equal(theChild.querySelector(selectors.key).textContent.trim(), label);
  const parent = theChild.querySelector(selectors.value);
  assert.equal(textWithoutChildren(parent), value);
  if (metaKey) {
    assert.equal(parent.attributes['data-entity-id'].nodeValue, value);
    assert.equal(parent.attributes['data-meta-key'].nodeValue, metaKey);
    assert.equal(parent.classList.contains('entity'), true);
  }
  return childElement;
};
