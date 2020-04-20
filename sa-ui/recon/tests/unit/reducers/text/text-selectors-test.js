import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  hasTextContent,
  allDataHidden,
  numberOfRenderableTextEntries,
  renderedText,
  eventHasPayload,
  metaHighlightCount,
  canGoToPreviousPage,
  canGoToNextPage,
  canGoToLastPage,
  _renderableText
} from 'recon/reducers/text/selectors';
import { augmentedTextData } from '../../../helpers/data/index';
import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

const requests = augmentedTextData.filter((t) => t.side === 'request');
const responses = augmentedTextData.filter((t) => t.side === 'response');

const _stateConfig = {
  meta: [],
  data: {
    eventType: 'NETWORK'
  }
};

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

test('canGoToPreviousPage', function(assert) {
  assert.expect(2);
  const tests = {
    onFirstPage: canGoToPreviousPage(Immutable.from({
      text: {
        textPageNumber: 1,
        canPrevious: false,
        isTextPageLoading: false
      }
    })),
    onRandomPage: canGoToPreviousPage(Immutable.from({
      text: {
        textPageNumber: 3,
        canPrevious: true,
        isTextPageLoading: false
      }
    }))
  };

  assert.equal(tests.onFirstPage, false, 'canGoToPreviousPage should return false, when we are on the first page');
  assert.equal(tests.onRandomPage, true, 'canGoToPreviousPage should return true, when we are on a random page');
});

test('canGoToNextPage', function(assert) {
  assert.expect(4);
  const tests = {
    onFirstPage: canGoToNextPage(Immutable.from({
      text: {
        canNext: true,
        isTextPageLoading: false,
        textPageNumber: 1,
        textLastPage: null
      }
    })),
    onRandomPage: canGoToNextPage(Immutable.from({
      text: {
        canNext: true,
        isTextPageLoading: false,
        textPageNumber: 3,
        textLastPage: null
      }
    })),
    onLastPage: canGoToNextPage(Immutable.from({
      text: {
        canNext: false,
        isTextPageLoading: false,
        textPageNumber: 6,
        textLastPage: 6
      }
    })),
    onRandomPageAfterLastPageSeen: canGoToNextPage(Immutable.from({
      text: {
        canNext: true,
        isTextPageLoading: false,
        textPageNumber: 2,
        textLastPage: 6
      }
    }))
  };

  assert.equal(tests.onFirstPage, true, 'canGoToNextPage should return true, when we are on the first page');
  assert.equal(tests.onRandomPage, true, 'canGoToNextPage should return true, when we are on a random page');
  assert.equal(tests.onLastPage, false, 'canGoToNextPage should return false, when we are on the last page');
  assert.equal(tests.onRandomPageAfterLastPageSeen, true, 'canGoToNextPage should return true, when we are on a random page after encountering the last page');
});

test('canGoToLastPage', function(assert) {
  assert.expect(4);
  const tests = {
    onFirstPage: canGoToLastPage(Immutable.from({
      text: {
        canLast: false,
        isTextPageLoading: false,
        textPageNumber: 1,
        textLastPage: null
      }
    })),
    onRandomPage: canGoToLastPage(Immutable.from({
      text: {
        canLast: false,
        isTextPageLoading: false,
        textPageNumber: 3,
        textLastPage: null
      }
    })),
    onLastPage: canGoToLastPage(Immutable.from({
      text: {
        canLast: false,
        isTextPageLoading: false,
        textPageNumber: 6,
        textLastPage: 6
      }
    })),
    onRandomPageAfterLastPageSeen: canGoToLastPage(Immutable.from({
      text: {
        canLast: true,
        isTextPageLoading: false,
        textPageNumber: 2,
        textLastPage: 6
      }
    }))
  };

  assert.equal(tests.onFirstPage, false, 'canGoToLastPage should return false, when we are on the first page and not encountered the last page');
  assert.equal(tests.onRandomPage, false, 'canGoToLastPage should return false, when we are on a random page and not encountered the last page');
  assert.equal(tests.onLastPage, false, 'canGoToLastPage should return false, when we are on the last page');
  assert.equal(tests.onRandomPageAfterLastPageSeen, true, 'canGoToLastPage should return true, when we are on a random page after encountering the last page');
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
      visuals,
      ..._stateConfig
    })),
    textContentNullAndRRShown: selector(Immutable.from({
      text: {
        textContent: null
      },
      visuals,
      ..._stateConfig
    })),
    textContentEmptyAndRRShown: selector(Immutable.from({
      text: {
        textContent: []
      },
      visuals,
      ..._stateConfig
    })),
    hasTextContentAndRRShown: selector(Immutable.from({
      text,
      visuals,
      ..._stateConfig
    })),
    hasTextContentAndResponseHidden: selector(Immutable.from({
      text,
      visuals: {
        isRequestShown: true,
        isResponseShown: false
      },
      ..._stateConfig
    })),
    hasTextContentAndRequestHidden: selector(Immutable.from({
      text,
      visuals: {
        isRequestShown: false,
        isResponseShown: true
      },
      ..._stateConfig
    })),
    hasTextContentAndRRHidden: selector(Immutable.from({
      text,
      visuals: {
        isRequestShown: false,
        isResponseShown: false
      },
      ..._stateConfig
    })),
    hasTextContentAndRRMissing: selector(Immutable.from({
      text,
      visuals: {},
      ..._stateConfig
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
      visuals,
      ..._stateConfig
    })),
    hasTextNoRenderIds: selector(Immutable.from({
      text: {
        textContent: augmentedTextData,
        renderIds: []
      },
      visuals,
      ..._stateConfig
    })),
    noTextHasRenderIds: selector(Immutable.from({
      text: {
        textContent: [],
        renderIds: ['1', '2']
      },
      visuals,
      ..._stateConfig
    })),
    hasTextHasIdsDoNotMatch: selector(Immutable.from({
      text: {
        textContent: augmentedTextData,
        renderIds: ['1', '2']
      },
      visuals,
      ..._stateConfig
    })),
    hasTextHasRequestIdsMatch: selector(Immutable.from({
      text: {
        textContent: augmentedTextData,
        renderIds: requests.map((t) => t.firstPacketId)
      },
      visuals,
      ..._stateConfig
    })),
    hasTextAllIdsMatch: selector(Immutable.from({
      text: {
        textContent: augmentedTextData,
        renderIds: augmentedTextData.map((t) => t.firstPacketId)
      },
      visuals,
      ..._stateConfig
    })),
    hasTextAllIdsMatchRRHidden: selector(Immutable.from({
      text: {
        textContent: augmentedTextData,
        renderIds: augmentedTextData.map((t) => t.firstPacketId)
      },
      visuals: {
        isRequestShown: false,
        isResponseShown: false
      },
      ..._stateConfig
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
    },
    ..._stateConfig
  }));

  assert.equal(noTextInEntries, false, 'eventHasPayload should return false when data present, and all renderable, but all has no text payload');

});

test('_renderableText will not generate data if req/response are hidden and it is a network event', function(assert) {
  const state = Immutable.from({
    visuals: {
      isRequestShown: false,
      isResponseShown: false
    },
    text: {
      textContent: augmentedTextData,
      renderIds: augmentedTextData.map((t) => t.firstPacketId)
    },
    ..._stateConfig
  });

  assert.equal(_renderableText(state).length, 0, 'All content should be filtered out');
});

test('_renderableText will generate data if req/response are not hidden and it is a network event', function(assert) {
  const state = Immutable.from({
    visuals: {
      isRequestShown: true,
      isResponseShown: true
    },
    text: {
      textContent: augmentedTextData,
      renderIds: augmentedTextData.map((t) => t.firstPacketId)
    },
    ..._stateConfig
  });

  assert.ok(_renderableText(state).length > 0, 'Did not find network content');
});

test('_renderableText will generate data if a log event, even though visual req/resp are hidden', function(assert) {
  const state = Immutable.from({
    visuals: {
      isRequestShown: false,
      isResponseShown: false
    },
    text: {
      textContent: augmentedTextData,
      renderIds: augmentedTextData.map((t) => t.firstPacketId)
    },
    meta: [],
    data: {
      eventType: 'LOG'
    }
  });

  assert.ok(_renderableText(state).length > 0, 'Did not find Log event content');
});

test('_renderableText will generate data if a endpoint event, even though visual req/resp are hidden', function(assert) {
  const state = Immutable.from({
    visuals: {
      isRequestShown: false,
      isResponseShown: false
    },
    text: {
      textContent: augmentedTextData,
      renderIds: augmentedTextData.map((t) => t.firstPacketId)
    },
    meta: [],
    data: {
      eventType: 'ENDPOINT'
    }
  });

  assert.ok(_renderableText(state).length > 0, 'Did not find endpoint event content');
});

test('metaHighlightCount', function(assert) {
  const data = { eventType: null };

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
      data,
      text,
      visuals,
      meta
    })),
    hasMetaToHighlightForLog: metaHighlightCount(Immutable.from({
      data,
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
      data,
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
      data,
      text,
      visuals,
      meta: {
        meta: [
          ['medium', 1],
          ['category', 'HTTP'] // category does not match Network/1
        ]
      }
    })),
    noMetaToHighlight: metaHighlightCount(Immutable.from({
      data,
      text,
      visuals,
      meta: {
        meta: []
      }
    })),
    notTextView: metaHighlightCount(Immutable.from({
      data,
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
      data,
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
      data,
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
