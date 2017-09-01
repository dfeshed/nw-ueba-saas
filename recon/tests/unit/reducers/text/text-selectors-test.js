import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  hasTextContent,
  allDataHidden,
  numberOfRenderableTextEntries,
  renderedText,
  eventHasPayload,
  metaHighlightCount
} from 'recon/reducers/text/selectors';
import { augmentedTextData } from '../../../helpers/data/index';
import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

const requests = augmentedTextData.filter((t) => t.side === 'request');
const responses = augmentedTextData.filter((t) => t.side === 'response');

module('Unit | selector | text');

test('hasTextContent', function(assert) {
  assert.expect(4);

  const tests = {
    noTextContent: hasTextContent(Immutable.from({
      text: {}
    })),
    textContentNull: hasTextContent(Immutable.from({
      text: {
        textContent: null
      }
    })),
    textContentEmpty: hasTextContent(Immutable.from({
      text: {
        textContent: []
      }
    })),
    hasTextContent: hasTextContent(Immutable.from({
      text: {
        textContent: augmentedTextData
      }
    }))
  };

  assert.equal(tests.noTextContent, false, 'hasTextContent should return false, when textContent missing');
  assert.equal(tests.textContentNull, false, 'hasTextContent should return false, when textContent null');
  assert.equal(tests.textContentEmpty, false, 'hasTextContent should return false, when textContent empty');
  assert.equal(tests.hasTextContent, true, 'hasTextContent should return true, when textContent present');
});

const textContentTests = (selector) => {
  const visuals = {
    isRequestShown: true,
    isResponseShown: true
  };

  const text = {
    textContent: augmentedTextData
  };

  return {
    noTextContentAndRRShown: selector(Immutable.from({
      text: {},
      visuals
    })),
    textContentNullAndRRShown: selector(Immutable.from({
      text: {
        textContent: null
      },
      visuals
    })),
    textContentEmptyAndRRShown: selector(Immutable.from({
      text: {
        textContent: []
      },
      visuals
    })),
    hasTextContentAndRRShown: selector(Immutable.from({
      text,
      visuals
    })),
    hasTextContentAndResponseHidden: selector(Immutable.from({
      text,
      visuals: {
        isRequestShown: true,
        isResponseShown: false
      }
    })),
    hasTextContentAndRequestHidden: selector(Immutable.from({
      text,
      visuals: {
        isRequestShown: false,
        isResponseShown: true
      }
    })),
    hasTextContentAndRRHidden: selector(Immutable.from({
      text,
      visuals: {
        isRequestShown: false,
        isResponseShown: false
      }
    })),
    hasTextContentAndRRMissing: selector(Immutable.from({
      text,
      visuals: {}
    }))
  };
};

test('allDataHidden', function(assert) {
  assert.expect(8);

  const tests = textContentTests(allDataHidden);

  assert.equal(tests.noTextContentAndRRShown, false, 'allDataHidden should return false, when textContent missing (data must be present to be hidden)');
  assert.equal(tests.textContentNullAndRRShown, false, 'allDataHidden should return false, when textContent null (data must be present to be hidden)');
  assert.equal(tests.textContentEmptyAndRRShown, false, 'allDataHidden should return false, when textContent empty (data must be present to be hidden)');
  assert.equal(tests.hasTextContentAndRRShown, false, 'allDataHidden should return false, when textContent present and R+R shown');
  assert.equal(tests.hasTextContentAndResponseHidden, false, 'allDataHidden should return false, when textContent present and request shown');
  assert.equal(tests.hasTextContentAndRequestHidden, false, 'allDataHidden should return false, when textContent present and response shown');
  assert.equal(tests.hasTextContentAndRRHidden, true, 'allDataHidden should return true, when textContent present and R+R hidden');
  assert.equal(tests.hasTextContentAndRRMissing, false, 'allDataHidden should return false, when textContent present and R+R missing');
});


test('numberOfRenderableTextEntries', function(assert) {
  assert.expect(8);

  const tests = textContentTests(numberOfRenderableTextEntries);

  assert.equal(tests.noTextContentAndRRShown, 0, 'numberOfRenderableTextEntries should return 0, when textContent missing');
  assert.equal(tests.textContentNullAndRRShown, 0, 'numberOfRenderableTextEntries should return 0, when textContent null');
  assert.equal(tests.textContentEmptyAndRRShown, 0, 'numberOfRenderableTextEntries should return 0, when textContent empty');
  assert.equal(tests.hasTextContentAndRRShown, augmentedTextData.length, 'numberOfRenderableTextEntries should return false, when textContent present and R+R shown');
  assert.equal(tests.hasTextContentAndResponseHidden, requests.length, 'numberOfRenderableTextEntries should return false, when textContent present and request shown');
  assert.equal(tests.hasTextContentAndRequestHidden, responses.length, 'numberOfRenderableTextEntries should return false, when textContent present and response shown');
  assert.equal(tests.hasTextContentAndRRHidden, 0, 'numberOfRenderableTextEntries should return 0, when textContent present and R+R hidden');
  assert.equal(tests.hasTextContentAndRRMissing, augmentedTextData.length, 'numberOfRenderableTextEntries should return false, when textContent present and R+R missing');
});

const renderedTextContentTests = (selector) => {

  const visuals = {
    isRequestShown: true,
    isResponseShown: true
  };

  return {
    noTextNoRenderIds: selector(Immutable.from({
      text: {
        textContent: [],
        renderIds: []
      },
      visuals
    })),
    hasTextNoRenderIds: selector(Immutable.from({
      text: {
        textContent: augmentedTextData,
        renderIds: []
      },
      visuals
    })),
    noTextHasRenderIds: selector(Immutable.from({
      text: {
        textContent: [],
        renderIds: ['1', '2']
      },
      visuals
    })),
    hasTextHasIdsDoNotMatch: selector(Immutable.from({
      text: {
        textContent: augmentedTextData,
        renderIds: ['1', '2']
      },
      visuals
    })),
    hasTextHasRequestIdsMatch: selector(Immutable.from({
      text: {
        textContent: augmentedTextData,
        renderIds: requests.map((t) => t.firstPacketId)
      },
      visuals
    })),
    hasTextAllIdsMatch: selector(Immutable.from({
      text: {
        textContent: augmentedTextData,
        renderIds: augmentedTextData.map((t) => t.firstPacketId)
      },
      visuals
    })),
    hasTextAllIdsMatchRRHidden: selector(Immutable.from({
      text: {
        textContent: augmentedTextData,
        renderIds: augmentedTextData.map((t) => t.firstPacketId)
      },
      visuals: {
        isRequestShown: false,
        isResponseShown: false
      }
    }))
  };
};

test('renderedText', function(assert) {
  assert.expect(7);

  const tests = renderedTextContentTests(renderedText);

  assert.equal(tests.noTextNoRenderIds.length, 0, 'renderedText should return empty array, when textContent/render IDs missing');
  assert.equal(tests.hasTextNoRenderIds.length, 0, 'renderedText should return empty array, when no render IDs missing');
  assert.equal(tests.noTextHasRenderIds.length, 0, 'renderedText should return empty array, when textContent missing');
  assert.equal(tests.hasTextHasIdsDoNotMatch.length, 0, 'renderedText should return empty array, when renderIds do not match IDs in textContent');
  assert.equal(tests.hasTextHasRequestIdsMatch.length, requests.length, 'renderedText should return requests when renderIds match request IDs');
  assert.equal(tests.hasTextAllIdsMatch.length, augmentedTextData.length, 'renderedText should return all the data when all the data\'s IDs are in renderIds');
  assert.equal(tests.hasTextAllIdsMatchRRHidden.length, 0, 'renderedText should return empty array when data present but all R+R hidden');

});


test('eventHasPayload', function(assert) {
  assert.expect(8);

  const tests = renderedTextContentTests(eventHasPayload);

  assert.equal(tests.noTextNoRenderIds, false, 'eventHasPayload should return false, when textContent/render IDs missing');
  assert.equal(tests.hasTextNoRenderIds, false, 'eventHasPayload should return false, when no render IDs missing');
  assert.equal(tests.noTextHasRenderIds, false, 'eventHasPayload should return false, when textContent missing');
  assert.equal(tests.hasTextHasIdsDoNotMatch, false, 'eventHasPayload should return false, when renderIds do not match IDs in textContent');
  assert.equal(tests.hasTextHasRequestIdsMatch, true, 'eventHasPayload should return true, when renderIds match request IDs and text is present');
  assert.equal(tests.hasTextAllIdsMatch, true, 'eventHasPayload should return true when all the data\'s IDs are in renderIds and text is present');
  assert.equal(tests.hasTextAllIdsMatchRRHidden, false, 'eventHasPayload should return false when data present but all R+R hidden');

  const textlessEntries = augmentedTextData.map((t) => Object.assign({}, t, { text: '' }));
  const noTextInEntries = eventHasPayload(Immutable.from({
    text: {
      textContent: textlessEntries,
      renderIds: textlessEntries.map((t) => t.firstPacketId)
    },
    visuals: {
      isRequestShown: true,
      isResponseShown: true
    }
  }));

  assert.equal(noTextInEntries, false, 'eventHasPayload should return false when data present, and all renderable, but all has no text payload');

});

test('metaHighlightCount', function(assert) {
  const text = {
    textContent: augmentedTextData,
    renderIds: augmentedTextData.map((t) => t.firstPacketId)
  };

  const visuals = {
    isRequestShown: true,
    isResponseShown: true,
    currentReconView: {
      code: RECON_VIEW_TYPES_BY_NAME.TEXT.code
    }
  };

  const meta = {
    meta: [
      ['medium', 1],
      ['client', 'HTTP']
    ]
  };

  const tests = {
    hasMetaToHighlight: metaHighlightCount(Immutable.from({
      text,
      visuals,
      meta
    })),
    hasMetaToHighlightForLog: metaHighlightCount(Immutable.from({
      text,
      visuals,
      meta: {
        meta: [
          ['medium', 32],
          ['username', 'HTTP']
        ]
      }
    })),
    hasBogusMeta: metaHighlightCount(Immutable.from({
      text,
      visuals,
      meta: {
        meta: [
          ['medium', 1],
          ['foo', 'bar']
        ]
      }
    })),
    hasMetaForOtherEventType: metaHighlightCount(Immutable.from({
      text,
      visuals,
      meta: {
        meta: [
          ['medium', 1],
          ['category', 'HTTP']  // category does not match Network/1
        ]
      }
    })),
    noMetaToHighlight: metaHighlightCount(Immutable.from({
      text,
      visuals,
      meta: {
        meta: []
      }
    })),
    notTextView: metaHighlightCount(Immutable.from({
      text,
      visuals: {
        isRequestShown: true,
        isResponseShown: true,
        currentReconView: {
          code: RECON_VIEW_TYPES_BY_NAME.PACKET.code
        }
      },
      meta
    })),
    rrHidden: metaHighlightCount(Immutable.from({
      text,
      visuals: {
        isRequestShown: false,
        isResponseShown: false,
        currentReconView: {
          code: RECON_VIEW_TYPES_BY_NAME.TEXT.code
        }
      },
      meta
    })),
    noRenderIds: metaHighlightCount(Immutable.from({
      text: {
        textContent: augmentedTextData,
        renderIds: []
      },
      visuals,
      meta
    }))
  };

  assert.equal(tests.hasMetaToHighlight.length, 1, 'hasMetaToHighlight should return array with meta entry for meta found in network text');
  assert.equal(tests.hasMetaToHighlight[0].name, 'client', 'hasMetaToHighlight return meta name with count');
  assert.equal(tests.hasMetaToHighlight[0].count, 652, 'hasMetaToHighlight should return count of meta found in text');
  assert.equal(tests.hasMetaToHighlightForLog.length, 1, 'hasMetaToHighlight should return array with meta entry for meta found in log text');
  assert.equal(tests.hasMetaToHighlightForLog[0].name, 'username', 'hasMetaToHighlight return meta name with count');
  assert.equal(tests.hasMetaToHighlightForLog[0].count, 652, 'hasMetaToHighlight should return count of meta found in text');
  assert.equal(tests.hasBogusMeta.length, 0, 'hasMetaToHighlight should return empty array when meta does not match');
  assert.equal(tests.hasMetaForOtherEventType.length, 0, 'hasMetaToHighlight should return empty array when meta matches but for wrong event type');
  assert.equal(tests.noMetaToHighlight.length, 0, 'metaHighlightCount should return empty array when there are no meta');
  assert.equal(tests.notTextView.length, 0, 'hasMetaToHighlight should return empty array if not on text view, no highlighting on text view');
  assert.equal(tests.rrHidden.length, 0, 'hasMetaToHighlight should return empty array if all data hidden');
  assert.equal(tests.noRenderIds.length, 0, 'hasMetaToHighlight should return empty array no text entries are rendered');

});