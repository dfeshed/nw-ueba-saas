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

const getRowElement = (column, row) => {
  const firstColumn = find(`${selectors.column}:nth-of-type(${column})`);
  const firstColumnElement = document.getElementById(firstColumn.getAttribute('id'));
  const firstRow = firstColumnElement.querySelector(`${selectors.row}:nth-of-type(${row})`);
  return document.getElementById(firstRow.getAttribute('id'));
};

export const assertDetailRow = (assert, { column, row, label, value, nestedColumns }) => {
  const rowElement = getRowElement(column, row);
  assert.equal(rowElement.querySelectorAll(selectors.key).length, 1);
  assert.equal(rowElement.querySelector(selectors.key).textContent.trim(), label);
  assert.equal(rowElement.querySelectorAll(selectors.value).length, 1);
  assert.equal(rowElement.querySelector(selectors.value).textContent.trim(), value);
  assert.equal(rowElement.querySelectorAll(selectors.column).length, nestedColumns || 0);
};

export const assertDetailRowParent = (assert, { column, row, label, value, childKeys, childValues, nestedColumns }) => {
  const rowElement = getRowElement(column, row);
  assert.equal(rowElement.querySelectorAll(selectors.key).length, childKeys);
  assert.equal(rowElement.querySelector(`${selectors.key}:nth-of-type(1)`).textContent.trim(), label);
  assert.equal(rowElement.querySelectorAll(selectors.value).length, childValues);
  const parent = rowElement.querySelector(selectors.value);
  assert.equal(textWithoutChildren(parent), value);
  assert.equal(rowElement.querySelectorAll(selectors.column).length, nestedColumns);
  return rowElement;
};

export const assertDetailRowChild = (assert, { parentElement, label, value, childKeys, childValues, nestedColumns }) => {
  const child = parentElement.querySelector(`${selectors.column}:nth-of-type(1)`);
  const childElement = document.getElementById(child.getAttribute('id'));
  assert.equal(childElement.querySelectorAll(selectors.key).length, childKeys);
  assert.equal(childElement.querySelector(`${selectors.key}:nth-of-type(1)`).textContent.trim(), label);
  assert.equal(childElement.querySelectorAll(selectors.value).length, childValues);
  const parent = childElement.querySelector(selectors.value);
  assert.equal(textWithoutChildren(parent), value);
  assert.equal(childElement.querySelectorAll(selectors.column).length, nestedColumns);
  return childElement;
};
