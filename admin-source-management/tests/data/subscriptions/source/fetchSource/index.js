import sources from '../fetchSources/data';

export default {
  subscriptionDestination: '/user/queue/usm/source/get',
  requestDestination: '/ws/usm/source/get',
  message(frame) {
    const body = JSON.parse(frame.body);
    let source = {};
    let returnCode = -1; // use the real error code if we ever add one for "Source does NOT exist"
    for (let index = 0; index < sources.length; index++) {
      if (sources[index].id === body.data) {
        source = sources[index];
        returnCode = 0;
        break;
      }
    }
    return {
      code: returnCode, // 0 == Ok, anything else == Error
      data: source // change this to error info if we ever add anything for "Source does NOT exist"
    };
  }
};
