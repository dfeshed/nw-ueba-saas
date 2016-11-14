import { module, test } from 'qunit';
import Helpers from 'sa/incident/helpers';

module('Unit | Utility | incident/helpers');

test('Test incident/helpers is available', function(assert) {
  assert.ok(Helpers);
});

test('Test source map long names', function(assert) {
  const sources = Helpers.sourceLongNames();
  assert.equal(sources.length, 7, 'Source map has correct number of known sources.');

  // Check source long names as they are going to be displayed to the user
  const sourceLongNames = sources.map((source) => source);
  assert.ok(sourceLongNames.includes('Event Stream Analysis'), 'Long Name: Event Stream Analysis');
  assert.ok(sourceLongNames.includes('Event Streaming Analytics'), 'Long Name: Event Streaming Analytics');
  assert.ok(sourceLongNames.includes('ECAT'), 'Long Name: ECAT');
  assert.ok(sourceLongNames.includes('Malware Analysis'), 'Long Name: Malware Analysis');
  assert.ok(sourceLongNames.includes('Reporting Engine'), 'Long Name: Reporting Engine');
  assert.ok(sourceLongNames.includes('Security Analytics Investigator'), 'Long Name: Security Analytics Investigator');
  assert.ok(sourceLongNames.includes('Web Threat Detection'), 'Long Name: Web Threat Detection');

});

test('Test source map short names', function(assert) {
  // Check source short names as they are going to be displayed to the user
  assert.equal(Helpers.sourceShortName('Event Stream Analysis'), 'ESA', 'Short Name: ESA');
  assert.equal(Helpers.sourceShortName('Event Streaming Analytics'), 'ESA', 'Short Name: ESA');
  assert.equal(Helpers.sourceShortName('ECAT'), 'ECAT', 'Short Name: ECAT');
  assert.equal(Helpers.sourceShortName('Malware Analysis'), 'MA', 'Short Name: MA');
  assert.equal(Helpers.sourceShortName('Reporting Engine'), 'RE', 'Short Name: RE');
  assert.equal(Helpers.sourceShortName('Security Analytics Investigator'), 'SAI', 'Short Name: SAI');
  assert.equal(Helpers.sourceShortName('Web Threat Detection'), 'WTD', 'Short Name: WTD');

  // Check default usage if unknown source is provided
  assert.equal(Helpers.sourceShortName('Unknown Source'), 'US', 'Default Short Name: US');
});


