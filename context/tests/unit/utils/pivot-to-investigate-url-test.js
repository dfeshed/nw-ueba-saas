import { pivotToInvestigateUrl } from 'context/util/context-data-modifier';
import { module, test } from 'qunit';

module('Unit | Utility | pivot to investigate url');

const singleQuote = '\'';
const singleQuoteRe = /\'/g;
const backslash = '\\';
const backslashRe = /\\/g;

test('it double encodes a URI with special characters ', function(assert) {
  const entityType = 'USER';
  const entityId = '%4';
  const metas = ['username'];

  const result = pivotToInvestigateUrl(entityType, entityId, metas);
  const resultDecoded = decodeURIComponent(decodeURIComponent(result));

  assert.notEqual(resultDecoded.indexOf(`${metas[0]}='${entityId}'`), -1);
});

test('it prefixes a single-quote in a meta value with a backslash', function(assert) {
  const entityType = 'USER';
  const entityId = `John ${singleQuote}JJ${singleQuote} Smith`;
  const entityIdWithPrefixes = entityId.replace(singleQuoteRe, `${backslash}${singleQuote}`);
  const metas = ['username'];

  const result = pivotToInvestigateUrl(entityType, entityId, metas);
  const resultDecoded = decodeURIComponent(decodeURIComponent(result));

  assert.notEqual(resultDecoded.indexOf(`${metas[0]}='${entityIdWithPrefixes}'`), -1);
});

test('it prefixes a backslash in a meta value with a backslash', function(assert) {
  const entityType = 'USER';
  const entityId = `local${backslash}admin`;
  const entityIdWithPrefixes = entityId.replace(backslashRe, `${backslash}${backslash}`);
  const metas = ['username'];

  const result = pivotToInvestigateUrl(entityType, entityId, metas);
  const resultDecoded = decodeURIComponent(decodeURIComponent(result));

  assert.notEqual(resultDecoded.indexOf(`${metas[0]}='${entityIdWithPrefixes}'`), -1);
});
