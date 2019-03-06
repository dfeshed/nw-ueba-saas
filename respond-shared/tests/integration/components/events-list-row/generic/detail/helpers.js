import { findAll, find } from '@ember/test-helpers';
import { selectors } from './selectors';

// nodeType of 3 === text node
const textWithoutChildren = (parentElement) => {
  return [].reduce.call(parentElement.childNodes, (a, b) => {
    return a + (b.nodeType === 3 ? b.textContent && b.textContent.trim() : '');
  }, '');
};

export const assertDetailColumns = (assert, { total, children }) => {
  assert.equal(findAll(`${selectors.column}:nth-of-type(1)`).length, total);
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

export const assertNoRelatedLinks = (assert, { column }) => {
  const context = find(`${selectors.column}:nth-of-type(${column})`);
  assert.equal(context.textContent.trim(), '');
};

export const assertRelatedLinks = (assert, { column, row, values, urls }) => {
  const element = getContextElement(column, row);
  assert.equal(element.querySelector(selectors.key).textContent.trim(), 'Related Links');
  values.forEach((value, i) => {
    const index = i + 1;
    const selector = `${selectors.column} ${selectors.row}:nth-of-type(${index}) ${selectors.relatedLink}`;
    assert.equal(element.querySelector(selector).textContent.trim(), value);
    assert.equal(element.querySelector(selector).getAttribute('href'), urls[i]);
  });
};

export const assertProcessAnalysisLink = (assert, { value }) => {

  const selector = `${selectors.column} ${selectors.row}:nth-of-type(3) ${selectors.relatedLink}`;

  assert.equal(find(selector).textContent.trim(), value);
  assert.ok(find(selector).getAttribute('href'));
};

export const assertDetailRowParent = (assert, { column, row, label, value }) => {
  const element = getContextElement(column, row);
  assert.equal(element.querySelector(selectors.key).textContent.trim(), label);
  const parent = element.querySelector(selectors.value);
  assert.equal(textWithoutChildren(parent), value);
  return element;
};

const getChildElement = (parentElement, childRow) => {
  if (!childRow) {
    const child = parentElement.querySelector(selectors.column);
    return document.getElementById(child.getAttribute('id'));
  }
  const columnElement = parentElement.querySelector(selectors.column);
  const childRows = [].filter.call(columnElement.childNodes, (node) => {
    return node.getAttribute && node.getAttribute('test-id') === 'keyValueRow';
  });
  return childRows[childRow - 1];
};

export const assertDetailRowChild = (assert, { parentElement, label, value, childRow, metaKey }) => {
  const childElement = getChildElement(parentElement, childRow);
  assert.equal(childElement.querySelector(selectors.key).textContent.trim(), label);
  const parent = childElement.querySelector(selectors.value);
  assert.equal(textWithoutChildren(parent), value);
  if (metaKey) {
    assert.equal(parent.attributes['data-entity-id'].nodeValue, value);
    assert.equal(parent.attributes['data-meta-key'].nodeValue, metaKey);
    assert.equal(parent.classList.contains('entity'), true);
  }
  return childElement;
};
