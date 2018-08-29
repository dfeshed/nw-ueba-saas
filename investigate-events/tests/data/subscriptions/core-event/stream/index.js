import { util } from 'mock-server';
import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/events',
  requestDestination: '/ws/investigate/events/stream',
  page(frame, sendMessage) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const { filter, stream: { limit } } = bodyParsed;
    const eventList = data();
    const query = (filter || []).find((ele) => ele.field === 'query');
    const queryValue = query && query.value;
    const match = queryValue ? String(queryValue).match(/\(sessionid > ([0-9]+)\)/) : null;
    const sessionId = match && parseInt(match[1], 10);
    let results = !sessionId ? eventList : eventList.filter(function(evt) {
      return evt.sessionId > sessionId;
    });
    results = limit ? results.slice(0, limit) : results;

    // Query console error types

    // const offlineRoot = [{
    //   data: [],
    //   meta: {
    //     description: 'Queued',
    //     percent: 0
    //   }
    // }, {
    //   data: [],
    //   meta: {
    //     description: 'Executing',
    //     percent: 0
    //   }
    // }, {
    //   data: [],
    //   meta: {
    //     percent: 100,
    //     completed: true,
    //     devices: [{
    //       serviceId: 'foo',
    //       on: false,
    //       elapsedTime: 2000
    //     }]
    //   }
    // }];

    // const fatalErrorRoot = [{
    //   data: [],
    //   meta: {
    //     description: 'Queued',
    //     percent: 0
    //   }
    // }, {
    //   data: [],
    //   meta: {
    //     description: 'Executing',
    //     percent: 0
    //   }
    // }, {
    //   data: [],
    //   meta: {
    //     completed: false,
    //     fatal: 'Syntax Error'
    //   }
    // }];

    // const offlineChild = [{
    //   data: [],
    //   meta: {
    //     description: 'Queued',
    //     percent: 0
    //   }
    // }, {
    //   data: [],
    //   meta: {
    //     description: 'Executing',
    //     percent: 0
    //   }
    // }, {
    //   data: [],
    //   meta: {
    //     description: 'Collecting index summaries',
    //     percent: 33
    //   }
    // }, {
    //   data: [],
    //   meta: {
    //     description: 'Scanning index pages',
    //     percent: 66
    //   }
    // }, {
    //   data: [],
    //   meta: {
    //     description: 'Deduplicating and sorting 0 results',
    //     percent: 100,
    //     devices: [{
    //       serviceId: 'foo',
    //       on: true,
    //       elapsedTime: 2000
    //     }, {
    //       serviceId: 'bar',
    //       on: false,
    //       elapsedTime: 2000
    //     }]
    //   }
    // }];

    // const setupFramesWithWarning = [{
    //   data: [],
    //   meta: {
    //     description: 'Queued',
    //     percent: 0
    //   }
    // }, {
    //   data: [],
    //   meta: {
    //     description: 'Executing',
    //     percent: 0
    //   }
    // }, {
    //   data: [],
    //   meta: {
    //     description: 'Collecting index summaries',
    //     percent: 33
    //   },
    // }, {
    //   data: [],
    //   meta: {
    //     description: 'Scanning index pages',
    //     percent: 66,
    //     serviceId: 'foo',
    //     error: 'some error message about foo'
    //   }
    // }, {
    //   data: [],
    //   meta: {
    //     description: 'Deduplicating and sorting 0 results',
    //     percent: 100
    //   }
    // }];

    // Query console successful response

    const setupFrames = [{
      data: [],
      meta: {
        description: 'Queued',
        percent: 0
      }
    }, {
      data: [],
      meta: {
        description: 'Executing',
        percent: 0
      }
    }, {
      data: [],
      meta: {
        description: 'Collecting index summaries',
        percent: 33
      }
    }, {
      data: [],
      meta: {
        description: 'Scanning index pages',
        percent: 66
      }
    }, {
      data: [],
      meta: {
        description: 'Deduplicating and sorting 0 results',
        percent: 100
      }
    }];

    const metaPostProcessing = (message, index) => {
      if (message.meta.percent === 100) {
        message.meta.devices = [{
          serviceId: bodyParsed.filter.find((f) => f.field === 'endpointId').value,
          on: true,
          elapsedTime: index * 1000
        }];
      }

      return message;
    };

    util.sendBatches({
      requestBody: bodyParsed,
      dataArray: results,
      sendMessage,
      delayBetweenBatches: 1000,
      setupFrames,
      metaPostProcessing
    });
  }
};
