export default {
  subscriptionDestination: '/user/queue/investigate/validate/query',
  requestDestination: '/ws/investigate/validate/query',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const [ query ] = (bodyParsed.filter || []).filter((ele) => ele.field === 'query');
    const { value } = query;
    const forceFailure = (value.indexOf('xxx') >= 0);
    if (forceFailure) {
      return {
        code: 1,
        meta: { message: 'Server validation failed' }
      };
    } else {
      return {
        code: 0,
        data: true
      };
    }
  }
};